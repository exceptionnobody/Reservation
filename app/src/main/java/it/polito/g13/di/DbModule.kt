package it.polito.g13.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

import it.polito.g13.db.ReservationDatabase
import it.polito.g13.entities.Reservation
import it.polito.g13.utils.Constants.RESERVATION_DATABASE
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DbModule {

    @Provides
    @Singleton
    fun provide(@ApplicationContext context: Context) = Room.databaseBuilder(
        context, ReservationDatabase::class.java, RESERVATION_DATABASE)
        .allowMainThreadQueries()
        .fallbackToDestructiveMigration()
        .build()

    @Provides
    @Singleton
    fun provideDao(db: ReservationDatabase) = db.reservationDao()

    @Provides
    fun provideEntity() = Reservation()


}