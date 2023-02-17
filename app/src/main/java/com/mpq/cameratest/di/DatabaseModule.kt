package com.mpq.cameratest.di

import android.content.ContentProvider
import android.content.ContentResolver
import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.mpq.cameratest.data.dao.ImageDao
import com.mpq.cameratest.data.db.ImageContentProvider
import com.mpq.cameratest.data.db.ImageDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.util.concurrent.Executors
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {
//    @Provides
//    @Singleton
//    fun provideImageDatabase(@ApplicationContext appContext: Context) : ImageDatabase {
//        return Room.databaseBuilder(
//            appContext.applicationContext,
//            ImageDatabase::class.java,
//            "imagefile"
//        ).setQueryCallback({ sqlQuery, bindArgs ->
//            println("SQL Query: $sqlQuery SQL Args: $bindArgs")
//        }, Executors.newSingleThreadExecutor())
//            .fallbackToDestructiveMigration().build()
//    }
//
//    @Provides
//    fun provideImageDao(imageDatabase: ImageDatabase): ImageDao {
//        return imageDatabase.imageDao()
//    }

    @Provides
    @Singleton
    fun provideImageContentProvider(@ApplicationContext appContext: Context,
                                    contentResolver: ContentResolver) :ImageContentProvider{
        return ImageContentProvider(appContext, contentResolver)
    }

    @Provides
    fun provideContentResolver(@ApplicationContext appContext: Context) : ContentResolver{
        return appContext.applicationContext.contentResolver
    }
}