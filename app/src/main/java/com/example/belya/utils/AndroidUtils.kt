package com.example.belya.utils
import android.content.Intent
import com.example.belya.model.User
import com.google.firebase.auth.FirebaseAuth

object AndroidUtils {
    fun passUserModelAsIntent(intent: Intent, user: User) {
        intent.putExtra("firstName", user.firstName)
        intent.putExtra("lastName", user.lastName)
        intent.putExtra("phone", user.phoneNumber)
        intent.putExtra("userId", user.userID)
    }

    fun getUserModelFromIntent(intent: Intent): User {
        val user = User()
        user.firstName = intent.getStringExtra("firstName") ?: ""
        user.lastName = intent.getStringExtra("lastName") ?: ""
        user.phoneNumber = intent.getStringExtra("phone") ?: ""
        user.userID = intent.getStringExtra("userId") ?: ""
        return user
    }
    fun currentUserId(): String? {
        return FirebaseAuth.getInstance().uid
    }
}
