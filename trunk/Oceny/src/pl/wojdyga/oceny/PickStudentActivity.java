/* Author: Aleksander Wojdyga <alek.wojdyga@gmail.com>
 * URL: http://code.google.com/p/Oceny
 * License: GNU GPL v3
 * */
package pl.wojdyga.oceny;

import java.io.IOException;
import android.app.ListActivity;
import android.database.sqlite.SQLiteCursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class PickStudentActivity 
	extends ListActivity 
	implements OnItemClickListener 
{
	String studentId;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
      
		studentId = getIntent().getStringExtra(MainMediator.LASTSTUDENTID_EXTRA);
		
		updateListAdapter();
		
        getListView().setOnItemClickListener (this);
	}

	private void updateListAdapter ()
	{
		try {
			String groupId = getIntent().getStringExtra(MainMediator.GROUPID_EXTRA);
			
			SimpleCursorAdapter cursorAdapter;
			cursorAdapter = new SimpleCursorAdapter(this, 
					android.R.layout.simple_list_item_1, 
					DBAdapter.getInstance().getStudentCursor (groupId), 
					new String[]{"Nazwa"}, 
					new int[]{ android.R.id.text1});
			
			setListAdapter(cursorAdapter);
			
			int i = 0;
			for (; i < getListAdapter().getCount(); i++) {
				SQLiteCursor cursor = (SQLiteCursor) getListAdapter().getItem(i);
				Log.d("halo", cursor.getString(1));
				if (cursor.getString(0).compareTo(studentId) == 0)
					break;
			}
			if (i < getListAdapter().getCount()) {
				getListView().smoothScrollToPosition(i);
			}
		} catch (IOException e) {
			e.printStackTrace();
			Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
		}
	
	}
	
	void rememberLastStudentId (String sid)
	{
		studentId = sid;
		MainMediator.getInstance().setLastClickedStudentID(studentId);
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) 
	{
		try {
			String studentID = DBAdapter.getInstance().getStringValueAtPosition(0, position);
			MainMediator.getInstance().startPickGrades(this, studentID);
			rememberLastStudentId(studentID);
		} catch (IOException e) {
			e.printStackTrace();
			Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
		}		
	}
	
	protected void onRestart() 
	{
		super.onRestart(); 
		updateListAdapter(); 
	}
}
