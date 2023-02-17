package com.mpq.cameratest.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.mpq.cameratest.data.dao.ImageDao
import com.mpq.cameratest.data.model.ImageFile

@Database(entities = [ImageFile::class], version = 1, exportSchema = false)
abstract class ImageDatabase : RoomDatabase(){
    abstract fun imageDao() : ImageDao
}