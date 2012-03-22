/* Author: Aleksander Wojdyga <alek.wojdyga@gmail.com>
 * URL: http://code.google.com/p/Oceny
 * License: GNU GPL v3
 * */
package pl.wojdyga.oceny;

import java.io.IOException;

import pl.wojdyga.oceny.LayoutDialogBuilder.ClickListener;
import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.Toast;

public class AddGroupActivity 
	extends Activity 
	implements ClickListener 
{
	AlertDialog alertDialog;
	LayoutDialogBuilder builder;
	LayoutDialogBuilder.DialogWrapper wrapper;
	
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
	    builder = new LayoutDialogBuilder(this, R.layout.addgroup);
	    builder.setTitle(R.string.add_group);
	    builder.setPositiveButtonLabel(android.R.string.ok);
	    builder.setNegativeButtonLabel(android.R.string.cancel);
	    builder.setClickListener(this);
	
	    wrapper = builder.getWrapper();
	    
	    alertDialog = builder.create();	    
	    alertDialog.show();
	}
	
	@Override
	public void onPositiveButtonClicked() {
		try {
			ClassInfoDialogWrapper infoWrapper = new ClassInfoDialogWrapper(wrapper);
			
			if (! DBAdapter.getInstance().addNewGroup(infoWrapper))
				Toast.makeText(this, R.string.cant_add_group, Toast.LENGTH_LONG);
			else
				Toast.makeText(this, R.string.new_group, Toast.LENGTH_SHORT);
		} catch (IOException e) {
			// tutaj nie powinno byc zadnych wyjatkow
			e.printStackTrace();
		}
	}

	@Override
	public void onNegativeButtonClicked() {
	}
	
	protected void onSaveInstanceState (Bundle outState)
	{
		outState.putString("Class", wrapper.getEditTextString(R.id.classET));
		outState.putString("Time", wrapper.getEditTextString(R.id.timeET));
		outState.putString("Place", wrapper.getEditTextString(R.id.placeET));
		outState.putString("Group", wrapper.getEditTextString(R.id.groupET));
	}
	
	protected void onRestoreInstanceState (Bundle savedInstanceState) 
	{
		wrapper.setEditTextString(R.id.classET, savedInstanceState.getString("Class"));
		wrapper.setEditTextString(R.id.timeET, savedInstanceState.getString("Time"));
		wrapper.setEditTextString(R.id.placeET, savedInstanceState.getString("Place"));
		wrapper.setEditTextString(R.id.groupET, savedInstanceState.getString("Group"));
	}
}
