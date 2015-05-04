package com.fakhouri.salim.limkokram;

import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class CommentFragment extends ListFragment {

    protected static CommentAdapter commentAdapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.list_fragment,container,false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        commentAdapter = new CommentAdapter(getActivity(),CommentPage.par );
        setListAdapter(commentAdapter);
        commentAdapter.loadObjects();



    }
}
