package com.example.tammy.mapviewer;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import static androidx.appcompat.app.AlertDialog.Builder;

/**
 * Start the app, display splash screen and check for permissions and location services
 */
public class StartActivity extends AppCompatActivity {

    static final int MY_PERMISSIONS_LOCATION = 0;
    static final int MY_PERMISSIONS_STORAGE = 2;
    static final int MY_PERMISSIONS_INTERNET = 4;
    
    /* Check for permissions before we start */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        Builder builder;

        Button splashBtn = findViewById(R.id.splashBtn);
        splashBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(StartActivity.this, MainActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                StartActivity.this.startActivity(i);
            }
        });

        if ((ContextCompat.checkSelfPermission(StartActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) ||
            (ContextCompat.checkSelfPermission(StartActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED)) {
            // Permission is not granted. Request the permission
            if (ActivityCompat.shouldShowRequestPermissionRationale(StartActivity.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                builder = new Builder(StartActivity.this);
                builder.setTitle("Storage Permission Needed");
                builder.setMessage("Permission to access your photos, media, and files is needed to load and import pdf maps. Please click ALLOW when asked.").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK button. Hide dialog. Ask again
                        ActivityCompat.requestPermissions(StartActivity.this,
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                MY_PERMISSIONS_STORAGE);
                    }
                }).show();
            }else {
                ActivityCompat.requestPermissions(StartActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_STORAGE);
            }
        }
        // Ask for location permissions
        if ((ContextCompat.checkSelfPermission(StartActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) ||
                (ContextCompat.checkSelfPermission(StartActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_DENIED)) {
            // Permission is not granted. Request the permission
            if ((ActivityCompat.shouldShowRequestPermissionRationale(StartActivity.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) ||
                    (ActivityCompat.shouldShowRequestPermissionRationale(StartActivity.this,
                            Manifest.permission.ACCESS_COARSE_LOCATION)))    {
                builder = new Builder(StartActivity.this);
                builder.setTitle("Location Permission Needed");
                builder.setMessage("Permission to access this device's location is needed to show your current location on the map. Please click ALLOW when asked.")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK button. Hide dialog. Ask again
                        ActivityCompat.requestPermissions(StartActivity.this,
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},
                                MY_PERMISSIONS_LOCATION);
                    }
                }).show();
            }else {
                ActivityCompat.requestPermissions(StartActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_LOCATION);
            }
        }

        // Check if location services are turned on
        if (!isLocationEnabled(StartActivity.this)) {
            builder = new Builder(StartActivity.this);
            builder.setTitle("Notice");
            builder.setMessage("Please turn ON Location Services. This can be done in your phone's Settings. If this is not turned on your current location will not appear on the map.")
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User clicked OK button. Hide dialog.
                        }
                    }).show();
        }
    }

    // PERMISSIONS
    // location service enabled?
    public static boolean isLocationEnabled(Context context) {
        int locationMode = 0;
        String locationProviders;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);
            } catch (android.provider.Settings.SettingNotFoundException e) {
                e.printStackTrace();
                return false;
            }

            return locationMode != Settings.Secure.LOCATION_MODE_OFF;

        }else{
            //LOCATION_PROVIDERS_ALLOWED - This constant was deprecated in API level 19.
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }
    }
    // Permission
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        Builder builder;
        if (grantResults.length == 0)return;
        switch (requestCode) {
           case MY_PERMISSIONS_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    //Toast.makeText(this,"Permission granted to access location.", Toast.LENGTH_LONG).show();
                } else {
                    // permission denied (this is the first time "never ask again" is not checked)
                    // so ask again explaining the usage of permission
                    // shouldShowRequestPermissionRationale will return true
                   if (ActivityCompat.shouldShowRequestPermissionRationale(StartActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                       builder = new Builder(StartActivity.this);
                       builder.setTitle("Location Permission Needed");
                       builder.setMessage("This app will not run without permission to access this device's location. Please click ALLOW when asked.")
                               .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                   public void onClick(DialogInterface dialog, int id) {
                                       // User clicked OK button. Hide dialog. Ask again
                                       dialog.dismiss();
                                       ActivityCompat.requestPermissions(StartActivity.this,
                                               new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                                               MY_PERMISSIONS_LOCATION);
                                   }
                               })
                               .setNegativeButton("No, Exit App", new DialogInterface.OnClickListener() {
                                   @Override
                                   public void onClick(DialogInterface dialogInterface, int i) {
                                       if (Build.VERSION.SDK_INT >= 21) {
                                           finishAndRemoveTask();
                                       } else {
                                           finish();
                                       }
                                   }
                               }).create().show();
                   }
                   // permission is denied (and never ask again is checked) go to Settings or exit
                   // shouldShowRequestPermissionRationale will return false
                    else {
                       // Ask user to go to setting and manually allow permissions
                       builder = new AlertDialog.Builder(StartActivity.this);
                       builder.setTitle("Permissions Needed");
                       builder.setMessage("You have denied some needed permissions. Go to Settings, click on Permissions and allow all permissions.  Then restart this app.")
                               .setPositiveButton("Yes, Go to Settings", new DialogInterface.OnClickListener() {
                                   public void onClick(DialogInterface dialog, int id) {
                                       // User clicked OK button. Hide dialog. Ask again
                                       dialog.dismiss();
                                       // Go to app settings
                                       Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                               Uri.fromParts("package",getPackageName(), null));
                                       intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                       startActivity(intent);
                                       if (Build.VERSION.SDK_INT >= 21) {
                                           finishAndRemoveTask();
                                       } else {
                                           finish();
                                       }
                                   }
                               })
                               .setNegativeButton("No, Exit App", new DialogInterface.OnClickListener() {
                                   @Override
                                   public void onClick(DialogInterface dialog, int i) {
                                       dialog.dismiss();
                                       if (Build.VERSION.SDK_INT >= 21) {
                                           finishAndRemoveTask();
                                       } else {
                                           finish();
                                       }
                                   }
                               }).create().show();
                    }
                }
                return;
            }
            case MY_PERMISSIONS_STORAGE:{
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    //Toast.makeText(this,"Permission granted to read external storage.", Toast.LENGTH_LONG).show();

                } else {
                    // permission denied (this is the first time "never ask again" is not checked)
                    // so ask again explaining the usage of permission
                    // shouldShowRequestPermissionRationale will return true
                    if (ActivityCompat.shouldShowRequestPermissionRationale(StartActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        builder = new Builder(StartActivity.this);
                        builder.setTitle("Storage Permission Needed");
                        builder.setMessage("This app will not run without permission to access your photos, media, and files. Please click ALLOW when asked.")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // User clicked OK button. Hide dialog. Ask again
                                    dialog.dismiss();
                                    ActivityCompat.requestPermissions(StartActivity.this,
                                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                            MY_PERMISSIONS_STORAGE);
                                }
                            })
                            .setNegativeButton("No, Exit App", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    if (Build.VERSION.SDK_INT >= 21) {
                                        finishAndRemoveTask();
                                    } else {
                                        finish();
                                    }
                                }
                            }).create().show();
                    }
                    // permission is denied (and never ask again is checked) go to Settings or exit
                    // shouldShowRequestPermissionRationale will return false
                    else{
                        // Ask user to go to setting and manually allow permissions
                        builder = new Builder(StartActivity.this);
                        builder.setTitle("Permissions Needed");
                        builder.setMessage("You have denied some needed permissions. Go to Settings, click on Permissions and allow all permissions. Then restart this app.")
                                .setPositiveButton("Yes, Go to Settings", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // User clicked OK button. Hide dialog. Ask again
                                        dialog.dismiss();
                                        // Go to app settings
                                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                                Uri.fromParts("package",getPackageName(), null));
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        if (Build.VERSION.SDK_INT >= 21) {
                                            finishAndRemoveTask();
                                        } else {
                                            finish();
                                        }
                                    }
                                })
                                .setNegativeButton("No, Exit App", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int i) {
                                        dialog.dismiss();
                                        if (Build.VERSION.SDK_INT >= 21) {
                                            finishAndRemoveTask();
                                        } else {
                                            finish();
                                        }
                                    }
                                }).create().show();
                    }
                }
            }
           /* case MY_PERMISSIONS_INTERNET:{
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    //Toast.makeText(this,"Permission granted for the internet.", Toast.LENGTH_LONG).show();

                } else {
                    // permission denied, close the app.
                    if (Build.VERSION.SDK_INT >= 21) {
                        finishAndRemoveTask();
                    }
                    else {
                        finish();
                    }
                }
                return;
            }*/
        }
    }
}
