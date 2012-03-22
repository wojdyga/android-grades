package pl.wojdyga.oceny;

import java.io.IOException;

import pl.wojdyga.oceny.LayoutDialogBuilder.ClickListener;
import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.Toast;

public class EditStudentActivity extends Activity implements ClickListener 
{
	LayoutDialogBuilder builder;
	LayoutDialogBuilder.DialogWrapper wrapper;
	AlertDialog alertDialog;
	
	String studentId;
	
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
	
		studentId = getIntent().getStringExtra(MainMediator.STUDENTID_EXTRA);
		
		builder = new LayoutDialogBuilder(this, R.layout.add_student);
		builder.setTitle(R.string.add_student);
		builder.setClickListener(this);
		
		wrapper = builder.getWrapper();
		wrapper.setEditTextString(R.id.familynameET, getIntent().getStringExtra(MainMediator.FAMILYNAME_EXTRA));
		wrapper.setEditTextString(R.id.nameET, getIntent().getStringExtra(MainMediator.NAME_EXTRA));
		wrapper.setEditTextString(R.id.indexNumET, getIntent().getStringExtra(MainMediator.INDEXNUM_EXTRA));
		wrapper.setEditTextString(R.id.keyNumET,  getIntent().getStringExtra(MainMediator.KEYNUM_EXTRA));
		
		alertDialog = builder.create();
		alertDialog.show();
		
		setResult(RESULT_CANCELED);
	}
	
	@Override
	public void onPositiveButtonClicked() 
	{
		StudentInfoWrapper w = new StudentInfoWrapper(wrapper);
		try {
			if (DBMediator.getInstance().editStudent(studentId, w)) {
				setResult(Activity.RESULT_OK);
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
}
