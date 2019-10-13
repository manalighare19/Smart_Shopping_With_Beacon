package com.example.inclass03_amad;

import androidx.appcompat.app.AppCompatActivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class EditUserProfile extends AppCompatActivity {

    private EditText firstNameEditText, lastNameEditText, addressEditText;
    private TextView emailTextView;
    private Button saveButton;
    private OkHttpClient client;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user_profile);
        setTitle("Edit Profile");

        client = new OkHttpClient();
        firstNameEditText = findViewById(R.id.firstNameEditText);
        lastNameEditText = findViewById(R.id.lastNameEditText);
        addressEditText = findViewById(R.id.addressEditText);
        emailTextView = findViewById(R.id.emailTextView);

        saveButton = findViewById(R.id.saveBtn);
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        String token = sharedPreferences.getString("Token",null);
        final String userID = sharedPreferences.getString("UserID",null);

        String url = "http://ec2-3-17-204-58.us-east-2.compute.amazonaws.com:4000/users/"+userID;
        final Request request = new Request.Builder()
                .url(url)
                .header("Authorization", "Bearer " + token)
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                try {

                    String json = response.body().string();
                    final JSONObject root = new JSONObject(json);
                    if(root.getString("_id").equals(userID)) {
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {

                                try {
                                    firstNameEditText.setText(root.getString("firstName"));
                                    lastNameEditText.setText(root.getString("lastName"));
                                    addressEditText.setText(root.getString("address"));
                                    emailTextView.setText(root.getString("email"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });

                    }

                    else
                    {

                        Looper.prepare();
                        Toast.makeText(EditUserProfile.this, "Update is not successful.", Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }


        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    update(firstNameEditText.getText().toString(),lastNameEditText.getText().toString(),addressEditText.getText().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void update(final String first, final String last, final String address) throws JSONException{
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("firstName",first);
        jsonObject.put("lastName",last);
        jsonObject.put("address",address);

        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        String token = sharedPreferences.getString("Token",null);
        final String userID = sharedPreferences.getString("UserID",null);
        String jsonString=jsonObject.toString();

        RequestBody requestBody = RequestBody.create(JSON, jsonString);

        String url = "http://ec2-3-17-204-58.us-east-2.compute.amazonaws.com:4000/users/"+userID;

        final Request request = new Request.Builder()
                .url(url)
                .header("Authorization", "Bearer " + token)
                .put(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                try {

                    String json = response.body().string();
                    JSONObject root = new JSONObject(json);
                    if(json.equals("{}")) {

                        Intent intent = new Intent(EditUserProfile.this, UserProfile.class);
                        startActivity(intent);
                        finish();

                    }

                    else
                    {

                        Looper.prepare();
                        Toast.makeText(EditUserProfile.this, "Update is not successful.", Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }


        });
    }

}
