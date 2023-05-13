package it.polito.g13.di

import android.content.Context
import androidx.room.Entity
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

import it.polito.g13.db.ReservationDatabase
import it.polito.g13.entities.*
import it.polito.g13.utils.Constants.RESERVATION_DATABASE
import java.util.Date
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
    fun provideReservationDao(db: ReservationDatabase) = db.reservationDao()

    @Provides
    @Singleton
    fun providePosResDao(db: ReservationDatabase) = db.posresDao()

    @Provides
    @Singleton
    fun provideUserDao(db:ReservationDatabase) = db.userDao()

    @Provides
    @Singleton
    fun provideStrutturaDao(db:ReservationDatabase) = db.structureDao()

    @Provides
    @Singleton
    fun provideCampoDao(db:ReservationDatabase) = db.campoDao()

    @Provides
    @Singleton
    fun provideSportsDao(db:ReservationDatabase) = db.sportsDao()

    @Provides
    @Singleton
    fun provideReviewStructDao(db:ReservationDatabase) = db.reviewStructDao()



    @Provides
    fun provideReservationEntity() = Reservation()

    @Provides
    fun providePosResEntity() = PosRes(data = Date())

    @Provides
    fun provideUserEntity() = User()
    @Provides
    fun provideStrutturaEntity()=Struttura()

    @Provides
    fun provideCampoEntity()=Campo()

    @Provides
    fun provideSportsEntity()=Sports()

    @Provides
    fun provideRevStruttEntity()=review_struct()

}