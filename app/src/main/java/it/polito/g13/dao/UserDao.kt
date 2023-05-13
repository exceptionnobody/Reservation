package it.polito.g13.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import it.polito.g13.entities.PosRes
import it.polito.g13.entities.User
import it.polito.g13.utils.Constants.POSRES
import it.polito.g13.utils.Constants.USER
import java.util.Date

@Dao
interface UserDao {
    @Update
    fun updateUser(user : User)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insertUser(User : User)

    @Query("SELECT * FROM $USER WHERE id == :id")
    fun getSingleUser(id: Int) : User
}