package com.sub6resources.utilities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.app.NavUtils
import android.support.v4.content.ContextCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem

/**
 * Copyright (c) 2017 Matthew Whitaker.
 */
abstract class BaseActivity(private val activityLayout: Int): AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
        open val menu: Int? = null
        open val toolbar: Int? = null
        open val fragmentTargets: Int = 0
        open val landingFragment: BaseFragment? = null
        open val drawer: DrawerLayout? = null
        open val sideNav: NavigationView? = null



        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(activityLayout)
            setUp()
            landingFragment?.let {
                FragmentTransaction(it, supportFragmentManager).into(fragmentTargets).switchFragment()
                fragmentTargets.let {
                    if(savedInstanceState == null){
                        FragmentTransaction(landingFragment as Fragment, supportFragmentManager)
                                .into(fragmentTargets)
                                .switchFragment()
                    }
                }
            }
            toolbar?.let {
                setSupportActionBar(findViewById(it))
                if(parentActivityIntent != null) {
                    supportActionBar?.setDisplayHomeAsUpEnabled(true)
                }
            }
            sideNav?.setNavigationItemSelectedListener(this)
        }

        open fun onBackButtonPressed() {}

        override fun onBackPressed() {

            onBackButtonPressed()

            fragmentTargets.let {
                val frag = supportFragmentManager.findFragmentById(fragmentTargets)
                if(frag is BaseFragment){
                    frag.onBackPressed()
                    return
                }
            }
            super.onBackPressed()
        }

        override fun onCreateOptionsMenu(_menu: Menu): Boolean {
            menu.isNotNull {
                menuInflater.inflate(this.menu as Int, _menu)
            }
            return true
        }

        override fun onNavigationItemSelected(item: MenuItem): Boolean {
            onNavItemSelected(item)
            drawer?.closeDrawers()
            drawer?.closeDrawer(sideNav!!)
            return true
        }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            android.R.id.home -> {
                val intent = NavUtils.getParentActivityIntent(this)
                intent!!.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                NavUtils.navigateUpTo(this, intent!!)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

        open fun openSideNav(){
            drawer?.openDrawer(sideNav!!)
        }

        open fun onNavItemSelected(item: MenuItem){}

        open fun setUp(){}

        fun addFragment(fragment: BaseFragment){
            FragmentTransaction(fragment, supportFragmentManager)
                    .into(fragmentTargets)
                    .addFragment()
        }

        fun switchFragment(fragment: BaseFragment){
            FragmentTransaction(fragment, supportFragmentManager)
                    .into(fragmentTargets)
                    .switchFragment()
        }

        fun popFragment(){
            fragmentManager?.popBackStack()
        }

        fun popAdd(fragment: BaseFragment){
            fragmentManager?.popBackStack()
            addFragment(fragment)
        }

    //Private variables to assist with the checkPermission function.
    private var onGranted: ArrayList<() -> Unit> = ArrayList<() -> Unit>()
    private var onDenied: ArrayList<() -> Unit> = ArrayList<() -> Unit>()
    private var savedPermissions: ArrayList<String> = ArrayList<String>()
    private var currentRequestCode = 0

    /**
     * @param permission A permission from [Manifest.permission]
     * @param onGranted A function with no parameters and a return type of [Unit] that is called when the permission is granted.
     * @param onDenied A function with no parameters and a return type of [Unit] that is called when the permission is denied or cancelled
     * @param showExplanation An optional lambda that is called when Android detects that the user has denied the permission that allows you to explain why you are using the permission.
     *
     * <h1>checkPermission</h1>
     * <p>
     * This function is an easy way to check for permissions without lots of unnecessary code.
     * </p>
     * <code>
     *     checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,
     *     { writeFile() },
     *     { cancel() },
     *     { dialog("This permission is required for the app to function").onClick(recheckPermission(it)) )
     *     )
     * </code>
     *
     * @author Matthew Whitaker
     * @sample checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, { writeFile() }, { cancel() }, { dialog("This permission is required for the app to function").onClick(recheckPermission(it)) ))
     *
     * @see recheckPermission(requestCode: Int)
     */
    fun checkPermission(permission: String, onGranted: () -> Unit = {}, onDenied: () -> Unit = {}, showExplanation: (requestCode: Int) -> Unit = {}) {
        this.savedPermissions.add(currentRequestCode, permission)
        this.onGranted.add(currentRequestCode, onGranted)
        this.onDenied.add(currentRequestCode, onDenied)

        if(ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                showExplanation(currentRequestCode)
            } else {
                ActivityCompat.requestPermissions(this, arrayOf(permission), currentRequestCode)
            }
        } else {
            this.onGranted[currentRequestCode]()
        }

        currentRequestCode++
    }

    /**
     * @param requestCode The string passed into the showExplanation function by [checkPermission]
     *
     * This function allows you to recheck permissions easily after they are denied multiple times.
     *
     * Do not use outside of the [checkPermission] showExplanation lambda
     *
     *
     * @author Matthew Whitaker
     * @see checkPermission
     */
    fun recheckPermission(requestCode: Int) {
        try {
            if (ContextCompat.checkSelfPermission(this, savedPermissions[requestCode]) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(savedPermissions[requestCode]), requestCode)
            } else {
                this.onGranted[requestCode]()
            }
        } catch(e: IndexOutOfBoundsException) {
            throw IllegalStateException("You must use this function inside of the checkPermission function")
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(grantResults.isNotEmpty()) {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                onGranted[requestCode]()
            } else {
                onDenied[requestCode]()
            }
        } else {
            onDenied[requestCode]()
        }
    }

    private var savedCallbacks = ArrayList<(resultCode: Int, data: Intent) -> Unit>()
    private var currentRequestCodeIntent = 0

    fun startActivityForResult(intent: Intent, callback: (resultCode: Int, data:Intent) -> Unit) {
        savedCallbacks.add(currentRequestCodeIntent, callback)
        startActivityForResult(intent, currentRequestCodeIntent)
        currentRequestCodeIntent++
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        savedCallbacks[requestCode](resultCode, data)
    }
}