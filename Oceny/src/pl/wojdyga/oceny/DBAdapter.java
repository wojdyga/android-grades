/* Author: Aleksander Wojdyga <alek.wojdyga@gmail.com>
 * URL: http://code.google.com/p/Oceny
 * License: GNU GPL v3
 * */
package pl.wojdyga.oceny;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.ContentValues;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

public class DBAdapter 
{
	private static final String DEFAULT_ASSETS_DB_FILE = "oceny.sqlite";
	private static final String DEFAULT_DB_DIR = "/data/data/pl.wojdyga.oceny/databases/";
	public static final String DEFAULT_DB_FILE = DEFAULT_DB_DIR + DEFAULT_ASSETS_DB_FILE;
	
	private SQLiteDatabase db = null; 
	
	private Cursor currentCursor = null;
	
	private static DBAdapter adapter;
	
	public static AssetManager assetManager;
	
	long lastInsertID;
	
	private DBAdapter () throws IOException
	{
		createDefaultDB();
	}

	private void createDefaultDB () throws IOException
	{
		File f = new File(DEFAULT_DB_FILE);
		if (! f.exists()) {
			InputStream in = assetManager.open (DEFAULT_ASSETS_DB_FILE);
			new File(DEFAULT_DB_DIR).mkdir();
			OutputStream out = new FileOutputStream(DEFAULT_DB_FILE);
			copyFile(in, out);
		}	
	}
	
	private void closeDBAndCursor ()
	{
		if (db != null) {
			if (currentCursor != null)
				currentCursor.close();
			db.close();
		}			
		db = null;
		currentCursor = null;
	}
	
	public void openDatabase (String dbFile) throws SQLiteException, FileNotFoundException
	{
		if ((db != null) && (db.getPath().compareTo(dbFile) == 0))
			return;
		
		closeDBAndCursor();
		
		File f = new File(dbFile);
		
		if (f.exists()) {
			db = SQLiteDatabase.openDatabase(dbFile, null, SQLiteDatabase.OPEN_READWRITE);
			//Log.d("DB", "DB file " + dbFile + " opened");
		} else
			throw new FileNotFoundException(dbFile);
	}
	
	public static DBAdapter getInstance () throws IOException
	{
		if (adapter == null) {
			adapter = new DBAdapter();
		}
		
		return adapter;
	}

