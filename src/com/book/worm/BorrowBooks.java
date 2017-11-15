package com.book.worm;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.os.Build;

public class BorrowBooks extends Activity implements OnItemSelectedListener {
	String barcode,title,author;
	Button borrowMe;
	EditText barcodeArea,titleArea,authorArea,idArea;
	String days;
	DatabaseHandler db = new DatabaseHandler(this);
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_borrow_books);
		Intent intent = getIntent();
		barcode=intent.getStringExtra("com.book.worm.barcode");
		title=intent.getStringExtra("com.book.worm.title");
		author=intent.getStringExtra("com.book.worm.author");
		barcodeArea=(EditText)findViewById(R.id.barcodeTextview);
		titleArea=(EditText)findViewById(R.id.titleTextview);
		borrowMe=(Button)findViewById(R.id.checkOut);
		authorArea=(EditText)findViewById(R.id.authorTextview);
		idArea=(EditText)findViewById(R.id.idNumberText);
		barcodeArea.setText(barcode);
		titleArea.setText(title);
		authorArea.setText(author);
		
		Spinner spinner = (Spinner) findViewById(R.id.daysSpinner);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
		        R.array.spinner_array, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		spinner.setOnItemSelectedListener(this);
		
		borrowMe.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				db.open();
				db.borrowBook(barcodeArea.getText().toString(),idArea.getText().toString(),days);
				db.setUnavailable(barcodeArea.getText().toString());
				db.close();
				Dialog d = new Dialog(BorrowBooks.this);
				d.setTitle("Save Successful");
				TextView tv = new TextView(BorrowBooks.this);
				tv.setText(titleArea.getText().toString()+" with barcode "+barcodeArea.getText().toString()+" was successfully borrowed by id "+idArea.getText().toString());
				d.setContentView(tv);
				d.show();
				finish();
			}
		});
		
	}

	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		days=parent.getItemAtPosition(position).toString();
	}


	public void onNothingSelected(AdapterView<?> parent) {

		
	}
	


	
}
