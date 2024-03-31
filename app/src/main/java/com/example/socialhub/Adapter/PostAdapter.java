package com.example.socialhub.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialhub.Model.NotiModel;
import com.example.socialhub.Model.PostModel;
import com.example.socialhub.Model.UserModel;
import com.example.socialhub.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.viewHolder> {

   ArrayList<PostModel> list;
    Context context;


    public PostAdapter(ArrayList<PostModel> list, Context context) {

        this.list = list;
        this.context = context;

    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.post_rv, parent, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {


         PostModel model = list.get(position);
        if (model.getPostImage() != null && model.getWritePost()==null) {
            Picasso.get().load(model.getPostImage())
                    .into(holder.postImg);
            holder.postImg.setVisibility(View.VISIBLE);

            Log.d("PostAdapter", "Position: " + position + ", Post ID: " + model.getPostId());

        }
        else if(model.getPostImage()== null && model.getWritePost()!=null){

            holder.postImg.setVisibility(View.GONE);
            holder.likeText.setText(model.getPostLike() + "");

            holder.postDes.setText(model.getWritePost());
        }
        else if(model.getPostImage()!= null && model.getWritePost()!=null) {
            Picasso.get().load(model.getPostImage())
                    .into(holder.postImg);
            holder.postImg.setVisibility(View.VISIBLE);
            holder.likeText.setText(model.getPostLike() + "");

            holder.postDes.setText(model.getWritePost());
        }
        FirebaseDatabase.getInstance().getReference().child("User")
                .child(model.getPostedBy()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        UserModel user = snapshot.getValue(UserModel.class);
                        Picasso.get()
                                .load(user.getProfilePhoto())
                                .placeholder(R.drawable.woman)
                                .into(holder.FollowImg);


                        holder.name.setText(user.getName());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


        FirebaseDatabase.getInstance().getReference()
                .child("post")
                .child(model.getPostId())
                .child("like")
                .child(FirebaseAuth.getInstance().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {

                            holder.like.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    // Like the post
                                    Toast.makeText(context, "Already liked", Toast.LENGTH_LONG).show();
                                }
                            });


                        } else {

                            holder.like.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    // Like the post
                                    likePost(model, holder);
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void likePost(PostModel model, viewHolder holder) {
        FirebaseDatabase.getInstance().getReference()
                .child("post")
                .child(model.getPostId())
                .child("like")
                .child(FirebaseAuth.getInstance().getUid())
                .setValue(true)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        FirebaseDatabase.getInstance().getReference()
                                .child("post")
                                .child(model.getPostId())
                                .child("postLike")
                                .setValue(model.getPostLike() + 1)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(context, "Liked", Toast.LENGTH_SHORT).show();


                                        notifyUser(model);
                                    }
                                });
                    }
                });
    }

    private void notifyUser(PostModel model) {
        NotiModel noti = new NotiModel();
        noti.setNotiBy(FirebaseAuth.getInstance().getUid());
        noti.setPostId(model.getPostId());
        noti.setPostedBy(model.getPostedBy());
        noti.setType("like");

        FirebaseDatabase.getInstance().getReference()
                .child("notification")
                .child(model.getPostedBy())
                .push()
                .setValue(noti);
    }

    @Override
    public int getItemCount() {
        return list.size();

    }



    public class viewHolder extends RecyclerView.ViewHolder {
        ImageView postImg, FollowImg, like, comment;
        TextView name, postDes, likeText, commenttxt;

        public viewHolder(@NonNull View itemView) {
            super(itemView);

            postImg = itemView.findViewById(R.id.post);
            FollowImg = itemView.findViewById(R.id.FollowImg);
            name = itemView.findViewById(R.id.name);
            postDes = itemView.findViewById(R.id.postDes);
            like = itemView.findViewById(R.id.like);
            likeText = itemView.findViewById(R.id.likeText);

        }
    }
}