	public void createEmptyTables () throws SQLiteException, IOException
	{
		openDatabase(DEFAULT_DB_FILE);
		
		String createSQL = "-- Table 'Studenci'\n" + 
				"-- \n" + 
				"-- ---\n" + 
				"-- ---\n" + 
				"\n" + 
				"DROP TABLE IF EXISTS 'Studenci';\n" + 
				"		\n" + 
				"CREATE TABLE 'Studenci' (\n" + 
				"  'id' INTEGER NULL PRIMARY KEY DEFAULT NULL,\n" + 
				"  'Nazwisko' CHAR(40) NOT NULL DEFAULT 'NULL',\n" + 
				"  'Imie' CHAR(30) NOT NULL DEFAULT 'NULL',\n" + 
				"  'NrIndeksu' CHAR(10) NULL DEFAULT NULL,\n" + 
				"  'KluczSkrzynki' CHAR(8) NULL DEFAULT NULL\n" + 
				");\n" + 
				"\n" + 
				"-- ---\n" + 
				"-- Table 'Grupa'\n" + 
				"-- \n" + 
				"-- ---\n" + 
				"\n" + 
				"DROP TABLE IF EXISTS 'Grupa';\n" + 
				"		\n" + 
				"CREATE TABLE 'Grupa' (\n" + 
				"  'id' INTEGER NULL  PRIMARY KEY DEFAULT NULL,\n" + 
				"  'Przedmiot' CHAR(50) NULL DEFAULT NULL,\n" + 
				"  'Czas' CHAR(16) NULL DEFAULT NULL,\n" + 
				"  'Miejsce' CHAR(16) NULL DEFAULT NULL,\n" + 
				"  'GrupaDziek' CHAR(16) NOT NULL DEFAULT NULL\n" + 
				");\n" + 
				"\n" + 
				"-- ---\n" + 
				"-- Table 'StudentWGrupie'\n" + 
				"-- \n" + 
				"-- ---\n" + 
				"\n" + 
				"DROP TABLE IF EXISTS 'StudentWGrupie';\n" + 
				"		\n" + 
				"CREATE TABLE 'StudentWGrupie' (\n" + 
				"  'id' INTEGER NULL  PRIMARY KEY DEFAULT NULL,\n" + 
				"  'IdStudenta' INTEGER NULL DEFAULT NULL REFERENCES 'Studenci' ('id'),\n" + 
				"  'IdGrupy' INTEGER NULL DEFAULT NULL REFERENCES 'Grupa' ('id')\n" + 
				");\n" + 
				"\n" + 
				"-- ---\n" + 
				"-- Table 'Oceny'\n" + 
				"-- \n" + 
				"-- ---\n" + 
				"\n" + 
				"DROP TABLE IF EXISTS 'Oceny';\n" + 
				"		\n" + 
				"CREATE TABLE 'Oceny' (\n" + 
				"  'id' INTEGER NULL PRIMARY KEY DEFAULT NULL,\n" + 
				"  'Tresc' CHAR(30) NOT NULL DEFAULT 'NULL',\n" + 
				"  'IdGrupy' INTEGER NULL DEFAULT NULL REFERENCES 'Grupa' ('id')\n" + 
				");\n" + 
				"\n" + 
				"-- ---\n" + 
				"-- Table 'OcenyStudenta'\n" + 
				"-- \n" + 
				"-- ---\n" + 
				"\n" + 
				"DROP TABLE IF EXISTS 'OcenyStudenta';\n" + 
				"		\n" + 
				"CREATE TABLE 'OcenyStudenta' (\n" + 
				"  'id' INTEGER NULL PRIMARY KEY DEFAULT NULL,\n" + 
				"  'Wartosc' CHAR(16) NOT NULL DEFAULT 'NULL',\n" + 
				"  'Data' TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,\n" + 
				"  'IdOceny' INTEGER NULL DEFAULT NULL REFERENCES 'Oceny' ('id'),\n" + 
				"  'IdStudenta' INTEGER NULL DEFAULT NULL REFERENCES 'Studenci' ('id')\n" + 
				");\n" + 
				"\n" + 
				"DROP VIEW IF EXISTS GrupyView;\n" + 
				"\n" + 
				"CREATE VIEW GrupyView AS \n" + 
				"SELECT id AS _id, \n" + 
				"	GrupaDziek||' '||Przedmiot||' '||Czas AS Nazwa \n" + 
				"FROM Grupa;\n" + 
				"\n" + 
				"DROP VIEW IF EXISTS GrupyStudentaView;\n" + 
				"\n" + 
				"CREATE VIEW GrupyStudentaView AS SELECT \n" + 
				"    Grupa.id AS _id, \n" + 
				"    GrupaDziek||' '||Przedmiot||' '||Czas AS Nazwa, \n" + 
				"    Studenci.id AS idS,\n" + 
				"    (SELECT COUNT(StudentWGrupie.id) \n" + 
				"     FROM StudentWGrupie \n" + 
				"     WHERE Grupa.id=StudentWGrupie.idGrupy \n" + 
				"    	AND Studenci.id=StudentWGrupie.idStudenta) AS Jest \n" + 
				"FROM Grupa, Studenci; \n" + 
				"\n" + 
				"DROP VIEW IF EXISTS StudenciView;\n" + 
				"\n" + 
				"CREATE VIEW StudenciView AS \n" + 
				"SELECT S.Id AS _id, \n" + 
				"  S.Nazwisko||' '||S.Imie AS Nazwa, \n" + 
				"  GROUP_CONCAT((SELECT grupadziek FROM grupa WHERE grupa.id=swg.idgrupy)) AS Grupy\n" +
				"FROM Studenci S " +
				"LEFT OUTER JOIN StudentWGrupie SWG ON S.id=SWG.IdStudenta " +
				"GROUP BY _id";
		
		db.execSQL(createSQL);
		//Log.d("", "Empty tables created");
	}

