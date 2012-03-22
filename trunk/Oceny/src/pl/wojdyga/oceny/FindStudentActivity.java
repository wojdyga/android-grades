/* Author: Aleksander Wojdyga <alek.wojdyga@gmail.com>
 * URL: http://code.google.com/p/Oceny
 * License: GNU GPL v3
 * */
package pl.wojdyga.oceny;

import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class FindStudentActivity 
	extends Activity 
	implements OnClickListener, TextWatcher, OnItemClickListener 
{
	EditText nameET;
	
	@Override
	protected void onCreate (Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.browse_student);
		
		ImageButton clearIB = (ImageButton) findViewById(R.id.clearIB);
		clearIB.setOnClickListener(this);
		
		nameET = (EditText) findViewById(R.id.nameFragmentET);
		nameET.addTextChangedListener(this);
		
		ListView studentsLV = (ListView) findViewById(R.id.studentsLV);
		studentsLV.setOnItemClickListener(this);
		
		setResult(RESULT_CANCELED);
	}

	@Override
	public void onClick(View arg0) {		
		nameET.setText("");
	}

	private void updateListAdapter ()
	{
		String name = nameET.getText().toString();
		
		if ((name.length() >= 3) && (getString(R.string.min3chars).compareTo(name) != 0)) {
			ListView lv = (ListView) findViewById(R.id.studentsLV);
			Cursor cursor;
			try {
				cursor = DBAdapter.getInstance().getAllStudentsCursor(name);
				lv.setAdapter(new SimpleCursorAdapter(this, 
						android.R.layout.simple_list_item_2, 
						cursor, 
						new String[]{"Nazwa", "Grupy"}, 
						new int[]{android.R.id.text1, android.R.id.text2}));
			} catch (IOException e) {
				// tu nie powinno być żadnych wyjątków
				e.printStackTrace();
			}
		}
	}
	
	protected void onRestart() 
	{
		super.onRestart(); 
		updateListAdapter(); 
	}
	
	@Override
	public void afterTextChanged(Editable s) {
		updateListAdapter();
	}

	@Override
	public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
			int arg3) {
	}

	@Override
	public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) 
	{
		try {
			Intent intent = new Intent();
			intent.putExtra(MainMediator.STUDENTID_EXTRA, DBAdapter.getInstance().getStringValueAtPosition(0, position));
			setResult(RESULT_OK, intent);
			finish();
		} catch (IOException e) {
			// tu nie ma żadnych wyjatków
			e.printStackTrace();
		}
	}
}
