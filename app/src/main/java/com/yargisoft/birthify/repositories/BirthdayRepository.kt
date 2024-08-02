package com.yargisoft.birthify.repositories

import android.content.Context
import android.content.SharedPreferences
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.yargisoft.birthify.models.Birthday
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class BirthdayRepository (context: Context){
    private val firestore = FirebaseFirestore.getInstance()
    private val gson = Gson()
    private val birthdayPreferences: SharedPreferences = context.getSharedPreferences("birthdays", Context.MODE_PRIVATE)
    private val pastBirthdayPreferences: SharedPreferences = context.getSharedPreferences("past_birthdays", Context.MODE_PRIVATE)
    private val deletedBirthdayPreferences: SharedPreferences = context.getSharedPreferences("deleted_birthdays", Context.MODE_PRIVATE)


    private fun <T> SharedPreferences.putList(key: String, list: List<T>) {
        edit().putString(key, gson.toJson(list)).apply()
    }


    // Doğum günlerini kaydetme fonksiyonu
    fun saveBirthday(newBirthday: Birthday) {
        val birthdays = getBirthdays().toMutableList()
        birthdays.add(newBirthday)
        saveBirthdays(birthdays)

        //doğum gününü firebase'e kaydediyoruz
        val document = firestore.collection("birthdays").document(newBirthday.id)
        firestore.runTransaction { transaction ->
            transaction.set(document, newBirthday)
        }
//            .addOnSuccessListener { onComplete(true) }
//            .addOnFailureListener { onComplete(false) }
    }

    // Geçmiş doğum günlerini kaydetme fonksiyonu
    private fun savePastBirthdayToFirebase(newBirthday: Birthday) {
        val document = firestore.collection("past_birthdays").document(newBirthday.id)
        firestore.runTransaction { transaction ->
            transaction.set(document, newBirthday)
        }
//            .addOnSuccessListener { onComplete(true) }
//            .addOnFailureListener { onComplete(false) }
    }

    //doğum gününü localden ve firebase'den silme fonksiyonu
    fun deleteBirthday(birthdayId: String) {
        val birthdays = getBirthdays().toMutableList()
        val deletedBirthdays = getDeletedBirthdays().toMutableList()

        val birthdayToDelete = birthdays.find { it.id == birthdayId }
        if (birthdayToDelete != null) {
            birthdays.remove(birthdayToDelete)
            deletedBirthdays.add(birthdayToDelete)
            saveBirthdays(birthdays)
            saveDeletedBirthdays(deletedBirthdays)


            //firebase üzerinden doğum gününü silip deleted birthdayse aktarıyoruz
            val birthdayRef = firestore.collection("birthdays").document(birthdayId)
            val deletedBirthdaysRef = firestore.collection("deleted_birthdays").document(birthdayId)

            firestore.runTransaction { transaction ->
                transaction.delete(birthdayRef)
                transaction.set(deletedBirthdaysRef, birthdayToDelete)
            }
//                .addOnSuccessListener { onComplete(true) }
//                .addOnFailureListener { onComplete(false) }
        }

    }


    //doğum gününü localde ve firebase'de update etme fonksiyonu
    fun updateBirthday(updatedBirthday: Birthday) {
        val birthdays = getBirthdays().toMutableList()
        val index = birthdays.indexOfFirst { it.id == updatedBirthday.id }

        if (index != -1) {
            birthdays[index] = updatedBirthday
            birthdayPreferences.putList("birthdays", birthdays)
        }

        //firebase'deki dg update ediliyor
        val birthdayRef = firestore.collection("birthdays").document(updatedBirthday.id)
        birthdayRef.set(updatedBirthday)
//            .addOnSuccessListener { onComplete(true) }
//            .addOnFailureListener { onComplete(false) }
    }



    // Doğum günlerini kaydetme işlemi
    private fun saveBirthdays(birthdays: List<Birthday>) {
        val editor = birthdayPreferences.edit()
        editor.putString("birthdays", gson.toJson(birthdays))
        editor.apply()

    }

    // Silinen doğum günlerini deleted_birthdays'e kaydetme işlemi
    private fun saveDeletedBirthdays(deletedBirthdays: List<Birthday>) {
        val editor = deletedBirthdayPreferences.edit()
        editor.putString("deleted_birthdays", gson.toJson(deletedBirthdays))
        editor.apply()
    }

    // Geçmiş doğum günlerini kaydetme işlemi
    private fun savePastBirthdays(birthdays: List<Birthday>) {
        val editor = pastBirthdayPreferences.edit()
        editor.putString("past_birthdays", gson.toJson(birthdays))
        editor.apply()
    }

    //doğum gününü silme fonksiyonu
    private fun removeBirthdayFromBirthdayList(birthdayId: String) {
        val birthdays = getBirthdays().toMutableList()
        val birthdayToDelete = birthdays.find { it.id == birthdayId }
        if (birthdayToDelete != null) {
            birthdays.remove(birthdayToDelete)
            saveBirthdays(birthdays)
        }

    }
    // Geçmiş doğum günlerini tek tek kaydetme işlemi
    private fun savePastBirthday(birthday: Birthday) {
        val pastBirthdaysJson = pastBirthdayPreferences.getString("past_birthdays", null)
        val pastBirthdays = if (pastBirthdaysJson != null) {
            gson.fromJson(pastBirthdaysJson, Array<Birthday>::class.java).toMutableList()
        } else {
            mutableListOf()
        }

        pastBirthdays.add(birthday)

        val editor = pastBirthdayPreferences.edit()
        editor.putString("past_birthdays", gson.toJson(pastBirthdays))
        editor.apply()
    }



    // Silinen doğum gününü tekrar kaydetme fonksiyonu (local ve firebase'de)
    fun reSaveDeletedBirthday(birthdayId: String) {
        val deletedBirthdays = getDeletedBirthdays().toMutableList()
        val birthdays = getBirthdays().toMutableList()
        val birthdayToReSave = deletedBirthdays.find { it.id == birthdayId }
        if (birthdayToReSave != null) {
            deletedBirthdays.remove(birthdayToReSave)
            birthdays.add(birthdayToReSave)
            saveBirthdays(birthdays)
            saveDeletedBirthdays(deletedBirthdays)

            //firebasedeki doğum gününü resave etme
            val birthdayRef = firestore.collection("birthdays").document(birthdayId)
            val deletedBirthdaysRef = firestore.collection("deleted_birthdays").document(birthdayId)
            firestore.runTransaction { transaction ->
                transaction.delete(deletedBirthdaysRef)
                transaction.set(birthdayRef, birthdayToReSave)
            }
//            .addOnSuccessListener { onComplete(true) }
//            .addOnFailureListener { onComplete(false) }
        }




    }
    // Silinen doğum gününü kalıcı olarak silme fonksiyonu (local ve firebase'de)
    fun permanentlyDeleteBirthday(birthdayId: String) {
        val deletedBirthdays = getDeletedBirthdays().toMutableList()
        val birthdayToDelete = deletedBirthdays.find { it.id == birthdayId }
        if (birthdayToDelete != null) {
            deletedBirthdays.remove(birthdayToDelete)
            saveDeletedBirthdays(deletedBirthdays)
        }

        //firebase üzerindeki doğum günlerini kalıcı olarak silme
        val birthdayRef = firestore.collection("deleted_birthdays").document(birthdayId)
        firestore.runTransaction { transaction ->
            transaction.delete(birthdayRef)
        }
//            .addOnSuccessListener { onComplete(true) }
//            .addOnFailureListener { onComplete(false) }

    }




    //geçmiş doğum günlerini ve yaklaşan doğum günlerini ayıran fonksiyon
    fun filterPastAndUpcomingBirthdays(birthdays: List<Birthday>) {
        val currentDate = LocalDate.now()
        val pastBirthdays = birthdays.filter {
            val birthdayDate = LocalDate.parse(it.birthdayDate, DateTimeFormatter.ofPattern("dd MMMM", Locale.ENGLISH))
            birthdayDate.isBefore(currentDate)
        }

        pastBirthdays.forEach { pastBirthday ->
            //geçmiş doğum günlerini tek tek firebase'e kaydediyoruz
            savePastBirthdayToFirebase(pastBirthday)
            //geçmiş doğum günlerini tek tek lokal'e kaydediyoruz
            savePastBirthday(pastBirthday)
            //geçmiş doğum günlerini tek tek firebase üzerinden ve local listeden kaldırıyoruz (yukarda past_birthdays olarak halihazırda kaydediyoruz)
            removeBirthdayFromMainList(pastBirthday.id)
        }
    }

    private fun removeBirthdayFromMainList(birthdayId: String) {
        val birthdays = getBirthdays().toMutableList()
        val birthdayToDelete = birthdays.find { it.id == birthdayId }
        if (birthdayToDelete != null) {
            birthdays.remove(birthdayToDelete)
            saveBirthdays(birthdays)

            //firebase üzerinden doğum gününü silip deleted birthdayse aktarıyoruz
            val birthdayRef = firestore.collection("birthdays").document(birthdayId)
            firestore.runTransaction { transaction ->
                transaction.delete(birthdayRef)
            }
//                .addOnSuccessListener { onComplete(true) }
//                .addOnFailureListener { onComplete(false) }
        }

    }


    //Bu fonksiyonlar kullanıcı hesabını "tamamen"!!! sildiğinde çalıştırılacak
    // Doğum günlerini temizleme fonksiyonu (local ve firebase)
    fun clearBirthdays() {
        val editor = birthdayPreferences.edit()
        editor.clear()
        editor.apply()
    }
    fun clearBirthdaysFirebase() {
        //firebase üzerindeki birthdays listesini tamamen silme
        val collection = firestore.collection("birthdays")
        collection.get().addOnSuccessListener { querySnapshot ->
            val batch = firestore.batch()
            for (document in querySnapshot.documents) {
                batch.delete(document.reference)
            }

            batch.commit()
//                .addOnSuccessListener { onComplete(true) }
//                .addOnFailureListener { onComplete(false) }
        }
//            .addOnFailureListener {onComplete(false)}

    }


    // deleted_birthdays listesini tamamen temizleme fonksiyonu
    fun clearDeletedBirthdays() {
        val editor = deletedBirthdayPreferences.edit()
        editor.clear()
        editor.apply()
    }

    // deleted_birthdays listesini tamamen temizleme fonksiyonu (firebaseden)
    fun clearDeletedBirthdaysFirebase() {
        //firebase üzerinden deleted_birthdays'i tamamen silme
        val collection = firestore.collection("deleted_birthdays")
        collection.get().addOnSuccessListener { querySnapshot ->
            val batch = firestore.batch()
            for (document in querySnapshot.documents) {
                batch.delete(document.reference)
            }
            batch.commit()
//                .addOnSuccessListener { onComplete(true) }
//                .addOnFailureListener { onComplete(false) }
        }
//      .addOnFailureListener {onComplete(false)}

    }

    // past_birthdays listesini tamamen temizleme fonksiyonu
    fun clearPastBirthdays() {
        val editor = pastBirthdayPreferences.edit()
        editor.clear()
        editor.apply()
    }

    // past_birthdays listesini tamamen temizleme fonksiyonu
    fun clearPastBirthdaysFirebase() {
        //firebase üzerinden deleted_birthdays'i tamamen silme
        val collection = firestore.collection("past_birthdays")
        collection.get().addOnSuccessListener { querySnapshot ->
            val batch = firestore.batch()
            for (document in querySnapshot.documents) {
                batch.delete(document.reference)
            }
            batch.commit()
//                .addOnSuccessListener { onComplete(true) }
//                .addOnFailureListener { onComplete(false) }
        }
//      .addOnFailureListener {onComplete(false)}
    }


    // Doğum günlerini lokalden getirme fonksiyonları
    fun getBirthdays(): List<Birthday> {
        val json = birthdayPreferences.getString("birthdays", null)
        return if (json != null) {
            val type = object : TypeToken<List<Birthday>>() {}.type
            gson.fromJson(json, type)
        } else {
            emptyList()
        }
    }
    fun getDeletedBirthdays(): List<Birthday> {
        val json = deletedBirthdayPreferences.getString("deleted_birthdays", null)
        return if (json != null) {
            val type= object : TypeToken<List<Birthday>>() {}.type
            gson.fromJson(json, type)
        } else {
            emptyList()
        }
    }
    fun
            PastBirthdays(): List<Birthday> {
        val json = pastBirthdayPreferences.getString("past_birthdays", null)
        return if (json != null) {
            val type = object : TypeToken<List<Birthday>>() {}.type
            gson.fromJson(json, type)
        } else {
            emptyList()
        }
    }

    //Firebase üzerinden doğum günlerini çekme fonksiyonları
    fun getUserBirthdaysFromFirebase(userId: String, onComplete: (List<Birthday>, Boolean) -> Unit) {
        firestore.collection("birthdays")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { snapshot ->
                val birthdays = snapshot.toObjects(Birthday::class.java)
                //gelen listeyi preferences'a kaydetme
                saveBirthdays(birthdays)
                onComplete(birthdays, true)
            }
            .addOnFailureListener {
                onComplete(emptyList(), false)
            }
    }
    fun getDeletedBirthdaysFromFirebase(userId: String, onComplete: (List<Birthday>,Boolean) -> Unit) {
        firestore.collection("deleted_birthdays")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { snapshot ->
                val birthdays = snapshot.toObjects(Birthday::class.java)

                //gelen listeyi preferences'a kaydetme
                saveDeletedBirthdays(birthdays)
                onComplete(birthdays,true)
            }
            .addOnFailureListener {
                onComplete(emptyList(),false)
            }
    }
    fun getPastBirthdaysFromFirebase(userId: String, onComplete: (List<Birthday>, Boolean) -> Unit) {
        firestore.collection("past_birthdays")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { snapshot ->
                val birthdays = snapshot.toObjects(Birthday::class.java)

                //gelen listeyi preferences'a kaydetme
                savePastBirthdays(birthdays)
                onComplete(birthdays, true)
            }
            .addOnFailureListener {
                onComplete(emptyList(), false)
            }
    }



}
