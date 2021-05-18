package com.jaykit.minimal.ui.dashboard;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.card.MaterialCardView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jaykit.minimal.MainActivity;
import com.jaykit.minimal.R;

import java.util.Objects;


public class DashboardFragment extends Fragment  implements View.OnClickListener {

    private DashboardViewModel dashboardViewModel;
    private FirebaseDatabase  mDatabase;
    private DatabaseReference mRootReference;
    private DatabaseReference mDeviceReference;
    private DatabaseReference mSensorReference;

    private static final int VALUE_ON = 1;
    private static final int VALUE_OFF = 0;

    private static final String HOME_CHILD = "Home";
    private static final String MAC_CHILD = "24:62:AB:F9:22:0C";
    private static final String DEVICE_CHILD = "Device";
    private static final String SENSOR_CHILD = "Sensor";


    //Declare Palette.
    private MaterialCardView btnDevice_1;
    private MaterialCardView btnDevice_2;
    private MaterialCardView btnDevice_3;
    private MaterialCardView btnDevice_4;
    private MaterialCardView btnDevice_5;
    private MaterialCardView btnDevice_6;
    private MaterialCardView btnDevice_7;
    private MaterialCardView btnDevice_8;
    private MaterialCardView btnDevice_9;
    private MaterialCardView btnDevice_10;
    private MaterialCardView btnDevice_11;
    private MaterialCardView btnDevice_12;
    private MaterialCardView btnDevice_13;
    private MaterialCardView btnDevice_14;
    private MaterialCardView btnDevice_15;
    private MaterialCardView btnDevice_16;

    private TextView txtTemperature;
    private TextView txtHumidity;
    private TextView txtGas;
    private TextView txtTemperatureFeel;
    private TextView txtHumidityFeel;

    //Declare number of status of devices.
    int light_bulb;
    int air_conditioner;
    int fan;
    int light;
    int outlet;
    int top_fan;
    int top_light;
    int top_light_bulb;
    int device_1;
    int device_2;
    int device_3;
    int device_4;
    int device_5;
    int device_6;
    int device_7;
    int device_8;

    //Declare sensor values.
    String temperature;
    String humidity;
    int    gas;
    float  tempFloat;
    float  humFloat;


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
        mDatabase = FirebaseDatabase.getInstance();
        mRootReference = mDatabase.getReference();
        mDeviceReference = mRootReference.child(HOME_CHILD).child(MAC_CHILD).child(DEVICE_CHILD);
        mSensorReference = mRootReference.child(HOME_CHILD).child(MAC_CHILD).child(SENSOR_CHILD);

        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        btnDevice_1 = view.findViewById(R.id.btnDevice_1);
        btnDevice_2 = view.findViewById(R.id.btnDevice_2);
        btnDevice_3 = view.findViewById(R.id.btnDevice_3);
        btnDevice_4 = view.findViewById(R.id.btnDevice_4);
        btnDevice_5 = view.findViewById(R.id.btnDevice_5);
        btnDevice_6 = view.findViewById(R.id.btnDevice_6);
        btnDevice_7 = view.findViewById(R.id.btnDevice_7);
        btnDevice_8 = view.findViewById(R.id.btnDevice_8);
        btnDevice_9 = view.findViewById(R.id.btnDevice_9);
        btnDevice_10 = view.findViewById(R.id.btnDevice_10);
        btnDevice_11 = view.findViewById(R.id.btnDevice_11);
        btnDevice_12 = view.findViewById(R.id.btnDevice_12);
        btnDevice_13 = view.findViewById(R.id.btnDevice_13);
        btnDevice_14 = view.findViewById(R.id.btnDevice_14);
        btnDevice_15 = view.findViewById(R.id.btnDevice_15);
        btnDevice_16 = view.findViewById(R.id.btnDevice_16);

