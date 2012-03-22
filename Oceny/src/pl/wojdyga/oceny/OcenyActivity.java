/* Author: Aleksander Wojdyga <alek.wojdyga@gmail.com>
 * URL: http://code.google.com/p/Oceny
 * License: GNU GPL v3
 * */
package pl.wojdyga.oceny;

import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class OcenyActivity extends Activity implements OnItemClickListener {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        ListView mainLV = (ListView) findViewById(R.id.mainLV);
        mainLV.setOnItemClickListener (this);
        
        DBAdapter.assetManager = getAssets();              
    }
    
    @Override
    protected void onStart ()
    {
    	super.onStart();
    	openDatabase();
    }
    
    private void openDatabase ()
    {
    	try {
    		SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(this);

    		String dbFile;
    		boolean lastFile = SP.getBoolean("last_file", true);
    		if (lastFile) {
    			dbFile = SP.getString("last_file_name", DBAdapter.DEFAULT_DB_FILE);
    		} else {
    			dbFile = DBAdapter.DEFAULT_DB_FILE;
    		}
    		//Log.d("", "OcenyAct last_file="+lastFile+" dbFile="+dbFile);    		
    		DBAdapter.getInstance().openDatabase(dbFile);
    	} catch (SQLiteException e) {
    		e.printStackTrace();
    		Toast.makeText(getBaseContext(), e.toString(), Toast.LENGTH_LONG).show();
    	} catch (IOException e) {
    		e.printStackTrace();
    		Toast.makeText(getBaseContext(), e.toString(), Toast.LENGTH_LONG).show();
    		finish ();
    	}
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	menu.add(Menu.NONE, 0, 0, R.string.settings);
    	return super.onCreateOptionsMenu(menu);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 0:
                startActivity(new Intent(this, MyPreferenceActivity.class));
                return true;
        }
        return false;
    }
    
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) 
	{
		switch (position) {
		case 0: MainMediator.getInstance().startPickGroup(this); 
		break;
		case 1: MainMediator.getInstance().startEditGroups(this);
		break;
		case 2: MainMediator.getInstance().startDBManipulation(this);
		break;
		}
	}
}