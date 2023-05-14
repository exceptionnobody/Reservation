package it.polito.g13.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import it.polito.g13.converter.DataConverter
import it.polito.g13.utils.Constants.RESERVATION_TABLE
import it.polito.g13.utils.Constants.USER
import java.util.Date

@Entity(tableName = USER)
@TypeConverters(DataConverter::class)
data class User(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val User_id: Int=0,

    @ColumnInfo(name = "photo")
    var photo: String? = null,

    @ColumnInfo(name = "name_surname")
    var name_surname: String = "",

    @ColumnInfo(name = "Nickname")
    var nickname: String = "",

    @ColumnInfo(name = "age")
    var age: Int = 0,

    @ColumnInfo(name = "date")
    var gender: String = "",

    @ColumnInfo(name = "mail")
    var mail : String = "",

    @ColumnInfo(name = "phone_number")
    var phone_number : String = "",

    @ColumnInfo(name = "description")
    var description : String = "",

    @ColumnInfo(name = "languages")
    var languages : String = "",

    @ColumnInfo(name = "city")
    var city : String = "",

    @ColumnInfo(name = "feedback")
    var feedback : Int = 0,

    @ColumnInfo(name = "user_Sports")
    var user_Sports : Int? = null,

) {
}

