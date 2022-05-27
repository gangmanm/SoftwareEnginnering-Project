package com.example.commento;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class CheckMyPost extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList<NewPost> newPostArrayList;
    MyAdapter myAdapter;
    FirebaseFirestore db;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String userUid = user.getUid();
    TextView textUserUid;
    ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_my_post);


        recyclerView = findViewById(R.id.noteRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        textUserUid = findViewById(R.id.userUid);
        mProgressBar = findViewById(R.id.progressBar);
        mProgressBar.setIndeterminate(false);
        textUserUid.setText(userUid);

        db = FirebaseFirestore.getInstance();
        newPostArrayList = new ArrayList<NewPost>();
        myAdapter = new MyAdapter(CheckMyPost.this,newPostArrayList);
        recyclerView.setAdapter(myAdapter);


        EventChangeListener();
        ImageView imageBack=findViewById(R.id.imageBack);
        imageBack.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v)
            {
                startActivityForResult(new Intent(getApplicationContext(), com.example.commento.PostList.class),
                        1);
            }
        });


    }
    private void EventChangeListener() {
        db.collection("NewPost").whereEqualTo("uid",user.getUid())
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if(error != null)
                        {
                            Log.e("Firestore error",error.getMessage());
                            return;
                        }
                        for (DocumentChange dc : value.getDocumentChanges())
                        {
                            if(dc.getType() == DocumentChange.Type.ADDED)
                            {
                                newPostArrayList.add(dc.getDocument().toObject(NewPost.class));
                                mProgressBar.setProgress(newPostArrayList.size(),true);

                            }
                            myAdapter.notifyDataSetChanged();
                        }
                    }
                });
    }
}