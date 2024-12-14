package com.yargisoft.birthify.data.repositories

import android.content.Context
import android.content.SharedPreferences
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.yargisoft.birthify.data.models.Birthday
import com.yargisoft.birthify.utils.sharedpreferences.UserConstants.PREFS_BIRTHDAYS
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.LocalDate
import java.time.Month
import java.util.Locale
import javax.inject.Inject

class BirthdayRepository @Inject constructor(
    @ApplicationContext val context: Context,
    private val gson: Gson,
    private val firestore: FirebaseFirestore
) {

    private val preferences: SharedPreferences = context.getSharedPreferences(PREFS_BIRTHDAYS, Context.MODE_PRIVATE)

    private fun <T> SharedPreferences.putList(key: String, list: List<T>) {
        edit().putString(key, gson.toJson(list)).apply()
    }


    private fun saveBirthdayToFirebase(newBirthday: Birthday, keyString: String) {
        val document = firestore.collection(keyString).document(newBirthday.id)
        firestore.runTransaction { transaction ->
            transaction.set(document, newBirthday)
        }
//            .addOnSuccessListener { onComplete(true) }
//            .addOnFailureListener { onComplete(false) }
    }

    // Doğum gününü kaydetme fonksiyonu
    fun saveBirthdaytoPreferences(newBirthday: Birthday, keyString: String) {
        val birthdays = getBirthdaysFromPreferences(keyString).toMutableList()
        birthdays.add(newBirthday)
        saveBirthdaysToPreferences(birthdays, keyString)

        //doğum gününü firebase'e kaydediyoruz
        saveBirthdayToFirebase(newBirthday, keyString)
    }


    // Doğum günü listesini preferences'a kaydetme işlemi
    private fun saveBirthdaysToPreferences(birthdays: List<Birthday>, keyString: String) {
        val editor = preferences.edit()
        editor.putString(keyString, gson.toJson(birthdays))
        editor.apply()

    }

    // Doğum günlerini lokalden getirme fonksiyonu
    fun getBirthdaysFromPreferences(keyString: String): List<Birthday> {
        val json = preferences.getString(keyString, null)
        return if (json != null) {
            val type = object : TypeToken<List<Birthday>>() {}.type
            gson.fromJson(json, type)
        } else {
            emptyList()
        }
    }

    fun getBirthdaysFromFirebase(
        keyString: String,
        userId: String,
        onComplete: (List<Birthday>, Boolean) -> Unit
    ) {
        firestore.collection(keyString)
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { snapshot ->
                val birthdays = snapshot.toObjects(Birthday::class.java)

                //gelen listeyi sharedPreferences'a kaydetme
                saveBirthdaysToPreferences(birthdays, keyString)
                onComplete(birthdays, true)
            }
            .addOnFailureListener {
                onComplete(emptyList(), false)
            }
    }


    //Bu fonksiyonlar kullanıcı hesabını "tamamen"!!! sildiğinde çalıştırılacak
    // Doğum günlerini temizleme fonksiyonu (local ve firebase)
    private fun clearBirthdaysFirebase(keyString: String) {
        //firebase üzerinden deleted_birthdays'i tamamen silme
        val collection = firestore.collection(keyString)
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

    private fun clearBirthdaysPreferences(keyString: String) {
        val editor = preferences.edit()
        editor.remove(keyString)
        editor.apply()
    }

    fun clearAllBirthdays() {

        //Localdeki doğum günlerini temizleme
        clearBirthdaysPreferences("birthdays")
        clearBirthdaysPreferences("past_birthdays")
        clearBirthdaysPreferences("deleted_birthdays")

        // Firebase'deki doğum günlerini temizleme
        clearBirthdaysFirebase("birthdays")
        clearBirthdaysFirebase("past_birthdays")
        clearBirthdaysFirebase("deleted_birthdays")

    }

    //doğum gününü localden ve firebase'den silme fonksiyonu
    fun deleteBirthday(birthdayId: String) {
        val birthdays = getBirthdaysFromPreferences("birthdays").toMutableList()
        val deletedBirthdays = getBirthdaysFromPreferences("deleted_birthdays").toMutableList()

        val birthdayToDelete = birthdays.find { it.id == birthdayId }
        if (birthdayToDelete != null) {
            birthdays.remove(birthdayToDelete)
            deletedBirthdays.add(birthdayToDelete)
            saveBirthdaysToPreferences(birthdays, "birthdays")
            saveBirthdaysToPreferences(birthdays, "deleted_birthdays")

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
        val birthdays = getBirthdaysFromPreferences("birthdays").toMutableList()
        val index = birthdays.indexOfFirst { it.id == updatedBirthday.id }

        if (index != -1) {
            birthdays[index] = updatedBirthday
            preferences.putList("birthdays", birthdays)
        }

        //firebase'deki dg update ediliyor
        val birthdayRef = firestore.collection("birthdays").document(updatedBirthday.id)
        birthdayRef.set(updatedBirthday)
//            .addOnSuccessListener { onComplete(true) }
//            .addOnFailureListener { onComplete(false) }
    }

    // Geçmiş doğum gününü tek tek kaydetme işlemi
    private fun saveBirthdayToPreferences(birthday: Birthday, keyString: String) {
        val birthdaysJson = preferences.getString(keyString, null)
        val birthdays = if (birthdaysJson != null) {
            gson.fromJson(birthdaysJson, Array<Birthday>::class.java).toMutableList()
        } else {
            mutableListOf()
        }

        birthdays.add(birthday)

        val editor = preferences.edit()
        editor.putString(keyString, gson.toJson(birthdays))
        editor.apply()
    }


    // Silinen doğum gününü tekrar kaydetme fonksiyonu (local ve firebase'de)
    fun reSaveDeletedBirthday(birthdayId: String) {
        val deletedBirthdays = getBirthdaysFromPreferences("deleted_birthdays").toMutableList()
        val birthdays = getBirthdaysFromPreferences("birthdays").toMutableList()
        val birthdayToReSave = deletedBirthdays.find { it.id == birthdayId }
        if (birthdayToReSave != null) {
            deletedBirthdays.remove(birthdayToReSave)
            birthdays.add(birthdayToReSave)
            saveBirthdaysToPreferences(birthdays, "birthdays")
            saveBirthdaysToPreferences(deletedBirthdays, "deleted_birthdays")

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
        val deletedBirthdays = getBirthdaysFromPreferences("deleted_birthdays").toMutableList()
        val birthdayToDelete = deletedBirthdays.find { it.id == birthdayId }
        if (birthdayToDelete != null) {
            deletedBirthdays.remove(birthdayToDelete)
            saveBirthdaysToPreferences(deletedBirthdays, "deleted_birthdays")
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
            val parts = it.birthdayDate.split(" ")
            val day = parts[0].toInt()
            val month = Month.valueOf(parts[1].uppercase(Locale.ENGLISH))
            val birthdayDate = LocalDate.of(currentDate.year, month, day)
            birthdayDate.isBefore(currentDate)
        }

        pastBirthdays.forEach { pastBirthday ->
            //geçmiş doğum günlerini tek tek firebase'e kaydediyoruz
            saveBirthdayToFirebase(pastBirthday, "past_birthdays")
            //geçmiş doğum günlerini tek tek lokal'e kaydediyoruz
            saveBirthdayToPreferences(pastBirthday, "past_birthdays")
            //geçmiş doğum günlerini tek tek firebase üzerinden ve local listeden kaldırıyoruz (yukarda past_birthdays olarak halihazırda kaydediyoruz)
            removeBirthdayFromMainList(pastBirthday.id)
        }
    }

    private fun removeBirthdayFromMainList(birthdayId: String) {
        val birthdays = getBirthdaysFromPreferences("birthdays").toMutableList()
        val birthdayToDelete = birthdays.find { it.id == birthdayId }
        if (birthdayToDelete != null) {
            birthdays.remove(birthdayToDelete)
            saveBirthdaysToPreferences(birthdays, "birthdays")

            //firebase üzerinden doğum gününü siliyoruz (deleted_birthdaye aktarmadan, çünkü öncesinde pasta aktarıldı)
            val birthdayRef = firestore.collection("birthdays").document(birthdayId)
            firestore.runTransaction { transaction ->
                transaction.delete(birthdayRef)
            }
//                .addOnSuccessListener { onComplete(true) }
//                .addOnFailureListener { onComplete(false) }
        }

    }
}
