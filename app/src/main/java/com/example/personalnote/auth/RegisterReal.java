package com.example.personalnote.auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.personalnote.MainActivity;
import com.example.personalnote.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterReal extends AppCompatActivity {

    EditText rUserName, rUserEmail, rUserPass, rUserConfPass;
    Button creatAccount;
    TextView loginAct;
    ProgressBar progressBar;
    FirebaseAuth fAuth;
    Intent data;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_real);
        rUserName = findViewById(R.id.userName);
        rUserEmail = findViewById(R.id.userEmail);
        rUserPass = findViewById(R.id.password);
        rUserConfPass = findViewById(R.id.passwordConfirm);

        creatAccount = findViewById(R.id.createAccount);
        loginAct = findViewById(R.id.login);
        progressBar = findViewById(R.id.progressBar4);

        fAuth = FirebaseAuth.getInstance();

        creatAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String uUsername = rUserName.getText().toString();
                String uUserEmail = rUserEmail.getText().toString();
                String uUserPass = rUserPass.getText().toString();
                String uConfPass = rUserConfPass.getText().toString();

                if (uUserEmail.isEmpty() || uUsername.isEmpty() || uUserPass.isEmpty() || uConfPass.isEmpty()) {
                    Toast.makeText(RegisterReal.this, "All Fields Are Required.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!uUserPass.equals(uConfPass)) {
                    rUserConfPass.setError("Password Do not Match.");
                }

                progressBar.setVisibility(View.VISIBLE);

                    Toast.makeText(RegisterReal.this, "I am in real part", Toast.LENGTH_SHORT).show();
                    fAuth.createUserWithEmailAndPassword(uUserEmail, uUserPass)
                            .addOnCompleteListener(RegisterReal.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Log.d("tag", "createUserWithEmail:success");
                                        FirebaseUser user = fAuth.getCurrentUser();
                                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                        finish();
                                        //updateUI(user);
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Toast.makeText(RegisterReal.this, "Authentication failed.",Toast.LENGTH_SHORT).show();
                                        //updateUI(null);
                                    }

                                    // ...
                                }
                            });

                
            }
        });
    }
}
