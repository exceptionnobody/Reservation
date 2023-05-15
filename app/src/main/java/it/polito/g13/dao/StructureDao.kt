package it.polito.g13.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import it.polito.g13.entities.PosRes
import it.polito.g13.entities.Struttura
import it.polito.g13.entities.User
import it.polito.g13.utils.Constants.POSRES
import it.polito.g13.utils.Constants.STRUCT
import it.polito.g13.utils.Constants.USER
import java.util.Date

@Dao
interface StructureDao {
    @Update
    fun updateStructure(struttura: Struttura)

    @Query("SELECT * FROM $STRUCT WHERE id == :id ")
    fun isPresent(id:Int): Boolean

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insertStructure(struttura: Struttura)

    @Query("SELECT * FROM $STRUCT WHERE id == :id")
    fun getSingleStructure(id: Int) : Struttura

    @Query("SELECT * FROM $STRUCT")
    fun getaLLStructure() : LiveData<List<Struttura>>

}