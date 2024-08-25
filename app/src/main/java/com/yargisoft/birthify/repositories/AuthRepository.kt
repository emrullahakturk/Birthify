package com.yargisoft.birthify.repositories

import android.content.Context
import android.content.SharedPreferences
import com.google.firebase.Timestamp
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.UUID

class AuthRepository(private val sharedPreferences: SharedPreferences, context:Context) {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val credentialPreferences: SharedPreferences = context.getSharedPreferences("user_credentials", Context.MODE_PRIVATE)


    // Function to register a new user
     fun registerUser(name: String, email: String, password: String, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        firestore.collection("users")
            .whereEqualTo("email", email)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val existingUsers = task.result?.documents
                    if (existingUsers.isNullOrEmpty()) {
                        auth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener { authTask ->
                                if (authTask.isSuccessful) {
                                    val user = auth.currentUser
                                    user?.let {
                                        val userProfile = mapOf(
                                            "name" to name,
                                            "email" to email,
                                            "userId" to user.uid,
                                            "recordedDate" to FieldValue.serverTimestamp(),
                                            "token" to "" // Initially, the token is empty
                                        )
                                        firestore.collection("users")
                                            .document(user.uid)
                                            .set(userProfile)
                                            .addOnCompleteListener { profileTask ->
                                                if (profileTask.isSuccessful) {
                                                    user.sendEmailVerification()
                                                        .addOnCompleteListener { emailTask ->
                                                            if (emailTask.isSuccessful) {
                                                                onSuccess()
                                                            } else {
                                                                onFailure(emailTask.exception?.message ?: "Email verification failed.")
                                                            }
                                                        }
                                                } else {
                                                    onFailure(profileTask.exception?.message ?: "Profile saving failed.")
                                                }
                                            }
                                    }
                                } else {
                                    onFailure(authTask.exception?.message ?: "Registration failed.")
                                }
                            }
                    } else {
                        onFailure("Email is already in use.")
                    }
                } else {
                    onFailure("Error checking existing users: ${task.exception?.message}")
                }
            }
    }

    // Function to log in a user
    fun loginUser(email: String, password: String, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    user?.let {
                        if (user.isEmailVerified) {
                            val token = UUID.randomUUID().toString() // Generate a unique token
                            saveUserSession(user.uid, email, token)
                            updateToken(user.uid, token) { success ->
                                if (success) {
                                    onSuccess()
                                } else {
                                    onFailure("Token update failed.")
                                }
                            }
                        } else {
                            user.sendEmailVerification()
                            clearUserSession()
                            onFailure("Please verify your email.")
                        }
                    }
                } else {
                    when (task.exception) {
                        is FirebaseAuthInvalidUserException -> onFailure("No user found with this email.")
                        is FirebaseAuthInvalidCredentialsException -> onFailure("Incorrect e-mail or password.")
                        else -> onFailure(task.exception?.message ?: "Login failed.")
                    }
                }
            }
    }

    // Function to update the token in Firestore
    private fun updateToken(userId: String, token: String, callback: (Boolean) -> Unit) {
        firestore.collection("users")
            .document(userId)
            .update("token", token)
            .addOnCompleteListener { task ->
                callback(task.isSuccessful)
            }
    }

    // Function to save the user session in SharedPreferences
    private fun saveUserSession(userId: String, email: String, token: String) {
        val editor = sharedPreferences.edit()
        editor.putString("userId", userId)
        editor.putString("email", email)
        editor.putString("token", token)
        editor.putBoolean("isLoggedIn", true)
        editor.apply()
    }

    // Function to clear the user session from SharedPreferences
    private fun clearUserSession() {
        val editor = sharedPreferences.edit()
        editor.remove("userId")
        editor.remove("email")
        editor.remove("token")
        editor.putBoolean("isLoggedIn", false)
        editor.apply()
    }

    // Function to log out the user
    fun logoutUser() {
        auth.signOut()
        clearUserSession()
    }

    // Function to reset the user's password
    fun resetPassword(email: String, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onSuccess()
                } else {
                    onFailure(task.exception?.message ?: "Password reset failed.")
                }
            }
    }

    // Function to check if the user is logged in
    fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null && sharedPreferences.getBoolean("isLoggedIn", false)
    }

    fun updateUserPassword(
        currentPassword: String,
        newPassword: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        val user = auth.currentUser

        if (user == null) {
            onFailure("User not logged in.")
            return
        }

        // Kullanıcının email adresini al
        val email = user.email
        if (email.isNullOrEmpty()) {
            onFailure("Email address not found.")
            return
        }

        // Kullanıcıyı yeniden kimlik doğrulama işlemi
        val credential = EmailAuthProvider.getCredential(email, currentPassword)

        user.reauthenticate(credential)
            .addOnCompleteListener { authTask ->
                if (authTask.isSuccessful) {
                    // Yeniden kimlik doğrulama başarılıysa, şifreyi güncelle
                    user.updatePassword(newPassword)
                        .addOnCompleteListener { passwordUpdateTask ->
                            if (passwordUpdateTask.isSuccessful) {
                                // Şifre başarıyla güncellendi, Firestore'da da güncellenebilir
                                firestore.collection("users").document(user.uid)
                                    .update("password", newPassword)
                                    .addOnCompleteListener { firestoreTask ->
                                        if (firestoreTask.isSuccessful) {
                                            onSuccess()
                                        } else {
                                            onFailure(firestoreTask.exception?.message ?: "Password update in Firestore failed.")
                                        }
                                    }
                            } else {
                                onFailure(passwordUpdateTask.exception?.message ?: "Password update failed.")
                            }
                        }
                } else {
                    // Eski şifre yanlışsa hata döndür
                    onFailure(authTask.exception?.message ?: "Current password is incorrect.")
                }
            }
    }


    // Function to update the user's name
