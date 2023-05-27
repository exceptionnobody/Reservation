package it.polito.g13.firebase_objects

import it.polito.g13.entities.review_struct


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

    var basket : String? = null,

    var calcio : String? = null,

    var tennis : String? = null,

    var rugby : String? = null,

    var volleyball : String? = null,

    var padel: String? = null,

    var reservations:Array<FirebasePosres>,

    var reviews:Array<review_struct>,

    ) {
}