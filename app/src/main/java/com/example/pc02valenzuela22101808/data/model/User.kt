package com.example.pc02valenzuela22101808.data.model

data class AppUser(
    val uid: String = "",
    val name: String = "",
    val email: String = ""
) {
    fun toMap(): MutableMap<String, Any> = mutableMapOf(
        "uid" to uid,
        "name" to name,
        "email" to email
    )

    companion object {
        fun fromMap(map: Map<String, Any?>): AppUser = AppUser(
            uid = map["uid"] as? String ?: "",
            name = map["name"] as? String ?: "",
            email = map["email"] as? String ?: ""
        )
    }
}
