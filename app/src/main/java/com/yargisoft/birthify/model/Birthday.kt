package com.yargisoft.birthify.model

data class Birthday (val id: String = "", // Firestore'da belge ID olarak kullanılacak
                     val name: String = "",
                     val surname: String = "",
                     val birthdayDate: String = "",
                     val recordedDate: String = "",
                     val note: String = ""){


}