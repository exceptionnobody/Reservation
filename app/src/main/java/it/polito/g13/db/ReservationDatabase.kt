package it.polito.g13.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import it.polito.g13.converter.DataConverter
import it.polito.g13.dao.*
import it.polito.g13.entities.*

@Database(entities = [Reservation::class,PosRes::class,User::class,Struttura::class,Campo::class,Sports::class,review_struct::class], version = 2 )
@TypeConverters(DataConverter::class)
abstract class ReservationDatabase :  RoomDatabase(){
    abstract fun reservationDao(): ReservationDao
    abstract fun posresDao(): PosresDao

    abstract fun userDao(): UserDao

    abstract fun structureDao(): StructureDao

    abstract fun campoDao(): CampoDao

    abstract fun sportsDao(): SportsDao

    abstract fun reviewStructDao(): ReviewStructDao
}