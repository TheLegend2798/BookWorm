package com.book.worm;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class SplashScreen extends Activity implements OnClickListener{
	private Button toLibrary,addBook,myBorrowed;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash_layout);
		
		toLibrary = (Button)findViewById(R.id.library);
		addBook = (Button)findViewById(R.id.addBook);
		myBorrowed = (Button)findViewById(R.id.myBorrowed);
		toLibrary.setOnClickListener(this);
		addBook.setOnClickListener(this);
		myBorrowed.setOnClickListener(this);
		
	}

	public void onClick(View v) {
		switch(v.getId()){
		case(R.id.library):
			Intent intent = new Intent(this, RetrieveData.class);
			startActivity(intent);
			break;
		case(R.id.addBook):
			Intent intent2 = new Intent(this, MainActivity.class);
			startActivity(intent2);
			break;
		//case(R.id.myBorrowed):
		
		}
		
	}

}
