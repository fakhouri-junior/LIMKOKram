package com.fakhouri.salim.limkokram;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class HomePage extends ActionBarActivity {

    protected RelativeLayout relative;
    protected EditText description;
    protected ParseImageView selfiePic;
    protected Button postSelfie;

    protected ParseUser currentUser;
    protected String mUsername;
    protected ParseFile mFile;

    protected ParseObject post;

    protected boolean selfiePicHasImage = false;
    /*
        due to scrren orientation restriction and my trick with visible and gone
        once the pic is captured in landscape mode the post container is gone
        and the pic in it ,,, solved with onResume method
     */
    protected ParseFile fileImage;
    protected Bitmap bitmap;

    private static int REQUEST_IMAGE_CAPTURE = 100;

    private int trackingLikes = 0;
    protected JSONArray peopleWhoLike;

    // dealing with comments will be on run time inside CustomAdapter

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_page_layout);


        // connect to parse
        Parse.initialize(this, "ZHGcPgshJgm9XBHK8ZO49XCfClXgurbUmJDytLv2", "s6kC6xbeKXMYDUsSJ9LTgAsennl1RcnljXymGQpO");

        // check the user
        currentUser = ParseUser.getCurrentUser();
        if(currentUser != null){
            // proceed
            // grab the username and the small image
            mUsername = currentUser.getUsername();

            mFile = (ParseFile)currentUser.get("smallImage");
            mFile.getDataInBackground(new GetDataCallback() {
                @Override
                public void done(byte[] bytes, ParseException e) {
                    if(e == null){
                        // we are good
                        bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                    }
                }
            });

        }else{
            // the user is not logged in take him to log in page
            Intent i = new Intent(HomePage.this,MainActivity.class);
            startActivity(i);
            finish();
        }

        // reference
        peopleWhoLike = new JSONArray();

        relative = (RelativeLayout)findViewById(R.id.RelativeContainerHomePage);
        description = (EditText)findViewById(R.id.postText);
        selfiePic = (ParseImageView)findViewById(R.id.selfiePic);
        postSelfie = (Button)findViewById(R.id.postButton);
        // add done button to keyboard it's quite annoying without
        postSelfie.setImeOptions(EditorInfo.IME_ACTION_DONE);




    }

    public void PostMethod(View view){

        // double check on image
        if(selfiePicHasImage){

            final ProgressDialog mProgressDialog = ProgressDialog.show(HomePage.this, null, getResources().getString(R.string.wait));

            // grab text if any
            String text = description.getText().toString();
            // check if text is empty write default instead
            if(text.length() == 0){
                text = mUsername+" "+getResources().getString(R.string.tookAselfie);
            }

            // CREATE THE post object
            post = new ParseObject("Posts");
            // fill the values
            post.put("owner", currentUser);
            post.put("ownerName", mUsername);
            post.put("ownerImage",mFile);
            post.put("description",text);
            post.put("selfie",fileImage);
            post.put("numberOfLikes",trackingLikes);
            post.put("peopleWhoLike",peopleWhoLike);

            // update the numberOFSelfies
            int i= currentUser.getInt("numberOFSelfies");
            i++;
            currentUser.put("numberOFSelfies",i);
            currentUser.saveInBackground();

            // make it read public
            ParseACL parseACL = new ParseACL();

            parseACL.setPublicReadAccess(true);
            parseACL.setWriteAccess(currentUser, true);
            post.setACL(parseACL);

            post.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        // success
                        mProgressDialog.dismiss();
                        // make the text empty now
                        description.setText("");
                        // update the list from adapter

                        MyListFragment.customAdapter.loadObjects();
                }else{
                        // wrong
                        Toast.makeText(HomePage.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });



            // done ,hide and return to normal

            selfiePicHasImage = false;
            selfiePic.setImageDrawable(null);
            relative.setVisibility(View.GONE);
        }else{
            relative.setVisibility(View.GONE);
        }


    }
    @Override
    protected void onResume() {
        super.onResume();
        if(selfiePicHasImage){
            relative.setVisibility(View.VISIBLE);
        }


    }

    public void showFriendPage(View view){
        TextView t = (TextView) view;
        String name = t.getText().toString();
        Intent friendIntent = new Intent(HomePage.this,FriendPage.class);
        friendIntent.putExtra("friendName",name);
        startActivity(friendIntent);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home_page_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_selfie) {
            Selfie();
            return true;
        }
        if(id == R.id.action_profile){
            Profile();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void Profile(){
        Intent intent = new Intent(HomePage.this,Profile.class);
        startActivity(intent);
    }



    public void Selfie() {
        // make an intent to camera
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {

            startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
            // show the post layout
            relative.setVisibility(View.VISIBLE);
        } else {
            Toast.makeText(HomePage.this, getResources().getString(R.string.noCamera), Toast.LENGTH_SHORT).show();
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK && data != null) {

            // works for thumbnail but we need a full size
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            selfiePic.setImageBitmap(imageBitmap);

            // set to true
            selfiePicHasImage = true;
            // convert
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            // Compress image to lower quality scale 1 - 100
            imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] newimage = stream.toByteArray();
            fileImage = new ParseFile("imagePost", newimage);
            fileImage.saveInBackground();

        }else{
            Toast.makeText(HomePage.this,getResources().getString(R.string.notTookSelfie),Toast.LENGTH_SHORT).show();
            // hide the container
            relative.setVisibility(View.GONE);
        }
    }
}
