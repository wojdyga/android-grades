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
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class StartGroupManipulationActivity 
	extends Activity implements OnItemClickListener 
{
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
        setContentView(R.layout.groupstudmain);
        
        ListView menuLV = (ListView) findViewById(R.id.groupstudmenu);
        menuLV.setOnItemClickListener (this);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) 
	{
		switch (position) {
		case 0: MainMediator.getInstance().startAddGroup (this);
			break;
		case 1: MainMediator.getInstance().startBrowseGroups (this);
			break;
		case 2: MainMediator.getInstance().startAddStudent (this);
			break;
		case 3: MainMediator.getInstance().startFindStudent (this);
			break;
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		//Log.d("", "requestCode="+requestCode+ " resultCode="+resultCode);
		
		if (requestCode == MainMediator.ADD_STUDENT_REQUEST) {
			if (resultCode == Activity.RESULT_OK) {
				long id = data.getLongExtra(MainMediator.STUDENTID_EXTRA, -1);
				MainMediator.getInstance().startPickGroupsFor(id, this);
			} else if (resultCode == Activity.RESULT_CANCELED) {
				
			}
		} else if (requestCode == MainMediator.ADD_STUDENT_GROUPS_REQUEST) {
			if (resultCode == Activity.RESULT_OK) {
				long studid = data.getLongExtra(MainMediator.STUDENTID_EXTRA, -1);
				long[] groupids = data.getLongArrayExtra(MainMediator.STUDENT_GROUPSID_EXTRA);
				//Log.d("", "studid="+studid+" groupsid.length="+(groupids == null ? -1 : groupids.length));
				try {
					if (DBAdapter.getInstance().addStudentGroups(studid, groupids))
						Toast.makeText(this, R.string.new_student_groups, Toast.LENGTH_SHORT);
					else
						Toast.makeText(this, R.string.cant_add_student_groups, Toast.LENGTH_LONG);
				} catch (IOException e) {
					// tu nie powinno być żadnych wyjątków
					e.printStackTrace();
				}
			}
		} else if (requestCode == MainMediator.BROWSE_STUDENT_REQUEST) {
			if (resultCode == Activity.RESULT_OK) {
				MainMediator.getInstance().startEditStudent (this, data.getStringExtra(MainMediator.STUDENTID_EXTRA));
			}
		}
	}
}
