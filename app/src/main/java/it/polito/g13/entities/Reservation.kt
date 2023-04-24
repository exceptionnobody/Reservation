package it.polito.g13.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "reservations")
data class Reservation(
    @PrimaryKey(autoGenerate = true)
    val id: Int=0,

    @ColumnInfo(name = "sport")
    val sport : String,

    @ColumnInfo(name = "date")
    val data : Date

)