package it.polito.g13.converter

import androidx.room.TypeConverter
import java.text.SimpleDateFormat
import java.util.*

class DataConverter {
    @TypeConverter
    fun fromString(value: String?): Date? {
        if (value != null) {
            return SimpleDateFormat("yyyy-mm-dd HH:mm", Locale.UK).parse(value)
        }
        else {
            return null
        }
    }

    @TypeConverter
    fun dateToString(date: Date?): String? {
        if (date != null) {
            val dateFormat = SimpleDateFormat("yyyy-mm-dd HH:mm", Locale.UK)
            return dateFormat.format(date)
        }
        else {
            return null
        }
    }
}