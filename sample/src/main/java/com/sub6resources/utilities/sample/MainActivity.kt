package com.sub6resources.utilities.sample

import co.zsmb.materialdrawerkt.builders.accountHeader
import co.zsmb.materialdrawerkt.builders.drawer
import co.zsmb.materialdrawerkt.draweritems.badgeable.primaryItem
import co.zsmb.materialdrawerkt.draweritems.profile.profile
import com.sub6resources.utilities.BaseActivity

class MainActivity : BaseActivity(R.layout.activity_main) {
    override val toolbar = R.id.toolbar
    //override val drawer by lazy {findViewById<DrawerLayout>(R.id.drawer_layout)}
    //override val sideNav by lazy {findViewById<NavigationView>(R.id.side_nav)}

    override fun setUp() {
        val drawer = drawer {
            toolbar = findViewById(this@MainActivity.toolbar)
            accountHeader {
                profile("Samantha", "samantha@gmail.com") {
                    //icon = "http://some.site/samantha.png"
                }
                profile("Laura", "laura@gmail.com") {
                    //icon = R.drawable.profile_laura
                }
                background = R.color.material_drawer_dark_background
            }
            primaryItem("Home") {
                icon = R.mipmap.ic_launcher
            }
        }
    }
}