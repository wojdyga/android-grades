package pl.wojdyga.oceny;

import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class EditStudentsInGroupActivity extends Activity implements OnClickListener {
	ListView lv;
	String groupId;
	ImageButton addIB;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.group_students);
		
		lv = (ListView) findViewById(R.id.groupStudentsLV);
		addIB = (ImageButton) findViewById(R.id.addStudentaIB);
		addIB.setOnClickListener(this);
		
		groupId = getIntent().getStringExtra(MainMediator.GROUPID_EXTRA);
	}
	
	private void updateListAdapter ()
	{
		try {
			lv.setAdapter(new SimpleCursorAdapter(this, 
					R.layout.student_del, 
					DBMediator.getInstance().getStudentCursor(groupId), 
					new String[]{"Nazwa"}, 
					new int[]{R.id.studentNameTV})
			);
		} catch (IOException e) {
			// tu nie ma żadnych wyjątków
			e.printStackTrace();
		}
	}
	
	@Override
	protected void onStart ()
	{
		super.onStart();
		updateListAdapter();
	}
	
	public void delStudentFromGroupClick (View v)
	{
		int position = lv.getPositionForView(v);
		try {
			DBMediator.getInstance().deleteStudentFromGroup (DBMediator.getInstance().getStringValueAtPosition(0, position), groupId);
			updateListAdapter();
		} catch (IOException e) {
			// tu nie ma żadnych wyjątków
			e.printStackTrace();
		}     
	}

	@Override
	public void onClick(View arg0) 
	{
		MainMediator.getInstance().startFindStudent(this);
	}
	
	public synchronized void onActivityResult(final int requestCode,
            int resultCode, final Intent data) 
	{
		try {
			if (resultCode == Activity.RESULT_OK) {
				if (requestCode == MainMediator.BROWSE_STUDENT_REQUEST) {
					String studentId = data.getStringExtra(MainMediator.STUDENTID_EXTRA);
					DBMediator.getInstance().addStudentToGroup (studentId, groupId);
				}
			}
		} catch (IOException ioe) {
			// tu nie ma żadnych wyjątków
			ioe.printStackTrace();
		}
	}
}
