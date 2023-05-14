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
import it.polito.g13.entities.review_struct
import it.polito.g13.utils.Constants.POSRES
import it.polito.g13.utils.Constants.REV_STRUTT
import it.polito.g13.utils.Constants.STRUCT
import it.polito.g13.utils.Constants.USER
import java.util.Date

@Dao
interface ReviewStructDao {
    @Update
    fun updateReviewStructure(reviewStruct: review_struct)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insertReviewStructure(reviewStruct: review_struct)

    @Query("SELECT * FROM $REV_STRUTT WHERE id == :id")
    fun getSingleRevStruct(id: Int) : review_struct

    @Query("SELECT * FROM $REV_STRUTT WHERE review_id_struct == :structureId and user_id == :userId")
    fun getSingleRevStructByUserAndStructureId(structureId: Int, userId: Int) : review_struct

    @Query("SELECT * FROM $REV_STRUTT Where review_id_struct == :id")
    fun getaLLRevOfAStructure(id:Int) : LiveData<List<review_struct>>

    @Query("SELECT * FROM $REV_STRUTT Where user_id == :id")
    fun getaLLRevDidByAnUser(id:Int) : LiveData<List<review_struct>>

    @Query("SELECT * FROM $REV_STRUTT Where id_campo == :id")
    fun getaLLRevOfAField(id:Int) : LiveData<List<review_struct>>


}