	private static void copyFile (String inFile, String outFile) throws IOException
	{
		FileInputStream in = new FileInputStream(inFile);
	    FileOutputStream out = new FileOutputStream(outFile);
	    
	    copyFile(in, out);
	}
	
	private static void copyFile (InputStream in, OutputStream out) throws IOException
	{	  
	    byte[] buffer = new byte[1024];
	    int length;
	    while ((length = in.read(buffer))>0){
	        out.write(buffer, 0, length);
	    }

	    out.flush();
	    out.close();
	    in.close();
	}
	
	public static void importDBFromFile (String fileName, String dbFileName) throws IOException
	{
		copyFile(fileName, dbFileName);
		//Log.d("", "Import from " + fileName + " to " + dbFileName + " successful");
	}
	
	public void exportCurrentDBToFile (String fileName) throws IOException
	{	
		copyFile(db.getPath(), fileName);
		//Log.d("", "Export to " + fileName + " successful");
	}
	
	public Cursor getGroupCursor()
	{
		if (currentCursor != null) {
			currentCursor.close();
		}
		
		currentCursor = db.rawQuery("SELECT _id, Nazwa FROM GrupyView ORDER BY Nazwa ASC", null);
		//Log.d("DB", "getGroupCursor: "+currentCursor.toString());
		return currentCursor;
	}
	
	public Cursor getStudentGroupsCursor (long idS)
	{
		if (currentCursor != null) {
			currentCursor.close();
		}
		
		currentCursor = db.rawQuery("SELECT * FROM GrupyStudentaView WHERE idS=? ORDER BY Nazwa ASC", new String[]{String.valueOf(idS)});
		//Log.d("DB", "getStudentGroupsCursor: "+currentCursor.toString());
		return currentCursor;
	}
	
	public String getStringValueAtPosition (int columnIndex, int position)
	{
		String retval;
		
		if (currentCursor.moveToPosition(position))
			retval = currentCursor.getString(columnIndex);
		else
			retval = null;
		
//		//Log.d("DB", "getStringValueAtPosition columnIndex="+columnIndex+" position="+position+" retval="+retval);
		
		return retval;
	}
	
	public boolean addNewGroup (ClassInfo info)
	{
		ContentValues cv = new ContentValues();
		cv.put("Przedmiot", info.getClassName());
		cv.put("Czas", info.getClassTime());
		cv.put("Miejsce", info.getClassPlace());
		cv.put("GrupaDziek", info.getClassGroup());
		
		long result = db.insert("Grupa", null, cv);
		//Log.d("DB", "Group insert result "+result);
		
		lastInsertID = result;
		
		return (result != -1);	
	}
	
	public boolean addNewStudent (StudentInfo info)
	{
		ContentValues cv = new ContentValues();
		cv.put("Nazwisko", info.getStudentFamilyName());
		cv.put("Imie", info.getStudentName());
		cv.put("NrIndeksu", info.getStudentIndexNumber());
		cv.put("KluczSkrzynki", info.getStudentKeyNumber());
		
		long result = db.insert("Studenci", null, cv);
		//Log.d("DB", "Student insert result "+result);
		
		lastInsertID = result;
		
		return (result != -1);
	}
	
	
	public long getLastInsertID ()
	{
		return lastInsertID;
	}

	public boolean addStudentGroups(long studid, long[] groupids) 
	{
		boolean result = true;
		
		db.beginTransaction();
		
		try {
			for (int i = 0; (i < groupids.length) && result; i++) {
				ContentValues cv = new ContentValues();
				cv.put("IdStudenta", studid);
				cv.put("IdGrupy", groupids[i]);
			
				long id = db.insert("StudentWGrupie", null, cv);
				//Log.d("DB", "Student " + studid + " group id " + groupids[i] + " insert result "+id);
			
				result = (id != -1);			
			}
			if (result)
				db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}

		return result;
	}

