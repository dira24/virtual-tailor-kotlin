package com.virtaapps.mobile.ui.auth

import com.virtaapps.mobile.base.BaseViewModel
import com.virtaapps.mobile.data.firebase.UserUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val userUtils: UserUtils
) : BaseViewModel() {

    fun login(email: String, password: String) = callApiReturnLiveData(
        apiCall = { userUtils.login(email, password) }
    )

    fun register(name: String, address: String, email: String, password: String) = callApiReturnLiveData(
        apiCall = { userUtils.register(name, address, email, password) }
    )

    fun validateIsLoggedIn() = userUtils.isLoggedIn()
}