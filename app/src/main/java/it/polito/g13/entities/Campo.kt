package it.polito.g13.entities

import androidx.room.*
import it.polito.g13.converter.DataConverter
import it.polito.g13.utils.Constants.CAMPO
import it.polito.g13.utils.Constants.POSRES
import java.util.Date

@Entity(tableName = CAMPO)
@TypeConverters(DataConverter::class)
data class Campo(
    @PrimaryKey(autoGenerate = true)
    val id: Int=0,

    @ColumnInfo(name = "id_struttura")
    var id_struttura: Int = 0,

    @ColumnInfo(name = "sport")
    var tipo_sport: String = "",

    @ColumnInfo(name = "service_id")
    var service_id: Int = 0,

)