	public Cursor getStudentCursor(String groupId) 
	{
		if (currentCursor != null) {
			currentCursor.close();
		}
		
		currentCursor = db.rawQuery("SELECT Studenci.ID AS _id, " +
				"Studenci.Nazwisko||' '||Studenci.Imie AS Nazwa " +
				"FROM StudentWGrupie, Studenci " +
				"WHERE Studenci.id=StudentWGrupie.IDStudenta AND StudentWGrupie.IDGrupy=? " +
				"ORDER BY Nazwa ASC", 
				new String[]{groupId});
		//Log.d("DB", "getStudentCursor: "+currentCursor.toString());
		
		return currentCursor;
	}
	
	public Cursor getGradesCursor (String groupId, String studentId)
	{
		if (currentCursor != null) {
			currentCursor.close();
		}
				
		String rawQuery = "SELECT IFNULL((SELECT os.id FROM ocenystudenta os WHERE os.idstudenta=% AND os.idoceny=oceny.id),-1) AS _id," +
							"oceny.tresc, " +
							"(SELECT wartosc FROM ocenystudenta os WHERE os.idstudenta=% AND os.idoceny=oceny.id) AS wartosc," +
							"%," +
							"oceny.id " +
							"FROM oceny " +
							"WHERE oceny.idgrupy=? " +
							"ORDER BY oceny.tresc";
		String query = rawQuery.replace("%", studentId).replace("?", groupId);
		//Log.d("DB", "query="+query);
		
		currentCursor = db.rawQuery(query, null);
		
		return currentCursor;
	}

	public boolean updateStudentGrade(String studGradeId, String grade) 
	{
		ContentValues cv = new ContentValues();
		cv.put("Wartosc", grade);
		
		int count = db.update("OcenyStudenta", cv, "id=?", new String[]{studGradeId});
		//Log.d("DB", "updateStudentGrade studGradeId="+studGradeId+" grade="+grade+" count="+count);
		
		return count == 1;
	}
	
	public boolean insertStudentGrade (String grade, String studentId, String taskId)
	{
		ContentValues cv = new ContentValues();
		cv.put("Wartosc", grade);
		cv.put("IdOceny", taskId);
		cv.put("IdStudenta", studentId);
		
		long result = db.insert("OcenyStudenta", null, cv);
		//Log.d("DB", "Student grade insert result "+result);
		
		lastInsertID = result;
		
		return (result != -1);
	}

	public boolean editGroup(String groupId, ClassInfo info) 
	{
		ContentValues cv = new ContentValues();
		cv.put("Przedmiot", info.getClassName());
		cv.put("Miejsce", info.getClassPlace());
		cv.put("Czas", info.getClassTime());
		cv.put("GrupaDziek", info.getClassGroup());
		
		long result = db.update("Grupa", cv, "id=?", new String[]{groupId});
		
		lastInsertID = result;
		
		return (result != -1);
	}

	public class DBClassInfo
		implements ClassInfo
	{
		Cursor cursor;
		
		public DBClassInfo(Cursor c) {
			cursor = c;
		}
		
		@Override
		public String getClassName() {
			return cursor.getString(0);
		}

		@Override
		public String getClassTime() {
			return cursor.getString(1);
		}

		@Override
		public String getClassPlace() {
			return cursor.getString(2);
		}

		@Override
		public String getClassGroup() {
			return cursor.getString(3);
		}
	}
	
	public ClassInfo getClassInfo(String groupId) 
	{
		if (currentCursor != null) {
			currentCursor.close();
		}

		//Log.d("DB", "getClassInfo groupId="+groupId);
		currentCursor = db.query("Grupa", new String[]{"Przedmiot", "Czas", "Miejsce", "GrupaDziek"}, "id=?", new String[]{groupId}, null, null, null);
		currentCursor.moveToFirst();
		
		return new DBClassInfo(currentCursor);
	}

