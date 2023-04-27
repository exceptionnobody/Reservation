package it.polito.g13.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import it.polito.g13.converter.DataConverter
import it.polito.g13.dao.PosresDao
import it.polito.g13.dao.ReservationDao
import it.polito.g13.entities.PosRes
import it.polito.g13.entities.Reservation

@Database(entities = [Reservation::class,PosRes::class], version = 1 )
@TypeConverters(DataConverter::class)
abstract class ReservationDatabase :  RoomDatabase(){
    abstract fun reservationDao(): ReservationDao
    abstract fun posresDao(): PosresDao

}