package com.jaykit.minimal;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.jaykit.minimal.api.GPSTracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;


public class MainActivity extends AppCompatActivity {

    private ArrayList<Object> permissionToRequest;
    private final ArrayList permissionRejected = new ArrayList();
    private final ArrayList<String> permissions = new ArrayList<>();

    private final static int ALL_PERMISSIONS_RESULT = 101;

    GPSTracker gps;

    protected double latitude;
    protected double longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        if ( mAuth.getCurrentUser() == null ) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        }
        else {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            setContentView(R.layout.activity_main);
            BottomNavigationView navView = findViewById(R.id.nav_view);
            // Passing each menu ID as a set of Ids because each
            // menu should be considered as top level destinations.
            AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                    R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications, R.id.navigation_user)
                    .build();
            NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
            //NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
            NavigationUI.setupWithNavController(navView, navController);
        }

        permissions.add(ACCESS_FINE_LOCATION);
        permissions.add(ACCESS_COARSE_LOCATION);
        permissionToRequest = findUnAskedPermissions(permissions);

        if ( permissionToRequest.size() > 0 ) {
            requestPermissions((String[]) permissionToRequest.toArray(new String[0]), ALL_PERMISSIONS_RESULT);
        }
        gps = new GPSTracker(this);

        if ( gps.canGetLocation() ) {
            longitude = gps.getLongitude();
            latitude = gps.getLatitude();
        }
        else {
            gps.showSettingsAlert();
        }
    }
    private ArrayList<Object> findUnAskedPermissions(ArrayList<String> wanted) {
        ArrayList<Object> result = new ArrayList<>();

        for (Object perm : wanted) {
            if (hasPermission((String) perm)) {
                result.add(perm);
            }
        }

        return result;
    }
    private boolean hasPermission(String permission) {
        if (canMakeSmores()) {
            return (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED);
        }
        return false;
    }
    private boolean canMakeSmores() {
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @NotNull String[] permissions, @NonNull @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case ALL_PERMISSIONS_RESULT:
                for (Object perms : permissionToRequest) {
                    if (hasPermission((String) perms))
                        permissionRejected.add(perms);
                }
                if ( permissionRejected.size() > 0 ) {
                    if (shouldShowRequestPermissionRationale((String) permissionRejected.get(0))) {
                        showMessageOKCancel(
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        requestPermissions((String[]) permissionRejected.toArray(new String[0]), ALL_PERMISSIONS_RESULT);
                                    }
                                });
                        return;
                    }
                    break;
                }
        }
    }
    private void showMessageOKCancel(DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage("These permissions are mandatory for the application. Please allow access.")
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        gps.stopListener();
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }
}