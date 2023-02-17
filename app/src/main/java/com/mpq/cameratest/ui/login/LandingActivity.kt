package com.mpq.cameratest.ui.login

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.google.android.material.button.MaterialButton
import com.mpq.cameratest.databinding.ActivityLandingBinding
import com.mpq.cameratest.ui.camera.CameraActivity
import com.mpq.cameratest.ui.gallery.MainActivity
import com.mpq.cameratest.utils.Utils.Companion.TAG
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LandingActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityLandingBinding
    private val landingViewModel: LandingViewModel by viewModels()
    private lateinit var onCheckedChangeListener: MaterialButton.OnCheckedChangeListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityLandingBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        landingViewModel.loginResult.observe(this@LandingActivity, Observer {
            val loginResult = it ?: return@Observer
            viewBinding.loading.visibility = View.GONE
            if(it.success != null){
                setResult(RESULT_OK)
                finish()
            }
        })

        landingViewModel.session.observe(this@LandingActivity, Observer{
            if(it){
                setResult(RESULT_OK)
                finish()
                startActivity(Intent(this@LandingActivity, MainActivity::class.java))
            }
        })

        viewBinding.login.setOnClickListener {
            Log.d(TAG, "login")
            landingViewModel.login(
            viewBinding.username.text.toString(),
            viewBinding.password.text.toString())
        }

        viewBinding.password.apply {
            setOnEditorActionListener { _, actionId, _ ->
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE ->
                        landingViewModel.login(
                            viewBinding.username.text.toString(),
                            viewBinding.password.text.toString()
                        )
                }
                false
            }
        }
    }

    fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
        this.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(editable: Editable?) {
                afterTextChanged.invoke(editable.toString())
            }
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })
    }
}