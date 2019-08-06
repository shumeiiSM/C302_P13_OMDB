package com.example.c302_p13_omdb;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    private EditText etLoginID, etPassword;
    private Button btnSubmit;

    AsyncHttpClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etLoginID = (EditText)findViewById(R.id.editTextLoginID);
        etPassword = (EditText)findViewById(R.id.editTextPassword);
        btnSubmit = (Button)findViewById(R.id.buttonSubmit);
        client = new AsyncHttpClient();

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etLoginID.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                if (username.equalsIgnoreCase("")) {
                    Toast.makeText(LoginActivity.this, "Login failed. Please enter username.", Toast.LENGTH_LONG).show();

                } else if (password.equalsIgnoreCase("")) {
                    Toast.makeText(LoginActivity.this, "Login failed. Please enter password.", Toast.LENGTH_LONG).show();

                } else {

					// TODO: call doLogin web service to authenticate user
					//save the apikey into SharedPreference

                    String url = "http://10.0.2.2/C302_P13_OMDB/doLogin.php";
                    RequestParams params = new RequestParams();
                    params.add("username", etLoginID.getText().toString());
                    params.add("password", etPassword.getText().toString());

                    client.post(url, params, new JsonHttpResponseHandler() {

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            try {
                                Log.i("JSON Results: ", response.toString());

                                Boolean authenticated = response.getBoolean("authenticated");
                                if (authenticated == true) {

                                    // extract apikey and id from JSON (doLogin.php)
                                    String apikey = response.getString("apikey");
                                    String id = response.getString("id");

                                    // store apikey and id into SharedPreference so that these 2 data do not need to pass via intent
                                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
                                    SharedPreferences.Editor editor = prefs.edit();
                                    editor.putString("loginID", id);
                                    editor.putString("apiKey", apikey);
                                    editor.commit();


                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                    startActivity(intent);

                                } else {
                                    Toast.makeText(LoginActivity.this,
                                            "Login failed. Please check your login credentials.",
                                            Toast.LENGTH_LONG).show();
                                }
                            }
                            catch(JSONException e){
                                e.printStackTrace();
                            }
                        }//end onSuccess
                    });



                }
            }
        });
    }
}


