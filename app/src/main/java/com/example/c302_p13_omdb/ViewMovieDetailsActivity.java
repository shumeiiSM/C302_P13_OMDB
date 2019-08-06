package com.example.c302_p13_omdb;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ViewMovieDetailsActivity extends AppCompatActivity {

    private EditText etTitle, etRated, etReleased, etRuntime, etGenre, etActors, etPlot, etLanguage, etPoster;
    private Button btnUpdate, btnDelete;
    private String movieId;

    private FirebaseFirestore db;
    private CollectionReference colRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_movie_details);

        etTitle = findViewById(R.id.etTitle);
        etRated = findViewById(R.id.etRated);
        etReleased = findViewById(R.id.etReleased);
        etRuntime = findViewById(R.id.etRuntime);
        etGenre = findViewById(R.id.etGenre);
        etActors = findViewById(R.id.etActors);
        etPlot = findViewById(R.id.etPlot);
        etLanguage = findViewById(R.id.etLanguage);
        etPoster = findViewById(R.id.etPoster);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnDelete = findViewById(R.id.btnDelete);

        Intent intent = getIntent();
        movieId = intent.getStringExtra("movie_id");

	//TODO: get the movie record from Firestore based on the movieId
	// set the edit fields with the details

        db = FirebaseFirestore.getInstance();
        colRef = db.collection("movies");

        // Task 3: Get document reference by the student's id and set the name and age to EditText

        final DocumentReference docRef = colRef.document(movieId);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Movie student = document.toObject(Movie.class);
                        etTitle.setText(student.getTitle());
                        etRated.setText(student.getRating());
                        etReleased.setText(student.getReleased());
                        etRuntime.setText(student.getRuntime());
                        etGenre.setText(student.getGenre());
                        etActors.setText(student.getActors());
                        etPlot.setText(student.getPlot());
                        etLanguage.setText(student.getLanguage());
                        etPoster.setText(student.getPoster());

                    }
                }
            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnUpdateOnClick(v);
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnDeleteOnClick(v);
            }
        });
    }//end onCreate

    
    private void btnUpdateOnClick(View v) {
		//TODO: create a Movie object and populate it with the values in the edit fields
		//save it into Firestore based on the movieId

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

        Movie updateStudent = new Movie(year, title, rated, released, runtime, genre, "director", "writer", actors, plot, language, poster);

        DocumentReference docRef = colRef.document(movieId);
        docRef.set(updateStudent)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(ViewMovieDetailsActivity.this, "Student record updated successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ViewMovieDetailsActivity.this, "Update failed", Toast.LENGTH_SHORT).show();
                    }
                });

        finish();


    }//end btnUpdateOnClick

    private void btnDeleteOnClick(View v) {
		//TODO: delete from Firestore based on the movieId

        DocumentReference docRef = colRef.document(movieId);
        docRef
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //Log.d(TAG, "Student successfully deleted!");
                        Toast.makeText(ViewMovieDetailsActivity.this, "Student record deleted successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //Log.w(TAG, "Error deleting student", e);
                        Toast.makeText(ViewMovieDetailsActivity.this, "Delete failed", Toast.LENGTH_SHORT).show();

                    }
                });

        finish();

    }//end btnDeleteOnClick

}//end class