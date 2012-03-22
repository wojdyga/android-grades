/* Author: Aleksander Wojdyga <alek.wojdyga@gmail.com>
 * URL: http://code.google.com/p/Oceny
 * License: GNU GPL v3
 * */
package pl.wojdyga.oceny;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class PickStudentGroupsActivity 
	extends Activity 
	implements LayoutDialogBuilder.ClickListener, OnMultiChoiceClickListener
{
	LayoutDialogBuilder builder;
	AlertDialog alertDialog;
	
	Set<Long> groupIds;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
	
		groupIds = new HashSet<Long>();
		
		builder = new LayoutDialogBuilder(this, R.layout.multi_list);
		builder.setTitle(R.string.pick_student_groups);
		builder.setClickListener(this);
			
		setResult(RESULT_CANCELED);
	
		groupIds.clear();
		
		try {
			long idS = getIntent().getLongExtra(MainMediator.STUDENTID_EXTRA, -1);
			Cursor cursor = DBAdapter.getInstance().getStudentGroupsCursor (idS);
			builder.setMultiChoiceItems(createGroupLabels(cursor), createCheckList(cursor), this);
		} catch (IOException e) {
			// tu nie powinno byc zadnych wyjatkow
			e.printStackTrace();
			Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
		}
	    
		alertDialog = builder.create();
		alertDialog.show();
	}

	private boolean[] createCheckList(Cursor cursor) 
	{
		int l = cursor.getCount();
		boolean[] checks = new boolean[l];
		int i = 0;
		
		cursor.moveToFirst();
		
		while (! cursor.isAfterLast()) {
			checks[i] = cursor.getString(3).compareTo("1") == 0; // 3 to numer (liczony od zera) kolumny Jest
			cursor.moveToNext();
			i++;
		}
		
		return null;
	}

	private String[] createGroupLabels (Cursor cursor)
	{
		int l = cursor.getCount();
		String[] labels = new String[l];
		int i = 0;
		
		cursor.moveToFirst();
		
		while (! cursor.isAfterLast()) {
			labels[i] = cursor.getString(1); // 1 to numer (liczony od zera) kolumny Nazwa
			cursor.moveToNext();
			i++;
		}
		
		return labels;
	}
	
	@Override
	public void onPositiveButtonClicked() {
		Intent intent = new Intent();
		intent.putExtra(MainMediator.STUDENTID_EXTRA, 
				getIntent().getLongExtra(MainMediator.STUDENTID_EXTRA, -1));
		
		long[] ids = new long[groupIds.size()];
		int k = 0;
		Iterator<Long> i = groupIds.iterator();
		while (i.hasNext()) {
			ids[k] = i.next();
			k++;
		}
		intent.putExtra(MainMediator.STUDENT_GROUPSID_EXTRA, ids);
		
		setResult(RESULT_OK, intent);
	}

	@Override
	public void onNegativeButtonClicked() {
	}

	@Override
	public void onClick(DialogInterface arg0, int position, boolean checked) {
		// TODO Auto-generated method stub
		//Log.d("", "onClick "+position+ " " + checked);
		Long result = new Long(-1);
		try {
			result = Long.parseLong(DBAdapter.getInstance().getStringValueAtPosition(0, position)); // na pozycji 0 jest _id			
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// tutaj nie powinno być żadnych takich wyjątków
			e.printStackTrace();
		}
		
		if (result.longValue() != -1) {
			if (checked)		
				groupIds.add(result);
			else
				groupIds.remove(result);
		}
	}
}
