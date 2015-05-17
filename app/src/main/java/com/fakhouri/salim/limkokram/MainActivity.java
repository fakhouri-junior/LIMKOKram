package com.fakhouri.salim.limkokram;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseUser;


public class MainActivity extends ActionBarActivity {

    protected EditText username;
    protected EditText password;

    protected Button login;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




        // reference
        username = (EditText)findViewById(R.id.editTextUsername);
        password = (EditText)findViewById(R.id.editTextPassword);

        login = (Button)findViewById(R.id.loginButton);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                // grab the value of username
                String usernameValue = username.getText().toString();
                // check if the value is empty
                if(usernameValue.length() == 0){
                    // show a toast message
                    Toast.makeText(MainActivity.this,getResources().getString(R.string.pleaseEnterUsername),Toast.LENGTH_SHORT).show();
                    return;
                }

                // grab the password
                String pass = password.getText().toString().toLowerCase().trim();
                // check if pass is not empty
                if(pass.length()==0){
                    //show a toast message
                    Toast.makeText(MainActivity.this,getResources().getString(R.string.pleaseEnterPassword),Toast.LENGTH_SHORT).show();
                    return;
                }

                // now everything is good log user in
                ParseUser.logInInBackground(usernameValue,pass,new LogInCallback() {
                    final ProgressDialog progressDialog = ProgressDialog.show(MainActivity.this,null,getResources().getString(R.string.wait));
                    @Override
                    public void done(ParseUser parseUser, ParseException e) {

                        progressDialog.dismiss();
                        if(parseUser != null){
                            // everything is good, user is logged in
                            // delete the toast after check
                            Toast.makeText(MainActivity.this,getResources().getString(R.string.welcome),Toast.LENGTH_SHORT).show();
                            // take him to the main page
                            Intent homePageIntent = new Intent(MainActivity.this,HomePage.class);
                            startActivity(homePageIntent);

                        }else{
                            // something is wrong
                            Toast.makeText(MainActivity.this,getResources().getString(R.string.loginFail),Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
    }

    public void goToSignup(View view){
        Intent signupIntent = new Intent(MainActivity.this,Signup.class);
        startActivity(signupIntent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();



        return super.onOptionsItemSelected(item);
    }
}