        txtTemperature = view.findViewById(R.id.txtTemperature);
        txtHumidity    = view.findViewById(R.id.txtHumidity);
        txtGas         = view.findViewById(R.id.txtGas);
        txtTemperatureFeel  = view.findViewById(R.id.txtTemperatureFeel);
        txtHumidityFeel     = view.findViewById(R.id.txtHumidityFeel);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mSensorReference.addValueEventListener(new ValueEventListener() {

            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String index_temperature = snapshot.child("temperature").getValue().toString();
                String index_humidity    = snapshot.child("humidity").getValue().toString();
                String index_gas         = snapshot.child("gas").getValue().toString();

                temperature = index_temperature;
                humidity    = index_humidity;
                gas         = Integer.parseInt(index_gas);
                tempFloat   = Float.parseFloat(index_temperature);
                humFloat    = Float.parseFloat(index_humidity);

                txtTemperature.setText(temperature);
                txtHumidity.setText(humidity);

                if ( gas == 1 ) {
                    txtGas.setText("warning, gas detect");
                }
                else {
                    txtGas.setText("no gas detect, all safe");
                }

                if ( tempFloat < 0 )
                    txtTemperatureFeel.setText("freeze, play with snow");
                else if (tempFloat >= 0 && tempFloat <= 20 )
                    txtTemperatureFeel.setText("cold, best time to sleep");
                else if ( tempFloat > 20 && tempFloat <= 26 )
                    txtTemperatureFeel.setText("comfort life, need party!");
                else if ( tempFloat > 26 && tempFloat <= 32 )
                    txtTemperatureFeel.setText("time to read a book");
                else if ( tempFloat > 32 )
                    txtTemperatureFeel.setText("too hot, pool party is ready!");

                if ( humFloat < 40 )
                    txtHumidityFeel.setText("the air humidity is too low");
                else if ( humFloat >= 40 && humFloat <= 70 )
                    txtHumidityFeel.setText("ideal humidity");
                else if ( humFloat > 70 )
                    txtHumidityFeel.setText("the air humidity is too high");


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        mDeviceReference.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String status_light_bulb        = snapshot.child("light_bulb").getValue().toString();
                String status_air_conditioner   = snapshot.child("air_conditioner").getValue().toString();
                String status_fan               = snapshot.child("fan").getValue().toString();
                String status_light             = snapshot.child("light").getValue().toString();
                String status_outlet            = snapshot.child("outlet").getValue().toString();
                String status_top_fan           = snapshot.child("top_fan").getValue().toString();
                String status_top_light         = snapshot.child("top_light").getValue().toString();
                String status_top_light_bulb    = snapshot.child("top_light_bulb").getValue().toString();
                String status_device_1          = snapshot.child("device_1").getValue().toString();
                String status_device_2          = snapshot.child("device_2").getValue().toString();
                String status_device_3          = snapshot.child("device_3").getValue().toString();
                String status_device_4          = snapshot.child("device_4").getValue().toString();
                String status_device_5          = snapshot.child("device_5").getValue().toString();
                String status_device_6          = snapshot.child("device_6").getValue().toString();
                String status_device_7          = snapshot.child("device_7").getValue().toString();
                String status_device_8          = snapshot.child("device_8").getValue().toString();

                light_bulb          = Integer.parseInt(status_light_bulb);
                air_conditioner     = Integer.parseInt(status_air_conditioner);
                fan                 = Integer.parseInt(status_fan);
                light               = Integer.parseInt(status_light);
                outlet              = Integer.parseInt(status_outlet);
                top_fan             = Integer.parseInt(status_top_fan);
                top_light           = Integer.parseInt(status_top_light);
                top_light_bulb      = Integer.parseInt(status_top_light_bulb);
                device_1            = Integer.parseInt(status_device_1);
                device_2            = Integer.parseInt(status_device_2);
                device_3            = Integer.parseInt(status_device_3);
                device_4            = Integer.parseInt(status_device_4);
                device_5            = Integer.parseInt(status_device_5);
                device_6            = Integer.parseInt(status_device_6);
                device_7            = Integer.parseInt(status_device_7);
                device_8            = Integer.parseInt(status_device_8);

                checkButtonState();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        btnDevice_1.setOnClickListener(this);
        btnDevice_2.setOnClickListener(this);
        btnDevice_3.setOnClickListener(this);
        btnDevice_4.setOnClickListener(this);
        btnDevice_5.setOnClickListener(this);
        btnDevice_6.setOnClickListener(this);
        btnDevice_7.setOnClickListener(this);
        btnDevice_8.setOnClickListener(this);
        btnDevice_9.setOnClickListener(this);
        btnDevice_10.setOnClickListener(this);
        btnDevice_11.setOnClickListener(this);
        btnDevice_12.setOnClickListener(this);
        btnDevice_13.setOnClickListener(this);
        btnDevice_14.setOnClickListener(this);
        btnDevice_15.setOnClickListener(this);
        btnDevice_16.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch ( view.getId() ) {
            case R.id.btnDevice_1:
                if (light_bulb == 0)
                    mDeviceReference.child("light_bulb").setValue(VALUE_ON);
                else
                    mDeviceReference.child("light_bulb").setValue(VALUE_OFF);
                break;
            case R.id.btnDevice_2:
                if (top_light == 0)
                    mDeviceReference.child("top_light").setValue(VALUE_ON);
                else
                    mDeviceReference.child("top_light").setValue(VALUE_OFF);
                break;
            case R.id.btnDevice_3:
                if (light == 0)
                    mDeviceReference.child("light").setValue(VALUE_ON);
                else
                    mDeviceReference.child("light").setValue(VALUE_OFF);
                break;
            case R.id.btnDevice_4:
                if (top_light_bulb == 0)
                    mDeviceReference.child("top_light_bulb").setValue(VALUE_ON);
                else
                    mDeviceReference.child("top_light_bulb").setValue(VALUE_OFF);
                break;
            case R.id.btnDevice_5:
                if (fan == 0)
                    mDeviceReference.child("fan").setValue(VALUE_ON);
                else
                    mDeviceReference.child("fan").setValue(VALUE_OFF);
                break;
            case R.id.btnDevice_6:
                if (top_fan == 0)
                    mDeviceReference.child("top_fan").setValue(VALUE_ON);
                else
                    mDeviceReference.child("top_fan").setValue(VALUE_OFF);
                break;
            case R.id.btnDevice_7:
                if (air_conditioner == 0)
                    mDeviceReference.child("air_conditioner").setValue(VALUE_ON);
                else
                    mDeviceReference.child("air_conditioner").setValue(VALUE_OFF);
                break;
            case R.id.btnDevice_8:
                if (outlet == 0)
                    mDeviceReference.child("outlet").setValue(VALUE_ON);
                else
                    mDeviceReference.child("outlet").setValue(VALUE_OFF);
                break;
            case R.id.btnDevice_9:
                if (device_1 == 0)
                    mDeviceReference.child("device_1").setValue(VALUE_ON);
                else
                    mDeviceReference.child("device_1").setValue(VALUE_OFF);
                break;
            case R.id.btnDevice_10:
                if (device_2 == 0)
                    mDeviceReference.child("device_2").setValue(VALUE_ON);
                else
                    mDeviceReference.child("device_2").setValue(VALUE_OFF);
                break;
            case R.id.btnDevice_11:
                if (device_3 == 0)
                    mDeviceReference.child("device_3").setValue(VALUE_ON);
                else
                    mDeviceReference.child("device_3").setValue(VALUE_OFF);
                break;
            case R.id.btnDevice_12:
                if (device_4 == 0)
                    mDeviceReference.child("device_4").setValue(VALUE_ON);
                else
                    mDeviceReference.child("device_4").setValue(VALUE_OFF);
                break;
            case R.id.btnDevice_13:
                if (device_5 == 0)
                    mDeviceReference.child("device_5").setValue(VALUE_ON);
                else
                    mDeviceReference.child("device_5").setValue(VALUE_OFF);
                break;
            case R.id.btnDevice_14:
                if (device_6 == 0)
                    mDeviceReference.child("device_6").setValue(VALUE_ON);
                else
                    mDeviceReference.child("device_6").setValue(VALUE_OFF);
                break;
            case R.id.btnDevice_15:
                if (device_7 == 0)
                    mDeviceReference.child("device_7").setValue(VALUE_ON);
                else
                    mDeviceReference.child("device_7").setValue(VALUE_OFF);
                break;
            case R.id.btnDevice_16:
                if (device_8 == 0)
                    mDeviceReference.child("device_8").setValue(VALUE_ON);
                else
                    mDeviceReference.child("device_8").setValue(VALUE_OFF);
                break;

        }

    }

    public void checkButtonState() {
        if ( getActivity() != null ) {
            if (light_bulb == 1)
                btnDevice_1.setCardBackgroundColor(getActivity().getResources().getColor(R.color.light_card_on));
            if (top_light == 1)
                btnDevice_2.setCardBackgroundColor(getActivity().getResources().getColor(R.color.light_card_on));
            if (light == 1)
                btnDevice_3.setCardBackgroundColor(getActivity().getResources().getColor(R.color.light_card_on));
            if (top_light_bulb == 1)
                btnDevice_4.setCardBackgroundColor(getActivity().getResources().getColor(R.color.light_card_on));
            if (fan == 1)
                btnDevice_5.setCardBackgroundColor(getActivity().getResources().getColor(R.color.light_card_on));
            if (top_fan == 1)
                btnDevice_6.setCardBackgroundColor(getActivity().getResources().getColor(R.color.light_card_on));
            if (air_conditioner == 1)
                btnDevice_7.setCardBackgroundColor(getActivity().getResources().getColor(R.color.light_card_on));
            if (outlet == 1)
                btnDevice_8.setCardBackgroundColor(getActivity().getResources().getColor(R.color.light_card_on));
            if (device_1 == 1)
                btnDevice_9.setCardBackgroundColor(getActivity().getResources().getColor(R.color.light_card_on));
            if (device_2 == 1)
                btnDevice_10.setCardBackgroundColor(getActivity().getResources().getColor(R.color.light_card_on));
            if (device_3 == 1)
                btnDevice_11.setCardBackgroundColor(getActivity().getResources().getColor(R.color.light_card_on));
            if (device_4 == 1)
                btnDevice_12.setCardBackgroundColor(getActivity().getResources().getColor(R.color.light_card_on));
            if (device_5 == 1)
                btnDevice_13.setCardBackgroundColor(getActivity().getResources().getColor(R.color.light_card_on));
            if (device_6 == 1)
                btnDevice_14.setCardBackgroundColor(getActivity().getResources().getColor(R.color.light_card_on));
            if (device_7 == 1)
                btnDevice_15.setCardBackgroundColor(getActivity().getResources().getColor(R.color.light_card_on));
            if (device_8 == 1)
                btnDevice_16.setCardBackgroundColor(getActivity().getResources().getColor(R.color.light_card_on));

            if (light_bulb == 0)
                btnDevice_1.setCardBackgroundColor(getActivity().getResources().getColor(R.color.light_card));
            if (top_light == 0)
                btnDevice_2.setCardBackgroundColor(getActivity().getResources().getColor(R.color.light_card));
            if (light == 0)
                btnDevice_3.setCardBackgroundColor(getActivity().getResources().getColor(R.color.light_card));
            if (top_light_bulb == 0)
                btnDevice_4.setCardBackgroundColor(getActivity().getResources().getColor(R.color.light_card));
            if (fan == 0)
                btnDevice_5.setCardBackgroundColor(getActivity().getResources().getColor(R.color.light_card));
            if (top_fan == 0)
                btnDevice_6.setCardBackgroundColor(getActivity().getResources().getColor(R.color.light_card));
            if (air_conditioner == 0)
                btnDevice_7.setCardBackgroundColor(getActivity().getResources().getColor(R.color.light_card));
            if (outlet == 0)
                btnDevice_8.setCardBackgroundColor(getActivity().getResources().getColor(R.color.light_card));
            if (device_1 == 0)
                btnDevice_9.setCardBackgroundColor(getActivity().getResources().getColor(R.color.light_card));
            if (device_2 == 0)
                btnDevice_10.setCardBackgroundColor(getActivity().getResources().getColor(R.color.light_card));
            if (device_3 == 0)
                btnDevice_11.setCardBackgroundColor(getActivity().getResources().getColor(R.color.light_card));
            if (device_4 == 0)
                btnDevice_12.setCardBackgroundColor(getActivity().getResources().getColor(R.color.light_card));
            if (device_5 == 0)
                btnDevice_13.setCardBackgroundColor(getActivity().getResources().getColor(R.color.light_card));
            if (device_6 == 0)
                btnDevice_14.setCardBackgroundColor(getActivity().getResources().getColor(R.color.light_card));
            if (device_7 == 0)
                btnDevice_15.setCardBackgroundColor(getActivity().getResources().getColor(R.color.light_card));
            if (device_8 == 0)
                btnDevice_16.setCardBackgroundColor(getActivity().getResources().getColor(R.color.light_card));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}