package com.mpq.cameratest.ui.gallery

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mpq.cameratest.data.datasource.ImageRepository
import com.mpq.cameratest.data.model.ImageFile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val imageRepository: ImageRepository
) : ViewModel()  {
    private var _currentPhotoList: MutableLiveData<List<ImageFile>> = MutableLiveData()
    val currentPhotoList : LiveData<List<ImageFile>> get() = _currentPhotoList

    init {
        refreshImages()
    }

    fun refreshImages(){
        viewModelScope.launch(Dispatchers.IO) {
            val list: List<ImageFile> = imageRepository.getAllImages()
            viewModelScope.launch(Dispatchers.Main) {
                _currentPhotoList.value = list
            }
            Log.d("CameraTest-Q", "Size of list is ${currentPhotoList.value?.size}")
        }
    }
}