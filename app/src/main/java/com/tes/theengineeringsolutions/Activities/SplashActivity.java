package com.tes.theengineeringsolutions.Activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.tes.theengineeringsolutions.R;

import java.util.ArrayList;
import java.util.List;


/*Created by pratik__katariya on 23/11/2019
 * */

public class SplashActivity extends AppCompatActivity {

    private final int PERMISSION_REQUEST_CODE = 200;

    private final String[] permissionList = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen_layout);

        Log.d("SPLASH ACTIVITY", "result check permission" + checkPermission() + "");

        if (checkPermission()) {
            requestPermissions(permissionList, PERMISSION_REQUEST_CODE);
        } else {
            startActivity();
            Toast.makeText(this, "go the main activity", Toast.LENGTH_SHORT).show();
        }

    }

    private boolean checkPermission() {
        List<String> permission = new ArrayList<>();
        for (String s : permissionList) {
            if (ActivityCompat.checkSelfPermission(this, s) != PackageManager.PERMISSION_GRANTED) {
                permission.add(s);
            }
        }

        if (permission.size() > 0) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE && grantResults.length > 0) {

            boolean anyPermissionDenied = false;
            boolean neverAskButtonChecked = false;

            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {

                    anyPermissionDenied = true;

                    if (!shouldShowRequestPermissionRationale(permissions[i])) {
                        neverAskButtonChecked = true;
                    }
                }
                Toast.makeText(this, "grant result  " + grantResults[0] + " permission name " + permissions[0], Toast.LENGTH_SHORT).show();
            }

            if (anyPermissionDenied) {
                if (neverAskButtonChecked) {
                    new AlertDialog.Builder(this).setTitle("app needs this permission work correctly ").setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            openSettingApp();
                        }
                    }).setCancelable(false)
                            .create().show();
                } else {
                    if (checkPermission()) {
                        requestPermissions(permissionList, PERMISSION_REQUEST_CODE);
                    }
                    ;
                }
            } else {
                startActivity();
            }
        }
    }

    private void startActivity() {

        new Handler().postDelayed(() -> startActivity(new Intent(
                SplashActivity.this,
                MainActivity.class
        )), 2200);
    }

    private void openSettingApp() {

        Intent i = new Intent();
        i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        i.setData(uri);
        startActivity(i);

        Toast.makeText(this, "setting app", Toast.LENGTH_SHORT).show();
    }

}
