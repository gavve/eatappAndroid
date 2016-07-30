package com.example.jacob.eatapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import org.json.JSONException;

import java.io.IOException;
import java.util.Dictionary;
import java.util.HashMap;

public class RegisterUser extends AppCompatActivity {
    private Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user2);
        mContext = getApplicationContext();


        final Button selectBirthDay = (Button) findViewById(R.id.choosedate);
        selectBirthDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateTimePickerFragment dtpf = new DateTimePickerFragment();
                dtpf.show(getFragmentManager(), "Date Picker");
            }
        });

        // Registerar knapp och onClickListener för att skicka data till servern
        Button registerButton = (Button) findViewById(R.id.register_button);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Skapa en dictionary med datan för nya användaren
                HashMap<String, String> tempUser = new HashMap<String, String>();
                EditText userEmail = (EditText) findViewById(R.id.newUseremail);
                EditText userName = (EditText) findViewById(R.id.newUserName);
                EditText userPassword = (EditText) findViewById(R.id.newUserpassword);
                if (isEmailValid(userEmail.getText().toString()) && isPasswordValid(userPassword.getText().toString())) {
                    tempUser.put("email", userEmail.getText().toString());
                    tempUser.put("first_name", userName.getText().toString());
                    tempUser.put("password", userPassword.getText().toString());
                    tempUser.put("date_of_birth", selectBirthDay.getText().toString().replace("/", "-"));

                    // Skicka dictionaryn till RegisterUserSync
                    RegisterUserSync sendUser = new RegisterUserSync(new AsyncInterface() {
                        @Override
                        public void processFinish(String output) throws JSONException {
                            // När användaren är registrerad så skickas tillbaka till Loginsidan
                            // TODO :: Lägga till säkerhetsåtgärder om tex emailen redan är registrerad
                            Intent intent=new Intent(RegisterUser.this,LoginActivity.class); // redirecting to MainActivity.
                            startActivity(intent);
                            finish();

                        }

                        @Override
                        public void bitMapFinished(Bitmap output) throws IOException {

                        }
                    },tempUser);
                    sendUser.execute();
                } else {
                    // TODO :: SKicka varning för fel email/lösenord
                }

            }
        });
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }
}
