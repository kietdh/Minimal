package com.jaykit.minimal.ui.user;

import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jaykit.minimal.ChangePassword;
import com.jaykit.minimal.LoginActivity;
import com.jaykit.minimal.R;

import org.jetbrains.annotations.NotNull;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static androidx.core.content.ContextCompat.getSystemService;

public class UserFragment extends Fragment implements View.OnClickListener {

    private UserViewModel mViewModel;
    FirebaseUser user;
    DatabaseReference databaseReference;

    ImageView   btnLogout;
    TextView    txtName;
    TextView    txtEmail;
    TextView   btnChangePwd;
    View view;

    public static UserFragment newInstance() {
        return new UserFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_user, container, false);
        btnLogout   = view.findViewById(R.id.btnLogout);
        txtName     = view.findViewById(R.id.txtName);
        txtEmail    = view.findViewById(R.id.txtEmail);
        btnChangePwd = view.findViewById(R.id.btnChangePwd);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        btnLogout.setOnClickListener(this);
        btnChangePwd.setOnClickListener(this);
        setUsername();
        setEmail();
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        // TODO: Use the ViewModel
    }

    @Override
    public void onClick(View view) {
        switch ( view.getId() ) {
            case R.id.btnLogout:
                logout();
                break;
            case R.id.btnChangePwd:
                changePassword(view);
                break;
        }
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        getActivity().finish();
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);


    }

    private void changePassword(View view) {
        startActivity( new Intent(getActivity(), ChangePassword.class));
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
                    txtName.setText(user_name);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.d("ERROR: ", "Cannot get data from real time database.");
                }
            });
        }
    }

    private void setEmail() {
        user = FirebaseAuth.getInstance().getCurrentUser();

        if ( user == null )
            startActivity(new Intent(getActivity(), LoginActivity.class));
        else {
            String uid = user.getUid();
            databaseReference = FirebaseDatabase.getInstance().getReference();

            databaseReference.addValueEventListener(new ValueEventListener() {

                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                    String email = snapshot.child("User").child(uid).child("email").getValue(String.class);
                    txtEmail.setText(email);
                }

                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error) {
                    Log.d("ERROR: ", "Cannot get data from real time database.");
                }
            });
        }
    }
}