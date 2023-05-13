package it.polito.g13.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import it.polito.g13.converter.DataConverter
import it.polito.g13.utils.Constants.RESERVATION_TABLE
import java.util.Date

@Entity(tableName = RESERVATION_TABLE)
@TypeConverters(DataConverter::class)
data class Reservation(
    @PrimaryKey(autoGenerate = true)
    val id: Int=0,

    @ColumnInfo(name = "idslot")
    var idsl: Int = 0,

    @ColumnInfo(name = "iduser")
    var iduser: Int = 0,

    @ColumnInfo(name = "struttura")
    var strut: String = "",

    @ColumnInfo(name = "sport")
    var sport: String = "",

    @ColumnInfo(name = "date")
    var data: Date? = null,

    @ColumnInfo(name = "note")
    var note: String = "",

    @ColumnInfo(name = "flag")
    var flag:Boolean=false
    )

