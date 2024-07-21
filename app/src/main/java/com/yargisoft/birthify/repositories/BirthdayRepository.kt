package com.yargisoft.birthify.repositories

import android.content.Context
import com.google.firebase.firestore.FirebaseFirestore
import com.yargisoft.birthify.models.Birthday
import kotlinx.coroutines.tasks.await

class BirthdayRepository (private val context: Context){
    private val firestore = FirebaseFirestore.getInstance()


    suspend fun saveBirthday(birthday: Birthday, onComplete: (Boolean) -> Unit) {
        try {
            val document = firestore.collection("birthdays").document()
            val id = document.id
            val birthdayWithId = birthday.copy(id = id)
            document.set(birthdayWithId).await()
            onComplete(true)

        } catch (e: Exception) {
            onComplete(false)
        }
    }

    fun getUserBirthdays(userId: String, onComplete: (List<Birthday>) -> Unit) {

        firestore.collection("birthdays")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { snapshot ->
                val birthdays = snapshot.toObjects(Birthday::class.java)
                onComplete(birthdays)
            }
            .addOnFailureListener {
                onComplete(emptyList())
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




    fun deleteBirthdayPermanently(birthdayId: String, onComplete: (Boolean) -> Unit) {
        val birthdayRef = firestore.collection("deleted_birthdays").document(birthdayId)
        firestore.runTransaction { transaction ->
            transaction.delete(birthdayRef)
        }.addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }



    fun reSaveDeletedBirthday(birthdayId: String, birthday: Birthday, onComplete: (Boolean) -> Unit) {
        val birthdayRef = firestore.collection("birthdays").document(birthdayId)
        val deletedBirthdaysRef = firestore.collection("deleted_birthdays").document(birthdayId)
        firestore.runTransaction { transaction ->
            transaction.delete(deletedBirthdaysRef)
            transaction.set(birthdayRef, birthday)
        }.addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }


    fun getDeletedBirthdays(userId: String, onComplete: (List<Birthday>) -> Unit) {
        firestore.collection("deleted_birthdays")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { snapshot ->
                val birthdays = snapshot.toObjects(Birthday::class.java)
                onComplete(birthdays)
            }
            .addOnFailureListener {
                onComplete(emptyList())
            }
    }
}
