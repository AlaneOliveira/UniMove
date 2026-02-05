package com.dm.unimove.model

import com.google.firebase.firestore.PropertyName

data class User(
    val name: String = "",
    val email: String = "",
    @get:PropertyName("is_busy") // Força o Firestore a ler este nome exato
    @set:PropertyName("is_busy") // Força o Firestore a escrever este nome exato
    var is_busy: Boolean = false
)