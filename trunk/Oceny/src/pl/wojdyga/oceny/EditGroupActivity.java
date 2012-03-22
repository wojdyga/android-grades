/* Author: Aleksander Wojdyga <alek.wojdyga@gmail.com>
 * URL: http://code.google.com/p/Oceny
 * License: GNU GPL v3
 * */
package pl.wojdyga.oceny;

import java.io.IOException;

import pl.wojdyga.oceny.LayoutDialogBuilder.ClickListener;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

public class EditGroupActivity 
	extends Activity 
	implements ClickListener, OnShowListener
{
	AlertDialog alertDialog;
	LayoutDialogBuilder builder;
	String groupId;
	
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		groupId = getIntent().getStringExtra(MainMediator.GROUPID_EXTRA);
		
	    builder = new LayoutDialogBuilder(this, R.layout.addgroup);
	    builder.setTitle(R.string.edit_group);
	    builder.setClickListener(this);
	    
	    alertDialog = builder.create();
	    alertDialog.setOnShowListener(this);	    
	    alertDialog.show();
	}

	@Override
	public void onPositiveButtonClicked() {
		try {			
			ClassInfoDialogWrapper wrapper = new ClassInfoDialogWrapper(builder.getWrapper());
			
			if (! DBAdapter.getInstance().editGroup(groupId, wrapper))
				Toast.makeText(this, R.string.cant_edit_group, Toast.LENGTH_LONG);
			else
				Toast.makeText(this, R.string.group_changed, Toast.LENGTH_SHORT);
		} catch (IOException e) {
			// tutaj nie powinno byc zadnych wyjatkow
			e.printStackTrace();
		}
	}

	@Override
	public void onNegativeButtonClicked() {		
	}

	@Override
	public void onShow(DialogInterface arg0) {
		try {
			ClassInfo info = DBAdapter.getInstance().getClassInfo(groupId);
			EditText et = (EditText) alertDialog.findViewById(R.id.classET);
			et.setText(info.getClassName());
			et = (EditText) alertDialog.findViewById(R.id.placeET);
			et.setText(info.getClassPlace());
			et = (EditText) alertDialog.findViewById(R.id.timeET);
			et.setText(info.getClassTime());
			et = (EditText) alertDialog.findViewById(R.id.groupET);
			et.setText(info.getClassGroup());
		} catch (IOException e) {
			// tu nie powinno być żadnych wyjątków
			e.printStackTrace();
		}
	}
}
