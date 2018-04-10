package com.sub6resources.utilities.sample

import com.sub6resources.utilities.BaseActivity
import com.sub6resources.utilities.sample.fragments.LoginFragment

class MainActivity : BaseActivity(R.layout.activity_fragment_container) {
    override val fragmentTargets = R.id.fragmentTarget
    override val landingFragment = LoginFragment()
}