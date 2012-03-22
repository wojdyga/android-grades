/* Author: Aleksander Wojdyga <alek.wojdyga@gmail.com>
 * URL: http://code.google.com/p/Oceny
 * License: GNU GPL v3
 * */
package pl.wojdyga.oceny;

import java.io.IOException;
import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class PickStudentActivity 
	extends ListActivity 
	implements OnItemClickListener 
{
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
      
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
		} catch (IOException e) {
			e.printStackTrace();
			Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
		}
	
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) 
	{
		try {
			MainMediator.getInstance().startPickGrades(this, 
					DBAdapter.getInstance().getStringValueAtPosition(0, position));
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
