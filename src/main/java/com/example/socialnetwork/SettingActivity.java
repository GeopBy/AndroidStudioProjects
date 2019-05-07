package com.example.socialnetwork;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import androidx.appcompat.widget.Toolbar;
import de.hdodenhof.circleimageview.CircleImageView;


public class SettingActivity extends AppCompatActivity {
    private Toolbar mToolbar;

    private EditText userName, userProfName,userStatus,userCountry,userGender,userRelation,userDOB;
    private Button UpdateAccountSettingButton;
    private CircleImageView userProfImage;

    private DatabaseReference SettinguserRef;
    private FirebaseAuth mAuth;
    private String currentUserId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        mAuth=FirebaseAuth.getInstance();
        currentUserId=mAuth.getCurrentUser().getUid();
        SettinguserRef= FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId);

        mToolbar=findViewById(R.id.setting_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Account Setting");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        userProfImage=findViewById(R.id.settings_profile_image);
        userName=findViewById(R.id.settings_username);
        userProfName=findViewById(R.id.settings_profile_full_name);
        userStatus=findViewById(R.id.settings_status);
        userCountry=findViewById(R.id.settings_country);
        userGender=findViewById(R.id.settings_gender);
        userRelation=findViewById(R.id.settings_relationship_status);
        userDOB=findViewById(R.id.settings_dob);
        UpdateAccountSettingButton=findViewById(R.id.update_account_settings_button);

        SettinguserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String myProfileImage=dataSnapshot.child("profileimage").getValue().toString();
                String myUserName=dataSnapshot.child("username").getValue().toString();
                String myProfileName=dataSnapshot.child("fullname").getValue().toString();
                String myProfileStatus=dataSnapshot.child("status").getValue().toString();
                String myDOB=dataSnapshot.child("dob").getValue().toString();
                String myCountry=dataSnapshot.child("country").getValue().toString();
                String myGender=dataSnapshot.child("gender").getValue().toString();
                String myRelationStatus=dataSnapshot.child("relationshipstatus").getValue().toString();

                Picasso.get().load(myProfileImage).placeholder(R.drawable.profile).into(userProfImage);

                userName.setText(myUserName);
                userProfName.setText(myProfileName);
                userStatus.setText(myProfileStatus);
                userDOB.setText(myDOB);
                userCountry.setText(myCountry);
                userGender.setText(myGender);
                userRelation.setText(myRelationStatus);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
