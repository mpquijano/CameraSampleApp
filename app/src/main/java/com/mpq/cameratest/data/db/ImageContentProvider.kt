package com.mpq.cameratest.data.db

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import androidx.documentfile.provider.DocumentFile
import com.mpq.cameratest.data.model.ImageFile
import com.mpq.cameratest.utils.MediaStoreUtils
import java.io.File
import javax.inject.Inject

class ImageContentProvider @Inject constructor(
    private val context: Context,
    private val contentResolver: ContentResolver
){

    fun getAllImages() : List<ImageFile> {
        val result = arrayListOf<ImageFile>()
        val uri: Uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(MediaStore.Images.Media._ID)
        contentResolver.query(uri, projection, null, null, null)?.use {
            while (it.moveToNext()) {

                var uri = ContentUris.withAppendedId(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    it.getLong(0)
                )
                var filename = MediaStoreUtils(context).getFileNameFromUri(uri)
                var file = DocumentFile.fromSingleUri(context,uri)
                if(filename != null && file != null && file.exists()){
                    result.add(ImageFile(uri.path!!,filename!!))
                }
            }
        }
        return result
    }

    fun insert(imagefile: ImageFile) {
        TODO("Not yet implemented")
    }

    fun insertAll(imagelist: List<ImageFile>) {
        TODO("Not yet implemented")
    }

    fun delete(imagefile: ImageFile) {
        contentResolver.delete(Uri.parse("content://media${imagefile.path}"),null,null)
    }

    fun update(imagefile: ImageFile) {
        TODO("Not yet implemented")
    }
}