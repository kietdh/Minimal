package com.jaykit.minimal.ui.home;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jaykit.minimal.LoginActivity;
import com.jaykit.minimal.MainActivity;
import com.jaykit.minimal.R;
import com.jaykit.minimal.api.Darksky;
import com.jaykit.minimal.api.Forecast;
import com.jaykit.minimal.api.GPSTracker;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Calendar;

import cz.msebera.android.httpclient.Header;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class HomeFragment extends Fragment {

    private ArrayList<Object> permissionToRequest;
    private final ArrayList permissionRejected = new ArrayList();
    private final ArrayList<String> permissions = new ArrayList<>();

    private final static int ALL_PERMISSIONS_RESULT = 101;

    GPSTracker gps;
    Darksky darksky;
    Forecast currentlyForecast;

    protected double latitude;
    protected double longitude;


    private HomeViewModel homeViewModel;
    FirebaseUser user;
    DatabaseReference databaseReference;

    //Declare Palette.
    View view;
    TextView username;
    TextView welcome;
    TextView txtTemperature;
    TextView txtHumidity;
    TextView txtUVIndex;
    TextView txtWindSpeed;
    TextView txtTimeZone;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        view = inflater.inflate(R.layout.fragment_home, container, false);
        welcome = view.findViewById(R.id.txtWelcome);
        username = view.findViewById(R.id.txtUsername);
        txtTemperature = view.findViewById(R.id.txtTemperature);
        txtHumidity = view.findViewById(R.id.txtHumidity);
        txtUVIndex = view.findViewById(R.id.txtUVIndex);
        txtWindSpeed = view.findViewById(R.id.txtWindSpeed);
        txtTimeZone = view.findViewById(R.id.txtTimeZone);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        permissions.add(ACCESS_FINE_LOCATION);
        permissions.add(ACCESS_COARSE_LOCATION);
        permissionToRequest = findUnAskedPermissions(permissions);

        if ( permissionToRequest.size() > 0 ) {
            requestPermissions((String[]) permissionToRequest.toArray(new String[0]), ALL_PERMISSIONS_RESULT);
        }
        gps = new GPSTracker(getContext());

        if ( gps.canGetLocation() ) {
            longitude = gps.getLongitude();
            latitude = gps.getLatitude();
        }
        else {
            gps.showSettingsAlert();
        }

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
            }
        });

        setWelcomeTitle();

    }

    @Override
    public void onResume() {
        super.onResume();
        setUsername();
        getForecast();
        boolean aclPermission = (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED);
        if (!aclPermission) {
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    1);
        }

        boolean aflPermission = (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED);
        if (!aflPermission) {
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    2);
        }
    }

    private void setUsername() {
        user = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        String uid = user.getUid();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = snapshot.child("User").child(uid).child("name").getValue(String.class);
                username.setText(name);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("ERROR: ", "Cannot get data from real time database.");
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void setWelcomeTitle() {
        int currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);

        if (currentHour > 0 && currentHour <= 12) {
            welcome.setText("Good morning, ");
        } else if (currentHour > 12 && currentHour <= 18) {
            welcome.setText("Good afternoon, ");
        } else {
            welcome.setText("Good night, ");
        }
    }



    private void getForecast() {
        gps = new GPSTracker(getContext());

        if (!gps.canGetLocation()) {
            Toast.makeText(getContext(), "Unable to get location", Toast.LENGTH_LONG).show();
        }

        Log.d("LONG: ", String.valueOf(longitude));
        Log.d("LAT: ", String.valueOf(latitude));


        darksky = new Darksky(Math.round(latitude), Math.round(longitude));

        AsyncHttpResponseHandler handler = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response = new String(responseBody, StandardCharsets.UTF_8);
                try {
                    JSONObject root = new JSONObject(response);
                    JSONObject currently = root.getJSONObject("currently");

                    String child = root.getString("timezone");
                    currentlyForecast = new Forecast (
                            currently.getInt("time"),
                            currently.getString("summary"),
                            currently.getString("icon"),
                            currently.getInt("uvIndex"),
                            currently.getDouble("temperature"),
                            currently.getDouble("humidity"),
                            currently.getDouble("windSpeed")
                    );

                    int temperature = (int) ((currentlyForecast.getTemperature() - 32) * 0.5556);
                    int humidity = (int) (currentlyForecast.getHumidity() * 100);
                    int wind_speed = (int) (currentlyForecast.getWindSpeed());

                    txtTemperature.setText(String.valueOf(temperature));
                    txtHumidity.setText(String.valueOf(humidity));
                    txtUVIndex.setText(String.valueOf(currentlyForecast.getUvIndex()));
                    txtWindSpeed.setText(String.valueOf(wind_speed));
                    txtTimeZone.setText(child);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.e("ERROR: ", "LOI KHONG THE GET API");
                Log.e("darksky error: ", String.valueOf(statusCode) + error.getMessage());
            }
        };
        darksky.getCurrentForecast(handler);
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
            return (getContext().checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED);
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
        new AlertDialog.Builder(getContext())
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
}