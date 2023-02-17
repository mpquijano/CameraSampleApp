package com.mpq.cameratest.data.datasource

import androidx.lifecycle.LiveData
import com.mpq.cameratest.data.db.ImageContentProvider
import com.mpq.cameratest.data.db.ImageDatabase
import com.mpq.cameratest.data.model.ImageFile
import javax.inject.Inject

class ImageRepository @Inject constructor(
//    private val imageDatabase : ImageDatabase
    private val imageContentProvider: ImageContentProvider
    ) {

    suspend fun getAllImages() = imageContentProvider.getAllImages()

    suspend fun insertAll(imagelist : List<ImageFile>){
        imageContentProvider.insertAll(imagelist)
    }

    suspend fun insert(image: ImageFile) {
        imageContentProvider.insert(image)
    }

    suspend fun delete(image: ImageFile) {
        imageContentProvider.delete(image)
    }

    suspend fun update(image: ImageFile) {
        imageContentProvider.update(image)
    }

//    fun getAllImages() = imageDatabase.imageDao().getAllImages()
//
//    suspend fun insertAll(imagelist : List<ImageFile>){
//        imageDatabase.imageDao().insertAll(imagelist)
//    }
//
//    suspend fun insert(image: ImageFile) {
//        imageDatabase.imageDao().insert(image)
//    }
//
//    suspend fun delete(image: ImageFile) {
//        imageDatabase.imageDao().delete(image)
//    }
//
//    suspend fun update(image: ImageFile) {
//        imageDatabase.imageDao().update(image)
//    }
}