package it.polito.g13.firebase_objects

data class ProfileUser(

   // var photo: String? = null,

    var name_surname: String = "",

    var nickname: String = "",

    var age: String = "",

    var gender: String? = null,

    var mail : String = "",

    var phone_number : String = "",

    var description : String = "",

    var languages : String?,

    var city : String = "",

    ) {
}