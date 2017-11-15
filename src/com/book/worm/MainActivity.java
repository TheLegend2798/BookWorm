package com.book.worm;

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
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.database.SQLException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class MainActivity extends Activity implements OnClickListener {

	private Button scanBtn,saveB;
	private EditText TitleArea,AuthorArea,BarcodeArea;
	private String title,author;
	DatabaseHandler entry;
	boolean  checkifSaved = true;
	String barcodeNumber;

	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_main);
		
		scanBtn = (Button)findViewById(R.id.scan_Button);
		saveB=(Button)findViewById(R.id.saveB);
		TitleArea=(EditText)findViewById(R.id.TitleArea);
		AuthorArea=(EditText)findViewById(R.id.AuthorArea);
		BarcodeArea=(EditText)findViewById(R.id.BarcodeArea);
		scanBtn.setOnClickListener(this);
		saveB.setOnClickListener(this);
		
		entry = new DatabaseHandler(MainActivity.this);
		
	}
	    
	public void onClick(View v) throws SQLException{
		if(v.getId()==R.id.scan_Button){
			IntentIntegrator scanIntegrator = new IntentIntegrator(this);
			scanIntegrator.initiateScan();
			}
		
		if(v.getId()==R.id.saveB){
			try{
			entry.open();
			title=TitleArea.getText().toString();
			author=AuthorArea.getText().toString();
			barcodeNumber=BarcodeArea.getText().toString();
			entry.createEntry(title,author,barcodeNumber);
			entry.close();
		
			if(checkifSaved){
				Dialog d = new Dialog(this);
				d.setTitle("Save Successful");
				TextView tv = new TextView(this);
				tv.setText(title+" written by "+author+" was successfully saved in library with id "+ barcodeNumber);
				d.setContentView(tv);
				d.show();
					}
	        
			}catch(Exception e){
					checkifSaved=false;
					String error = e.toString();
					Dialog d = new Dialog(this);
					d.setTitle("Save Failed");
					TextView tv = new TextView(this);
					tv.setText("Error "+ error);
					d.setContentView(tv);
					d.show();
			}

		}
		
		
		
		
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
		if (scanningResult != null) {
			barcodeNumber = scanningResult.getContents();
			String scanFormat = scanningResult.getFormatName();
			if(barcodeNumber!=null && scanFormat!=null && scanFormat.equalsIgnoreCase("EAN_13")){
				String bookSearchString = "https://www.googleapis.com/books/v1/volumes?"+
					    "q=isbn:"+barcodeNumber+"&key=AIzaSyCmcsRq5ibn4vLupYTUXMsnMhLAmJeSnRA";
				new GetBookInfo().execute(bookSearchString);
				
			}
			else{
				Toast toast = Toast.makeText(getApplicationContext(),"Not a valid scan!", Toast.LENGTH_SHORT);
				toast.show();
			}
			
			BarcodeArea.setText(barcodeNumber);
			

			
			}	
		else{
		    Toast toast = Toast.makeText(getApplicationContext(),
		        "No scan data received!", Toast.LENGTH_SHORT);
		    toast.show();
		}
	
	}
	
	private class GetBookInfo extends AsyncTask<String, Void, String> {
		protected String doInBackground(String... bookURLs) {
			StringBuilder bookBuilder = new StringBuilder();
			for (String bookSearchURL : bookURLs) {
				HttpClient bookClient = new DefaultHttpClient();
				try {
					HttpGet bookGet = new HttpGet(bookSearchURL);
					HttpResponse bookResponse = bookClient.execute(bookGet);
					StatusLine bookSearchStatus = bookResponse.getStatusLine();
					if (bookSearchStatus.getStatusCode()==200) {
						HttpEntity bookEntity = bookResponse.getEntity();
						InputStream bookContent = bookEntity.getContent();
						InputStreamReader bookInput = new InputStreamReader(bookContent);
						BufferedReader bookReader = new BufferedReader(bookInput);
						String lineIn;
						while ((lineIn=bookReader.readLine())!=null) {
						    bookBuilder.append(lineIn);
						}
					}
					
				}
				catch(Exception e){}
				}
			
				return bookBuilder.toString();
			}		
		
		protected void onPostExecute(String result) {
				try{
					JSONObject resultObject = new JSONObject(result);
					JSONArray bookArray = resultObject.getJSONArray("items");
					JSONObject bookObject = bookArray.getJSONObject(0);
					JSONObject volumeObject = bookObject.getJSONObject("volumeInfo");
					
				
				try{ 
					TitleArea.setText(volumeObject.getString("title")); }
				
				catch(JSONException jse){ 
				    TitleArea.setText("Unknown");
				    jse.printStackTrace(); 
				}
				
				
				
				StringBuilder authorBuild = new StringBuilder("");
				try{
				    JSONArray authorArray = volumeObject.getJSONArray("authors");
				    for(int a=0; a<authorArray.length(); a++){
				        if(a>0) authorBuild.append(", ");
				        authorBuild.append(authorArray.getString(a));
				    }
				   AuthorArea.setText(authorBuild.toString());
				}
				catch(JSONException jse){ 
					AuthorArea.setText("Unknown");
				    jse.printStackTrace(); 
				}
			
				
			}
			catch (Exception e) {
			e.printStackTrace();
			
			}
		}
	}

	
}
