package com.sub6resources.utilities.sample

import android.arch.lifecycle.MutableLiveData
import com.sub6resources.utilities.sample.api.Login
import com.sub6resources.utilities.sample.api.LoginApi
import com.sub6resources.utilities.sample.api.Token

class LoginRepository(private val loginApi: LoginApi) {

    //Normally you would make a call to the API at this point, but this is just a demo, so we don't really care.
    fun logIn(login: Login) = MutableLiveData<Token>().apply { value = Token("T35+T0k3n") }
}