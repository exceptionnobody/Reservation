package it.polito.g13.converter

import androidx.room.TypeConverter
import java.text.SimpleDateFormat
import java.util.*

class DataConverter {
    @TypeConverter
    fun fromString(value: String?): Date? {
        if (value != null) {
            val d = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.UK).parse(value)
            return d
        }
        else {
            return null
        }
    }

    @TypeConverter
    fun dateToString(date: Date?): String? {
        return date?.let { SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.UK).format(date) }
    }
}