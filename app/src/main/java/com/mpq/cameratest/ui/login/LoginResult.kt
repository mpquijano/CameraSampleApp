package com.mpq.cameratest.ui.login

import com.mpq.cameratest.data.model.User

data class LoginResult(
    val success: User? = null,
    val error: Int? = null
)