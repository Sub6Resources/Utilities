package com.sub6resources.utilities.sample.viewmodels

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.sub6resources.utilities.sample.LoginRepository
import com.sub6resources.utilities.sample.api.Login
import com.sub6resources.utilities.sample.api.Token
import com.sub6resources.utilities.switchMap

class LoginViewModel(private val loginRepository: LoginRepository): ViewModel() {
    val loginCredentials = MutableLiveData<Login>()
    val token = loginCredentials.switchMap { loginRepository.logIn(it) }

    fun login(login: Login) {
        loginCredentials.value = login
    }
}