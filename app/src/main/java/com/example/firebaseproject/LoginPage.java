package com.example.firebaseproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.firebaseproject.util.JournalApi;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginPage extends AppCompatActivity {

    @BindView(R.id.emailInput)
    AutoCompleteTextView emailInput;
    @BindView(R.id.passwordInput)
    AutoCompleteTextView passwordInput;
    @BindView(R.id.loginButton)
    Button loginButton;
    @BindView(R.id.createAccountButton)
    Button button2;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseFirestore db =  FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("Users");
    private  String username;
    private String  userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);
        ButterKnife.bind(this);
        mAuth = FirebaseAuth.getInstance();
        progressBar.setVisibility(View.INVISIBLE);
        ;

    }

    @OnClick({R.id.loginButton, R.id.createAccountButton})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.loginButton:
                String  email = emailInput.getText().toString().trim();
                String   password = passwordInput.getText().toString().trim();
                if(!email.isEmpty() && !password.isEmpty())
                {
                    signIn(email,password);
                }
                else
                {
                    Toast.makeText(LoginPage.this,"Empty Field not Required",Toast.LENGTH_LONG).show();
                }

                break;
            case R.id.createAccountButton:
                startActivity(new Intent(LoginPage.this,CreateSignUp.class));

                break;
        }
    }

    private void signIn(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("inTAG", "signInWithEmail:success");
                            Toast.makeText(LoginPage.this, "Authentication Successful.",
                                    Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.VISIBLE);
                            FirebaseUser user = mAuth.getCurrentUser();
                            assert user != null;
                            String currentUser = user.getUid();
                            Toast.makeText(LoginPage.this,"Current UserId :"+currentUser ,Toast.LENGTH_LONG).show();
                            collectionReference.whereEqualTo("userId",currentUser).addSnapshotListener(new EventListener<QuerySnapshot>() {
                                @Override
                                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                  for(QueryDocumentSnapshot snapshot :value)
                                  {
                                     username = snapshot.getString("username");
                                     userId  = snapshot.getString("userId");
                                      JournalApi.getInstance().setUsername(username);
                                      JournalApi.getInstance().setUserId(userId);
                                  }
                                }
                            });



                            startActivity (new Intent (LoginPage.this,postJournalActivity.class));
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("errInTAG", "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginPage.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            Toast.makeText(LoginPage.this, Objects.requireNonNull(task.getException()).toString(),
                                    Toast.LENGTH_SHORT).show();


                        }

                        // ...
                    }
                });
    }


    }

