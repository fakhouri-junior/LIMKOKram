package com.fakhouri.salim.limkokram;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;


public class FriendPage extends ActionBarActivity {

    protected ParseImageView friendImage;
    protected TextView friendName;
    protected TextView friendSelfies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friend_page);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        friendImage = (ParseImageView) findViewById(R.id.friendImage);
        friendName = (TextView) findViewById(R.id.friendName);
        friendSelfies = (TextView) findViewById(R.id.friendSelfies);

        // receive the intent extra
        Bundle bundle = this.getIntent().getExtras();
        String fn = bundle.getString("friendName");

        // search for the user
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("username", fn);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> parseUsers, ParseException e) {
                if (e == null) {
                    //ok
                    //final ProgressDialog progressDialog = ProgressDialog.show(FriendPage.this,null,"");
                    if (parseUsers.size() != 0) {
                        ParseUser myResult = parseUsers.get(0);
                        friendName.setText(myResult.getString("username"));
                        ParseFile imageFile = myResult.getParseFile("image");
                        if (imageFile != null) {
                            friendImage.setParseFile(imageFile);
                            friendImage.loadInBackground();
                        }

                        int i = myResult.getInt("numberOFSelfies");
                        String yt = getResources().getString(R.string.yt);
                        String sl = getResources().getString(R.string.selfies);
                        StringBuilder sb = new StringBuilder();
                        sb.append(yt);
                        sb.append(" ");
                        sb.append(i);
                        sb.append(" ");
                        sb.append(sl);
                        friendSelfies.setText(sb.toString());

                        //progressDialog.dismiss();
                    } else {
                        Toast.makeText(FriendPage.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(FriendPage.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if(id == R.id.home){
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
