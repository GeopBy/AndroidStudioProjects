package com.example.socialnetwork;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class MyPostsActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private RecyclerView myPostList;
    private FirebaseAuth mAuth;
    private DatabaseReference PostsRef,UsersRef,LikesRef;
    String currentUserId;
    Boolean LikeChecker=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_posts);

        mAuth=FirebaseAuth.getInstance();
        currentUserId=mAuth.getCurrentUser().getUid();
        PostsRef= FirebaseDatabase.getInstance().getReference().child("Posts");
        UsersRef= FirebaseDatabase.getInstance().getReference().child("Users");
        LikesRef= FirebaseDatabase.getInstance().getReference().child("Likes");

        mToolbar =  findViewById(R.id.my_posts_bar_layout);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("My Posts");

        myPostList=findViewById(R.id.my_all_posts_list);
        myPostList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        myPostList.setLayoutManager(linearLayoutManager);
        
        DisplayMyAllPost();
    }

    private void DisplayMyAllPost() {
        Query myPostsQuery=PostsRef.orderByChild("uid")
                .startAt(currentUserId).endAt(currentUserId+"\uf8ff");
        FirebaseRecyclerOptions<Posts> options = new FirebaseRecyclerOptions.Builder<Posts>().setQuery(myPostsQuery, Posts.class).build();
        FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<Posts, MyPostsViewHolder>(options){
                    @Override
                    protected void onBindViewHolder(@NonNull MyPostsViewHolder myPostsViewHolder, int i, @NonNull Posts posts) {
                        final String PostKey=getRef(i).getKey();
                        myPostsViewHolder.setFullname(posts.getFullname());
                        myPostsViewHolder.setDescription(posts.getDescription());
                        myPostsViewHolder.setProfileImage(getApplicationContext(),posts.getProfileimage());
                        myPostsViewHolder.setPostImage(getApplicationContext(),posts.getPostimage());
                        myPostsViewHolder.setDate(posts.getDate());
                        myPostsViewHolder.SetTime(posts.getTime());
                        myPostsViewHolder.setLikeButtonStatus(PostKey);
                        myPostsViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent clickPostIntent=new Intent(MyPostsActivity.this,ClickPostActivity.class);
                                clickPostIntent.putExtra("PostKey",PostKey);
                                startActivity(clickPostIntent);
                            }
                        });
                        myPostsViewHolder.CommentPostButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent commentsIntent=new Intent(MyPostsActivity.this,CommentsActivity.class);
                                commentsIntent.putExtra("PostKey",PostKey);
                                startActivity(commentsIntent);
                            }
                        });
                        myPostsViewHolder.LikePostButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                LikeChecker=true;
                                LikesRef.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if(LikeChecker.equals(true)){
                                            if(dataSnapshot.child(PostKey).hasChild(currentUserId)){
                                                LikesRef.child(PostKey).child(currentUserId).removeValue();
                                                LikeChecker=false;
                                            }
                                            else{
                                                LikesRef.child(PostKey).child(currentUserId).setValue(true);
                                                LikeChecker=false;
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }
                        });

                    }

                    @NonNull
                    @Override
                    public MyPostsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.all_posts_layout,parent,false);
                        return new MyPostsViewHolder(view);
                    }
                };
        adapter.startListening();
        myPostList.setAdapter(adapter);
    }
    private static class MyPostsViewHolder extends RecyclerView.ViewHolder{
        View mView;
        ImageButton LikePostButton,CommentPostButton;
        TextView DisplayNoOfLikes;
        int countLikes;
        String currentUserId;
        DatabaseReference LikesRef;

        public MyPostsViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            LikePostButton=mView.findViewById(R.id.like_button);
            CommentPostButton=mView.findViewById(R.id.comment_button);
            DisplayNoOfLikes=mView.findViewById(R.id.display_no_of_likes);
            LikesRef=FirebaseDatabase.getInstance().getReference().child("Likes");
            currentUserId=FirebaseAuth.getInstance().getCurrentUser().getUid();
        }
        public void setLikeButtonStatus(final String PostKey){
            LikesRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.child(PostKey).hasChild(currentUserId)){
                        countLikes=(int)dataSnapshot.child(PostKey).getChildrenCount();
                        LikePostButton.setImageResource(R.drawable.like);
                        DisplayNoOfLikes.setText((Integer.toString(countLikes)+" Likes"));
                    }
                    else{
                        countLikes=(int)dataSnapshot.child(PostKey).getChildrenCount();
                        LikePostButton.setImageResource(R.drawable.dislike);
                        DisplayNoOfLikes.setText((Integer.toString(countLikes)+" Likes"));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        public void setFullname (String fullname){
            TextView username = (TextView) mView.findViewById(R.id.post_user_name);
            username.setText(fullname);
        }

        public void setProfileImage (Context ctx, String profileimage){
            CircleImageView image = (CircleImageView) mView.findViewById(R.id.post_profile_image);
            Picasso.get().load(profileimage).into(image);
        }

        public void SetTime (String time){
            TextView PostTime = (TextView) mView.findViewById(R.id.post_time);
            PostTime.setText("     "+time);
        }

        public void setDate (String date){
            TextView postDate = (TextView) mView.findViewById(R.id.post_date);
            postDate.setText("     "+date);
        }

        public void setDescription (String description){
            TextView postDescription = (TextView) mView.findViewById(R.id.post_description);
            postDescription.setText(description);
        }

        public void setPostImage (Context ctx1, String postImage){
            ImageView postImages = (ImageView) mView.findViewById(R.id.post_image);
            Picasso.get().load(postImage).into(postImages);
        }
    }
}
