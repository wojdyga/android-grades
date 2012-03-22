package pl.wojdyga.oceny;

class StudentInfoWrapper implements StudentInfo
{
	LayoutDialogBuilder.DialogWrapper wrapper;
	
	public StudentInfoWrapper(LayoutDialogBuilder.DialogWrapper w) {
		wrapper = w;
	}

	@Override
	public String getStudentFamilyName() {
		return wrapper.getEditTextString(R.id.familynameET);
	}

	@Override
	public String getStudentName() {
		return wrapper.getEditTextString(R.id.nameET);
	}

	@Override
	public String getStudentIndexNumber() {
		return wrapper.getEditTextString(R.id.indexNumET);
	}

	@Override
	public String getStudentKeyNumber() {
		return wrapper.getEditTextString(R.id.keyNumET);
	}
}