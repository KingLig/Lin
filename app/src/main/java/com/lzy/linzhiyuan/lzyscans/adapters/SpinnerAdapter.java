package com.lzy.linzhiyuan.lzyscans.adapters;

import android.content.Context;
import android.widget.ArrayAdapter;

public class SpinnerAdapter<T> extends ArrayAdapter<T> {
	
	public SpinnerAdapter(Context context, int resource, T[] array) {
		super(context, resource, array);
		
		this.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	}
}
