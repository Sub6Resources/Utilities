package com.sub6resources.utilities.sample

import android.support.design.widget.NavigationView
import android.support.v4.widget.DrawerLayout
import com.sub6resources.utilities.BaseActivity

class MainActivity : BaseActivity(R.layout.activity_main) {
    override val toolbar = R.id.toolbar
    override val drawer by lazy {findViewById<DrawerLayout>(R.id.drawer_layout)}
    override val sideNav by lazy {findViewById<NavigationView>(R.id.side_nav)}
}