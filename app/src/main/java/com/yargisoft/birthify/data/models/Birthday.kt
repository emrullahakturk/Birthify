package com.yargisoft.birthify.data.models

import java.io.Serializable

data class Birthday (val id: String = "", // Firestore'da belge ID olarak kullanılacak
                     val name: String = "",
                     val birthdayDate: String = "",
                     val recordedDate: String = "",
                     val note: String = "",
                     val userId : String = "",
                     val notifyDate : String = "",
    ): Serializable {

    override fun toString(): String {
        return "Birthday(id=$id, name=$name, " +
                "userId= $userId, " +
                "birthdayDate = $birthdayDate," +
                " note = $note, " +
                " notifyDate = $notifyDate, " +
                "recordedDate = $recordedDate  )"
    }

}