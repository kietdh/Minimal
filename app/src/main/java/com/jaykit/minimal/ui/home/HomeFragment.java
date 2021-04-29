package com.jaykit.minimal.ui.home;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
import com.jaykit.minimal.R;

import java.util.Calendar;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private FirebaseAuth mAuth;
    FirebaseUser user;
    DatabaseReference databaseReference;
    SharedPreferences sharedPref;

    //Declare Palette.
    View view;
    TextView username;
    TextView welcome;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        view = inflater.inflate(R.layout.fragment_home, container, false);
        welcome = view.findViewById(R.id.txtWelcome);
        username = view.findViewById(R.id.txtUsername);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                //
            }
        });

        setWelcomeTitle();
        setUsername();
    }

    private void setUsername() {
        user = FirebaseAuth.getInstance().getCurrentUser();
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
}