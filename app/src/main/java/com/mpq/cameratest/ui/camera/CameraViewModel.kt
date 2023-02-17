package com.mpq.cameratest.ui.camera

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mpq.cameratest.data.datasource.ImageRepository
import com.mpq.cameratest.data.model.ImageFile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import javax.inject.Inject

@HiltViewModel
class CameraViewModel @Inject constructor(
    private val imageRepository: ImageRepository
): ViewModel() {

    private val ONE_SECOND = 1000L
    private val _captureProgress: MutableLiveData<Long> = MutableLiveData()
    private val _photoPathList: MutableLiveData<MutableList<ImageFile>> = MutableLiveData()
    val captureProgress : LiveData<Long> get() = _captureProgress
    val photoPathList : LiveData<MutableList<ImageFile>> get() = _photoPathList
    private var currentPhotoList: List<ImageFile> = arrayListOf()

    //TODO handle navigation through CameraUIState

    private val totalSeconds = 5L
    private lateinit var job : Job;

    init {
        _captureProgress.value = 0
        _photoPathList.value = arrayListOf()
        viewModelScope.launch(Dispatchers.IO) {
            currentPhotoList = imageRepository.getAllImages()
        }
    }

    fun initCaptureProcess(){
        Log.d(TAG, "initCaptureProcess()")
        Log.d(TAG, "current have $imageRepository")
        Log.d(TAG, "current have $currentPhotoList")
        Log.d(TAG, "current have ${currentPhotoList.size}")
        job = viewModelScope.launch(Dispatchers.Default){
            delay(2000)
            repeat(totalSeconds.toInt()) {
                Log.d(TAG, "initCaptureProcess() - ${_captureProgress.value}")
                _captureProgress.postValue(_captureProgress.value?.plus(1))
                delay(ONE_SECOND);
            }
        }
    }

    fun onImageCapture(name: String, path: String?){
        Log.d(TAG, "onImageCapture() - ${path}")
        if (path != null) {
            _photoPathList.apply {
                var image = ImageFile(path,name)
                value?.add(image)
                postValue(value)
            }
        }

    }

    fun onImageCaptureComplete(){
        Log.d(TAG, "onImageCaptureComplete()")
        GlobalScope.launch(Dispatchers.IO) {
            try {
                for (imageFile in currentPhotoList) {
                    Log.d(TAG, "onImageCaptureComplete() delete - ${imageFile.name}")
                    imageRepository.delete(imageFile)
                }
                delay(1000)
                currentPhotoList = imageRepository.getAllImages()
            } catch (e: Exception) {
                Log.d(TAG, "onImageCaptureComplete() error - ${e.message}")
            }
        }
    }

    fun cancel(){
        if(job != null){
            job.cancel()

            GlobalScope.launch(Dispatchers.IO) {
                try {
                    for (imageFile in photoPathList.value!!) {
                        Log.d(TAG, "onImageCaptureComplete() delete - ${imageFile.name}")
                        imageRepository.delete(imageFile)
                    }
                } catch (e: Exception) {
                    Log.d(TAG, "onImageCaptureComplete() error - ${e.message}")
                }
            }
        }
    }

    companion object {
        private const val TAG = "CameraTest-Q"
    }
}