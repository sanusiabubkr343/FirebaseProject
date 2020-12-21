package com.example.firebaseproject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.firebaseproject.util.JournalApi;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import net.time4j.android.ApplicationStarter;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class JournalActivity extends AppCompatActivity {

    @BindView(R.id.imageView)
    ImageView imageView;
    @BindView(R.id.selectImageButton)
    ImageView selectImageButton;
    @BindView(R.id.usernameText)
    TextView usernameText;
    @BindView(R.id.timestampText)
    TextView timestampText;
    @BindView(R.id.titleText2)
    EditText titleText;
    @BindView(R.id.thoughtText)
    EditText thoughtText;
    @BindView(R.id.saveButton)
    Button saveButton;
    @BindView(R.id.progressBar3)
    ProgressBar progressBar3;
    private  int GALLERY_CODE = 1;
     private    Uri imageUri ;
    private StorageReference mStorageRef;
   private   FirebaseFirestore db = FirebaseFirestore.getInstance();
   private CollectionReference collectionReference = db.collection("Journal");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journal);
        ButterKnife.bind(this);
        progressBar3.setVisibility(View.INVISIBLE);
        usernameText.setText(JournalApi.getInstance().getUsername());

        mStorageRef = FirebaseStorage.getInstance().getReference();
        ApplicationStarter.initialize(this, true);


    }

    @OnClick({R.id.selectImageButton, R.id.saveButton})
    public void onViewClicked(View view) {

        switch (view.getId()) {
            case R.id.selectImageButton:
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, GALLERY_CODE);
                break;
            case R.id.saveButton:
                String title = titleText.getText().toString().trim();
                String thought = thoughtText.getText().toString().trim();
                if(!title.isEmpty() && !thought.isEmpty()&& imageUri != null )
                {
                    save(title,thought,imageUri);
                }
                else
                {
                    Toast.makeText(this,"Emprty Field not required",Toast.LENGTH_LONG).show();
                }


                break;
        }
    }

    private void save(String title, String thought, Uri imageUri) {
        StorageReference filePath = mStorageRef.child("journal_images")
                .child("my_image"+Timestamp.now().getSeconds());
        filePath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @SuppressLint("SimpleDateFormat")
                    @Override
                    public void onSuccess(Uri uri) {
                          String  imageUrl  = uri.toString();
                          JournalModel model = new JournalModel();
                          model.setThought(thought);
                          model.setTitle(title);
                          model.setUserId(JournalApi.getInstance().getUserId());
                          model.setUserName(JournalApi.getInstance().getUsername());
                          model.setImageUrl(imageUrl);
                          collectionReference.add(model).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                              @Override
                              public void onSuccess(DocumentReference documentReference) {
                                   progressBar3.setVisibility(View.VISIBLE);
                                   startActivity(new Intent(JournalActivity.this,postJournalActivity.class));
                              }
                          }).addOnFailureListener(new OnFailureListener() {
                              @Override
                              public void onFailure(@NonNull Exception e) {
                                  Log.d("'errorMessage", "onFailure: "+e.getMessage());
                              }
                          });



                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GALLERY_CODE && resultCode ==RESULT_OK && data != null && data.getData() != null)
        {
            imageUri = data.getData();
            Picasso.get().load(imageUri).into(imageView);

        }
    }
}
