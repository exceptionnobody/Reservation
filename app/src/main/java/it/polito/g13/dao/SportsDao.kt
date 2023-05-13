package it.polito.g13.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import it.polito.g13.entities.*
import it.polito.g13.utils.Constants.CAMPO
import it.polito.g13.utils.Constants.POSRES
import it.polito.g13.utils.Constants.SPORTS
import it.polito.g13.utils.Constants.STRUCT
import it.polito.g13.utils.Constants.USER
import java.util.Date
import it.polito.g13.entities.Sports as Sports

@Dao
interface SportsDao{
    @Update
    fun updateSports(sports: Sports)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insertSports(sports: Sports)

    @Query("SELECT * FROM $SPORTS WHERE id == :id")
    fun getUserSportsByIdSports(id: Int) : Sports

    @Query("SELECT * FROM $SPORTS WHERE user_id == :id")
    fun getUserSportsByIdUser(id: Int) : Sports

}
