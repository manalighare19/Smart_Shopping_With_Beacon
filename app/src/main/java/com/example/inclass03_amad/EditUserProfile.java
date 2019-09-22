package com.example.inclass03_amad;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class EditUserProfile extends AppCompatActivity {

    private EditText firstNameEditText, lastNameEditText, addressEditText;
    private TextView emailTextView;
    private Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user_profile);

        firstNameEditText = findViewById(R.id.firstNameEditText);
        lastNameEditText = findViewById(R.id.lastNameEditText);
        addressEditText = findViewById(R.id.addressEditText);
        emailTextView = findViewById(R.id.emailTextView);

        saveButton = findViewById(R.id.saveBtn);
    }
}
