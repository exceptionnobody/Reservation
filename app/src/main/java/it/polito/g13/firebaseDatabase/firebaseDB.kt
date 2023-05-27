import com.google.firebase.firestore.FirebaseFirestore
import it.polito.g13.firebase_objects.FirebaseField
import it.polito.g13.firebase_objects.FirebaseStructure
import it.polito.g13.firebase_objects.FirebaseStructureReviews

class firebaseDB(mail:String){
    private val db = FirebaseFirestore.getInstance()
    private val mail=mail
    val collectionUserInfo = "user_infos"
    private val collectionStructures =""
    var strutture:Array<FirebaseStructure> = emptyArray()



    //Tutti i metodi get
    fun getDocumentsUser() {
        db.document("users/$mail/")
            .get()
            .addOnSuccessListener { querySnapshot ->

            }
            .addOnFailureListener { exception ->
                // Gestisci l'errore qui
            }
    }
    fun getDocumentsStructures() {
        strutture=emptyArray()
        db.collection("struttura/")
            .get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot) {
                    val campi:Array<FirebaseField> = emptyArray()
                    val reviews:Array<FirebaseStructureReviews> = emptyArray()
                    var idStruttura:String=""
                    var nomeStruttura:String=""
                    db.collection("struttura/").document(document.id).collection("campistruttura").get()
                        .addOnSuccessListener { querySnapshot ->
                            for (document in querySnapshot) {
                                var campValues = FirebaseField()
                                campValues.campoId=document.id
                                campValues.maxpeople= document.data["maxpeople"].toString().toInt()
                                campValues.struttura= document.data["idstruttura"].toString()
                                campValues.tiposport= document.data["tiposport"].toString()
                                campi.plus(campValues)
                            }
                        }
                        .addOnFailureListener { exception ->
                            // Gestisci l'errore qui
                        }
                    val review=db.collection("struttura/").document(document.id).collection("reviewStruttura").get()
                        .addOnSuccessListener { querySnapshot ->
                            for (document in querySnapshot) {
                                var reviewValues = FirebaseStructureReviews()
                                reviewValues.reviewId=document.id
                                reviewValues.struttura= document.data["struttura"].toString()
                                reviewValues.voto1= document.data["voto1"].toString().toInt()
                                reviewValues.voto2= document.data["voto2"].toString().toInt()
                                reviewValues.voto3= document.data["voto3"].toString().toInt()
                                reviewValues.voto4= document.data["voto4"].toString().toInt()
                                reviewValues.voto5= document.data["voto5"].toString().toInt()
                                reviews.plus(reviewValues)
                            }
                        }
                        .addOnFailureListener { exception ->
                            // Gestisci l'errore qui
                        }
                    val infostruttra=db.collection("struttura/").document(document.id).collection("infostruttura").get()
                        .addOnSuccessListener { querySnapshot ->
                            for (document in querySnapshot) {
                                nomeStruttura=document.data.get("nomestruttura").toString()
                                idStruttura=document.data.get("idstruttura").toString()
                            }
                        }
                        .addOnFailureListener { exception ->
                            // Gestisci l'errore qui
                        }
                    var struttura=FirebaseStructure(fields = campi, reviews = reviews)
                    struttura.idstruttura=idStruttura
                    struttura.nomestruttura=nomeStruttura
                    strutture.plus(struttura)
                }
            }
            .addOnFailureListener { exception ->
                // Gestisci l'errore qui
            }

    }

    fun getDocumentsPosres() {
        db.collection("posres/")
            .get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot) {
                    val data = document.data
                    // Manipola i dati come preferisci
                }
            }
            .addOnFailureListener { exception ->
                // Gestisci l'errore qui
            }
    }





    /*fun addDocument(data: Map<String, Any>) {
        db.collection(collectionName)
            .add(data)
            .addOnSuccessListener { documentReference ->
                // Il documento è stato aggiunto con successo
                val documentId = documentReference.id
                // Esegui altre operazioni dopo l'aggiunta del documento
            }
            .addOnFailureListener { exception ->
                // Gestisci l'errore qui
            }
    }

    fun updateDocument(documentId: String, data: Map<String, Any>) {
        db.collection(collectionName)
            .document(documentId)
            .set(data)
            .addOnSuccessListener {
                // Il documento è stato aggiornato con successo
                // Esegui altre operazioni dopo l'aggiornamento del documento
            }
            .addOnFailureListener { exception ->
                // Gestisci l'errore qui
            }
    }*/

}