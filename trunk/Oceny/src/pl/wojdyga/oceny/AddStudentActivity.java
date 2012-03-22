/* Author: Aleksander Wojdyga <alek.wojdyga@gmail.com>
 * URL: http://code.google.com/p/Oceny
 * License: GNU GPL v3
 * */
package pl.wojdyga.oceny;

import java.io.IOException;

import pl.wojdyga.oceny.LayoutDialogBuilder.ClickListener;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

public class AddStudentActivity 
	extends Activity implements ClickListener 
{
	public static final String NEW_STUDENT_ID = "pl.wojdyga.oceny.NewStudentID";
	
	LayoutDialogBuilder builder;
	AlertDialog alertDialog;
	StudentInfoWrapper infoWrapper;
	
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		builder = new LayoutDialogBuilder(this, R.layout.add_student);
		builder.setTitle(R.string.add_student);
		builder.setClickListener(this);
		
		infoWrapper = new StudentInfoWrapper(builder.getWrapper());
		
		alertDialog = builder.create();
		alertDialog.show();
	}
	
	@Override
	public void onPositiveButtonClicked() 
	{
		try {
			if (DBAdapter.getInstance().addNewStudent(infoWrapper)) {
				Toast.makeText(this, R.string.new_student, Toast.LENGTH_SHORT).show();
				Intent intent = new Intent();
				intent.putExtra(MainMediator.STUDENTID_EXTRA, DBAdapter.getInstance().getLastInsertID());
				setResult(Activity.RESULT_OK, intent);
			} else
				Toast.makeText(this, R.string.cant_add_student, Toast.LENGTH_LONG).show();
		} catch (IOException e) {
			// tu nie powinno być żadnych wyjątków
			e.printStackTrace();
		}
	}

	@Override
	public void onNegativeButtonClicked() {
		setResult(Activity.RESULT_CANCELED);
	}
	
	protected void onSaveInstanceState (Bundle outState)
	{
		outState.putString("Name", infoWrapper.getStudentName());
		outState.putString("FamilyName", infoWrapper.getStudentFamilyName());
		outState.putString("IndexNumber", infoWrapper.getStudentIndexNumber());
		outState.putString("KeyNumber", infoWrapper.getStudentKeyNumber());
	}
	
	protected void onRestoreInstanceState (Bundle savedInstanceState) 
	{
		LayoutDialogBuilder.DialogWrapper dialogWrapper = infoWrapper.getDialogWrapper();
		
		dialogWrapper.setEditTextString(R.id.nameET, savedInstanceState.getString("Name"));
		dialogWrapper.setEditTextString(R.id.familynameET, savedInstanceState.getString("FamilyName"));
		dialogWrapper.setEditTextString(R.id.indexNumET, savedInstanceState.getString("IndexNumber"));
		dialogWrapper.setEditTextString(R.id.keyNumET, savedInstanceState.getString("KeyNumber"));
	}
}
