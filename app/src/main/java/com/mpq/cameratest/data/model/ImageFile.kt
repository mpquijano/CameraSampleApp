package com.mpq.cameratest.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ImageFile constructor(
    @PrimaryKey val path:String,
    val name:String
    )
