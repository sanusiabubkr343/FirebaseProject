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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CreateSignUp extends AppCompatActivity {

    @BindView(R.id.progressBar2)
    ProgressBar progressBar2;
    @BindView(R.id.usernameInput)
    AutoCompleteTextView usernameInput;
    @BindView(R.id.emailInputText)
    AutoCompleteTextView emailInputText;
    @BindView(R.id.passwordTextInput)
    AutoCompleteTextView passwordTextInput;
    @BindView(R.id.createButton)
    Button createButton;
    private FirebaseAuth mAuth ;
   FirebaseFirestore db = FirebaseFirestore.getInstance();
   private CollectionReference Users =  db.collection("Users");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_sign_up);
        ButterKnife.bind(this);
        mAuth = FirebaseAuth.getInstance();
        progressBar2.setVisibility(View.INVISIBLE);

    }

    @OnClick(R.id.createButton)
    public void onViewClicked() {
           String username  = usernameInput.getText().toString().trim();
           String email  = emailInputText.getText().toString().trim();
           String password  = passwordTextInput.getText().toString().trim();
           if(!email.isEmpty() && !username.isEmpty() && !password.isEmpty())
           {
               SignUp(username,email,password);
           }
           else
           {
               Toast.makeText(CreateSignUp.this,"Empty Field not required",Toast.LENGTH_LONG).show();
           }

    }

    private void SignUp(String username, String email, String password) {

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            progressBar2.setVisibility(View.VISIBLE);
                            Toast.makeText(CreateSignUp.this, "Registration Successful.",
                                    Toast.LENGTH_SHORT).show();
                            FirebaseUser user = mAuth.getCurrentUser();
                            Map<String,String> data = new HashMap<>();
                            data.put("username",username);
                            assert user != null;
                            data.put("userId",user.getUid());
                            Users.add(data).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) { /// then get the uploaded data from the server
                                    documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                               final String username = Objects.requireNonNull(task.getResult()).getString("username");
                                               final String userId = task.getResult().getString("userId");

                                            JournalApi api =  JournalApi.getInstance();
                                            api.setUsername(username);
                                            api.setUserId(userId);

                                            Log.d("myTAG", "onComplete: "+"userName: "+ api.getUsername() +","
                                            + "userId :"+ api.getUserId()
                                            );

                                        }
                                    });
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                }
                            });
                            // make it retrur to login page the automatically sends it to next activity
                            startActivity(new Intent(CreateSignUp.this,LoginPage.class));


                        } else {
                            // If sign in fails, display a message to the user.

                            Toast.makeText(CreateSignUp.this, "Authentication failed."+ task.getException(),
                                    Toast.LENGTH_LONG).show();
                            Log.d("innerTag", "onComplete: "+task.getException());

                        }

                        // ...
                    }
                });
    }
}
