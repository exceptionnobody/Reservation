package it.polito.g13.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import it.polito.g13.entities.PosRes
import it.polito.g13.utils.Constants.POSRES
import java.util.Date

@Dao
interface PosresDao {
    @Update
    fun updatePosRes(pos : PosRes)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insertPosRes(pos : PosRes)

    @Query("SELECT * FROM $POSRES WHERE flag==1")
    fun getAllPosRes() : LiveData<List<PosRes>>

    @Query("SELECT * FROM $POSRES WHERE id == :id ")
    fun isPresent(id:Int): Boolean

    @Update
    fun updateSinglePosRes(posres: PosRes )

    @Query("SELECT * FROM $POSRES WHERE id == :id")
    fun getSinglePosRes(id: Int) : PosRes

    @Query("SELECT * FROM $POSRES WHERE sport == :sport AND strftime('%Y-%m-%d', date) = strftime('%Y-%m-%d', :date) AND flag == 1")
    fun getPosResSportDate(sport: String, date: String) : List<PosRes>
}