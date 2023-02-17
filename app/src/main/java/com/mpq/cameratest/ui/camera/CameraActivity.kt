package com.mpq.cameratest.ui.camera

import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.mpq.cameratest.databinding.ActivityCameraBinding
import com.mpq.cameratest.utils.MediaStoreUtils
import com.mpq.cameratest.utils.Utils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.collections.ArrayList

@AndroidEntryPoint
class CameraActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityCameraBinding
    private val cameraViewModel: CameraViewModel by viewModels()

    private var imageCapture: ImageCapture? = null
    private lateinit var cameraExecutor: ExecutorService

    //private lateinit var outputDirectory: File
    private var warningList = ArrayList<TextView>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        warningList.add(viewBinding.warning1)
        warningList.add(viewBinding.warning2)
        warningList.add(viewBinding.warning3)
        warningList.add(viewBinding.warning4)
        warningList.add(viewBinding.warning5)

        cameraViewModel.captureProgress.observe(this@CameraActivity) {
            Log.d(Utils.TAG, "_captureProgrescaptureProgresss- $it")
            warningList.forEachIndexed{ index, element ->
                if((index+1).compareTo(it) == 0){
                    warningList[index].setTextColor(Color.parseColor("#FF0000"))
                }else{
                    warningList[index].setTextColor(Color.parseColor("#FFFFFF"))
                }
            }
            if (it == 1L || it == 3L || it == 5L) {
                takePhoto()
            }
        }

        cameraViewModel.photoPathList.observe(this@CameraActivity){
            Log.d(Utils.TAG, "photoPathList- ${it.size}")
            if(it.size == 3){
                cameraViewModel.onImageCaptureComplete()
                finish()
            }
        }

        // Request camera permissions
        if (allPermissionsGranted()) {
            // Set up the camera and its use cases
            lifecycleScope.launch {
                setUpCamera()
            }
        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            )
        }
        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                setUpCamera()
            } else {
                Toast.makeText(this,
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setUpCamera() {
        Log.d(Utils.TAG, "setUpCamera()")
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(viewBinding.viewFinder.surfaceProvider)
                }

            imageCapture = ImageCapture.Builder().build()

            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
            try {
                // Unbind use cases before rebinding
                cameraProvider.apply {
                    unbindAll()
                    bindToLifecycle(
                        this@CameraActivity, cameraSelector, preview, imageCapture)
                }
                Log.d(Utils.TAG, "Camera bound success")
                cameraViewModel.initCaptureProcess()
            } catch(exc: Exception) {
                Log.e(Utils.TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))

    }

    private fun takePhoto() {
        Log.d(Utils.TAG, "takePhoto()")
        // Get a stable reference of the modifiable image capture use case
        val imageCapture = imageCapture ?: return

        // Create time stamped name and MediaStore entry.
        val datestamp = SimpleDateFormat(FILENAME_FORMAT, Locale.US)
            .format(System.currentTimeMillis())
        val name = "CameraTest-$datestamp"

        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if(Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CameraTest")
            }
        }

        // Create output options object which contains file + metadata
        val outputOptions = ImageCapture.OutputFileOptions
            .Builder(contentResolver,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues)
            .build()

        // Set up image capture listener, which is triggered after photo has been taken
        imageCapture.takePicture(
            outputOptions,
            cameraExecutor,
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e(Utils.TAG, "Photo capture failed: ${exc.message}", exc)
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults){
                    val msg = "Photo capture succeeded"
                    Log.d(Utils.TAG, msg)
                    //val uriFromFile = Uri.fromFile(photoFile)
                    if(output.savedUri != null){
                        var filename = MediaStoreUtils(this@CameraActivity).getFileNameFromUri(output.savedUri!!)
                        if(filename != null){
                            cameraViewModel.onImageCapture(output.savedUri?.path!!,filename)
                        }
                        val msg2 = "Photo capture succeeded: $filename & ${output.savedUri}"
                        Log.d(Utils.TAG, msg2)
                    }

                }
            }
        )
        // We can only change the foreground Drawable using API level 23+ API
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            // Display flash animation to indicate that photo was captured
            viewBinding.root.postDelayed({
                viewBinding.root.foreground = ColorDrawable(Color.WHITE)
                viewBinding.root.postDelayed(
                    { viewBinding.root.foreground = null }, ANIMATION_FAST_MILLIS
                )
            }, ANIMATION_SLOW_MILLIS)
        }
    }

    override fun onDestroy() {
        Log.d(Utils.TAG, "onDestroy()")
        super.onDestroy()
        imageCapture = null
        cameraExecutor.shutdown()
    }

    companion object {

        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 20
        private const val ANIMATION_FAST_MILLIS = 100L
        private const val ANIMATION_SLOW_MILLIS = 150L
        private val REQUIRED_PERMISSIONS =
            mutableListOf (
                Manifest.permission.CAMERA,
            ).apply {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }.toTypedArray()
    }
}