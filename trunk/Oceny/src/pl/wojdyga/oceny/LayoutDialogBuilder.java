/* Author: Aleksander Wojdyga <alek.wojdyga@gmail.com>
 * URL: http://code.google.com/p/Oceny
 * License: GNU GPL v3
 * */
package pl.wojdyga.oceny;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class LayoutDialogBuilder 
	implements DialogInterface.OnShowListener
{
	Activity activity;
	boolean finishActivity;
	
	AlertDialog alertDialog;
	DialogWrapper wrapper;
	int layoutID;
	View view;
	AlertDialog.Builder builder;
	
	int okButtonRID;
	int cancelButtonRID;
	ClickListener listener;
	
	boolean checkedItems[];
	
	LayoutDialogBuilder(Activity a, int lID)
	{
		activity = a;
		finishActivity = true;
		layoutID = lID;
		
		view = LayoutInflater.from(activity).inflate(layoutID, null);
	    wrapper = new DialogWrapper(view);
	    
	    builder = new AlertDialog.Builder(activity);
  
	    okButtonRID = android.R.string.ok;
	    cancelButtonRID = android.R.string.cancel;
	    
	    listener = new ClickAdapter();
	    
	    checkedItems = null;
	}
    
	public LayoutDialogBuilder setTitle (int rID)
	{
		builder.setTitle(rID);
		return this;
	}
	
	public LayoutDialogBuilder setPositiveButtonLabel (int rID)
	{
		okButtonRID = rID;
		return this;
	}
	
	public LayoutDialogBuilder setNegativeButtonLabel (int rID)
	{
		okButtonRID = rID;
		return this;
	}
	
	public DialogWrapper getWrapper ()
	{
		return wrapper;
	}
	
	public LayoutDialogBuilder setClickListener (ClickListener l)
	{
		listener = l;
		return this;
	}
	
	public LayoutDialogBuilder setFinishActivity (boolean v)
	{
		finishActivity = v;
		return this;
	}
	
	public LayoutDialogBuilder setListAdapter (ListAdapter la, android.content.DialogInterface.OnClickListener l)
	{
		builder.setAdapter(la, l);
		return this;
	}
	
	void closeAndFinish ()
	{
		alertDialog.dismiss();
		if (finishActivity)
			activity.finish();
	}
		
	public AlertDialog create()
	{	    	 
	    builder.setPositiveButton(okButtonRID, new DialogInterface.OnClickListener() {
	        @Override
	        public void onClick(DialogInterface dialog, int which) 
	        {
	        	listener.onPositiveButtonClicked();
	    		closeAndFinish();
	        }
	    });
	    builder.setNegativeButton(cancelButtonRID, new DialogInterface.OnClickListener() {
	        @Override
	        public void onClick(DialogInterface dialog, int which) 
	        {
	        	listener.onNegativeButtonClicked();
	    		closeAndFinish();	
	        }
	    });
	
	    builder.setView (view);	
	    
	    alertDialog = builder.create();
	    
	    return alertDialog;
	}
	
	
	public interface ClickListener
	{
		public void onPositiveButtonClicked ();
		public void onNegativeButtonClicked ();		
	}
	
	public class ClickAdapter
		implements ClickListener
	{
		public void onPositiveButtonClicked () {}
		public void onNegativeButtonClicked () {}
	}
	
	public class DialogWrapper
	{
		View base = null;
		
		public DialogWrapper(View view) 
		{
			base = view;
		}
		
		public String getEditTextString (int id)
		{
			EditText et = (EditText) base.findViewById(id);
			
			if (et == null)
				return null;
			else
				return et.getText().toString();
		}
		
		public void setEditTextString (int id, String s)
		{
			EditText et = (EditText) base.findViewById(id);
			
			if (et != null)
				et.setText(s);
		}
		
		public long[] getSelectedIds (int id)
		{
			ListView lv = (ListView) base.findViewById(id);
			
			if (lv == null) 
				return null;
			else
				return lv.getCheckedItemIds();
		}

		public void setTextViewString(int tvid, String s) 
		{
			TextView tv = (TextView) base.findViewById(tvid);
			
			if (tv != null)
				tv.setText(s);
		}
	}

	public LayoutDialogBuilder setMultiChoiceItems(String[] groupLabels, boolean checkedItems[], OnMultiChoiceClickListener listener) 
	{
		builder.setMultiChoiceItems(groupLabels, null, listener);		
		return this;
	}

	@Override
	public void onShow(DialogInterface arg0) {
		if (checkedItems != null) {
			ListView lv = alertDialog.getListView();
			for (int i = 0; i < checkedItems.length; i++) {
				lv.setItemChecked(i, checkedItems[i]);
			}
		}
	}
}
