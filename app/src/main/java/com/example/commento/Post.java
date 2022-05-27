package com.example.commento;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;

public class Post extends AppCompatActivity {
EditText post_title,post_subtitle,post_content,highlight;
com.example.commento.NewPost newpost;
private TextView mtags;
private AlertDialog mtageSelected;
FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        RadioGroup radtag = findViewById(R.id.tagGroup);
        RadioButton health = findViewById(R.id.tag_health);
        RadioButton godlife = findViewById(R.id.tag_godlife);
        RadioButton skincare = findViewById(R.id.tag_skincare);
        RadioButton study = findViewById(R.id.tag_study);
        post_title = (EditText)findViewById(R.id.inputNoteTitle);
        post_subtitle = (EditText)findViewById(R.id.inputNoteSubtitle);
        post_content = (EditText)findViewById(R.id.content);


        //Pring New Post Class
        newpost = new com.example.commento.NewPost();
        DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
         DatabaseReference conditionRef = mRootRef.child("txt");

        ImageView imageBack=findViewById(R.id.imageBack);
        imageBack.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v)
            {
                onBackPressed();
            }
        });



        EditText NoteTitle  = (EditText)findViewById(R.id.inputNoteTitle);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        ImageView imageSave = findViewById(R.id.imageSave);
        imageSave.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
               newpost.setTitle(post_title.getText().toString());
               newpost.setSubtitle(post_subtitle.getText().toString());
               newpost.setContent(post_content.getText().toString());
               newpost.setTime(Calendar.getInstance().getTime().toString());
               newpost.setUid(user.getUid());

               String mytag="";
               int radioId = radtag.getCheckedRadioButtonId();
               if(health.getId()==radioId)
                   mytag ="소설";
               if(study.getId()==radioId)
                   mytag="에세이";
               if(skincare.getId()==radioId)
                    mytag="수필";
               if(godlife.getId()==radioId)
                    mytag="시";



                newpost.setTag(mytag);

                db.collection("NewPost")
                        .add(newpost);

                Toast.makeText(Post.this,"data inserted succefully",Toast.LENGTH_LONG).show();

                startActivityForResult(new Intent(getApplicationContext(), com.example.commento.PostList.class),
                        1);
            }
        });
    }
}