package com.yargisoft.birthify.repositories

import android.content.Context
import com.google.firebase.firestore.FirebaseFirestore
import com.yargisoft.birthify.models.Birthday
import kotlinx.coroutines.tasks.await

class BirthdayRepository (private val context: Context){
    private val firestore = FirebaseFirestore.getInstance()

    suspend fun saveBirthday(birthday: Birthday): Boolean {
        return try {
            val document = firestore.collection("birthdays").document()
            val id = document.id
            val birthdayWithId = birthday.copy(id = id)
            document.set(birthdayWithId).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun getUserBirthdays(userId: String): List<Birthday> {
        return try {
            val snapshot = firestore.collection("birthdays")
                .whereEqualTo("userId", userId)
                .get()
                .await()

            snapshot.toObjects(Birthday::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun updateBirthday(birthday: Birthday, onComplete: (Boolean) -> Unit) {
        val birthdayRef = firestore.collection("birthdays").document(birthday.id)
        birthdayRef.set(birthday)
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }



    fun deleteBirthday(birthdayId: String, birthday: Birthday, onComplete: (Boolean) -> Unit) {
        val birthdayRef = firestore.collection("birthdays").document(birthdayId)
        val deletedBirthdaysRef = firestore.collection("deleted_birthdays").document(birthdayId)

        firestore.runTransaction { transaction ->
            transaction.delete(birthdayRef)
            transaction.set(deletedBirthdaysRef, birthday)
        }.addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }

    suspend fun getDeletedBirthdays(userId: String): List<Birthday> {
        return try {
            val snapshot = firestore.collection("deleted_birthdays")
                .whereEqualTo("userId", userId)
                .get()
                .await()

            snapshot.toObjects(Birthday::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }


}
