package com.example.c302_p13_omdb;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.loopj.android.http.AsyncHttpClient;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ListView listView;
    private ArrayList<Movie> list;
    private MovieAdapter adapter;

    private AsyncHttpClient client;

    // Task 1 - Declare Firebase variables
    private FirebaseFirestore db;
    private CollectionReference colRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(R.id.listViewMovies);
        list = new ArrayList<Movie>();

        client = new AsyncHttpClient();


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

		//TODO: retrieve all documents from the "movies" collection in Firestore (realtime)
		//populate the movie objects into the ListView

        // Task 2: Get FirebaseFirestore instance and reference
        db = FirebaseFirestore.getInstance();

        // Task 3: Get real time updates from firestore by listening to collection "students"
        colRef = db.collection("movies");

        colRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    return;
                }

                list = new ArrayList<Movie>();
                for (QueryDocumentSnapshot documentSnapshot : snapshot) {
                    if (documentSnapshot.get("title") != null) {
                        Movie student = documentSnapshot.toObject(Movie.class);
                        student.setMovieId(documentSnapshot.getId()); // set document id as the student id
                        // Task 4: Read from Snapshot and add into ArrayAdapter for ListView
                        list.add(student);
                    }
                }
                adapter = new MovieAdapter(getApplicationContext(), R.layout.movie_row, list);
                listView.setAdapter(adapter);
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Movie selectedContact = list.get(position);
                Intent i = new Intent(getBaseContext(), ViewMovieDetailsActivity.class);
                i.putExtra("movie_id", selectedContact.getMovieId());
                startActivity(i);

            }
        });

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.menu_add) {
            Intent intent = new Intent(getApplicationContext(), CreateMovieActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}