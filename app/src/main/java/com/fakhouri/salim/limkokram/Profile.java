package com.fakhouri.salim.limkokram;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.ByteArrayOutputStream;
import java.util.List;


public class Profile extends ActionBarActivity {

    protected ParseImageView userImage;
    protected TextView usernameValue;
    protected TextView numberOfSelfies;

    protected ParseUser currentUser;
    protected ParseFile getImage;

    private static int REQUEST_CODE = 1;
    String imgDecodableString;
    protected ParseFile file;
    protected ParseFile smallFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // reference
        userImage = (ParseImageView)findViewById(R.id.userImage);
        usernameValue = (TextView)findViewById(R.id.userNameValue);
        numberOfSelfies = (TextView)findViewById(R.id.numberOfSelfies);

        // get the current user
        currentUser = ParseUser.getCurrentUser();
        // check if everything is okay
        if(currentUser != null){

            // grab the values
            String usern = currentUser.getUsername();
            usernameValue.setText(usern);

            // get the image
            getImage = (ParseFile)currentUser.get("image");
            // convert from byte to bitmap
            getImage.getDataInBackground(new GetDataCallback() {
                @Override
                public void done(byte[] bytes, ParseException e) {
                    Bitmap bmp = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                    if(bmp != null){
                        // set the bitmap image
                        userImage.setImageBitmap(bmp);
                    }else{
                        Toast.makeText(Profile.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                }
            });

            // update numberOFSelfies
            int ns = currentUser.getInt("numberOFSelfies");
            String yt = getResources().getString(R.string.youtook);
            String sl = getResources().getString(R.string.selfies);
            StringBuilder sb = new StringBuilder();
            sb.append(yt);
            sb.append(" ");
            sb.append(ns);
            sb.append(" ");
            sb.append(sl);
            numberOfSelfies.setText(sb.toString());

        }else{
            Toast.makeText(Profile.this,getResources().getString(R.string.somethingWentWrong),Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try{
            // when an image is picked
            if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null){
                // get the image from data
                Uri selectedImage = data.getData();
                String[] filePathColumn = { MediaStore.Images.Media.DATA };

                // Get the cursor
                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                // Move to first row
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                imgDecodableString = cursor.getString(columnIndex);
                cursor.close();
                Bitmap bitmap = BitmapFactory
                        .decodeFile(imgDecodableString);
                // create a small version of the image to use it in status

                Bitmap smallBitmap = Bitmap.createScaledBitmap(bitmap, 78, 78, false);


                // this is for the big image
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                // Compress image to lower quality scale 1 - 100
                bitmap.compress(Bitmap.CompressFormat.PNG, 10, stream);
                byte[] newimage = stream.toByteArray();

                // this is for the small image
                ByteArrayOutputStream smallStream = new ByteArrayOutputStream();
                // Compress image to lower quality scale 1 - 100
                smallBitmap.compress(Bitmap.CompressFormat.PNG, 10, smallStream);
                byte[] smallImage = smallStream.toByteArray();



                // Create the ParseFile for the big image
                file = new ParseFile("new_image.png", newimage);
                // Upload the image into Parse Cloud
                file.saveInBackground();

                // create a new ParseFile to hold the small image
                smallFile = new ParseFile("small_image.png",smallImage);
                smallFile.saveInBackground();

                // show the image
                userImage.setImageBitmap(BitmapFactory
                        .decodeFile(imgDecodableString));

                // save the new image to user
                currentUser.put("image",file);
                currentUser.put("smallImage",smallFile);
                currentUser.saveInBackground();

                // REMEMBER TO UPDATE THE PICS IN SELFIES POSTS
                // update the image in posts
                ParseQuery<ParseObject> query = ParseQuery.getQuery("Posts");
                query.whereEqualTo("ownerName",currentUser.getUsername());
                query.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> parseObjects, ParseException e) {
                        int size = parseObjects.size();
                        for(int i = 0;i < size;i++){
                            parseObjects.get(i).put("ownerImage",smallFile);
                            //Log.d("score", "Retrieved " + parseObjects.size() + " scores");
                            parseObjects.get(i).saveInBackground();

                        }
                    }
                });


            }else{
                Toast.makeText(Profile.this,getResources().getString(R.string.notPickedImage),Toast.LENGTH_LONG).show();
            }
        }catch (Exception e){
            Toast.makeText(Profile.this,e.getMessage(), Toast.LENGTH_LONG)
                    .show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_pic){
            changePic();
            return true;
        }
        if(id == R.id.home){
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void changePic(){

        //create an intent to access gallery
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent,REQUEST_CODE);
    }
}
