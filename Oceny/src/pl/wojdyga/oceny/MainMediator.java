/* Author: Aleksander Wojdyga <alek.wojdyga@gmail.com>
 * URL: http://code.google.com/p/Oceny
 * License: GNU GPL v3
 * */
package pl.wojdyga.oceny;

import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteException;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

public class MainMediator {	
	public static final String GROUPID_EXTRA = "pl.wojdyga.Oceny.group";
	public static final String STUDENTID_EXTRA = "pl.wojdyga.Oceny.student";
	public static final String GRADE_EXTRA = "pl.wojdyga.Oceny.grade";
	public static final String STUDENTGRADEID_EXTRA = "pl.wojdyga.Oceny.studentgradeid";
	public static final String TASKID_EXTRA = "pl.wojdyga.Oceny.taskid";
	public static final String GRADEID_EXTRA = "pl.wojdyga.Oceny.gradeid";
	public static final String STUDENT_GROUPSID_EXTRA = "pl.wojdyga.Oceny.studentgroupsid";
	public static final String TASKNAME_EXTRA = "pl.wojdyga.Oceny.taskname";
	public static final String FAMILYNAME_EXTRA = "pl.wojdyga.Oceny.familyname";
	public static final String NAME_EXTRA = "pl.wojdyga.Oceny.name";
	public static final String INDEXNUM_EXTRA = "pl.wojdyga.Oceny.indexnum";
	public static final String KEYNUM_EXTRA = "pl.wojdyga.Oceny.keynum";
	public static final String LASTTASKNAME_EXTRA = "pl.wojdyga.Oceny.lasttaskname";
	
	public static final int ADD_GRADE_REQUEST = 0;
	public static final int EDIT_GRADE_REQUEST = 1;
	public static final int ADD_STUDENT_REQUEST = 2;
	public static final int ADD_STUDENT_GROUPS_REQUEST = 3;
	public static final int BROWSE_GROUPS_REQUEST = 4;
	public static final int BROWSE_STUDENT_REQUEST = 5;
	
	private String lastClickedTaskName;
	
	private MainMediator ()
	{
		lastClickedTaskName = "(null)";
	}
	
	public void startPickGroup (Activity act)
	{
		Intent intent = new Intent(act, PickGroupActivity.class);
		act.startActivity(intent);
	}
	
	public void startPickStudent (Activity act, String groupID)
	{
		//Log.d("", "startPickStudent "+ groupID);
		
		Intent intent = new Intent(act, PickStudentActivity.class);
		intent.putExtra(GROUPID_EXTRA, groupID);		
		act.startActivity(intent);
	}
	
	public void startPickGrades (Activity act, String studentID)
	{
		String groupID = act.getIntent().getStringExtra(GROUPID_EXTRA);
		//Log.d("", "startPickGrades groupID="+groupID+" studentID="+studentID);
		
		Intent intent = new Intent(act, PickGradesActivity.class);
		intent.putExtra(STUDENTID_EXTRA, studentID);		
		intent.putExtra(GROUPID_EXTRA, groupID);
		intent.putExtra(LASTTASKNAME_EXTRA, lastClickedTaskName);
		act.startActivity(intent);
	}
	
	private static MainMediator mediator;
	
	public static MainMediator getInstance ()
	{
		if (mediator == null) {
			mediator = new MainMediator();
		}
		
		return mediator;
	}

	public void startEditGroups (Activity activity) 
	{
		Intent intent = new Intent(activity, StartGroupManipulationActivity.class);
		activity.startActivity(intent);
	}

	public void startDBManipulation (Activity activity) 
	{
		Intent intent = new Intent(activity, StartDBManipulationActivity.class);
		activity.startActivity(intent);
	}
	
