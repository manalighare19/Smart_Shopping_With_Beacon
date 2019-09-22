package com.example.inclass03_amad;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class SignUp extends AppCompatActivity {

    private EditText firstName, lastName, email, password, confirmPassword, address;
    private Button registerBtn,cancelBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        firstName = findViewById(R.id.firstName);
        lastName = findViewById(R.id.lastName);
        email = findViewById(R.id.emailID);
        password = findViewById(R.id.passwordID);
        confirmPassword = findViewById(R.id.confirmPasswordID);
        address = findViewById(R.id.address);

        registerBtn = findViewById(R.id.registerID);
        cancelBtn = findViewById(R.id.cancelID);

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cancelIntent = new Intent(SignUp.this, Login.class);
                startActivity(cancelIntent);
            }
        });
    }
}
