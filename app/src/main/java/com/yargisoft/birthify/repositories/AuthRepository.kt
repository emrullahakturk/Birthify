package com.yargisoft.birthify.repositories

import android.content.SharedPreferences
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import java.util.UUID

class AuthRepository(private val sharedPreferences: SharedPreferences) {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

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
                            clearUserSession()
                            user.sendEmailVerification()
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

    // Function to update the user's profile
    fun updateUserProfile(name: String?, password: String?, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        val user = auth.currentUser
        user?.let {
            val updates = mutableMapOf<String, Any>()
            name?.let { updates["name"] = it }
            password?.let {
                user.updatePassword(it)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            updates["password"] = it
                        } else {
                            onFailure(task.exception?.message ?: "Password update failed.")
                        }
                    }
            }
            if (updates.isNotEmpty()) {
                firestore.collection("users").document(user.uid).update(updates)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            onSuccess()
                        } else {
                            onFailure(task.exception?.message ?: "Profile update failed.")
                        }
                    }
            } else {
                onSuccess()
            }
        } ?: onFailure("User not logged in.")
    }

  /*  // Function to check if the user's email is verified
    fun isEmailVerified(): Boolean {
        return auth.currentUser?.isEmailVerified ?: false
    }*/
}


/*
class AuthRepository(context: Context) {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private  val userSharedPreferencesManager = UserSharedPreferencesManager(context)


    suspend fun registerUser(name: String, email: String, password: String,recordedDate:String): Boolean {
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
                    "recorded_date" to recordedDate,
                )
                firestore.collection("users").document(firebaseUser.uid).set(user).await()
            }
            true
        } catch (e: Exception) {
            Log.e("exception","$e")
            false
        }
    }



    // Doğrulama e-postası gönder
    private suspend fun sendVerificationEmail(user: FirebaseUser) {
        user.sendEmailVerification().await()
    }

    // Kullanıcının e-postasını doğrula
    suspend fun isEmailVerified(): Boolean {
       return try {
           val currentUser = auth.currentUser
           currentUser?.reload()?.await()
           return currentUser?.isEmailVerified ?: false
       }catch (e:Exception){
           Log.e("exception","$e")
           false
       }
    }

    //oturum açan kullanıcının idsine erişme
    suspend fun loggedUserId(): String {
        val currentUser = auth.currentUser
        currentUser?.reload()?.await()
        return currentUser?.uid ?: ""
    }

    //oturum açma
    suspend fun loginUser(email: String, password: String, isChecked: Boolean): Pair<Boolean,Boolean> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            userSharedPreferencesManager.saveUserSession(email, result.user!!.uid, isChecked)
           Pair(result.user!=null, result.user!!.isEmailVerified)
        } catch (e: Exception) {
            Log.e("exception","$e")
            Pair(false,false)
        }
    }


    fun logout() {
        auth.signOut()
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
*/
