# Utilities
[![](https://jitpack.io/v/Sub6Resources/Utilities.svg)](https://jitpack.io/#Sub6Resources/Utilities)
## Kotlin Utilites and Extensions for Android
Add utilites to your Android project using Jitpack and Gradle:

    maven {url 'https://jitpack.io'}
    ...
    dependencies {
      compile 'com.github.Sub6Resources:Utilities:1.1.8'
    }


### PermissionActivity from Kotlin

Example usage:
    
    import android.Manifest
    import com.sub6resources.utilities.PermissionActivity
    
    class MyActivity: PermissionActivity {
        ...
        checkPermission(Manifest.permission.RECORD_AUDIO,
            onGranted = {
                //Do something on permission granted.
            },
            onDenied = {
                //Do something on permission denied.
            },
            showExplanation = { id ->
                //Do something when an explanation dialog needs to be shown.
                recheckPermission(id)
            }
            ...
    }

This is an expanded example of the PermissionActivity. It will show a dialog for all permissions and if a permission is denied, will continue to show the dialog until the permission is granted.

Condensed example that does the exact same as above code:

    import android.Manifest
    import com.sub6resources.utilites.PermissionActivity
    
    class MyActivity: PermissionActivity {
        ...
        checkPermission(Manifest.permission.RECORD_AUDIO)
        ...
    }

### PermissionActivity from Java

Example usage:
    
    import android.Manifest;
    import com.sub6resources.utilites.PermissionActivity;
    
    class MyActivity extends PermissionActivity {
        ...
        checkListOfPermissions(new String[] {Manifest.permission.CAMERA, Manifest.permission.READ_SMS}, //etc. Just make sure to include the permission in your manifest as well.
                    (permission) -> {
                        //Do something on permission granted.
                        return null;
                    },
                    (permission) -> {
                        //Do something on permission denied.
                        return null;
                    },
                    (permission, id) -> {
                        //Do when a permission needs to be explained to the user, then...
                        recheckPermission(id);
                        return null;
                    });
         ...
     }
                
This is the most basic java example for the PermissionActivity. It will show a dialog for all permissions and if a permission is denied, will continue to show the dialog until the permission is granted.
