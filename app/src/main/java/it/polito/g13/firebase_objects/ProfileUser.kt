package it.polito.g13.firebase_objects

import java.io.Serializable

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

    var basketLevel: String = "",
    var basketAchievements: String = "",

    var footballLevel: String = "",
    var footballAchievements: String = "",

    var padelLevel: String = "",
    var padelAchievements: String = "",

    var rugbyLevel: String = "",
    var rugbyAchievements: String = "",

    var tennisLevel: String = "",
    var tennisAchievements: String = "",

    var volleyballLevel: String = "",
    var volleyballAchievements: String = ""

    ) : Serializable {
}