/* Author: Aleksander Wojdyga <alek.wojdyga@gmail.com>
 * URL: http://code.google.com/p/Oceny
 * License: GNU GPL v3
 * */
package pl.wojdyga.oceny;

import java.io.IOException;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class BrowseGroupsActivity 
	extends ListActivity implements OnItemClickListener, OnClickListener 
{
	AlertDialog alertDialog;
	int action;
	int groupNum;
	private String groupId;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		updateListAdapter();
		
		getListView().setOnItemClickListener (this);
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.choose_group_actions);
		builder.setItems(R.array.group_actions, this);
		
		alertDialog = builder.create();
	}

	private void updateListAdapter ()
	{
		SimpleCursorAdapter cursorAdapter;
		try {
			cursorAdapter = new SimpleCursorAdapter(this, 
					android.R.layout.simple_list_item_1, 
					DBAdapter.getInstance().getGroupCursor (), 
					new String[]{"Nazwa"}, 
					new int[]{ android.R.id.text1});
			setListAdapter(cursorAdapter);
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

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) 
	{
		try {
			groupNum = position;
			groupId = DBAdapter.getInstance().getStringValueAtPosition(0, groupNum);
			alertDialog.show();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void onClick(DialogInterface arg0, int position) 
	{
		if (position == 0) {
			MainMediator.getInstance().startEditGroup (this, groupId);
		} else if (position == 1) {
			MainMediator.getInstance().startEditGroupGrades (this, groupId);
		} else if (position == 2) {
			MainMediator.getInstance().startEditStudentsInGroup (this, groupId);
		}
	}
}
