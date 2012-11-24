package com.example.mediacontrol;

import java.util.List;

import android.app.Activity;
import android.support.v4.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MediaFragment extends ListFragment implements IMediaFragment{
	
	public interface IMediaListener {
		void onContentSelected(ContentDisplay content);
	}
	
	private ArrayAdapter<ContentDisplay> adapter;
	private IMediaListener mCallback;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, 
        Bundle savedInstanceState) {
		return super.onCreateView(inflater, container, savedInstanceState);
	}
	
	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (IMediaListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
        
        adapter = new ArrayAdapter<ContentDisplay>(this.getActivity(), android.R.layout.simple_list_item_1);
        this.setListAdapter(adapter);
    }

	@Override
	public void setContent(List<ContentDisplay> content) {
		// TODO Auto-generated method stub
		adapter.clear();
		adapter.addAll(content);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		mCallback.onContentSelected(adapter.getItem(position));
	}
}
