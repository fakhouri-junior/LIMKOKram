package com.fakhouri.salim.limkokram;

import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MyListFragment extends ListFragment {

    protected static CustomAdapter customAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.list_fragment,container,false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        customAdapter = new CustomAdapter(getActivity());

        setListAdapter(customAdapter);
        customAdapter.loadObjects();
    }
<<<<<<< HEAD

    @Override
    public void onResume() {
        super.onResume();
       //customAdapter.loadObjects();
    }
=======
>>>>>>> 294daba1b580df7c74a670e3deb3a06f5aedcfc7
}
