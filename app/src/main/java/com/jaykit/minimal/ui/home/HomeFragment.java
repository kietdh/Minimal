package com.jaykit.minimal.ui.home;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.jaykit.minimal.R;
import com.jaykit.minimal.api.Darksky;
import com.jaykit.minimal.api.Forecast;
import com.jaykit.minimal.api.GPSTracker;
import com.loopj.android.http.AsyncHttpResponseHandler;
import org.json.JSONException;
import org.json.JSONObject;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import cz.msebera.android.httpclient.Header;

public class HomeFragment extends Fragment {

    GPSTracker gps;
    Darksky darksky;
    Forecast currentlyForecast;

    private HomeViewModel homeViewModel;
    private FirebaseAuth mAuth;
    FirebaseUser user;
    DatabaseReference databaseReference;
    //SharedPreferences sharedPref;

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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) { }
        });

        setWelcomeTitle();
        setUsername();
        getForecast();
    }

    private void setUsername() {

        user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null ) {
            startActivity(new Intent(getActivity(), LoginActivity.class));
        }
        else {
            String uid = user.getUid();
            databaseReference = FirebaseDatabase.getInstance().getReference();

            databaseReference.addValueEventListener(new ValueEventListener() {

                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String user_name = snapshot.child("User").child(uid).child("name").getValue(String.class);
                    username.setText(user_name);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.d("ERROR: ", "Cannot get data from real time database.");
                }
            });
        }
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

        double latitude = gps.getLatitude();
        double longitude = gps.getLongitude();

        if (!gps.canGetLocation()) {
            Toast.makeText(getContext(), "Unable to get location", Toast.LENGTH_LONG).show();
        }
        darksky = new Darksky(latitude, longitude);

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
            }
        };
        darksky.getCurrentForecast(handler);
    }


}