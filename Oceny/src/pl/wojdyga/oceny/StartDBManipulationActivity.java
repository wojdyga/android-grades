/* Author: Aleksander Wojdyga <alek.wojdyga@gmail.com>
 * URL: http://code.google.com/p/Oceny
 * License: GNU GPL v3
 * */
package pl.wojdyga.oceny;

import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

public class StartDBManipulationActivity 
	extends Activity implements OnItemClickListener
{
	public static final int REQUEST_BROWSE_OPEN = 0;
	public static final int REQUEST_BROWSE_IMPORT = 1;
	public static final int REQUEST_BROWSE_EXPORT = 2;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dbmain);
		ListView dbmainLV = (ListView) findViewById(R.id.dbmainLV);
        dbmainLV.setOnItemClickListener (this);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) 
	{
		switch (position) {
			case 0:
				MainMediator.getInstance().startNewDB (this);
			break;
			case 1:
				MainMediator.getInstance().openAndUseDB (this);
			break;
			case 2: 
				MainMediator.getInstance().importDB (this);
			break;
			case 3: 
				MainMediator.getInstance().exportDB (this);
			break;
		}		
	}
	
	public synchronized void onActivityResult(final int requestCode,
            int resultCode, final Intent data) 
	{
		//Log.d("StartDBManipulationActivity", "onActivityResult requestCode="+requestCode+" resultCode="+resultCode);
		try {
			if (resultCode == Activity.RESULT_OK) {
				String fileName = data.getStringExtra(FileDialog.RESULT_PATH);
			 
				if (requestCode == REQUEST_BROWSE_OPEN) {            	 
					DBAdapter.getInstance().openDatabase(fileName);					
					MainMediator.getInstance().rememberLastFile(this, fileName);
				} else if (requestCode == REQUEST_BROWSE_IMPORT) {         	
         			DBAdapter.importDBFromFile(fileName, DBAdapter.DEFAULT_DB_FILE);
         			DBAdapter.getInstance().openDatabase(DBAdapter.DEFAULT_DB_FILE);         		
				} else if (requestCode == REQUEST_BROWSE_EXPORT) {
            		DBAdapter.getInstance().exportCurrentDBToFile (fileName);         	
				}
			}
		} catch (IOException e) {
 			e.printStackTrace();
 			Toast.makeText(getBaseContext(), e.toString(), Toast.LENGTH_LONG).show();
 		}
	}
	
}
