package it.polito.g13.firebase_objects



data class FirebaseStructure(
    var nomestruttura:String="",
    var idstruttura:String="",
    var reviews: Array<FirebaseStructureReviews>,
    var fields: Array<FirebaseField>
    ) {
}