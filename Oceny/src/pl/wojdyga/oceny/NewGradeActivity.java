/* Author: Aleksander Wojdyga <alek.wojdyga@gmail.com>
 * URL: http://code.google.com/p/Oceny
 * License: GNU GPL v3
 * */
package pl.wojdyga.oceny;

import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class NewGradeActivity 
	extends Activity
	implements LayoutDialogBuilder.ClickListener, OnShowListener 
{
	LayoutDialogBuilder builder;
	AlertDialog alertDialog;
	
	@Override	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);	  
	    
        builder = new LayoutDialogBuilder(this, R.layout.grade_edit);
        builder.setTitle(R.string.change_grade);
        builder.setClickListener(this);
		
		setResult(RESULT_CANCELED);

		alertDialog = builder.create();
		alertDialog.setOnShowListener(this);	
        alertDialog.show();
	}

	@Override
	public void onPositiveButtonClicked() {
		LayoutDialogBuilder.DialogWrapper wrapper = builder.getWrapper();
		String grade = wrapper.getEditTextString(R.id.gradeET);
		String taskId = getIntent().getStringExtra(MainMediator.TASKID_EXTRA);
		String studentId = getIntent().getStringExtra(MainMediator.STUDENTID_EXTRA);
		
		boolean result = false; 
		try {
			result = DBAdapter.getInstance().insertStudentGrade(grade, studentId, taskId);		
		} catch (IOException e) {
			// tu nie ma zadnych wyjatkow
			e.printStackTrace();
		}
		
		if (result) {
			Toast.makeText(this, R.string.grade_changed, Toast.LENGTH_SHORT).show();
			setResult(RESULT_OK);
		} else
			Toast.makeText(this, R.string.cant_change_grade, Toast.LENGTH_SHORT).show();	
	}

	@Override
	public void onNegativeButtonClicked() {
	}

	@Override
	public void onShow(DialogInterface arg0) {
		TextView taskNameTV = (TextView) alertDialog.findViewById(R.id.taskNameTV);
		String taskName = getIntent().getStringExtra(MainMediator.TASKNAME_EXTRA);
		taskNameTV.setText(taskName);
	}
}
