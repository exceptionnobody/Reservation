package it.polito.g13.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import it.polito.g13.entities.Campo
import it.polito.g13.entities.PosRes
import it.polito.g13.entities.Struttura
import it.polito.g13.entities.User
import it.polito.g13.utils.Constants.CAMPO
import it.polito.g13.utils.Constants.POSRES
import it.polito.g13.utils.Constants.STRUCT
import it.polito.g13.utils.Constants.USER
import java.util.Date

@Dao
interface CampoDao {
    @Update
    fun updateCampo(campo: Campo)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insertCampo(campo: Campo)

    @Query("SELECT * FROM $CAMPO WHERE id == :id")
    fun getSingleCampo(id: Int) : Campo

    @Query("SELECT * FROM $CAMPO WHERE id_struttura == :id")
    fun getAllCampOfStruct(id:Int) : LiveData<List<Campo>>

    @Query("SELECT * FROM $CAMPO WHERE sport == :sport")
        fun getAllCampOfSports(sport:String) : LiveData<List<Campo>>
}