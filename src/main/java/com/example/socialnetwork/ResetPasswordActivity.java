package com.example.socialnetwork;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
;
import androidx.appcompat.widget.Toolbar;


public class ResetPasswordActivity extends AppCompatActivity {

    private Toolbar mToolbar;

    private Button ResetPasswordSendEmailButton;
    private EditText ResetEmailInput;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        mToolbar=findViewById(R.id.forget_password_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Reset Password");

        ResetPasswordSendEmailButton=findViewById(R.id.reset_password_email_button);
        ResetEmailInput=findViewById(R.id.reset_password_EMAIL);
    }
}
