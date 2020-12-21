package com.example.firebaseproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.firebaseproject.util.JournalApi;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.getStatted)
    TextView getStatted;
    private  FirebaseAuth mAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference collectionReference = db.collection("Users");




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mAuth = FirebaseAuth.getInstance();
        Objects.requireNonNull(getSupportActionBar()).setElevation(0);
    }

    @OnClick(R.id.getStatted)
    public void onViewClicked() {

        FirebaseUser  user = mAuth.getCurrentUser();
        if(user != null)
        {

            String userId = user.getUid();
            collectionReference.whereEqualTo("userId",userId).
                    addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                            for(QueryDocumentSnapshot snapshot :value)
                            {
                                String  username = snapshot.getString("username");
                                String  userId  = snapshot.getString("userId");
                                JournalApi.getInstance().setUsername(username);
                                JournalApi.getInstance().setUserId(userId);
                            }
                            startActivity(new Intent(MainActivity.this,postJournalActivity.class));
                        }
                    });
        }
        else
        {

            startActivity(new Intent(MainActivity.this,LoginPage.class));
        }

    }

}

