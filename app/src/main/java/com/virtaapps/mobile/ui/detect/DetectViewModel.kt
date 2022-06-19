package com.virtaapps.mobile.ui.detect

import android.graphics.Bitmap
import android.util.Log
import com.virtaapps.mobile.base.BaseViewModel
import com.virtaapps.mobile.data.firebase.MeasurementHistoryUtils
import com.virtaapps.mobile.data.model.MeasurementHistory
import com.virtaapps.mobile.data.remote.DetectionApi
import dagger.hilt.android.lifecycle.HiltViewModel
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import javax.inject.Inject

@HiltViewModel
class DetectViewModel @Inject constructor(
    private val measurementHistoryUtils: MeasurementHistoryUtils,
    private val api: DetectionApi
) : BaseViewModel() {
    fun uploadImage(data: ByteArray) = callApiReturnLiveData(
        apiCall = { measurementHistoryUtils.uploadImage(data) }
    )

    fun detectImage(file: File) = callApiReturnLiveData(
        apiCall = {
            val requestFile: RequestBody = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
            val body = requestFile.let { MultipartBody.Part.createFormData("imagedata", file.name, it) }
            api.uploadImage(body)
        }
    )

    fun saveMeasurement(data: MeasurementHistory) = callApiReturnLiveData(
        apiCall = { measurementHistoryUtils.saveHistory(data) }
    )

    fun getHistories() = callApiReturnLiveData(
        apiCall = { measurementHistoryUtils.getHistories() }
    )
}