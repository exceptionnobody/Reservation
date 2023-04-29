package it.polito.g13.entities

import androidx.room.*
import it.polito.g13.converter.DataConverter
import it.polito.g13.utils.Constants.POSRES
import java.util.Date

@Entity(tableName = POSRES)
@TypeConverters(DataConverter::class)
data class PosRes(
    @PrimaryKey(autoGenerate = true)
    val id: Int=0,

    @ColumnInfo(name = "struttura")
    val strut: String = "",

    @ColumnInfo(name = "campo")
    val campo: Int = 0,

    @ColumnInfo(name = "sport")
    val sport: String = "",

    @ColumnInfo(name = "date", defaultValue = "CURRENT_TIMESTAMP")
    var data: Date,

    @ColumnInfo(name = "flag")
    val flag: Boolean = false,

    )