package com.jaykit.minimal;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jaykit.minimal.ui.dashboard.DashboardViewModel;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private DashboardViewModel dashboardViewModel;

    private static final String CHANNEL_ID = "01";
    private static final String HOME_CHILD = "Home";
    private static final String MAC_CHILD = "24:62:AB:F9:22:0C";
    private static final String SENSOR_CHILD = "Sensor";
    int fire = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
        DatabaseReference mRootReference = mDatabase.getReference();
        DatabaseReference mSensorReference = mRootReference.child(HOME_CHILD).child(MAC_CHILD).child(SENSOR_CHILD);

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
            NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
            //NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
            NavigationUI.setupWithNavController(navView, navController);
        }

        mSensorReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                String fire_index = Objects.requireNonNull(snapshot.child("fire").getValue()).toString();

                fire = Integer.parseInt(fire_index);

                if ( fire == 1 ) {
                    createNotificationChannel();
                    String message = "SOS, FIRE detected.";
                    NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this, CHANNEL_ID)
                            .setSmallIcon(R.drawable.ic_launcher_background)
                            .setContentTitle("Minimal")
                            .setContentText(message)
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT);
                    //Vibration
                    builder.setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 });
                    //LED
                    builder.setLights(Color.RED, 3000, 3000);
                    //Ton
                    //builder.setSound(Uri.parse("uri://sadfasdfasdf.mp3"));

                    Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
                    builder.setSound(alarmSound);
                    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(MainActivity.this);

                    // notificationId is a unique int for each notification that you must define
                    notificationManager.notify(1, builder.build());
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                Log.d("Minimal: ", error.getMessage());
            }
        });
    }
    private void createNotificationChannel() {

        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String description = "test notification";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_ID, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}