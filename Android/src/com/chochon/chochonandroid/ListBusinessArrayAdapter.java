package com.chochon.chochonandroid;

import java.util.ArrayList;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.TextView;

import com.chochon.chochonandroid.DBObjects.BusinessInfo;
//Custom list business array adapter. Set image, name, description business.
public class ListBusinessArrayAdapter extends ArrayAdapter<BusinessInfo> {
	private final Activity context;
	private final ArrayList<BusinessInfo> listBusinessInfo;
	
	public  ListBusinessArrayAdapter(Activity context, ArrayList<BusinessInfo> listBusiness ){
		super(context, R.layout.listview_row_list_business, listBusiness);
		this.context = context;
		this.listBusinessInfo = listBusiness;
	}
	
	static class ViewContainer {
		public ImageView imageView;
		public CheckedTextView checkedTextViewBusinessName;
		public TextView textViewBusinessAddress;
		public TextView textViewBusinessReview;
	}
	
	@Override
	public View getView(final int postiton, View view, ViewGroup parent){
		ViewContainer viewContainer;
		View rowView = view;
		
		//If row is displayed for thie first time.
		if(rowView == null){
			LayoutInflater inflater = context.getLayoutInflater();
			rowView = inflater.inflate(R.layout.listview_row_list_business, null, true);
			
			//Create view container object
			viewContainer = new ViewContainer();
			
			//Get reference to all views in the row
			viewContainer.imageView = (ImageView) rowView.findViewById(R.id.imageView_business_listview);
			viewContainer.checkedTextViewBusinessName = (CheckedTextView) rowView.findViewById(R.id.textView_business_name_listview);
			viewContainer.textViewBusinessAddress = (TextView) rowView.findViewById(R.id.textView_business_address_listview);
			viewContainer.textViewBusinessReview = (TextView) rowView.findViewById(R.id.textView_business_review_listview);
			
			//Assign the view container to the rowView
			rowView.setTag(viewContainer);
		} else {
			//View was previously created, can recycle
			//retrieve the previously assigned tag to get
			//a reference to all the views, bypass the findViewByid()		
			viewContainer = (ViewContainer) rowView.getTag();
		}
		//customize the content of each row based on postion
		//viewContainer.imageView.seti(BusinessInfoList.get(postiton).getImagePath());
		viewContainer.checkedTextViewBusinessName.setText(listBusinessInfo.get(postiton).getName());
		viewContainer.textViewBusinessAddress.setText(listBusinessInfo.get(postiton).getAddress());
		viewContainer.textViewBusinessReview.setText(listBusinessInfo.get(postiton).getWebsite());
		
		
		return rowView;
	}
}
