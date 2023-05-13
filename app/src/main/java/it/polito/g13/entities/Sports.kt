package it.polito.g13.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import it.polito.g13.converter.DataConverter
import it.polito.g13.utils.Constants.RESERVATION_TABLE
import it.polito.g13.utils.Constants.SPORTS
import java.util.Date

@Entity(tableName = SPORTS)
@TypeConverters(DataConverter::class)
data class Sports(
    @PrimaryKey(autoGenerate = true)
    val id: Int=0,

    @ColumnInfo(name = "user_id")
    var user_id: Int = 0,

    @ColumnInfo(name = "Basket")
    var basket: String? = null,

    @ColumnInfo(name = "Football")
    var football: String? = null,

    @ColumnInfo(name = "Padel")
    var padel: String? = null,

    @ColumnInfo(name = "Rugby")
    var rugby: String? = null,

    @ColumnInfo(name = "Tennis")
    var tennis: String? = null,

    @ColumnInfo(name = "Volleyball")
    var volleyball: String? = null,
    )