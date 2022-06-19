package com.virtaapps.mobile.base

data class BaseResponse<T>(
    val success: Boolean,
    val data: T
)
