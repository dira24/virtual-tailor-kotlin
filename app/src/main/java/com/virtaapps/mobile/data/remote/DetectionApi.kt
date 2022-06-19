package com.virtaapps.mobile.data.remote

import com.virtaapps.mobile.base.BaseResponse
import com.virtaapps.mobile.data.model.DetectionResult
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface DetectionApi {
    @Multipart
    @POST("api/upload")
    suspend fun uploadImage(
        @Part imagedata: MultipartBody.Part
    ): BaseResponse<DetectionResult>
}