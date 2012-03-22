/* Author: Aleksander Wojdyga <alek.wojdyga@gmail.com>
 * URL: http://code.google.com/p/Oceny
 * License: GNU GPL v3
 * */
package pl.wojdyga.oceny;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.util.Log;

public class MyPreferenceActivity extends PreferenceActivity implements OnPreferenceClickListener, OnPreferenceChangeListener {
	Preference customPref;
	
	public static String PreferencesName = "pl.wojdyga.oceny.Preferences";
	CheckBoxPreference togglePref;
	Preference fileDialogPref;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setPreferenceScreen(createPreferenceHierarchy());
	} 
	
    private PreferenceScreen createPreferenceHierarchy() {
        PreferenceScreen root = getPreferenceManager().createPreferenceScreen(this);
        
        PreferenceCategory inlinePrefCat = new PreferenceCategory(this);
        inlinePrefCat.setTitle(R.string.db_file);
        root.addPreference(inlinePrefCat);

        togglePref = new CheckBoxPreference(this);
        togglePref.setKey("last_file");
        togglePref.setTitle(R.string.last_file);
        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(this);
        boolean last = SP.getBoolean("last_file", true);      
		if (last)
			togglePref.setSummary(R.string.using_last_file);
		else
			togglePref.setSummary(R.string.using_default_file);
		togglePref.setChecked(last);		
		togglePref.setOnPreferenceClickListener(new OnPreferenceClickListener(){
			public boolean onPreferenceClick(Preference preference) {
				boolean lastFile = togglePref.isChecked();
				if (lastFile) {
					togglePref.setSummary(R.string.using_last_file);
				} else {
					togglePref.setSummary(R.string.using_default_file);
				}
				fileDialogPref.setEnabled(lastFile);
				return true;
			}
		});
		togglePref.setOnPreferenceChangeListener(this);
		
        inlinePrefCat.addPreference(togglePref);
        
        fileDialogPref = new Preference(this);
        fileDialogPref.setKey("last_file_name");
        fileDialogPref.setTitle(R.string.last_file_name);
        fileDialogPref.setOnPreferenceClickListener(this);
        String fileName = SP.getString("last_file_name", DBAdapter.DEFAULT_DB_FILE);
        if (last)
        	fileDialogPref.setSummary(fileName);
        else
        	fileDialogPref.setSummary(R.string.using_default_file);
        fileDialogPref.setEnabled(last);
        fileDialogPref.setOnPreferenceChangeListener(this);
        
        inlinePrefCat.addPreference(fileDialogPref);
        
        //Log.d("", "pref lastFile="+last+" fileName="+fileName);
        return root;
    }
	
    
	public boolean onPreferenceClick(Preference preference) {
		Intent intent = new Intent(this, FileDialog.class);
        intent.putExtra(FileDialog.START_PATH, "/sdcard");
        intent.putExtra(FileDialog.CAN_SELECT_DIR, false);
        intent.putExtra(FileDialog.OPTION_ONE_CLICK_SELECT, true);
        intent.putExtra(FileDialog.SELECTION_MODE, SelectionMode.MODE_OPEN);        
        startActivityForResult(intent, StartDBManipulationActivity.REQUEST_BROWSE_OPEN);
               
        return true;
	}
	
	public synchronized void onActivityResult(final int requestCode,
            int resultCode, final Intent data) 
	{
		if (resultCode == Activity.RESULT_OK) {
			String fileName = data.getStringExtra(FileDialog.RESULT_PATH);
			//Log.d("", "fileName="+fileName);
			if (requestCode == StartDBManipulationActivity.REQUEST_BROWSE_OPEN) {
				fileDialogPref.setSummary(fileName);
				MainMediator.getInstance().rememberLastFile(this, fileName);				
			}
		}
	}

	@Override
	public boolean onPreferenceChange(Preference arg0, Object arg1) {
		SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = SP.edit();
        if (arg0 == togglePref) {
        	boolean b = ((Boolean) arg1).booleanValue();
        	editor.putBoolean("last_file", b);
        	//Log.d("", "last_file="+b);
        } else if (arg0 == fileDialogPref) {
        	String last_file_name = (String) arg1;
        	editor.putString("last_file_name", last_file_name);
        	//Log.d("", "last_file_name="+last_file_name);
        }
        editor.commit();
        
		return true;
	}
}
