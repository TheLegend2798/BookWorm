package com.book.worm;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class RetrieveData extends Activity {
Button searchByScan,searchByTitle,displayAll,next,prev,save,borrowThisBook,returnBook;
EditText barcode,title,author,searchTitle,availability;
DatabaseHandler dbH;
IntentIntegrator scanIntegrator = new IntentIntegrator(this);
String barcodeNumber;
String[] results;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.library_layout);
		//initialisation of all objects to be used
		dbH=new DatabaseHandler(RetrieveData.this);
		searchByScan=(Button)findViewById(R.id.searchScan);
		searchByTitle=(Button)findViewById(R.id.searchTitle);
		displayAll=(Button)findViewById(R.id.displayAll);
		next=(Button)findViewById(R.id.nextB);
		prev=(Button)findViewById(R.id.previousB);
		borrowThisBook=(Button)findViewById(R.id.borrowBook);
		next.setVisibility(View.INVISIBLE);
		prev.setVisibility(View.INVISIBLE);
		save=(Button)findViewById(R.id.saveChanges);
		barcode=(EditText)findViewById(R.id.barcodeText);
		author=(EditText)findViewById(R.id.authorText);
		title=(EditText)findViewById(R.id.titleText);
		searchTitle=(EditText)findViewById(R.id.inputTitle);
		availability=(EditText)findViewById(R.id.available);
		returnBook=(Button)findViewById(R.id.returnBook);
		borrowThisBook.setVisibility(View.INVISIBLE);
		returnBook.setVisibility(View.INVISIBLE);
	
		//onclicklisteners for all buttons
		displayAll.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				dbH.open();
				results=dbH.getFirstData();
				dbH.close();
				
				barcode.setText(results[0]);
				title.setText(results[1]);
				author.setText(results[2]);
				availability.setText(results[3]);
				
				if(availability.getText().toString().equals("Available")){
					borrowThisBook.setVisibility(View.VISIBLE);
					returnBook.setVisibility(View.INVISIBLE);
				}
				if(availability.getText().toString().equals("Unavailable")){
					returnBook.setVisibility(View.VISIBLE);
					borrowThisBook.setVisibility(View.INVISIBLE);
				}
				
				next.setVisibility(View.VISIBLE);
				prev.setVisibility(View.VISIBLE);
			}
			
		});
		searchByTitle.setOnClickListener(new View.OnClickListener() {
		    public void onClick(View v) {
		    	String titleToSearch=searchTitle.getText().toString();
				dbH.open();
		    	String[] result=dbH.getTitleData(titleToSearch);
		    	dbH.close();
				barcode.setText(result[0]);
				title.setText(result[1]);
				author.setText(result[2]);
				availability.setText(result[3]);
				if(availability.getText().toString().equals("Available")){
					borrowThisBook.setVisibility(View.VISIBLE);
					returnBook.setVisibility(View.INVISIBLE);
				}
				if(availability.getText().toString().equals("Unavailable")){
					returnBook.setVisibility(View.VISIBLE);
					borrowThisBook.setVisibility(View.INVISIBLE);
				}
				
		    	

	    }
	});
		searchByScan.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				scanIntegrator.initiateScan();
				
			}
		});
		save.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				try{
				dbH.open();
				dbH.editEntry(title.getText().toString(), author.getText().toString(), barcode.getText().toString());
				dbH.close();
				}catch(Exception e){};
				
				Dialog d = new Dialog(RetrieveData.this);
				d.setTitle("Save Successful");
				TextView tv = new TextView(RetrieveData.this);
				tv.setText(title.getText().toString()+" written by "+author.getText().toString()+" was successfully saved in library with id "+barcode.getText().toString());
				d.setContentView(tv);
				d.show();
				displayAll.performClick();
			}
		});
		next.setOnClickListener(new View.OnClickListener(){
			  public void onClick(View v) {
				//dbH.open();
				results=dbH.getNextData();
				dbH.close();
				barcode.setText(results[0]);
				title.setText(results[1]);
				author.setText(results[2]);
				availability.setText(results[3]);
			  }
		});
		prev.setOnClickListener(new View.OnClickListener(){
			  public void onClick(View v) {
				 //dbH.open();
				 results=dbH.getPreviousData();
				 dbH.close();
					barcode.setText(results[0]);
					title.setText(results[1]);
					author.setText(results[2]);
					availability.setText(results[3]);
			  }
		});
		borrowThisBook.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				Intent intent = new Intent(RetrieveData.this, BorrowBooks.class);
				intent.putExtra("com.book.worm.barcode",barcode.getText().toString());
				intent.putExtra("com.book.worm.title",title.getText().toString());
				intent.putExtra("com.book.worm.author",author.getText().toString());
				startActivity(intent);
				
			}
		}); 
		returnBook.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				dbH.open();
				dbH.returnBook(barcode.getText().toString());
				dbH.setAvailable(barcode.getText().toString());
				dbH.close();
				displayAll.performClick();
			}
		});
	}
	
	
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
		if (scanningResult != null) {
			barcodeNumber = scanningResult.getContents();
			dbH.open();
			String[] result=dbH.getData(barcodeNumber);
			dbH.close();
			barcode.setText(result[0]);
			title.setText(result[1]);
			author.setText(result[2]);
			availability.setText(result[3]);
			if(availability.getText().toString().equals("Available")){
				borrowThisBook.setVisibility(View.VISIBLE);
				returnBook.setVisibility(View.INVISIBLE);
			}
			if(availability.getText().toString().equals("Unavailable")){
				returnBook.setVisibility(View.VISIBLE);
				borrowThisBook.setVisibility(View.INVISIBLE);
			}
			
		}	
		else{
		    Toast toast = Toast.makeText(getApplicationContext(),
		        "No scan data received!", Toast.LENGTH_SHORT);
		    toast.show();
		}
	
	}
	
	
}
