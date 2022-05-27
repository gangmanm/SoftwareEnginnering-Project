package com.example.commento;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
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
import java.util.Arrays;
import java.util.List;

public class Post_Detail extends AppCompatActivity {

    public static String WriterUid;
    public static String Time;
    public static String highlight_list;
    public static ArrayList<String> highlight_hey;
    public String color[] = {"yellow","#c4e2f5","#74b55c","#de97d7","#d9de97"};
    public List<String> list;
    public static String contentGet;
    RecyclerView recyclerView;
    RecyclerView recyclerCommentView;
    ArrayList<com.example.commento.NewPost> newPostArrayList;
    ArrayList<com.example.commento.NewComment> newCommentArrayList;
    com.example.commento.MyAdapter myAdapter;
    com.example.commento.MyCommentAdapter myCommentAdapter;
    FirebaseFirestore db;
    com.example.commento.NewComment newComment;
    com.example.commento.NewPost newPost;
    EditText post_comment;
    EditText highlight;
    TextView contentPost;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    public static String title="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        /*
        recyclerView = findViewById(R.id.noteRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

         */





        recyclerCommentView = findViewById(R.id.commentRecyclerView);
        recyclerCommentView.setHasFixedSize(true);
        recyclerCommentView.setLayoutManager(new LinearLayoutManager(this));

        highlight = (EditText)findViewById(R.id.inputhighlight);


        highlight.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    if(highlight.length() < 8) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(Post_Detail.this);
                        builder.setMessage("하이라이트는 8자 이상이어야 합니다.");
                        builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                highlight.setText("");
                                dialog.dismiss();

                            }
                        });
                        builder.show();
                    }
                }
            }
        });
        post_comment = (EditText)findViewById(R.id.inputcomment);



        newComment = new com.example.commento.NewComment();
        db = FirebaseFirestore.getInstance();
        newPostArrayList = new ArrayList<com.example.commento.NewPost>();
        newCommentArrayList = new ArrayList<com.example.commento.NewComment>();
       // myAdapter = new com.example.commento.MyAdapter(Post_Detail.this,newPostArrayList);
        myCommentAdapter = new com.example.commento.MyCommentAdapter(Post_Detail.this,newCommentArrayList);
        myCommentAdapter = new com.example.commento.MyCommentAdapter(Post_Detail.this,newCommentArrayList);
        //recyclerView.setAdapter(myAdapter);
        recyclerCommentView.setAdapter(myCommentAdapter);



        ImageView imageBack=findViewById(R.id.imageBack);
        imageBack.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v)
            {
               // startActivityForResult(new Intent(getApplicationContext(),PostList.class),
                       // 1);
                onBackPressed();

            }
        });


        Button highlight_button = findViewById(R.id.ButtonHighlight);
        highlight_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HighlightText();
            }
        });


        ImageView imageAddComment=findViewById(R.id.add_comment);
        imageAddComment.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v)
            {

                if(post_comment == null)
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Post_Detail.this);
                    builder.setMessage("댓글을 입력하세요");
                    builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.show();
                }
                else {
                    newComment.setComment(post_comment.getText().toString());

                    newComment.setHighlight(highlight.getText().toString());

                    newComment.setUid(user.getUid());

                    newComment.setWriterUid(WriterUid);


                    db.collection("NewComment")
                            .add(newComment);


                    String modifiedText = "";
                    Toast.makeText(Post_Detail.this, "data inserted succefully", Toast.LENGTH_LONG).show();

                }
            }

        });



        System.out.println("This is content"+contentGet);


        contentPost = findViewById(R.id.inputContent2);
        contentPost.setText(contentGet);


        newCommentArrayList.clear();
        // EventChangeListener();
        EventCommentChangeListener();


    }


    public void HighlightText() {

        String modifiedText = "";

        contentPost = findViewById(R.id.inputContent2);
        contentPost.setText(contentGet);

        System.out.println(highlight_list);

        if (highlight_list != null) {
            list = new ArrayList<String>();

            String[] splitStr = highlight_list.split("#");
            for (int i = 0; i < splitStr.length; i++) {
                list.add(splitStr[i]);
            }

            for (int i = 0; i < list.size(); i++) {
                System.out.println("get this text" + list.get(i).toString());
            }
            modifiedText = contentGet;

            for (int i = 0; i < list.size(); i++) {
                String textToHighlight = list.get(i);
                System.out.println("Text to highlight" + textToHighlight);
                String replaceWith = "<span style ='background-color:"+color[i]+"'>" + textToHighlight + "</span>";

                modifiedText = modifiedText.replaceAll(textToHighlight, replaceWith);

            }
            contentPost.setText(Html.fromHtml(modifiedText));

        }

    }

    private void EventChangeListener() {
        db.collection("NewPost").whereEqualTo("title",title)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {

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

                                newPostArrayList.add(dc.getDocument().toObject(com.example.commento.NewPost.class));

                            }
                            myAdapter.notifyDataSetChanged();
                        }
                    }
                });
    }


    private void EventCommentChangeListener() {
        db.collection("NewComment").whereEqualTo("writerUid",WriterUid)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {


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
                                newCommentArrayList.add(dc.getDocument().toObject(com.example.commento.NewComment.class));
                            }
                            myCommentAdapter.notifyDataSetChanged();
                        }
                    }



                });



    }
}