	public void startNewDB (final Activity activity)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
	    builder.setMessage(R.string.create_empty)
	           .setCancelable(false)
	           .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
	        	   public void onClick(DialogInterface dialog, int id) {	            	   
	        		   try {
	        			   DBAdapter.getInstance().createEmptyTables();	
	        			   rememberLastFile(activity, DBAdapter.DEFAULT_DB_FILE);
	        		   } catch (SQLiteException e) {
	        			   e.printStackTrace();
	        			   Toast.makeText(activity, e.toString(), Toast.LENGTH_LONG).show();
	        		   } catch (IOException e) {
	        			   e.printStackTrace();
	        			   Toast.makeText(activity, e.toString(), Toast.LENGTH_LONG).show();
	        		   }
	               }
	           })
	           .setNegativeButton(R.string.no, null);
	    AlertDialog alert = builder.create();
	    alert.show();
	}

	public void openAndUseDB(Activity act) 
	{
		Intent intent = new Intent(act, FileDialog.class);
        intent.putExtra(FileDialog.START_PATH, "/sdcard");
        intent.putExtra(FileDialog.CAN_SELECT_DIR, false);
        intent.putExtra(FileDialog.OPTION_ONE_CLICK_SELECT, true);
        intent.putExtra(FileDialog.SELECTION_MODE, SelectionMode.MODE_OPEN);
        
        act.startActivityForResult(intent, StartDBManipulationActivity.REQUEST_BROWSE_OPEN);		        
	}

	public void importDB(Activity act) 
	{
		Intent intent = new Intent(act, FileDialog.class);
        intent.putExtra(FileDialog.START_PATH, "/sdcard");
        intent.putExtra(FileDialog.CAN_SELECT_DIR, false);
        intent.putExtra(FileDialog.OPTION_ONE_CLICK_SELECT, true);
        intent.putExtra(FileDialog.SELECTION_MODE, SelectionMode.MODE_OPEN);
        
        act.startActivityForResult(intent, StartDBManipulationActivity.REQUEST_BROWSE_IMPORT);
	}

	public void exportDB(Activity act) 
	{
		Intent intent = new Intent(act, FileDialog.class);
        intent.putExtra(FileDialog.START_PATH, "/sdcard");
        intent.putExtra(FileDialog.CAN_SELECT_DIR, false);
        intent.putExtra(FileDialog.OPTION_ONE_CLICK_SELECT, true);
        intent.putExtra(FileDialog.SELECTION_MODE, SelectionMode.MODE_CREATE);
        
        act.startActivityForResult(intent, StartDBManipulationActivity.REQUEST_BROWSE_EXPORT);
	}

	public void startAddGroup(Activity activity) 
	{
		Intent intent = new Intent(activity, AddGroupActivity.class);
		activity.startActivity(intent);
	}

	public void startBrowseGroups(Activity activity) 
	{
		Intent intent = new Intent(activity, BrowseGroupsActivity.class);
		activity.startActivity(intent);
	}

	public void startAddStudent(Activity activity) 
	{
		Intent intent = new Intent(activity, AddStudentActivity.class);
		activity.startActivityForResult(intent, MainMediator.ADD_STUDENT_REQUEST);
	}
	
	public void startPickGroupsFor (long id, Activity activity)
	{
		Log.d ("", "startPickGroupsFor "+id);
		Intent intent = new Intent(activity, PickStudentGroupsActivity.class);
		intent.putExtra(MainMediator.STUDENTID_EXTRA, id);
		activity.startActivityForResult(intent, MainMediator.ADD_STUDENT_GROUPS_REQUEST);
	}

	public void startFindStudent(Activity activity) 
	{
		Intent intent = new Intent(activity, FindStudentActivity.class);
		activity.startActivityForResult(intent, MainMediator.BROWSE_STUDENT_REQUEST);	
	}

	public void startNewGrade(Activity activity, String studentId, String idTask, String taskName) 
	{
		Intent intent = new Intent(activity, NewGradeActivity.class);
		intent.putExtra(MainMediator.STUDENTID_EXTRA, studentId);
		intent.putExtra(MainMediator.TASKID_EXTRA, idTask);
		intent.putExtra(MainMediator.TASKNAME_EXTRA, taskName);
		activity.startActivityForResult(intent, MainMediator.ADD_GRADE_REQUEST);
	}

	public void startChangeGrade(Activity activity, String idStudentGrade,
			String idTask, String taskName, String grade) 
	{
		Intent intent = new Intent(activity, ChangeGradeActivity.class);
		intent.putExtra(MainMediator.STUDENTGRADEID_EXTRA, idStudentGrade);
		intent.putExtra(MainMediator.TASKID_EXTRA, idTask);
		intent.putExtra(MainMediator.GRADE_EXTRA, grade);
		intent.putExtra(MainMediator.TASKNAME_EXTRA, taskName);
		activity.startActivityForResult(intent, MainMediator.EDIT_GRADE_REQUEST);
	}

	public void startEditGroup(Activity activity, String groupId) 
	{
		Intent intent = new Intent(activity, EditGroupActivity.class);
		intent.putExtra(MainMediator.GROUPID_EXTRA, groupId);
		activity.startActivity(intent);
	}

	public void startEditGroupGrades(Activity activity,	String groupId) 
	{
		Intent intent = new Intent(activity, EditGroupGradesActivity.class);
		intent.putExtra(MainMediator.GROUPID_EXTRA, groupId);
		activity.startActivity(intent);
	}

	public void rememberLastFile(Context context, String fileName) {
		SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(context);
		boolean lastfile = SP.getBoolean("last_file", true);
		if (lastfile) {
			SharedPreferences.Editor editor = SP.edit();
			editor.putString("last_file_name", fileName);
			editor.commit();
		}
	}

	public void startEditStudent(Activity activity, String idStudent) 
	{
		try {
			StudentInfo info = DBAdapter.getInstance().getStudentInfo(idStudent);
			Intent intent = new Intent(activity, EditStudentActivity.class);
			intent.putExtra(MainMediator.STUDENTID_EXTRA, idStudent);
			intent.putExtra(MainMediator.FAMILYNAME_EXTRA, info.getStudentFamilyName());
			intent.putExtra(MainMediator.NAME_EXTRA, info.getStudentName());
			intent.putExtra(MainMediator.INDEXNUM_EXTRA, info.getStudentIndexNumber());
			intent.putExtra(MainMediator.KEYNUM_EXTRA, info.getStudentKeyNumber());
			activity.startActivity(intent);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void startEditStudentsInGroup(Activity activity, String groupId) 
	{
		Intent intent = new Intent(activity, EditStudentsInGroupActivity.class);
		intent.putExtra(MainMediator.GROUPID_EXTRA, groupId);
		activity.startActivity(intent);	
	}

	public void setLastClickedTaskName(String taskName) {
		lastClickedTaskName = taskName;
	}
}
