package it.polito.g13.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import it.polito.g13.dao.ReservationDao
import it.polito.g13.entities.Reservation

@Database(entities = [Reservation::class], version = 1 )
abstract class ReservationDatabase :  RoomDatabase(){

    abstract fun reservationDao(): ReservationDao

    companion object{
        @Volatile
        private var INSTANCE : ReservationDatabase? = null

        fun getDatabase(context: Context) : ReservationDatabase = (
                INSTANCE ?: synchronized(this){
                    val i = INSTANCE ?: Room.databaseBuilder(context.applicationContext, ReservationDatabase::class.java, "reservation_dabatase")
                        .fallbackToDestructiveMigration().build()
                    INSTANCE=i
                    INSTANCE
                }
                )!!
    }

}