package com.book.worm;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHandler {
	
	//coloumns for table1
	public static final String KEY_ID="Book_ID";
	public static final String KEY_TITLE="Book_Title";
	public static final String KEY_AUTHOR="Author_Name";
	public static final String KEY_AVAILABILTY="Available";

	//table2 coloumns:
	public static final String KEY_IDNUMBER="ID_Number";
	public static final String KEY_NAME="Name";
	//Table 3 coloumns: due,idnumber + :
	public static final String KEY_DUE="Due";
	
	private static final String DB_NAME="Library";
	//tables
	private static final String DB_TABLE="Books";
	private static final String DB_TABLE_TWO="Users";
	private static final String DB_TABLE_THREE="Borrowed";
	
	private static final int DB_VERSION=1;
	
	private DbHelper ourHelper;
	private final Context ourContext;
	private SQLiteDatabase ourDatabase;
	
	String[]coloumns = new String[]{KEY_AUTHOR,KEY_ID,KEY_TITLE,KEY_AVAILABILTY};
	Cursor c ;
	String result[]=new String[4];

	
	
	private static class DbHelper extends SQLiteOpenHelper{

		public DbHelper(Context context) {
			super(context, DB_NAME, null,DB_VERSION);
		
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE "+ DB_TABLE +" ("+
					KEY_ID+" INTEGER PRIMARY KEY, "+
					KEY_TITLE +" TEXT NOT NULL, "+
					KEY_AVAILABILTY +" TEXT NOT NULL, "+
					KEY_AUTHOR+" TEXT NOT NULL);"
					);
			
			db.execSQL("CREATE TABLE "+ DB_TABLE_TWO +" ("+
					KEY_IDNUMBER+" INTEGER PRIMARY KEY, "+
					KEY_NAME +" TEXT NOT NULL);"
					);
			
			db.execSQL("CREATE TABLE "+ DB_TABLE_THREE +" ("+
					KEY_IDNUMBER +" INTEGER PRIMARY KEY, "+
					KEY_ID +" INTEGER NOT NULL, "+
					KEY_DUE+" TEXT NOT NULL);"
					);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS "+ DB_TABLE);
			db.execSQL("DROP TABLE IF EXISTS "+ DB_TABLE_TWO);
			db.execSQL("DROP TABLE IF EXISTS "+ DB_TABLE_THREE);
			onCreate(db);
			
		}
		
	}

	public DatabaseHandler(Context cu){
		
		ourContext=cu;
	}
	
	public DatabaseHandler open() throws SQLException{
		ourHelper = new DbHelper(ourContext);
		ourDatabase=ourHelper.getWritableDatabase();
		c = ourDatabase.query(DB_TABLE, coloumns, null, null, null, null, null);
		
		
		return this;
	}
	
	public void close(){
		ourHelper.close();
	}

	public long createEntry(String title, String author, String barcodeNumber) {
		ContentValues cv =new ContentValues();
		cv.put(KEY_ID,barcodeNumber);
		cv.put(KEY_TITLE, title);
		cv.put(KEY_AUTHOR, author);
		cv.put(KEY_AVAILABILTY,"Available");
		return ourDatabase.insert(DB_TABLE, null, cv);
		
	}

	public long editEntry(String title,String author,String barcodeNumber){
		ContentValues cv =new ContentValues();
		cv.put(KEY_ID,barcodeNumber);
		cv.put(KEY_TITLE, title);
		cv.put(KEY_AUTHOR, author);
		cv.put(KEY_AVAILABILTY,"Available");
		return ourDatabase.update(DB_TABLE, cv,KEY_ID+" ="+barcodeNumber, null);
	}
	
	public long borrowBook(String barcodeNumber,String idNumber,String days){
		ContentValues cv =new ContentValues();
		cv.put(KEY_IDNUMBER, idNumber);
		cv.put(KEY_ID, barcodeNumber);
		cv.put(KEY_DUE, days);
		return ourDatabase.insert(DB_TABLE_THREE, null, cv);
	}
	
	public long returnBook(String barcodeNumber){
		
		return ourDatabase.delete(DB_TABLE_THREE, KEY_ID+"="+barcodeNumber, null);
	}
	public long setAvailable(String barcode){
		ContentValues cv =new ContentValues();
		cv.put(KEY_AVAILABILTY,"Available");
		return ourDatabase.update(DB_TABLE, cv,KEY_ID+"="+barcode, null);
	}
	
	public long setUnavailable(String barcode){
		ContentValues cv =new ContentValues();
		cv.put(KEY_AVAILABILTY,"Unavailable");
		return ourDatabase.update(DB_TABLE, cv,KEY_ID+" ="+barcode, null);
	}
	
	public String[] getFirstData() {
		int iID= c.getColumnIndex(KEY_ID);
		int iAuthor= c.getColumnIndex(KEY_AUTHOR);
		int iTitle= c.getColumnIndex(KEY_TITLE);
		int iAvailable=c.getColumnIndex(KEY_AVAILABILTY);
		c.moveToFirst();
		result[0]=c.getString(iID);
		result[1]=c.getString(iTitle);
		result[2]=c.getString(iAuthor);
		result[3]=c.getString(iAvailable);
		
		return result;
	}
	
	public String[] getNextData() {
		int iID= c.getColumnIndex(KEY_ID);
		int iAuthor= c.getColumnIndex(KEY_AUTHOR);
		int iTitle= c.getColumnIndex(KEY_TITLE);
		int iAvailable=c.getColumnIndex(KEY_AVAILABILTY);
		
		if(!c.isLast()){
			 c.moveToNext();
		 
		result[0]=c.getString(iID);
		result[1]=c.getString(iTitle);
		result[2]=c.getString(iAuthor);
		result[3]=c.getString(iAvailable);
		
		}
		
		return result;
	}
	
	public String[] getPreviousData() {
		int iID= c.getColumnIndex(KEY_ID);
		int iAuthor= c.getColumnIndex(KEY_AUTHOR);
		int iTitle= c.getColumnIndex(KEY_TITLE);
		int iAvailable=c.getColumnIndex(KEY_AVAILABILTY);
		
		if(!c.isFirst()){
			 c.moveToPrevious();
		 
		result[0]=c.getString(iID);
		result[1]=c.getString(iTitle);
		result[2]=c.getString(iAuthor);
		result[3]=c.getString(iAvailable);
		
		}
		
		return result;
	}
	
	public String[] getData(String barcodeNumber){
		
		Cursor c1 = ourDatabase.query(DB_TABLE, coloumns, null, null, null, null, null);
		int iID= c1.getColumnIndex(KEY_ID);
		int iAuthor= c1.getColumnIndex(KEY_AUTHOR);
		int iTitle= c1.getColumnIndex(KEY_TITLE);
		int iAvailable=c.getColumnIndex(KEY_AVAILABILTY);
		
		for(c1.moveToFirst();!c1.getString(iID).equals(barcodeNumber);c1.moveToNext()){
		}

		result[0]=c1.getString(iID);
		result[1]=c1.getString(iTitle);
		result[2]=c1.getString(iAuthor);
		result[3]=c1.getString(iAvailable);
		
		return result;
		
	}
	
	public String[] getTitleData(String title){
		
		Cursor c2 = ourDatabase.query(DB_TABLE, coloumns, null, null, null, null, null);
		int iID= c2.getColumnIndex(KEY_ID);
		int iAuthor= c2.getColumnIndex(KEY_AUTHOR);
		int iTitle= c2.getColumnIndex(KEY_TITLE);
		int iAvailable=c.getColumnIndex(KEY_AVAILABILTY);
		
		for(c2.moveToFirst();!c2.getString(iTitle).equalsIgnoreCase(title);c2.moveToNext()){
		}

		result[0]=c2.getString(iID);
		result[1]=c2.getString(iTitle);
		result[2]=c2.getString(iAuthor);
		result[3]=c2.getString(iAvailable);
		
		return result;
		
	}
	
}
