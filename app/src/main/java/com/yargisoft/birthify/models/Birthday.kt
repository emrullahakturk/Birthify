package com.yargisoft.birthify.models

data class Birthday (val id: String = "", // Firestore'da belge ID olarak kullanılacak
                     val name: String = "",
                     val birthdayDate: String = "",
                     val recordedDate: String = "",
                     val note: String = "",
                     val userId : String = ""
    ){

    override fun toString(): String {
        return "Birthday(id=$id, name=$name, " +
                "userId= $userId, " +
                "birthdayDate = $birthdayDate," +
                " note = $note, " +
                "recordedDate = $recordedDate  )"
    }

}