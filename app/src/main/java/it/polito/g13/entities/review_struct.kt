package it.polito.g13.entities

import androidx.room.*
import it.polito.g13.converter.DataConverter
import it.polito.g13.utils.Constants.POSRES
import it.polito.g13.utils.Constants.REV_STRUTT
import java.util.Date

@Entity(tableName = REV_STRUTT)
@TypeConverters(DataConverter::class)
data class review_struct(
    @PrimaryKey(autoGenerate = true)
    val id: Int=0,

    @ColumnInfo(name = "user_id")
    var user_id: Int = 0,

    @ColumnInfo(name = "review_id_struct")
    var review_id_struct: Int = 0,

    @ColumnInfo(name = "id_campo")
    var id_campo: Int = 0,

    @ColumnInfo(name = "s_q1")
    var s_q1: Int = 0,

    @ColumnInfo(name = "s_q2")
    var s_q2: Int = 0,

    @ColumnInfo(name = "s_q3")
    var s_q3: Int = 0,

    @ColumnInfo(name = "s_q4")
    var s_q4: Int = 0,

    @ColumnInfo(name = "s_q5")
    var s_q5: Int = 0,

    @ColumnInfo(name = "note")
    var description:String = ""
    )