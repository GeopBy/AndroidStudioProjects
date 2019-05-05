package com.example.socialnetwork;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ClickPostActivity extends AppCompatActivity {

    private ImageView PostImage;
    private TextView PostDescription;
    private Button DeletePostButton,EditPostButton;
    private DatabaseReference clickPostRef;

    private String PostKey;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_click_post);

        PostKey=getIntent().getExtras().get("PostKey").toString();
        clickPostRef= FirebaseDatabase.getInstance().getReference().child("Posts").child(PostKey);
        PostImage=findViewById(R.id.click_post_image);
        PostDescription=findViewById(R.id.click_post_description);
        DeletePostButton=findViewById(R.id.delete_post_button);
        EditPostButton=findViewById(R.id.edit_post_button);

        clickPostRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String description=dataSnapshot.child("description").getValue().toString();
                String image=dataSnapshot.child("postimage").getValue().toString();

                PostDescription.setText(description);
                Picasso.get().load(image).into(PostImage);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
