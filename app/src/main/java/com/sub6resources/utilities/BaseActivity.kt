package com.sub6resources.utilities

import android.Manifest
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.app.NavUtils
import android.support.v4.content.ContextCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View


abstract class BaseActivity(private val activityLayout: Int): AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    open val menu: Int? = null
    open val toolbar: Int? = null
    open val fragmentTargets: Int = 0
    open val landingFragment: BaseFragment? = null

    open val drawer: DrawerLayout? = null
    open val sideNav: NavigationView? = null

    private var actionBarDrawerToggle: ActionBarDrawerToggle? = null
    val app by lazy {application as BaseApplication}


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SharedPrefs.sharedPreferences = sharedPreferences
        setContentView(activityLayout)
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
            drawer?.let {
                supportActionBar?.setDisplayHomeAsUpEnabled(true)
                supportActionBar?.setHomeButtonEnabled(true)
            }
        }
        drawer?.let {
            actionBarDrawerToggle = object : ActionBarDrawerToggle(this, drawer,
                    R.string.drawer_open, R.string.drawer_closed) {

                /** Called when a drawer has settled in a completely open state.  */
                override fun onDrawerOpened(drawerView: View) { drawerOpened(drawerView) }

                /** Called when a drawer has settled in a completely closed state.  */
                override fun onDrawerClosed(view: View) { drawerClosed(view) }
            }
            it.addDrawerListener(actionBarDrawerToggle!!)
        }
        sideNav?.setNavigationItemSelectedListener(this)

        setUp()
    }

    open fun drawerOpened(drawerView: View) {}
    open fun drawerClosed(view: View) {}

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
                intent?.let {
                    it.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    NavUtils.navigateUpTo(this, it)
                }
            }
        }
        if (actionBarDrawerToggle?.onOptionsItemSelected(item) == true) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        drawer?.let {
            actionBarDrawerToggle?.syncState()
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        drawer?.let {
            actionBarDrawerToggle?.onConfigurationChanged(newConfig)
        }
    }

    /**
     * Use this method instead of @onNavigationItemSelected for implementation with a navigation drawer.
     */
    open fun onNavItemSelected(item: MenuItem){}

    open fun setUp(){}

    //Fragment Transactions
    fun addFragment(fragment: BaseFragment) {
        FragmentTransaction(fragment, supportFragmentManager)
                .into(fragmentTargets)
                .addFragment()
    }

    fun switchFragment(fragment: BaseFragment) {
        FragmentTransaction(fragment, supportFragmentManager)
                .into(fragmentTargets)
                .switchFragment()
    }

    fun showFragment(fragment: BaseFragment) {
        FragmentTransaction(fragment, supportFragmentManager)
                .into(fragmentTargets)
                .showFragment()
    }

    fun hideFragment(fragment: BaseFragment) {
        FragmentTransaction(fragment, supportFragmentManager)
                .into(fragmentTargets)
                .hideFragment()
    }

    fun hideShowFragment(fragmentToHide: BaseFragment, fragmentToShow: BaseFragment) {
        hideFragment(fragmentToHide)
        showFragment(fragmentToShow)
    }

    fun popFragment(){
        fragmentManager?.popBackStack()
    }

    fun popAdd(fragment: BaseFragment){
        fragmentManager?.popBackStack()
        addFragment(fragment)
    }

    //Private variables to assist with the checkPermission function.
    private var onGranted: ArrayList<() -> Unit> = ArrayList()
    private var onDenied: ArrayList<() -> Unit> = ArrayList()
    private var savedPermissions: ArrayList<String> = ArrayList()
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
    @JvmOverloads fun checkPermission(permission: String, onGranted: () -> Unit = {}, onDenied: () -> Unit = {}, showExplanation: (requestCode: Int) -> Unit = {recheckPermission(it)}) {
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
     * @param requestCode The id passed into the showExplanation function by [checkPermission]
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

    /**
     * @param permissions A list of Manifest.permission strings to check.
     *
     * @param onGranted A lambda with the granted permission as a string
     * @param onDenied A lambda with the denied permission as a string
     * @param showExplanation Show an explanation for the permission being requested
     */
    @JvmOverloads fun checkListOfPermissions(permissions: List<String>, onGranted: (permission: String) -> Unit = {}, onDenied: (permission: String) -> Unit = {}, showExplanation: (permission: String, requestCode: Int) -> Unit = {_,id -> recheckPermission(id)}) {
        for (permission in permissions) {
            checkPermission(permission, { onGranted(permission) }, { onDenied(permission) }, { showExplanation(permission, it) })
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


    private var currentRequestCodeIntent = 0

    fun startActivityForResult(intent: Intent, callback: (resultCode: Int, data: Intent?) -> Unit) {
        app.savedCallbacks.add(currentRequestCodeIntent, callback)
        startActivityForResult(intent, currentRequestCodeIntent)
        currentRequestCodeIntent++
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        app.savedCallbacks[requestCode](resultCode, data)
    }

    fun <T: ViewModel> getViewModel(javaClass: Class<T>): Lazy<T> = lazy { ViewModelProviders.of(this).get(javaClass) }
}