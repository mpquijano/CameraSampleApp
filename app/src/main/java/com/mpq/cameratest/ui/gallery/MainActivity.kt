package com.mpq.cameratest.ui.gallery

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.se.omapi.Session
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mpq.cameratest.R
import com.mpq.cameratest.databinding.ActivityMainBinding
import com.mpq.cameratest.network.SessionManager
import com.mpq.cameratest.ui.camera.CameraActivity
import com.mpq.cameratest.ui.login.LandingActivity
import com.mpq.cameratest.utils.Utils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityMainBinding
    private val mainViewModel: MainViewModel by viewModels()
    var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // There are no request codes
            val data: Intent? = result.data
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        // getting the recyclerview by its id
        viewBinding.listPhotoView?.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = GalleryRecyclerAdapter(this@MainActivity, mainViewModel.currentPhotoList)
        }
        viewBinding.btnTakePhoto.setOnClickListener {
            resultLauncher.launch(Intent(this@MainActivity, CameraActivity::class.java))
        }

        mainViewModel.currentPhotoList.observe(this@MainActivity) {
            Log.d(Utils.TAG, "Observe currentPhotoList - Size = ${mainViewModel.currentPhotoList.value?.size}")
            if(viewBinding.listPhotoView.adapter != null) {
                viewBinding.listPhotoView.adapter!!.notifyDataSetChanged();
            }
        }
    }

    override fun onResume() {
        super.onResume()
        mainViewModel.refreshImages()
        if(viewBinding.listPhotoView.adapter != null){
            viewBinding.listPhotoView.adapter!!.notifyDataSetChanged()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Inflate the menu
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_logout -> {
                SessionManager(this@MainActivity).clearData()
                finish()
                startActivity(Intent(this@MainActivity, LandingActivity::class.java))
                true
            } else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        invalidateOptionsMenu()
        menu.findItem(R.id.menu_logout).isVisible = true
        return super.onPrepareOptionsMenu(menu)
    }

}