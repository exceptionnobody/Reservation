package it.polito.g13.firebase_objects

import java.util.Date

data class FirebasePosres(

        var data:Date?=null,
        var idcampo:String="",
        var idstruttura:String="",
        var posresid:String="",
        var tiposport:String="",
        var maxpeople:Int=0,
        var users:Array<FirebaseUser>

    ) {
}