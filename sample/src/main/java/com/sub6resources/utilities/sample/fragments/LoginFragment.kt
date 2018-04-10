package com.sub6resources.utilities.sample.fragments

import android.arch.lifecycle.Observer
import android.util.Log
import com.sub6resources.utilities.BaseFragment
import com.sub6resources.utilities.sample.R
import com.sub6resources.utilities.sample.api.Login
import com.sub6resources.utilities.sample.viewmodels.LoginViewModel

class LoginFragment: BaseFragment() {
    override val fragLayout = R.layout.fragment_login
    val loginViewModel by getViewModel<LoginViewModel>()

    override fun setUp() {
        loginViewModel.login(Login("Username", "Password"))

        loginViewModel.token.observe(this, Observer { token ->
            Log.d("TOKEN", "Token!!! ${token?.token}")
        })
    }
}