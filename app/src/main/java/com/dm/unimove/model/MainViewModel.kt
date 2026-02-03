package com.dm.unimove.model

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore

class MainViewModel : ViewModel() {
    private val db = Firebase.firestore

    // Estado do Usuário Logado
    private val _user = mutableStateOf<User?>(null)
    val user: State<User?> = _user

    // Estado para a lista de caronas (Ex: para o Feed/Mapa)
    private val _availableRides = mutableStateOf<List<Pair<String, Ride>>>(emptyList())
    val availableRides: State<List<Pair<String, Ride>>> = _availableRides

    /**
     * Salva uma nova carona no Firestore.
     * Implementa a lógica de salvar na coleção global e no histórico do motorista.
     */
    fun createNewRide(ride: Ride) {
        db.collection("CARONAS")
            .add(ride)
            .addOnSuccessListener { docRef ->
                ride.driver_ref?.let { driverRef ->
                    // Usamos o ID da carona como ID do documento na subcoleção
                    driverRef.collection("caronas como motorista")
                        .document(docRef.id)
                        .set(mapOf("ride_ref" to docRef)) // Guarda a referência direta
                }
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
                val list = value?.documents?.mapNotNull { doc ->
                    val ride = doc.toObject(Ride::class.java)
                    if (ride != null) doc.id to ride else null
                }
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

    /**
     * Cria o documento com o ID do Auth e coloca os campos name e email
     */
    fun saveUserToFirestore(user: User, userId: String) {
        db.collection("USERS").document(userId)
            .set(user)
            .addOnSuccessListener {
                Log.d("Firestore", "Perfil do usuário atualizado com sucesso!")
            }
    }

    fun canUserStartNewActivity(): Boolean {
        val currentUser = _user.value
        return currentUser?.is_busy == false || currentUser == null
    }

    fun updateUserBusyStatus(userId: String, busy: Boolean) {
        db.collection("USERS").document(userId)
            .update("is_busy", busy)
            .addOnSuccessListener { Log.d("Firestore", "Status de ocupação atualizado: $busy") }
    }

    fun sendRideSolicitation(ride: Ride, rideId: String, passengerId: String) {
        val db = com.google.firebase.firestore.FirebaseFirestore.getInstance()
        val passengerRef = db.collection("USERS").document(passengerId)
        val rideRef = db.collection("CARONAS").document(rideId)

        val solicitation = mapOf(
            "ride_id" to rideRef,
            "passenger_ref" to passengerRef,
            "driver_ref" to ride.driver_ref,
            "status" to "PENDING",
            "timestamp" to com.google.firebase.Timestamp.now()
        )

        db.collection("SOLICITACOES")
            .add(solicitation)
            .addOnSuccessListener {
                Log.d("Firestore", "Solicitação enviada!")
                updateUserBusyStatus(passengerId, true)
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Erro ao enviar solicitação", e)
            }
    }

    fun registerNewUser(user: User, password: String, onComplete: (Boolean, String?) -> Unit) {
        val auth = com.google.firebase.Firebase.auth
        auth.createUserWithEmailAndPassword(user.email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = task.result?.user?.uid
                    if (userId != null) {
                        db.collection("USERS").document(userId)
                            .set(user).addOnSuccessListener { onComplete(true, null) }.addOnFailureListener { e -> onComplete(false, e.message) }
                    }
                } else {
                    onComplete(false, task.exception?.message)
                }
            }
    }
}