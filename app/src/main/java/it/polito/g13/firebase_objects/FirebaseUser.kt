package it.polito.g13.firebase_objects

data class FirebaseUser(

    var photo: String? = null,

    var name_surname: String = "",

    var nickname: String = "",

    var age: Int = 0,

    var gender: String = "",

    var mail : String = "",

    var phone_number : String = "",

    var description : String = "",

    var languages : String = "",

    var city : String = "",

    var feedback : Int = 0,

    var user_Sports : Int? = null,

    ) {
}