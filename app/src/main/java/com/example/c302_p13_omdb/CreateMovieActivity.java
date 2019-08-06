package com.example.c302_p13_omdb;


import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;

public class CreateMovieActivity extends AppCompatActivity {

    private EditText etTitle, etRated, etReleased, etRuntime, etGenre, etActors, etPlot, etLanguage, etPoster;
    private Button btnCreate, btnSearch;
    private ImageButton btnCamera;
    private String apikey;

    // Task 1 - Declare Firebase variables
    private FirebaseFirestore db;
    private CollectionReference colRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_movie);

        etTitle = findViewById(R.id.etTitle);
        etRated = findViewById(R.id.etRated);
        etReleased = findViewById(R.id.etReleased);
        etRuntime = findViewById(R.id.etRuntime);
        etGenre = findViewById(R.id.etGenre);
        etActors = findViewById(R.id.etActors);
        etPlot = findViewById(R.id.etPlot);
        etLanguage = findViewById(R.id.etLanguage);
        etPoster = findViewById(R.id.etPoster);
        btnCreate = findViewById(R.id.btnCreate);
        btnSearch = findViewById(R.id.btnSearch);
        btnCamera = findViewById(R.id.btnCamera);

        //TODO: Retrieve the apikey from SharedPreferences
        //If apikey is empty, redirect back to LoginActivity

        //TODO: read loginId and apiKey from SharedPreferences
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String loginID = prefs.getString("loginID", "");
        String apikey = prefs.getString("apiKey", "");

        // TODO: if loginId and apikey is empty, go back to LoginActivity
        if(loginID.equalsIgnoreCase("") || apikey.equalsIgnoreCase("")) {
            // redirect back to login screen
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
        }

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnCreateOnClick(v);
            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnSearchOnClick(v);
            }
        });

        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnCameraOnClick(v);
            }
        });

    }//end onCreate

	//TODO: extract the fields and populate into a new instance of Movie class
	// Add the new movie into Firestore
    private void btnCreateOnClick(View v) {
        String title = etTitle.getText().toString();
        String rated = etRated.getText().toString();
        String released = etReleased.getText().toString();
        String runtime = etRuntime.getText().toString();
        String genre = etGenre.getText().toString();
        String actors = etActors.getText().toString();
        String plot = etPlot.getText().toString();
        String language = etLanguage.getText().toString();
        String poster = etPoster.getText().toString();

        int year = Integer.parseInt(released.substring(released.length() - 4));

        Movie newStudent = new Movie(year, title, rated, released, runtime, genre, "director", "writer", actors, plot, language, poster);

        db = FirebaseFirestore.getInstance();

        // Task 3: Get real time updates from firestore by listening to collection "students"
        colRef = db.collection("movies");

        // Task 4: Add student to database and go back to main screen
        colRef.add(newStudent)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        //Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                        Toast.makeText(CreateMovieActivity.this, "Movie Record Added Successfully", Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //Log.w(TAG, "Error adding document", e);
                        Toast.makeText(CreateMovieActivity.this, "Movie Record Not Added", Toast.LENGTH_LONG).show();

                    }
                });
        finish();


    }

	//TODO: Call www.omdbapi.com passing the title and apikey as parameters
	// extract from JSON response and set into the edit fields
    private void btnSearchOnClick(View v) {

    }


    static final int REQUEST_IMAGE_CAPTURE = 1;

    private void btnCameraOnClick(View v) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(imageBitmap);
            FirebaseVisionTextRecognizer detector = FirebaseVision.getInstance().getOnDeviceTextRecognizer();
            detector.processImage(image)
                    .addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                        @Override
                        public void onSuccess(FirebaseVisionText firebaseVisionText) {
                            etTitle.setText("");
                            if(firebaseVisionText.getTextBlocks().size() == 0) {
                                etTitle.setText("");
                                return;
                            }

                            for (FirebaseVisionText.TextBlock block : firebaseVisionText.getTextBlocks()) {
                                etTitle.append(block.getText());
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(CreateMovieActivity.this, "Failed", Toast.LENGTH_LONG).show();
                        }
                    });
			
			//TODO: feed imageBitmap into FirebaseVisionImage for text recognizing
        }
    }
}