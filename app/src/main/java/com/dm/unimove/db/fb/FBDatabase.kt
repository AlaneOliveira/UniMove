package com.dm.unimove.db.fb

class FBDatabase {
    interface Listener {
        fun onUserLoaded(user: FBUser)
        fun onUserSignOut()
        fun onCityAdded(city: FBRide)
        fun onCityUpdated(city: FBRide)
        fun onCityRemoved(city: FBRide)
    }
    private val auth = Firebase.auth
    private val db = Firebase.firestore
    private var ridesListReg: ListenerRegistration? = null
    private var listener : Listener? = null


    init {
        auth.addAuthStateListener { auth ->
            if (auth.currentUser == null) {
                ridesListReg?.remove()
                listener?.onUserSignOut()
                return@addAuthStateListener
            }


            val refCurrUser = db.collection("users").document(auth.currentUser!!.uid)


            refCurrUser.get().addOnSuccessListener {
                it.toObject(FBUser::class.java)?.let { user ->
                    listener?.onUserLoaded(user)
                }
            }


            ridesListReg = refCurrUser.collection("rides")
                .addSnapshotListener { snapshots, ex ->
                    if (ex != null) return@addSnapshotListener
                    snapshots?.documentChanges?.forEach { change ->
                        val fbRide = change.document.toObject(FBRide::class.java)
                        if (change.type == DocumentChange.Type.ADDED) {
                            listener?.onCityAdded(fbRide)
                        } else if (change.type == DocumentChange.Type.MODIFIED) {
                            listener?.onCityUpdated(fbRide)
                        } else if (change.type == DocumentChange.Type.REMOVED) {
                            listener?.onCityRemoved(fbRide)
                        }
                    }
                }
        }
    }


    fun setListener(listener: Listener? = null) {
        this.listener = listener
    }


    fun register(user: FBUser) {
        if (auth.currentUser == null)
            throw RuntimeException("User not logged in!")
        val uid = auth.currentUser!!.uid
        db.collection("users").document(uid + "").set(user);
    }


    fun add(city: FBRide) {
        if (auth.currentUser == null)
            throw RuntimeException("User not logged in!")
        if (city.name == null || city.name!!.isEmpty())
            throw RuntimeException("Ride with null or empty name!")
        val uid = auth.currentUser!!.uid
        db.collection("users").document(uid).collection("rides")
            .document(city.name!!).set(city)
    }


    fun remove(city: FBRide) {
        if (auth.currentUser == null)
            throw RuntimeException("User not logged in!")
        if (city.name == null || city.name!!.isEmpty())
            throw RuntimeException("City with null or empty name!")
        val uid = auth.currentUser!!.uid
        db.collection("users").document(uid).collection("rides")
            .document(city.name!!).delete()
    }
}

