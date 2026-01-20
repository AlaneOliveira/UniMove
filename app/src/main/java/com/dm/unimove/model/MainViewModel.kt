package com.dm.unimove.model

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class MainViewModel : ViewModel() {
    private val db = Firebase.firestore

    // Estado do Usuário Logado
    private val _user = mutableStateOf<User?>(null)
    val user: State<User?> = _user

    // Estado para a lista de caronas (Ex: para o Feed/Mapa)
    private val _availableRides = mutableStateOf<List<Ride>>(emptyList())
    val availableRides: State<List<Ride>> = _availableRides

    /**
     * Salva uma nova carona no Firestore.
     * Implementa a lógica de salvar na coleção global e no histórico do motorista.
     */
    fun createNewRide(ride: Ride) {
        // 1. Cria a carona na coleção grande CARONAS
        db.collection("CARONAS")
            .add(ride)
            .addOnSuccessListener { documentReference ->
                Log.d("Firestore", "Carona criada com ID: ${documentReference.id}")

                // 2. Adiciona a referência no perfil do motorista (Redundância para histórico rápido)
                ride.driver_ref?.let { driverRef ->
                    driverRef.collection("taken_rides_as_driver")
                        .document(documentReference.id)
                        .set(mapOf("ride_ref" to documentReference))
                }
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Erro ao criar carona", e)
            }
    }

    /**
     * Busca todas as caronas com status AVAILABLE para mostrar no mapa.
     */
    fun fetchAvailableRides() {
        db.collection("CARONAS")
            .whereEqualTo("status", RideStatus.AVAILABLE.name)
            .addSnapshotListener { value, error ->
                if (error != null) return@addSnapshotListener

                val list = value?.documents?.mapNotNull { it.toObject(Ride::class.java) }
                _availableRides.value = list ?: emptyList()
            }
    }

    /**
     * Carrega os dados do perfil do usuário do Firestore.
     * Chamado logo após o login ou na inicialização.
     */
    fun loadUserProfile(userId: String) {
        db.collection("USERS").document(userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.w("Firestore", "Erro ao carregar perfil", error)
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    // Converte o documento do Firestore para o seu modelo User.kt
                    _user.value = snapshot.toObject(User::class.java)
                }
            }
    }

    /**
     * Define o usuário atual após o login.
     */
    fun setUser(loggedUser: User) {
        _user.value = loggedUser
    }
}