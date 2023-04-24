package it.polito.g13

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context: Context, factory: SQLiteDatabase.CursorFactory?) :
    SQLiteOpenHelper(context, DATABASE_NAME, factory, DATABASE_VERSION) {



    override fun onCreate(db: SQLiteDatabase) {
        // below is a sqlite query, where column names
        // along with their data types is given
        val query = ("CREATE TABLE " + TABLE_NAME + " ("
                + ID_COL + " INTEGER PRIMARY KEY, " +
                NAME_COl + " TEXT," +
                AGE_COL + " TEXT" + ")")
        val query1 = ("CREATE TABLE " + RESERV + " ("
                + idPreno
                + " INTEGER PRIMARY KEY, " +
                idslot+ "Integer" +
                idutente
                + " INTEGER," +
                tipoprenotazione
                + " TEXT," +
                strutturaPrenotazione
                + " TEXT," +
                DATA
                +"DATETIME,"+
                ORA
                +"TEXT,"+
                flagattivo
                +"flag,"+
                descrizione
                +"TEXT,"+
                note+"TEXT"+
        ")")
        val query2 = ("CREATE TABLE " + POSRES + " ("
                + idslot
                + " INTEGER PRIMARY KEY, " +
                idstruttura
                + " INTEGER," +
                idcampo
                + " INTEGER," +
                tiposport
                +"TEXT"+
                DATA
                +"DATETIME,"+
                ORA
                +"TEXT,"+
                flagattivo+"flag"+
                ")")

        // we are calling sqlite
        // method for executing our query
        db.execSQL(query)
        db.execSQL(query1)
        db.execSQL(query2)
    }

    override fun onUpgrade(db: SQLiteDatabase, p1: Int, p2: Int) {
        // this method is to check if table already exists
        //db.execSQL( "DROP TABLE IF EXISTS $TABLE_NAME")
        //db.execSQL( "DROP TABLE IF EXISTS $POSRES")
        //db.execSQL( "DROP TABLE IF EXISTS $RESERV")
        onCreate(db)
    }

    fun addPrenotazione(user : Int, idsl:Int, tipo : String, strutt:String, data:String, ora:String, flag:Boolean,descrizion:String, not:String ){
        // below we are creating
        // a content values variable
        val values = ContentValues()
        val dbread=this.readableDatabase
        val ret = modPosPre(idsl,true) // inserisco true  per farlo diventare false
        if (!ret){
            return
        }
        val x=dbread.rawQuery("select count(1) from $RESERV",null).getInt(1)+1
        dbread.close()
        // we are inserting our values
        // in the form of key-value pair

        values.put(idPreno,x)
        values.put(idslot,idsl)
        values.put(idutente, user)
        values.put(tipoprenotazione, tipo)
        values.put(strutturaPrenotazione, strutt)
        values.put(DATA, data)
        values.put(ORA, ora)
        values.put(flagattivo, flag)
        values.put(descrizione, descrizion)
        values.put(note, not)

        // here we are creating a
        // writable variable of
        // our database as we want to
        // insert value in our database
        val db = this.writableDatabase
        // all values are inserted into database
        db.insert(RESERV, null, values)
        // at last we are
        // closing our database
        db.close()
    }
    fun modPrenotazione(pren:Int, idsl:Int, user : Int, tipo : String, strutt:String, data:String, ora:String, flag:Boolean,descrizion:String, not:String ){
        // prendo prima dell'update l'id slot della prenotazione iniziale, poi controllo se il nuovo idslot è accessibile e infine modifico
        // below we are creating
        // a content values variable
        val values = ContentValues()
        val dbread=this.readableDatabase
        val x=dbread.rawQuery("select count(1) from $RESERV",null).getInt(1)+1
        val id=dbread.rawQuery("select idslot from $RESERV WHERE idpreno=$pren",null).getInt(1)
        dbread.close()
        val ret = modPosPre(id,false) // inserisco true  per farlo diventare false
        if (!ret){
            return
        }
        val ret1 = modPosPre(idsl,true) // inserisco true  per farlo diventare false
        if (!ret1){
            return
        }
        // we are inserting our values
        // in the form of key-value pair
        values.put(idPreno,x)
        values.put(idutente, user)
        values.put(tipoprenotazione, tipo)
        values.put(strutturaPrenotazione, strutt)
        values.put(DATA, data)
        values.put(ORA, ora)
        values.put(flagattivo, flag)
        values.put(descrizione, descrizion)
        values.put(note, not)

        // here we are creating a
        // writable variable of
        // our database as we want to
        // insert value in our database
        val db = this.writableDatabase

        // all values are inserted into database
        db.rawQuery("UPDATE R SET flag=0 FROM $RESERV R WHERE idPreno=$pren",null)
        db.insert(RESERV, null, values)

        // at last we are
        // closing our database
        db.close()
    }

    // below method is to get
    // all data from our database
    fun getPrenotazione(): Cursor? {
        // here we are creating a readable
        // variable of our database
        // as we want to read value from it
        val db = this.readableDatabase
        // below code returns a cursor to
        // read data from the database
        return db.rawQuery("SELECT * FROM $RESERV WHERE flag=1", null)
    }
    fun delPrenotazione(pren:Int, idsl:Int){
        val ret1 = modPosPre(idsl,true) // inserisco true  per farlo diventare false
        if (!ret1){
            return
        }
        val db = this.writableDatabase
        db.rawQuery("UPDATE R SET flag=0 FROM $RESERV R WHERE idPreno=$pren",null)
        db.close()
    }


    private fun modPosPre(id:Int,flag:Boolean):Boolean{
        val dbRead=this.readableDatabase
        var f=dbRead.rawQuery("SELECT flag FROM $POSRES WHERE idslot=$id",null).getInt(1)==1
        val db = this.writableDatabase
        if (f==flag){
            f= (!f)
            db.rawQuery("UPDATE P SET flag = $f FROM $POSRES P WHERE idslot = $id",null)
            db.close()
            return true
        }
        db.close()
        return false
    }

    fun getPosPre(): Cursor? {
        // here we are creating a readable
        // variable of our database
        // as we want to read value from it
        val db = this.readableDatabase
        // below code returns a cursor to
        // read data from the database
        return db.rawQuery("SELECT * FROM $POSRES WHERE flag=1", null)
    }








    companion object{
        // here we have defined variables for our database

        // below is variable for database name
        private val DATABASE_NAME = "dbMad"

        // below is the variable for database version
        private val DATABASE_VERSION = 1

        // below is the variable for table name
        val TABLE_NAME = "proftab"

        // below is the variable for id column
        val ID_COL = "id"

        // below is the variable for name column
        val NAME_COl = "name"

        // below is the variable for age column
        val AGE_COL = "age"
        val idPreno="idPren"
        val idutente="idutente"
        val tipoprenotazione="tipoprenotazione"
        val strutturaPrenotazione="struttura"
        val DATA="data"
        val ORA="ora"
        val flagattivo="flag"
        val descrizione="descrizione"
        val note="note"
        val idslot="idslot"
        val idstruttura="idstruttura"
        val idcampo="idcampo"
        val tiposport="tiposport"

        const val RESERV= "reserv"
        const val POSRES= "posres"
    }
}