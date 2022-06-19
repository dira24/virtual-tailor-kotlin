package com.virtaapps.mobile.ui.main

import com.virtaapps.mobile.base.BaseViewModel
import com.virtaapps.mobile.data.firebase.MeasurementHistoryUtils
import com.virtaapps.mobile.data.firebase.UserUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val userUtils: UserUtils,
    private val measurementHistoryUtils: MeasurementHistoryUtils
) : BaseViewModel() {

    fun getHistories() = callApiReturnLiveData(
        apiCall = { measurementHistoryUtils.getHistories() }
    )

    fun logout() = callApiReturnLiveData(
        apiCall = { userUtils.logout() }
    )

}