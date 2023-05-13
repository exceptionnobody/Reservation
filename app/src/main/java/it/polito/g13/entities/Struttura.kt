package it.polito.g13.entities

import androidx.room.*
import it.polito.g13.converter.DataConverter
import it.polito.g13.utils.Constants.POSRES
import it.polito.g13.utils.Constants.STRUCT
import java.util.Date

@Entity(tableName = STRUCT)
@TypeConverters(DataConverter::class)
data class Struttura(
    @PrimaryKey(autoGenerate = true)
    val id: Int=0,

    @ColumnInfo(name = "struttura")
    var structure_name: String = "",

    @ColumnInfo(name = "campo")
    var review_id_struct: Int = 0,
    )