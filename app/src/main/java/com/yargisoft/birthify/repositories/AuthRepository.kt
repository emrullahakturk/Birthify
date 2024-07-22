package com.yargisoft.birthify.repositories

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.yargisoft.birthify.sharedpreferences.UserSharedPreferencesManager
import kotlinx.coroutines.tasks.await

class AuthRepository(private val context: Context) {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private  val userSharedPreferencesManager = UserSharedPreferencesManager(context)


    // Kullanıcıyı kayıt et ve doğrulama e-postası gönder
    suspend fun registerUser(name: String, email: String, password: String): Boolean {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            result.user?.let { firebaseUser ->
                // Kullanıcı adını güncelle
                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName(name)
                    .build()
                firebaseUser.updateProfile(profileUpdates).await()

                // Doğrulama e-postası gönder
                sendVerificationEmail(firebaseUser)

                // Firestore'a kullanıcı bilgilerini kaydet
                val user = hashMapOf(
                    "uid" to firebaseUser.uid,
                    "name" to name,
                    "email" to email,
                )
                firestore.collection("users").document(firebaseUser.uid).set(user).await()
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    // Doğrulama e-postası gönder
    private suspend fun sendVerificationEmail(user: FirebaseUser) {
        user.sendEmailVerification().await()
    }

    // Kullanıcının e-postasını doğrula
    suspend fun isEmailVerified(): Boolean {
        val currentUser = auth.currentUser
        currentUser?.reload()?.await()
        return currentUser?.isEmailVerified ?: false
    }

    suspend fun loginUser(email: String, password: String, isChecked: Boolean): Boolean {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            userSharedPreferencesManager.saveUserSession(email, result.user?.uid!!, isChecked)
            result.user != null
        } catch (e: Exception) {
            false
        }
    }

    suspend fun resetPassword(email: String): Boolean {
        return try {
            auth.sendPasswordResetEmail(email).await()
            true
        } catch (e: Exception) {
            false
        }
    }
}
