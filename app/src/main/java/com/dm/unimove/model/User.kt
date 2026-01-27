package com.dm.unimove.model

import com.google.firebase.firestore.DocumentReference

data class User(
    val name: String = "",
    val email: String = "",
    val is_busy: Boolean = false
)