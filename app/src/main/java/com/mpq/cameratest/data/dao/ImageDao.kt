package com.mpq.cameratest.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.mpq.cameratest.data.model.ImageFile

@Dao
interface ImageDao {
    @Query("SELECT * FROM ImageFile")
    fun getAllImages(): List<ImageFile>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(imagefile: ImageFile)
    @Insert
    suspend fun insertAll(imagelist: List<ImageFile>)
    @Delete
    suspend fun delete(imagefile: ImageFile)
    @Update
    suspend fun update(imagefile: ImageFile)
}