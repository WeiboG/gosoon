package com.gosoon.adapter;

import java.util.ArrayList;
import java.util.List;

import com.gosoon.fragment.baseFragment;
import android.app.Activity;
import android.widget.BaseAdapter;

public abstract class BaseListAdapter<T> extends BaseAdapter{

	protected baseFragment fragment;
	protected Activity activity;
	List<? extends T> list;

	public BaseListAdapter(Activity activity,List<? extends T> list){
		super();
		this.activity = activity;
		this.list = list;
		if(this.list == null){
			this.list = new ArrayList<T>();
		}
	}

	public BaseListAdapter(baseFragment fragment, List<? extends T> list) {
		super();
		this.fragment = fragment;
		this.activity = fragment.getActivity();
		this.list = list;
		if(this.list == null){
			this.list = new ArrayList<T>();
		}
	}

	@Override
	public int getCount(){
		return list.size();
	}

	@Override
	public T getItem(int position){
		return list.get(position);
	}

	@Override
	public long getItemId(int position){
		return position;
	}

	public void changeData(List<? extends T> newList){
		this.list = newList;
		if(this.list == null){
			this.list = new ArrayList<T>();
		}
		notifyDataSetChanged();
	}

	@SuppressWarnings("unchecked")
	public void addData(List<? extends T> newList) {
		((ArrayList<T>) this.list).addAll((ArrayList<T>)newList);
		notifyDataSetChanged();
	}
}
	
