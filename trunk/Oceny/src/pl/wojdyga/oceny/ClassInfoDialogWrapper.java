/* Author: Aleksander Wojdyga <alek.wojdyga@gmail.com>
 * URL: http://code.google.com/p/Oceny
 * License: GNU GPL v3
 * */
package pl.wojdyga.oceny;

import pl.wojdyga.oceny.LayoutDialogBuilder.DialogWrapper;

class ClassInfoDialogWrapper
	implements ClassInfo
{
	DialogWrapper wrapper;
	
	public ClassInfoDialogWrapper(LayoutDialogBuilder.DialogWrapper _wrapper) 
	{
		wrapper = _wrapper;
	}
				
	public String getClassName () 
	{
		return wrapper.getEditTextString(R.id.classET);
	}
	
	public String getClassTime () 
	{
		return wrapper.getEditTextString(R.id.timeET);
	}
	
	public String getClassPlace () 
	{
		return wrapper.getEditTextString(R.id.placeET);
	}
	
	public String getClassGroup () 
	{
		return wrapper.getEditTextString(R.id.groupET);
	}
}
