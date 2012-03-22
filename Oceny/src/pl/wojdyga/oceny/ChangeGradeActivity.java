/* Author: Aleksander Wojdyga <alek.wojdyga@gmail.com>
 * URL: http://code.google.com/p/Oceny
 * License: GNU GPL v3
 * */
package pl.wojdyga.oceny;

import java.io.IOException;
import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.Toast;

public class ChangeGradeActivity 
	extends Activity 
	implements LayoutDialogBuilder.ClickListener 
{
	LayoutDialogBuilder builder;
	LayoutDialogBuilder.DialogWrapper wrapper;
	
	@Override	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);	  
	    
        builder = new LayoutDialogBuilder(this, R.layout.grade_edit);
        builder.setTitle(R.string.change_grade);
        builder.setClickListener(this);
		
        wrapper = builder.getWrapper();
        wrapper.setTextViewString(R.id.taskNameTV, 
        		getIntent().getStringExtra(MainMediator.TASKNAME_EXTRA));
        wrapper.setEditTextString(R.id.gradeET, 
        		getIntent().getStringExtra(MainMediator.GRADE_EXTRA));
        
		setResult(RESULT_CANCELED);

		AlertDialog alertDialog = builder.create();		
		alertDialog.show();
	}

	@Override
	public void onPositiveButtonClicked() {
		String grade = wrapper.getEditTextString(R.id.gradeET);
		String studGradeId = getIntent().getStringExtra(MainMediator.STUDENTGRADEID_EXTRA);
		
		boolean result = false;
		try {
			result = DBAdapter.getInstance().updateStudentGrade (studGradeId, grade);		
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
}
