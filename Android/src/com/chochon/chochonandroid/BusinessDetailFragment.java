package com.chochon.chochonandroid;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class BusinessDetailFragment extends Fragment {
	
	
	public static final String ARG_BUSINESS_ID = "ARG_BUSINESS_ID";
	
	//View
	private TextView textViewBusinessId;
	private TextView textViewBusinessName ;
	private TextView textViewBusinessAddress;
	private TextView textViewBusinessWebsite;
	private TextView textViewBusinessPhone;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	
		//Get business from service
        String id = getArguments().getString(ARG_BUSINESS_ID);
        
        String url = getString(R.string.chochon_webserver) + "/business/" + id;       	
		new GetBusinessTask().execute( url );
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_business_detail,
				container, false);
		
		//
		textViewBusinessAddress = (TextView) view.findViewById(R.id.textview_business_detail_address);
		textViewBusinessId = (TextView) view.findViewById(R.id.textview_business_detail_id);
		textViewBusinessName = (TextView) view.findViewById(R.id.textview_business_detail_name);
		textViewBusinessWebsite = (TextView) view.findViewById(R.id.textview_business_detail_website);
		textViewBusinessPhone = (TextView) view.findViewById(R.id.textview_business_detail_phone);
		
		return view;
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
	        	
	            try {
	            	
	            	
	                	
	                    JSONObject businessItem = new JSONObject(result);        
	                   
	                   textViewBusinessId.setText(businessItem.getString("_id"));
	                   textViewBusinessName.setText(businessItem.getString("name"));
	                   textViewBusinessAddress.setText(businessItem.getString("address"));
	                   textViewBusinessPhone.setText(businessItem.getString("phone"));
	                   textViewBusinessWebsite.setText(businessItem.getString("website"));
	          
	                
	            } catch (Exception e) {
	                Log.d("Exception onPostExecute", e.getLocalizedMessage());
	            }          
	        }
	    }
	
	
}