	public boolean newGrade(String groupId, String task) 
	{
		ContentValues cv = new ContentValues();
		cv.put ("Tresc", task);
		cv.put ("IdGrupy", groupId);
		
		//Log.d("DB", "newGrade groupId="+groupId+" task="+task);
		long result = db.insert("Oceny", null, cv);
		
		lastInsertID = result;
		
		return (result != -1);
	}

	public boolean deleteGrade(String gradeId) 
	{
		db.delete("Oceny", "id=?", new String[]{gradeId});
		
		return true;
	}

	public Cursor getGroupGradesCursor(String groupId) 
	{
		if (currentCursor != null) {
			currentCursor.close();
		}
		
		//Log.d("DB", "getGroupGradesCursor groupId="+groupId);
		currentCursor = db.query("Oceny", new String[]{"Id", "Tresc"}, "IdGrupy=?", new String[]{groupId}, null, null, "Id ASC");
		
		return currentCursor;
	}

	public Cursor getAllStudentsCursor(String name) 
	{
		if (currentCursor != null) {
			currentCursor.close();
		}
		
		//Log.d("DB", "getAllStudentsCursor name="+name);
		currentCursor = db.query("StudenciView", new String[]{"_id", "Nazwa", "Grupy"}, "Nazwa LIKE ?", new String[]{"%"+name+"%"}, null, null, null);
		
		return currentCursor;
	}

	public boolean editGrade(String gradeId, String task) 
	{
		ContentValues cv = new ContentValues();
		cv.put ("Tresc", task);
		
		//Log.d("DB", "newGrade gradeId="+gradeId+" task="+task);
		long result = db.update("Oceny", cv, "id=?", new String[]{gradeId});
		
		lastInsertID = result;
		
		return (result != -1);
	} 
	
	public class DBStudentInfo implements StudentInfo
	{
		Cursor cursor;
		
		public DBStudentInfo(Cursor _c) {
			cursor = _c;
		}
		
		@Override
		public String getStudentFamilyName() {
			return cursor.getString(0);
		}

		@Override
		public String getStudentName() {
			return cursor.getString(1);
		}

		@Override
		public String getStudentIndexNumber() {
			return cursor.getString(2);
		}

		@Override
		public String getStudentKeyNumber() {
			return cursor.getString(3);
		}		
	}
	
	public StudentInfo getStudentInfo (String studentId)
	{
		if (currentCursor != null) {
			currentCursor.close();
		}

		//Log.d("DB", "getStudentInfo studentId="+studentId);
		currentCursor = db.query("Studenci", new String[]{"Nazwisko", "Imie", "NrIndeksu", "KluczSkrzynki"}, "id=?", new String[]{studentId}, null, null, null);
		currentCursor.moveToFirst();
		
		return new DBStudentInfo(currentCursor);
	}


	public boolean editStudent(String studentId, StudentInfo info) 
	{
		ContentValues cv = new ContentValues();
		cv.put("Nazwisko", info.getStudentFamilyName());
		cv.put("Imie", info.getStudentName());
		cv.put("NrIndeksu", info.getStudentIndexNumber());
		cv.put("KluczSkrzynki", info.getStudentKeyNumber());
		
		long result = db.update("Studenci", cv, "id=?", new String[]{studentId});
		
		lastInsertID = result;
		
		return (result != -1);
	}

	public void deleteStudentFromGroup(String studentId, String groupId) 
	{
		db.delete("StudentWGrupie", "idStudenta=? AND idGrupy=?", new String[]{studentId, groupId});
	}

	public boolean addStudentToGroup(String studentId, String groupId) 
	{		
		deleteStudentFromGroup(studentId, groupId);
		
		ContentValues cv = new ContentValues();
		cv.put("IdStudenta", studentId);
		cv.put("IdGrupy", groupId);
		
		long result = db.insert("StudentWGrupie", null, cv);
		
		lastInsertID = result;
		
		return (result != -1);
	}
}
