package com.chochon.chochonandroid;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import com.chochon.chochonandroid.DBObjects.BusinessInfo;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class BusinessListFragment extends ListFragment {
	
	
	public static final String ARG_LOCATION = "location";
	private ArrayList<BusinessInfo> listBusiness =  new ArrayList<BusinessInfo>()  ;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	
		//Get business from service
        //String businessName = getArguments().getString(ARG_LOCATION).replace(" ","%20"); // Encode for httpGet.
        String business = "Hồ";
        String url = getString(R.string.chochon_webserver) + "/business/address/" + business;       	
		new GetBusinessTask().execute( url );
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	public String readJsonBusiness(String URL) {
        StringBuilder stringBuilder = new StringBuilder();
        HttpClient httpClient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(URL);
        try {
            HttpResponse response = httpClient.execute(httpGet);
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            if (statusCode == 200) {
                HttpEntity entity = response.getEntity();
                InputStream inputStream = entity.getContent();
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                }
                inputStream.close();
            } else {
                Log.d("JSON", "Failed to download file");
            }
        } catch (Exception e) {
            Log.d("Error", e.getLocalizedMessage());
        }        
        return stringBuilder.toString();
    }

	private class GetBusinessTask extends AsyncTask <String, Void, String> {
		 	protected String doInBackground(String... urls) {
	            	return readJsonBusiness(urls[0]);
	        }
	        
	        protected void onPostExecute(String result) {
	        	BusinessInfo businessInfo ;
	            try {
	            	
	            	JSONArray businessNameItems = new JSONArray(result);
	            
	                for (int i = 0; i < businessNameItems.length(); i++) {
	                	
	                    JSONObject businessItem = businessNameItems.getJSONObject(i);        
	                    businessInfo = new BusinessInfo();
	                    businessInfo.set_id(businessItem.getString("_id"));
	                    businessInfo.setName(businessItem.getString("name"));
	                    businessInfo.setAddress(businessItem.getString("address"));
	                    businessInfo.setPhone(businessItem.getString("phone"));
	                    businessInfo.setWebsite(businessItem.getString("website"));
	                    listBusiness.add(businessInfo);
	                    Log.d("Business Information", businessInfo.getName());
	                }
	            	ListBusinessArrayAdapter adapter = new ListBusinessArrayAdapter(getActivity(), listBusiness);
	                setListAdapter(adapter);
	               
	                
	            } catch (Exception e) {
	                Log.d("Exception onPostExecute", e.getLocalizedMessage());
	            }          
	        }
	    }
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);
		Intent intent = new Intent(getActivity(), BusinessDetailActivity.class);
		intent.putExtra(BusinessDetailActivity.ARG_BUSINESS_ID, listBusiness.get(position).get_id());
		getActivity().startActivity(intent);
		
	}
}