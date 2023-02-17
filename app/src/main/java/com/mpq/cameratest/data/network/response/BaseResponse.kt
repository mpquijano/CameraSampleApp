package com.mpq.cameratest.data.network.response

import com.mpq.cameratest.network.Status

data class BaseResponse<out T>(val status: Status, val data: T?, val message: String?) {
    companion object {
        fun <T> success(data: T): BaseResponse<T> = BaseResponse(status = Status.SUCCESS, data = data, message = null)

        fun <T> error(data: T?, message: String): BaseResponse<T> =
            BaseResponse(status = Status.ERROR, data = data, message = message)

        fun <T> loading(data: T?): BaseResponse<T> = BaseResponse(status = Status.LOADING, data = data, message = null)
    }
}