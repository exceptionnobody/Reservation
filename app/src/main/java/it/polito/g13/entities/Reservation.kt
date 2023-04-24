package it.polito.g13.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import it.polito.g13.utils.Constants.RESERVATION_TABLE
import java.util.Date

@Entity(tableName = RESERVATION_TABLE)
data class Reservation(
    @PrimaryKey(autoGenerate = true)
    val id: Int=0,

    @ColumnInfo(name = "date")
    val data: Date? = null,

    @ColumnInfo(name = "sport")
val sport: String = "",
)