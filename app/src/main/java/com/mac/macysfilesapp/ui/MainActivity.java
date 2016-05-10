package com.mac.macysfilesapp.ui;

import android.Manifest;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.fastaccess.permission.base.PermissionHelper;
import com.fastaccess.permission.base.callback.OnPermissionCallback;
import com.mac.macysfilesapp.R;
import com.mac.macysfilesapp.fragments.MainFragment;

public class MainActivity extends AppCompatActivity implements OnPermissionCallback {

    MainFragment myfragment;
    FragmentManager fragmentManager;
    PermissionHelper permissionHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get the permission helper to ask for the runtime permission
        permissionHelper = PermissionHelper.getInstance(this);

        // Check if we have the permission
        int permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE);

        String message = (permissionCheck == 0) ? "Granted" : "Not Granted";
        Log.i("MYTAG", "Permission Check: "+message);

        // if permission granted
        if (permissionCheck == 0) {

        // if permission not granted
        } else {
            // request permission
            permissionHelper.setForceAccepting(false)
                    .request(Manifest.permission.READ_EXTERNAL_STORAGE);
        }

        fragmentManager = getSupportFragmentManager();
        Log.i("MYTAG", "External Storage Readable: "+isExternalStorageReadable());

        // retrieve or create fragment
        if (savedInstanceState == null) {
            myfragment = new MainFragment();
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, myfragment, "fragment_main")
                    .commit();
        } else {
            myfragment = (MainFragment) fragmentManager.findFragmentByTag("fragment_main");
        }
    }

    @Override
    public void onBackPressed() {
        myfragment.cancelTask();
    }

    /* Checks if external storage is available to at least read */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    @Override
    public void onPermissionGranted(String[] permissionName) {
        Log.d("MYTAG", "onPermissionGranted "+permissionName.toString());
    }

    @Override
    public void onPermissionDeclined(String[] permissionName) {
        Log.d("MYTAG", "onPermissionDeclined " + permissionName.toString());
    }

    @Override
    public void onPermissionPreGranted(String permissionName) {
        Log.d("MYTAG", "onPermissionPreGranted " + permissionName);
    }

    @Override
    public void onPermissionNeedExplanation(String permissionName) {
        Log.d("MYTAG", "onPermissionNeedExplanation " + permissionName);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.permission_dialog_title)
                .setMessage(R.string.permission_dialog_message)
                .setPositiveButton("Request", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        permissionHelper.requestAfterExplanation(Manifest.permission.READ_EXTERNAL_STORAGE);
                    }
                })
                .create();
        dialog.show();
    }

    @Override
    public void onPermissionReallyDeclined(String permissionName) {
        Log.d("MYTAG", "onPermissionReallyDeclined " + permissionName);
    }

    @Override
    public void onNoPermissionNeeded() {
        Log.d("MYTAG", "onNoPermissionNeeded");
    }

}
