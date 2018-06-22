package com.sub6resources.utilities.sample

import com.sub6resources.utilities.BaseActivity
import com.sub6resources.utilities.sample.fragments.LoginFragment
import com.sub6resources.utilities.sample.viewmodels.LoginViewModel

class MainActivity : BaseActivity(R.layout.activity_fragment_container) {

    //Has no purpose, just a test.
    val viewModel by getGlobalViewModel<LoginViewModel>()

    override val fragmentTargets = R.id.fragmentTarget
    override val landingFragment = LoginFragment()
}