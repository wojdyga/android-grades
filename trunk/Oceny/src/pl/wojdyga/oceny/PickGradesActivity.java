/* Author: Aleksander Wojdyga <alek.wojdyga@gmail.com>
 * URL: http://code.google.com/p/Oceny
 * License: GNU GPL v3
 * */
package pl.wojdyga.oceny;

import java.io.IOException;

import android.app.Activity;
import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

public class PickGradesActivity 
	extends ListActivity  
{
	String taskName;
	
	@Override	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        taskName = getIntent().getStringExtra(MainMediator.LASTTASKNAME_EXTRA);
        updateListAdapter();
	}
	
	private void updateListAdapter ()
	{
		String groupId = getIntent().getStringExtra(MainMediator.GROUPID_EXTRA);
        String studentId = getIntent().getStringExtra(MainMediator.STUDENTID_EXTRA);        
        
		try {
			Cursor cursor = DBAdapter.getInstance().getGradesCursor(groupId, studentId);
			GradeInfoArray array = new GradeInfoArray(cursor);
			
	        setListAdapter(new MyCustomBaseAdapter(this, array, getListView()));
	        	       
	        int i;
	        for (i = array.grades.length - 1; 
	        	 (i >= 0) && (array.grades[i].taskName.compareTo(taskName) != 0); 
	        	 i--) {
	        	//Log.d("", i + " " + array.grades[i].taskName + " " + taskName);
	        }
	        i++;
	        
	        getListView().setFastScrollEnabled(true);
	        getListView().smoothScrollToPosition(i);
		} catch (IOException e) {
			// tutaj nie powinno byc zadnych wyjatkow
			e.printStackTrace();
		}
	}
	
	protected void onRestart() 
	{
		super.onRestart(); 
		updateListAdapter(); 
	}
	
	void rememberLastClickedTaskName (String name)
	{
		taskName = name;
		MainMediator.getInstance().setLastClickedTaskName (taskName);
	}
}

class GradeInfo {
	public String studentId;
	public String studentGradeId;
	public String grade;	
	public String taskId;
	public String taskName;	
}

class GradeInfoArray {
	public GradeInfo grades[];
	
	public GradeInfoArray(Cursor c) 
	{
		grades = new GradeInfo[c.getCount()];
		int i = 0;
		for (c.moveToFirst(); ! c.isAfterLast(); c.moveToNext()) {
			grades[i] = new GradeInfo();
			grades[i].studentId = c.getString(3); 
			grades[i].studentGradeId = c.getString(0);
			grades[i].grade = c.getString(2);	
			grades[i].taskId = c.getString(4);
			grades[i].taskName = c.getString(1);
			i++;
		}
	}
}

class MyCustomBaseAdapter extends BaseAdapter {
	GradeInfoArray array;
	private LayoutInflater mInflater;
	PickGradesActivity act;
	ClickGradeListener clickGradeListener;
	
	class ViewHolder {
		ImageButton editIB;
		TextView taskTV;
		TextView gradeTV;
	}
	 
	 public MyCustomBaseAdapter(PickGradesActivity _act, GradeInfoArray _array, ListView listView) 
	 {
		 array = _array;
		 act = _act;
		 mInflater = LayoutInflater.from(act);		
		 
		 clickGradeListener = new ClickGradeListener(act, array, listView);
	 }

	 public int getCount() {
		 return array.grades.length;
	 }

	 public Object getItem(int position) {
		 return array.grades[position];
	 }

	 public long getItemId(int position) {
		 return Long.parseLong(array.grades[position].studentGradeId);
	 }

	 public View getView(int position, View convertView, ViewGroup parent) 
	 {
		 ViewHolder holder;
		 if (convertView == null) {
			 convertView = mInflater.inflate(R.layout.grades, null);
			 holder = new ViewHolder();
			 holder.editIB = (ImageButton) convertView.findViewById(R.id.editIB);
			 holder.taskTV = (TextView) convertView.findViewById(R.id.taskTV);
			 holder.gradeTV = (TextView) convertView.findViewById(R.id.gradeTV);
			 holder.editIB.setOnClickListener(clickGradeListener);

			 convertView.setTag(holder);
		 } else {
			 holder = (ViewHolder) convertView.getTag();
		 }
	  
		 String gradeId = array.grades[position].studentGradeId;
		 if (gradeId.compareTo("-1") == 0)
			 holder.editIB.setImageResource(android.R.drawable.ic_menu_add);
		 else
			 holder.editIB.setImageResource(android.R.drawable.ic_menu_edit);

		 String taskName = array.grades[position].taskName;
		 holder.taskTV.setText(taskName);
		 
		 String grade = array.grades[position].grade;
		 holder.gradeTV.setText(grade);
		 
		 //Log.d("", "position="+position+" gradeId="+gradeId+" taskTV="+taskName);
		 
		 return convertView;
	 }
}

class ClickGradeListener implements OnClickListener {
	PickGradesActivity activity;
	GradeInfoArray infos;
	ListView listView;
	
	public ClickGradeListener(PickGradesActivity _activity, GradeInfoArray _infos, ListView _listView) {
		activity = _activity;
		infos = _infos;
		listView = _listView;
	}

	@Override
	public void onClick(View arg0) 
	{
		int ind = listView.getPositionForView((View) arg0.getParent());
		GradeInfo info = infos.grades[ind];
		
		String idStudentGrade = info.studentGradeId;  
		String idStudent = info.studentId;
		String idTask = info.taskId;
		String taskName = info.taskName; 
		String grade = info.grade;
		
		//Log.d("", "idStudentGrade="+idStudentGrade+" grade="+grade+" idStudent="+idStudent+" idTask="+idTask+" taskName="+taskName);

		activity.rememberLastClickedTaskName(taskName);
		
		if (idStudentGrade.compareTo("-1") == 0) {
			MainMediator.getInstance().startNewGrade(activity, idStudent, idTask, taskName);			
		} else {
			MainMediator.getInstance().startChangeGrade(activity, idStudentGrade, idTask, taskName, grade);
		}
	}
}
