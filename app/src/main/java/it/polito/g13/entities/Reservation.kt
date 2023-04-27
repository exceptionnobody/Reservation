package it.polito.g13.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import it.polito.g13.utils.Constants.POSRES
import it.polito.g13.utils.Constants.RESERVATION_TABLE
import java.util.Date

@Entity(tableName = RESERVATION_TABLE)
data class Reservation(
    @PrimaryKey(autoGenerate = true)
    val id: Int=0,

    @ColumnInfo(name = "idslot")
    val idsl: Int = 0,

    @ColumnInfo(name = "iduser")
    val iduser: Int = 0,

    @ColumnInfo(name = "struttura")
    val strut: String = "",

    @ColumnInfo(name = "sport")
    val sport: String = "",

    @ColumnInfo(name = "date")
    val data: Date? = null,

    @ColumnInfo(name = "note")
    val note: String = "",

    @ColumnInfo(name = "flag")
    val flag:Boolean=false
    )

@Entity(tableName = POSRES)
data class PosRes(
    @PrimaryKey(autoGenerate = true)
    val id: Int=0,

    @ColumnInfo(name = "struttura")
    val strut: String = "",

    @ColumnInfo(name = "campo")
    val campo: Int = 0,

    @ColumnInfo(name = "sport")
    val sport: String = "",

    @ColumnInfo(name = "date")
    val data: Date? = null,

    @ColumnInfo(name = "flag")
    val flag: Boolean = false,

    )