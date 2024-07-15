package com.yargisoft.birthify.repositories

 import android.content.Context
 import android.util.Log
 import androidx.core.content.ContentProviderCompat.requireContext
 import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.yargisoft.birthify.sharedpreferences.SharedPreferencesManager
import kotlinx.coroutines.tasks.await

class AuthRepository(private val context: Context){
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private  val sharedPreferencesManager = SharedPreferencesManager(context)

    suspend fun loginUser(email: String, password: String, isChecked:Boolean): Boolean {
         // Firebase kimlik doğrulama kodu burada olacak
        return try {
            val result = Firebase.auth.signInWithEmailAndPassword(email, password).await()
            sharedPreferencesManager.saveUserSession(email,result.user?.uid!!,isChecked)
            Log.e("usercred"," ${sharedPreferencesManager.getUserCredentials()}" )
            Log.e("ischeck"," ${sharedPreferencesManager.checkIsUserLoggedIn()}" )
            result.user != null
        } catch (e: Exception) {
            false
        }
    }
    suspend fun registerUser(name: String, email: String, password: String): Boolean {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            result.user?.let { firebaseUser ->
                val user = hashMapOf(
                    "uid" to firebaseUser.uid,
                    "name" to name,
                    "email" to email
                )
                firestore.collection("users").document(firebaseUser.uid).set(user).await()
            }
            true
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
