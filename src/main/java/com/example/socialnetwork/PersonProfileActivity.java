package com.example.socialnetwork;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import de.hdodenhof.circleimageview.CircleImageView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;

public class PersonProfileActivity extends AppCompatActivity {

    private TextView userName, userProfName,userStatus,userCountry,userGender,userRelation,userDOB;
    private CircleImageView userProfileImage;

    private Button SendFriendReqButton,DeclineFriendReqButton;

    private DatabaseReference FriendRequestRef,UsersRef;
    private FirebaseAuth mAuth;

    private String senderUserId,receiverUserId,CURRENT_STATE;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_profile);

        mAuth=FirebaseAuth.getInstance();
        senderUserId=mAuth.getCurrentUser().getUid();
        receiverUserId=getIntent().getExtras().get("visit_user_id").toString();

        UsersRef= FirebaseDatabase.getInstance().getReference().child("Users");
        FriendRequestRef= FirebaseDatabase.getInstance().getReference().child("FriendRequests");

        IntializeFields();
        //UsersRef.child(receiverUserId);
        UsersRef.child(receiverUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String myProfileImage = dataSnapshot.child("profileimage").getValue().toString();
                    String myUserName = dataSnapshot.child("username").getValue().toString();
                    String myProfileName = dataSnapshot.child("fullname").getValue().toString();
                    String myProfileStatus = dataSnapshot.child("status").getValue().toString();
                    String myDOB = dataSnapshot.child("dob").getValue().toString();
                    String myCountry = dataSnapshot.child("country").getValue().toString();
                    String myGender = dataSnapshot.child("gender").getValue().toString();
                    String myRelationStatus = dataSnapshot.child("relationshipstatus").getValue().toString();

                    Picasso.get().load(myProfileImage).placeholder(R.drawable.profile).into(userProfileImage);

                    userName.setText("@" + myUserName);
                    userProfName.setText(myProfileName);
                    userStatus.setText(myProfileStatus);
                    userDOB.setText("DOB: " + myDOB);
                    userCountry.setText("Country: " + myCountry);
                    userGender.setText("Gender: " + myGender);
                    userRelation.setText("RelationShip: " + myRelationStatus);
                    MaintananceofButton();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        // xác nhận để enable button lời mời kết bạn
        DeclineFriendReqButton.setVisibility(View.INVISIBLE);
        DeclineFriendReqButton.setEnabled(false);
        if(!senderUserId.equals(receiverUserId)){
            SendFriendReqButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                     SendFriendReqButton.setEnabled(false);
                     if(CURRENT_STATE.equals("not_friends")){
                         SendFriendRequestToaPerson();
                     }
                }
            });
        }
        else{
            DeclineFriendReqButton.setVisibility(View.INVISIBLE);
            SendFriendReqButton.setVisibility(View.INVISIBLE);
        }

    }

    private void MaintananceofButton() {
        FriendRequestRef.child(senderUserId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild(receiverUserId))
                        {

                            String request_type=dataSnapshot.child(receiverUserId)
                                    .child("request_type").getValue().toString();

                            if(request_type.equals("sent"))
                            {
                                CURRENT_STATE="request_sent";
                                SendFriendReqButton.setText("Cancel Friend Request");
                                DeclineFriendReqButton.setVisibility(View.INVISIBLE);
                                DeclineFriendReqButton.setEnabled(false);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void SendFriendRequestToaPerson() {
        FriendRequestRef.child(senderUserId).child(receiverUserId)
                .child("request_type").setValue("sent")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            FriendRequestRef.child(receiverUserId).child(senderUserId)
                                    .child("request_type").setValue("received")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                SendFriendReqButton.setEnabled((true));
                                                CURRENT_STATE="request_sent";

                                                SendFriendReqButton.setText("Cancle friend Request");

                                                DeclineFriendReqButton.setVisibility(View.INVISIBLE);
                                                DeclineFriendReqButton.setEnabled(false);
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void IntializeFields() {
        userProfileImage=findViewById(R.id.person_profile_pic);
        userName=findViewById(R.id.person_username);
        userProfName=findViewById(R.id.person_full_name);
        userStatus=findViewById(R.id.person_profile_status);
        userCountry=findViewById(R.id.person_country);
        userGender=findViewById(R.id.person_gender);
        userRelation=findViewById(R.id.person_relationship_status);
        userDOB=findViewById(R.id.person_dob);

        SendFriendReqButton=findViewById(R.id.person_send_friend_request_btn);
        DeclineFriendReqButton=findViewById(R.id.person_decline_friend_request_btn);

        CURRENT_STATE="not_friends";
    }
}
