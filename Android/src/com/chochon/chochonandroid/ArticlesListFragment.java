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

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.widget.ArrayAdapter;

public class ArticlesListFragment extends ListFragment {
	
	
	private ArrayList<String> listArticles =  new ArrayList<String>()  ;
		
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	
		//Get all articles
		String url = getString(R.string.chochon_webserver) + "/articles";       	
		new GetBusinessTask().execute( url );
      
		
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
	            	
	            	JSONArray businessNameItems = new JSONArray(result);
	            
	                for (int i = 0; i < businessNameItems.length(); i++) {
	                	
	                    JSONObject articleItem = businessNameItems.getJSONObject(i);        
	   
	                   listArticles.add(articleItem.getString("content"));
	                }
	            	
	                setListAdapter(new ArrayAdapter<String>(getActivity(),
	                            android.R.layout.simple_list_item_1, listArticles ));
	               
	                
	            } catch (Exception e) {
	                Log.d("Exception onPostExecute", e.getLocalizedMessage());
	            }          
	        }
	    }
	
}