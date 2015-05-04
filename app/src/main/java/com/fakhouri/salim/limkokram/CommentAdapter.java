package com.fakhouri.salim.limkokram;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;


public class CommentAdapter extends ParseQueryAdapter<ParseObject> {

    // CONSTRUCTOR
    public CommentAdapter(Context context, final String p) {

        super(context, new ParseQueryAdapter.QueryFactory<ParseObject>() {
            public ParseQuery create() {
                ParseQuery query = new ParseQuery("Comment");

                query.whereEqualTo("parent",p);
                query.orderByAscending("createdAt");
                // Log.e("hello",p);

                return query;

            }
        });
    }

    public View getItemView(final ParseObject object, View v, ViewGroup parent) {

        if (v == null) {
            v = View.inflate(getContext(), R.layout.comment_layout, null);
        }

        super.getItemView(object, v, parent);

        TextView uname = (TextView)v.findViewById(R.id.commentUsername);
        TextView uText = (TextView)v.findViewById(R.id.commentText);

        String u = object.getString("commentUsername");
        String t = object.getString("commentItself");

        uname.setText(u);
        uText.setText(t);

        return v;
    }
}
