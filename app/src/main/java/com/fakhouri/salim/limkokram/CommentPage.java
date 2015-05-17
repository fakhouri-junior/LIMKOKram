package com.fakhouri.salim.limkokram;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;


public class CommentPage extends ActionBarActivity {

    public static String par;

    protected EditText editTextComment;
    protected ImageButton send;
    protected ParseObject commentObject;
    protected ParseUser currentUser;
    protected String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comment_page_layout);
        Bundle bundle = this.getIntent().getExtras();
        String s = bundle.getString("id");

        par = s;

        currentUser = ParseUser.getCurrentUser();
        username = currentUser.getUsername();

        // reference
        editTextComment = (EditText)findViewById(R.id.editTextComment);
        send = (ImageButton)findViewById(R.id.send);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeAcomment();
            }
        });



    }

    public void makeAcomment(){
        // check if there is a comment
        String commentText = editTextComment.getText().toString();
        if(commentText.length() == 0){
            // no text return and spit out a toast
            Toast.makeText(CommentPage.this,getResources().getString(R.string.noComment),Toast.LENGTH_SHORT).show();
            return;
        }

        // now everything is good let's make a parseObject
        final ProgressDialog mProgressDialog = ProgressDialog.show(CommentPage.this, null, getResources().getString(R.string.wait));

        commentObject = new ParseObject("Comment");
        commentObject.put("parent",par);
        commentObject.put("commentUsername",username);
        commentObject.put("commentItself", commentText);

        // make it read public
        ParseACL parseACL = new ParseACL();

        parseACL.setPublicReadAccess(true);
        parseACL.setWriteAccess(currentUser, true);
        commentObject.setACL(parseACL);

        commentObject.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e == null){
                    // we are good
                    mProgressDialog.dismiss();
                    editTextComment.setText("");
                    CommentFragment.commentAdapter.loadObjects();

                }else{
                    mProgressDialog.dismiss();
                    // something went wrong
                    Toast.makeText(CommentPage.this,getResources().getString(R.string.somethingWentWrong),Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });

        // get rid of the keyboard
        InputMethodManager imm = (InputMethodManager)this.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editTextComment.getWindowToken(), 0);

    }
}
