package com.fakhouri.salim.limkokram;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.io.ByteArrayOutputStream;


public class Signup extends ActionBarActivity {

    protected EditText usernameSignup;
    protected EditText passwordSignup;
    protected EditText confirmPasswordSignup;
    protected EditText email;
    protected Button register;

    protected ParseFile file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_layout);

        // reference
        usernameSignup = (EditText)findViewById(R.id.editTextUsernameSignup);
        passwordSignup = (EditText)findViewById(R.id.editTextPasswordSignup);
        confirmPasswordSignup = (EditText)findViewById(R.id.editTextConfirmPasswordSignup);
        email = (EditText)findViewById(R.id.editTextEmailSignup);

        // deal with default picture for all users
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.default_user_limkok);
        // convert it to byte
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100,stream);
        byte[] image = stream.toByteArray();

        // set it to file
        file = new ParseFile("default_image.png",image);
        file.saveInBackground();


        register = (Button)findViewById(R.id.registerButton);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // check if username is empty
                if(usernameSignup.length() == 0){
                    Toast.makeText(Signup.this,getResources().getString(R.string.sorryUsername),Toast.LENGTH_SHORT).show();
                    return;
                }

                // check if passwords are empty
                if(passwordSignup.length() == 0 || confirmPasswordSignup.length() == 0){
                    Toast.makeText(Signup.this,getResources().getString(R.string.sorryPassword),Toast.LENGTH_SHORT).show();
                    return;
                }

                // check if email is empty
                if(email.length() == 0){
                    Toast.makeText(Signup.this,getResources().getString(R.string.sorryEmail),Toast.LENGTH_SHORT).show();
                    return;
                }

                // let's get the values
                String username = usernameSignup.getText().toString();
                String password = passwordSignup.getText().toString().toLowerCase().trim();
                String passConfirm = confirmPasswordSignup.getText().toString().toLowerCase().trim();
                String em = email.getText().toString();

                // make checks
                if(password.equals(passConfirm)){
                    // proceed

                    // show a progress dialog
                    final ProgressDialog progressDialog = ProgressDialog.show(Signup.this,null,getResources().getString(R.string.wait));

                    // create the parseUser
                    ParseUser user = new ParseUser();
                    user.setUsername(username);
                    user.setPassword(password);
                    user.setEmail(em);

                    // put picture
                    user.put("image",file);

                    // for later use

                    user.put("smallImage",file);
                    int numOFSelfies = 0;
                    user.put("numberOFSelfies",numOFSelfies);

                    // now sign user up
                    user.signUpInBackground(new SignUpCallback() {
                        @Override
                        public void done(ParseException e) {
                            // close the progress dailog we are goo
                            progressDialog.dismiss();

                            if(e == null){
                                // hooray he is in
                                // welcome
                                Toast.makeText(Signup.this,getResources().getString(R.string.welcome),Toast.LENGTH_LONG).show();
                                // take him to home page
                                Intent goToHomePage = new Intent(Signup.this,HomePage.class);
                                startActivity(goToHomePage);

                            }else{
                                // something is wrong
                                Toast.makeText(Signup.this,e.getMessage()+"hello",Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                    });
                }else{
                    Toast.makeText(Signup.this,getResources().getString(R.string.sorryPasswordConfirm),Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });
    }
}
