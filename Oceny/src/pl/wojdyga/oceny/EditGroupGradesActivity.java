/* Author: Aleksander Wojdyga <alek.wojdyga@gmail.com>
 * URL: http://code.google.com/p/Oceny
 * License: GNU GPL v3
 * */
package pl.wojdyga.oceny;

import java.io.IOException;

import pl.wojdyga.oceny.LayoutDialogBuilder.ClickListener;

import android.app.Activity;
import android.app.AlertDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

public class EditGroupGradesActivity 
extends Activity 
implements OnClickListener, ClickListener 
{
	String groupId;
	AlertDialog alertDialog;
	ListView gradesLV;
	
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_groupgrades);
	
		groupId = getIntent().getStringExtra(MainMediator.GROUPID_EXTRA);

		Button newGrade = (Button) findViewById(R.id.newGradeButton);
		newGrade.setOnClickListener(this);
		
		gradesLV = (ListView) findViewById(R.id.gradesLV);
		
		updateListAdapter();
	}

	private void updateListAdapter ()
	{				
		try {
			Cursor cursor = DBAdapter.getInstance().getGroupGradesCursor (groupId);
			gradesLV.setAdapter(new GradesAdapter(this, cursor, gradesLV));
		} catch (IOException e) {
			// tu nie powinno być żadnych wyjątków
			e.printStackTrace();
		}
	}
	
	protected void onRestart() 
	{
		super.onRestart(); 
		updateListAdapter(); 
	}
	
	class GradeInfo {
		public String id;
		public String taskName;
	}
	
	class GradesAdapter 
		extends BaseAdapter 
	{
		 private Cursor gradesCursor;	 
		 private LayoutInflater mInflater;
		 Activity act;
		 
		 GradeInfo grades[];
		 
		 ClickDeleteGradeListener deleteListener;
		 ClickEditGradeListener editListener;
		 
		 class ViewHolder {
			 ImageButton deleteGradeIB;
			 TextView gradeTaskTV;
			 ImageButton editGradeIB;
		 }
		 
		 public GradesAdapter(Activity _act, Cursor _cursor, ListView lv) 
		 {
			 gradesCursor = _cursor;		 	 
			 act = _act;
			 mInflater = LayoutInflater.from(act);
			 
			 grades = new GradeInfo[gradesCursor.getCount()];
			 int i = 0;
			 for (gradesCursor.moveToFirst(); ! gradesCursor.isAfterLast(); gradesCursor.moveToNext()) {
				 grades[i] = new GradeInfo();
				 grades[i].id = gradesCursor.getString(0);
				 grades[i].taskName = gradesCursor.getString(1);
				 i++;
			 }
			 
			 deleteListener = new ClickDeleteGradeListener(act, lv, this);
			 editListener = new ClickEditGradeListener(act, lv, this);
		 }

		 public int getCount() {
			 return grades.length;
		 }

		 public Object getItem(int position) 
		 {
			 return grades[position];
		 }

		 public long getItemId(int position) {
			 return Long.parseLong(grades[position].id);
		 }

		 public View getView(int position, View convertView, ViewGroup parent) 
		 {
			 ViewHolder holder;
			 if (convertView == null) {
				 convertView = mInflater.inflate(R.layout.grade, null);
				 
				 holder = new ViewHolder();
				 holder.deleteGradeIB = (ImageButton) convertView.findViewById(R.id.deleteGradeIB);					 					 
				 holder.deleteGradeIB.setOnClickListener(deleteListener);
				 holder.gradeTaskTV = (TextView) convertView.findViewById(R.id.gradeTaskTV);
				 holder.editGradeIB = (ImageButton) convertView.findViewById(R.id.editGradeIB);
				 holder.editGradeIB.setOnClickListener(editListener);

				 convertView.setTag(holder);
			 } else {
				 holder = (ViewHolder) convertView.getTag();
			 }
  
			 String taskName = grades[position].taskName;
			 holder.gradeTaskTV.setText(taskName);
			 
			 return convertView;
		 }
	}
	
	class ClickEditGradeListener
		implements OnClickListener, ClickListener
	{
		Activity activity;
		ListView listView;
		GradesAdapter gradesAdapter;
		int index;
		
		ClickEditGradeListener(Activity _activity, ListView _listView, GradesAdapter _gradesAdapter) {
			activity = _activity;
			listView = _listView;
			gradesAdapter = _gradesAdapter;
		}

		@Override
		public void onClick(View arg0) {
			index = listView.getPositionForView((View) arg0.getParent());
			GradeInfo info = (GradeInfo) gradesAdapter.getItem(index);
			
			LayoutDialogBuilder builder = new LayoutDialogBuilder(activity, R.layout.new_grade);
			builder.setClickListener(this);
			builder.setFinishActivity(false);

			LayoutDialogBuilder.DialogWrapper wrapper = builder.getWrapper();
			wrapper.setEditTextString(R.id.gradeTaskET, info.taskName);
			
			alertDialog = builder.create();					
			alertDialog.show();							
		}

		@Override
		public void onPositiveButtonClicked() {
			String task = ((EditText) alertDialog.findViewById(R.id.gradeTaskET)).getText().toString();
			GradeInfo info = (GradeInfo) gradesAdapter.getItem(index);
			
			try {
				DBAdapter.getInstance().editGrade (info.id, task);
				updateListAdapter();
			} catch (IOException e) {
				// tu nie ma żadnych wyjatków
				e.printStackTrace();
			}			
		}

		@Override
		public void onNegativeButtonClicked() {
		}
	}
	
	class ClickDeleteGradeListener
		implements OnClickListener
	{
		Activity activity;
		ListView listView;
		GradesAdapter gradesAdapter;
		
		ClickDeleteGradeListener(Activity _activity, ListView _listView, GradesAdapter _gradesAdapter) {
			activity = _activity;
			listView = _listView;
			gradesAdapter = _gradesAdapter;
		}
		
		@Override
		public void onClick(View arg0) {
			int index = listView.getPositionForView((View) arg0.getParent());
			GradeInfo info = (GradeInfo) gradesAdapter.getItem(index);
			
			try {
				DBAdapter.getInstance().deleteGrade (info.id);
				updateListAdapter();
			} catch (IOException e) {
				// tu nie powinno być żadnych wyjątków
				e.printStackTrace();
			}
		}
	}

	
	@Override
	public void onClick(View arg0) {
		LayoutDialogBuilder builder = new LayoutDialogBuilder(this, R.layout.new_grade);
		builder.setClickListener(this);
		builder.setFinishActivity(false);
		
		alertDialog = builder.create();
		alertDialog.show();
	}

	@Override
	public void onPositiveButtonClicked() {
		String task = ((EditText) alertDialog.findViewById(R.id.gradeTaskET)).getText().toString();
		try {
			DBAdapter.getInstance().newGrade (groupId, task);
			updateListAdapter();
		} catch (IOException e) {
			// tu nie powinno być żadnych wyjątków
			e.printStackTrace();
		}
	}

	@Override
	public void onNegativeButtonClicked() {
	}
}
