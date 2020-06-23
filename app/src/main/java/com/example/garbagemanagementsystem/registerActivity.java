package com.example.garbagemanagementsystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.*;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class registerActivity extends AppCompatActivity {

    EditText emailEditText, passwordEditText, nameEditText;
    Button registerButton;
    Spinner ageSpinner, genderSpinner;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        registerButton = findViewById(R.id.registerButton);
        nameEditText = findViewById(R.id.nameEditText);
        ageSpinner = findViewById(R.id.ageSpinner);
        genderSpinner = findViewById(R.id.genderSpinner);
        auth = FirebaseAuth.getInstance();


        //AGE Spinner Setup
        List age = new ArrayList<Integer>();
        for (int i = 16; i <= 121; i++) {
            age.add(Integer.toString(i));
        }
        ArrayAdapter<Integer> ageArrayAdapter = new ArrayAdapter<Integer>(
                this, android.R.layout.simple_spinner_item, age);
        ageArrayAdapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );
        ageSpinner.setAdapter(ageArrayAdapter);

        //GENDER Spinner Setup
        List gender = new ArrayList<String>();
        gender.add("Male");
        gender.add("Female");
        gender.add("Others");
        ArrayAdapter<String> genderArrayAdapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, gender);
        genderArrayAdapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );
        genderSpinner.setAdapter(genderArrayAdapter);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString();
                String pass = passwordEditText.getText().toString();
                String name = nameEditText.getText().toString();
                int age = ageSpinner.getSelectedItemPosition() + 16;
                String gender = genderSpinner.getSelectedItem().toString();

                if(TextUtils.isEmpty(email) || TextUtils.isEmpty(pass) || TextUtils.isEmpty(name)){
                    Toast.makeText(registerActivity.this, "Empty Credentials", Toast.LENGTH_SHORT).show();
                } else if (pass.length() < 6){
                    Toast.makeText(registerActivity.this, "Password too Short!", Toast.LENGTH_SHORT).show();
                } else {
                    registerUser(email,pass,name,age,gender);
                }
            }
        });
    }

    private void registerUser(final String email, final String pass, final String name,final int age,final String gender){
        auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(registerActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(registerActivity.this, "Registration Successful!", Toast.LENGTH_SHORT).show();
                    auth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()) {
                                HashMap<String, Object> map = new HashMap<>();
                                map.put("Name",name);
                                map.put("Age",age);
                                map.put("Gender",gender);
                                map.put("Email", email);
                                map.put("Password",pass);
                                String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                FirebaseDatabase.getInstance().getReference().child("Users").child(uid).updateChildren(map);
                                startActivity(new Intent(registerActivity.this, StartActivity.class));
                                finish();
                            }
                        }
                    });
                } else {
                    if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                        Toast.makeText(registerActivity.this, "User with this email already exist.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(registerActivity.this, "Registration Failed!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}
