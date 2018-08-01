package com.sub6resources.utilities

import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AppCompatActivity

/**
 * Created by whitaker on 1/17/18.
 */

/**
 * This parent activity class allows simple use of my permission library
 */
open class PermissionActivity: AppCompatActivity() {
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

    @JvmOverloads fun checkListOfPermissions(permissions: Array<String>, onGranted: (permission: String) -> Unit = {}, onDenied: (permission: String) -> Unit = {}, showExplanation: (permission: String, requestCode: Int) -> Unit = {_,id -> recheckPermission(id)}) {
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
}