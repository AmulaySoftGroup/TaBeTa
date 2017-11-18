package ir.amulay.tabeta.database;

import ir.amulay.tabeta.globals.Constants;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class InternalDB extends SQLiteOpenHelper {

	private final Context mycontext;
	private SQLiteDatabase mydb;
	 private static InternalDB sInstance;


	
	
	  public static synchronized InternalDB getInstance(Context context) {
		    // Use the application context, which will ensure that you 
		    // don't accidentally leak an Activity's context.
		    // See this article for more information: http://bit.ly/6LRzfx
		    if (sInstance == null) {
		      sInstance = new InternalDB(context);
		    }
		    return sInstance;
		  }

	public InternalDB (Context context) {
		super(context, Constants.DBName, null, 1);
		mycontext = context;
	}

	
	public void onCreate(SQLiteDatabase arg0) {
		
		
	}

 
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
	
		
	} 
	 
	//if there is no database then copy it from assets
	public void database(){
		if(!doesDatabaseExist(mycontext,Constants.DBName)){
			Log.e("Database", "Database DOESENT Exist!");
			Log.e("In dataabse Method", "Creating New Database");
			this.getReadableDatabase();
			try{
				copydatabase(); 
			}catch(IOException e){
				Log.e("EROR IN COPY DATABASE", e.getStackTrace().toString());
			}
			//***********Delete Last Database *************
			if(doesDatabaseExist(mycontext,Constants.PreDBName)){
				Log.e("In dataabse Method", "Deleting PreDb Database");
			 final File f0 = new File("data/data/ir.amulay.tabeta/databases/"+Constants.PreDBName);
			 f0.delete(); 
			 //*******************************************
			}
		}
		else{
			Log.e("Database", "Database Exist!");
		}
	}
	
	
	private static boolean doesDatabaseExist(Context context, String dbName) {
	    File dbFile = context.getDatabasePath(dbName);
	    return dbFile.exists();
	}


	public void copydatabase () throws IOException{
		OutputStream myoutput = new FileOutputStream(Constants.DBPath+Constants.DBName);
		byte[] buffer = new byte[1024];
		int length;
		
		InputStream myinput = mycontext.getAssets().open(Constants.DBName);
		while( (length = myinput.read(buffer)) > 0){
			myoutput.write(buffer, 0, length);
		}
		myinput.close();
		myoutput.flush();
		myoutput.close();
		
	}
	
	public void open(){
		mydb = SQLiteDatabase.openDatabase(Constants.DBPath+Constants.DBName, null, SQLiteDatabase.OPEN_READWRITE);
	}
	
	public void close(){
		mydb.close();
	}
	
	public float GetPointX(int level, int n){
		Cursor cu = mydb.query("Pointsx", null, "ID="+level, null, null, null, null);
		cu.moveToPosition(0);
		float point = cu.getFloat(n+1);
		return point;
	}

	public float GetPointY(int level, int n){
		Cursor cu = mydb.query("Pointsy", null, "ID="+level, null, null, null, null);
		cu.moveToPosition(0);
		float point = cu.getFloat(n+1);
		return point;
	}
	
	public double GetCurPosX(int level, int n){

		Cursor cu = mydb.query("CurlPosx", null, "ID="+level, null, null, null, null);
		cu.moveToPosition(0);
		double point = cu.getDouble(n+1);
		
		return point;
		
	}
	
	public double GetCurPosY(int level, int n){

		Cursor cu = mydb.query("CurlPosy", null, "ID="+level, null, null, null, null);
		cu.moveToPosition(0);
		double point = cu.getDouble(n+1);
		
		return point;
		
	}
	
	public double GetCurlDirX(int level, int n){

		Cursor cu = mydb.query("CurlDirx", null, "ID="+level, null, null, null, null);
		cu.moveToPosition(0);
		double point = cu.getDouble(n+1);
		
		return point;
		
	}
	
	public double GetCurlDirY(int level, int n){

		Cursor cu = mydb.query("CurlDiry", null, "ID="+level, null, null, null, null);
		cu.moveToPosition(0);
		double point = cu.getDouble(n+1);
		
		return point;
		
	}
	
	public int GetPointsCount(int level){
		
		int n=0 ;

		Cursor cu = mydb.query("Pointsx", null, "ID="+level, null, null, null, null);
		cu.moveToPosition(0);
		
		for(int i=1;i<=24;i++){
			
			if(cu.getFloat(i) != 8){
				n +=1;
			}else if(cu.getFloat(i) == 8)break;
			
		}
		
		return n;
		
	}
	
	public int GetHintCount(int level){

		int n=0 ;

		Cursor cu = mydb.query("CurlPosx", null, "ID="+level, null, null, null, null);
		
		if(cu.getCount()>0){
		cu.moveToPosition(0);
		
		for(int i=1;i<=8;i++){
			
			if(cu.getFloat(i) != 8){
				n +=1;
			}else if(cu.getFloat(i) == 8)break;
			
		}
		}
		return n;
		}
	

	
}//End of Class

