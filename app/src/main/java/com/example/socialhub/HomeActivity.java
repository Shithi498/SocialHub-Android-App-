package com.example.socialhub;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.socialhub.Adapter.PostAdapter;
import com.example.socialhub.Model.PostModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {
    RecyclerView  postRv;


    Button back;

    ArrayList<PostModel> list2;
    FirebaseAuth auth;
    FirebaseDatabase database;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        back=findViewById(R.id.back);
        auth= FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();



        postRv = findViewById(R.id.postRv);
        list2 = new ArrayList<>();

        PostAdapter adapter2 = new PostAdapter(list2, getBaseContext());
        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(getBaseContext(), LinearLayoutManager.VERTICAL, false);
        postRv.setLayoutManager(linearLayoutManager2);
        postRv.setNestedScrollingEnabled(true);
        postRv.setAdapter(adapter2);

        database.getReference().child("post").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list2.clear();
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    PostModel post = dataSnapshot.getValue(PostModel.class);
                    post.setPostId(dataSnapshot.getKey());
                    list2.add(post);
                }
                adapter2.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this,MainActivity.class));
            }
        });

    }
}



