package com.virtaapps.mobile.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.virtaapps.mobile.utils.Resource
import kotlinx.coroutines.launch

abstract class BaseViewModel: ViewModel() {

    fun <T> callApiReturnLiveData(
        apiCall: suspend () -> T,
        handleBeforePostSuccess: (T) -> Unit = {}
    ): LiveData<Resource<T>> {
        val data: MutableLiveData<Resource<T>> = MutableLiveData()
        viewModelScope.launch {
            data.postValue(Resource.Loading())
            try {
                val response = apiCall()
                handleBeforePostSuccess(response)
                data.postValue(Resource.Success(response))
            } catch (e: Exception) {
                data.postValue(Resource.Error(e.localizedMessage ?: "unknown error"))
            }
        }
        return data
    }

}