package com.fakhouri.salim.limkokram;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;


public class CustomAdapter extends ParseQueryAdapter<ParseObject> {

    // CONSTRUCTOR
    public CustomAdapter(Context context) {

        super(context, new ParseQueryAdapter.QueryFactory<ParseObject>() {
            public ParseQuery create() {
                ParseQuery query = new ParseQuery("Posts");
                query.orderByDescending("createdAt");

                return query;
            }
        });
    }

    public static String id;

    public View getItemView(final ParseObject object, View v, ViewGroup parent) {

        if (v == null) {
            v = View.inflate(getContext(), R.layout.post_layout, null);
        }

        super.getItemView(object, v, parent);




        ParseFile pf = object.getParseFile("selfie");
        ParseImageView imagePost = (ParseImageView) v.findViewById(R.id.selfie);

        imagePost.setParseFile(pf);
        imagePost.loadInBackground();

        ParseImageView userImage = (ParseImageView) v.findViewById(R.id.ownerImage);
        ParseFile imageFile = object.getParseFile("ownerImage");
        if (imageFile != null) {
            userImage.setParseFile(imageFile);
            userImage.loadInBackground();
        }


        // add the username
        final String usernameString = object.getString("ownerName");
        TextView usernameTextView = (TextView) v.findViewById(R.id.ownerName);
        // underline it
        SpannableString content = new SpannableString(usernameString);
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        usernameTextView.setText(content);


        String desc = object.getString("description");
        TextView userStatusTextView = (TextView) v.findViewById(R.id.ownerDescription);
        userStatusTextView.setText(desc);


        final ImageButton emptyHeart = (ImageButton) v.findViewById(R.id.emptyHeart);
        final ImageButton redHeart = (ImageButton) v.findViewById(R.id.redHeart);

        final TextView comment = (TextView)v.findViewById(R.id.comment);
        comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                id = object.getObjectId();
                Intent intent = new Intent(getContext(),CommentPage.class);
                intent.putExtra("id",id);
                getContext().startActivity(intent);
            }
        });

        final TextView numberOfHearts = (TextView) v.findViewById(R.id.numberOfHearts);

        final JSONArray peopleWhoLike = object.getJSONArray("peopleWhoLike");



        boolean bol = false;
        boolean likeChecker = false;
        // loop through the names and check if the current has clicked on the post if not show him the button else hide
        ParseUser current = ParseUser.getCurrentUser();
        String currentName = current.getUsername();
        for(int s = 0; s < peopleWhoLike.length();s++){
            String n;
            try {
                 n = peopleWhoLike.getString(s);
                if(currentName.equals(n)){
                    likeChecker = true;

                }else{

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        if(likeChecker){
            emptyHeart.setVisibility(View.INVISIBLE);
            redHeart.setVisibility(View.VISIBLE);
        }else{
            emptyHeart.setVisibility(View.VISIBLE);
            redHeart.setVisibility(View.INVISIBLE);
        }

        emptyHeart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int numberLikes = object.getInt("numberOfLikes");
                // increase it
                numberLikes++;

                // save the username who clicks it
                ParseUser currentUser = ParseUser.getCurrentUser();
                peopleWhoLike.put(currentUser.getUsername());
                //Toast.makeText(getContext(),currentUser.getUsername(),Toast.LENGTH_SHORT).show();
                // update the object
                object.put("peopleWhoLike", peopleWhoLike);
                // update the value
                object.put("numberOfLikes", numberLikes);
                // save
                object.saveEventually();

                // hide the emptyHeart
                emptyHeart.setVisibility(View.INVISIBLE);
                // show the redHeart
                redHeart.setVisibility(View.VISIBLE);

                // adjust peopleLike(number+hearts)
                String oui = getContext().getResources().getString(R.string.heart);
                StringBuilder sb = new StringBuilder();
                sb.append(numberLikes);
                sb.append(" ");
                sb.append(oui);
                oui = sb.toString();
                numberOfHearts.setText(oui);
            }
        });

        int numberLikes = object.getInt("numberOfLikes");
        // adjust peopleLike(number+hearts)
        String oui = getContext().getResources().getString(R.string.heart);
        StringBuilder sb = new StringBuilder();
        sb.append(numberLikes);
        sb.append(" ");
        sb.append(oui);
        oui = sb.toString();
        numberOfHearts.setText(oui);


        /*/ check if user clicked before on the post
        if (numberLikes != 0) {
            for (int i = 0; i < peopleWhoLike.length(); i++) {
                String asd = null;
                try {
                    asd = peopleWhoLike.getString(i);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (currentName.equals(asd)) {
                    // he liked it so hide the emptyHeart
                    emptyHeart.setVisibility(View.INVISIBLE);
                    redHeart.setVisibility(View.VISIBLE);

                } else {
                    emptyHeart.setVisibility(View.VISIBLE);
                    redHeart.setVisibility(View.INVISIBLE);
                }

            }
        }else{
            emptyHeart.setVisibility(View.VISIBLE);
            redHeart.setVisibility(View.INVISIBLE);
        }*/


        numberOfHearts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // create an alertDialog to display the list of user who click on it
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle(getContext().getResources().getString(R.string.peopleWhoLovedIt));
                final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getContext(),android.R.layout.select_dialog_item);
                // now we need to add strings to the adapter so we loop through the json array and extract each string out of it and save it in the adapter
                for(int i = 0;i < peopleWhoLike.length();i++){
                    try {
                        String str = peopleWhoLike.getString(i);
                        arrayAdapter.add(str);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                builder.setPositiveButton(getContext().getResources().getString(R.string.ok),new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.setAdapter(arrayAdapter,new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }

        });
        object.saveEventually();

        return v;
    }

}
