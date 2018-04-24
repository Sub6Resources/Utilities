package com.sub6resources.utilities.sample.fragments

import android.arch.lifecycle.Observer
import android.util.Log
import com.sub6resources.utilities.BaseFragment
import com.sub6resources.utilities.getString
import com.sub6resources.utilities.onClick
import com.sub6resources.utilities.sample.PreferencesActivity
import com.sub6resources.utilities.sample.R
import com.sub6resources.utilities.sample.api.Login
import com.sub6resources.utilities.sample.viewmodels.LoginViewModel
import com.sub6resources.utilities.startActivity
import kotlinx.android.synthetic.main.fragment_login.*

class LoginFragment: BaseFragment() {
    override val fragLayout = R.layout.fragment_login
    val loginViewModel by getViewModel<LoginViewModel>()

    override fun setUp() {

        btn_submit.onClick {
            loginViewModel.login(Login(et_username.getString(), et_password.getString()))
        }

        loginViewModel.token.observe(this, Observer { token ->
            Log.d("TOKEN", "Token!!! ${token?.token}")
            baseActivity.startActivity<PreferencesActivity>()
        })
    }
}