package com.yargisoft.birthify.repositories

import com.google.firebase.firestore.FirebaseFirestore
import com.yargisoft.birthify.model.Birthday
import kotlinx.coroutines.tasks.await

class BirthdayRepository {
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
}
