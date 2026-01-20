package com.dm.unimove.db.fb

import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.firestore

class FBDatabase {
    interface Listener {
        fun onUserLoaded(user: FBUser)
        fun onUserSignOut()
        fun onRideAdded(ride: FBRide)
        fun onRideUpdated(ride: FBRide)
        fun onRideRemoved(ride: FBRide)
    }

    private val auth = Firebase.auth
    private val db = Firebase.firestore
    private var ridesListReg: ListenerRegistration? = null
    private var listener: Listener? = null

    init {
        auth.addAuthStateListener { auth ->
            if (auth.currentUser == null) {
                ridesListReg?.remove()
                listener?.onUserSignOut()
                return@addAuthStateListener
            }

            val refCurrUser = db.collection("users").document(auth.currentUser!!.uid)

            refCurrUser.get().addOnSuccessListener { document ->
                document.toObject(FBUser::class.java)?.let { user ->
                    listener?.onUserLoaded(user)
                }
            }

            ridesListReg = refCurrUser.collection("rides")
                .addSnapshotListener { snapshots, ex ->
                    if (ex != null) return@addSnapshotListener
                    snapshots?.documentChanges?.forEach { change ->
                        val fbRide = change.document.toObject(FBRide::class.java)
                        if (fbRide != null) {
                            when (change.type) {
                                DocumentChange.Type.ADDED -> listener?.onRideAdded(fbRide)
                                DocumentChange.Type.MODIFIED -> listener?.onRideUpdated(fbRide)
                                DocumentChange.Type.REMOVED -> listener?.onRideRemoved(fbRide)
                            }
                        }
                    }
                }
        }
    }

    fun setListener(listener: Listener? = null) {
        this.listener = listener
    }

    fun register(user: FBUser) {
        val uid = auth.currentUser?.uid ?: throw RuntimeException("User not logged in!")
        db.collection("users").document(uid).set(user)
    }

    fun add(ride: FBRide) {
        val uid = auth.currentUser?.uid ?: throw RuntimeException("User not logged in!")
        val name = ride.startingName ?: throw RuntimeException("Ride with null or empty name!")
        db.collection("users").document(uid).collection("rides")
            .document(name).set(ride)
    }

    fun remove(ride: FBRide) {
        val uid = auth.currentUser?.uid ?: throw RuntimeException("User not logged in!")
        val name = ride.startingName ?: throw RuntimeException("Ride with null or empty name!")
        db.collection("users").document(uid).collection("rides")
            .document(name).delete()
    }
}