/*
    fun updateUserName(name: String, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        val user = auth.currentUser
        user?.let {
            val updates = mapOf("name" to name)
            firestore.collection("users").document(user.uid).update(updates)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        onSuccess()
                    } else {
                        onFailure(task.exception?.message ?: "Name update failed.")
                    }
                }
        } ?: onFailure("User not logged in.")
    }
*/


    fun deleteUserAccount(onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        val user = auth.currentUser
        user?.let {
            // İlk olarak Firestore'dan kullanıcı verilerini siliyoruz
            firestore.collection("users").document(user.uid).delete()
                .addOnCompleteListener { deleteTask ->
                    if (deleteTask.isSuccessful) {
                        // Firestore silme başarılı, şimdi kullanıcıyı Firebase Authentication'dan siliyoruz
                        user.delete()
                            .addOnCompleteListener { authTask ->
                                if (authTask.isSuccessful) {
                                    clearUserSession() // Kullanıcı oturum verilerini temizle
                                    onSuccess()
                                } else {
                                    onFailure(authTask.exception?.message ?: "User deletion failed.")
                                }
                            }
                    } else {
                        onFailure(deleteTask.exception?.message ?: "Failed to delete user data.")
                    }
                }
        } ?: onFailure("User not logged in.")
    }

    fun getUserCredentials(onSuccess: (Map<String, Any>) -> Unit, onFailure: (String) -> Unit) {
        val user = auth.currentUser
        if (user != null) {
            // Fetch user information from Firebase Authentication
            val userId = user.uid
            val email = user.email ?: "No email available"

            // Fetch additional user information from Firestore
            firestore.collection("users").document(userId).get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val document = task.result
                        if (document != null && document.exists()) {
                            // Extract user data from Firestore document
                            val name = document.getString("name") ?: "No name available"
                            val token = document.getString("token") ?: "No token available"
                            val recordedDate = document.getTimestamp("recordedDate") ?: Timestamp(seconds = 0, nanoseconds = 0)

                            // Create a map with the user credentials
                            val userCredentials = mapOf(
                                "userId" to userId,
                                "email" to email,
                                "name" to name,
                                "token" to token,
                                "recordedDate" to recordedDate,
                            )

                            val editor = credentialPreferences.edit()
                            editor.putString("name", name)
                            editor.putString("email", email)

                            val instant = Instant.ofEpochSecond(recordedDate.seconds, recordedDate.nanoseconds.toLong())
                            val dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
                            val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")
                            val formattedDateTime = dateTime.format(formatter)

                            editor.putString("recordedDate", formattedDateTime)
                            editor.apply()

                            // Return the user credentials via the success callback
                            onSuccess(userCredentials)
                        } else {
                            onFailure("User document does not exist.")
                        }
                    } else {
                        onFailure("Failed to retrieve user data from Firestore: ${task.exception?.message}")
                    }
                }
        } else {
            onFailure("No user is currently logged in.")
        }
    }

}

