package hit.cs.iread.model;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class NoteBook {
	private static final String TABLE = "note";
	private SQLiteDatabase db;
	private String nid;
	private ArrayList<Note> notes;

	public NoteBook(SQLiteDatabase db) {
		this.db = db;
		this.notes = new ArrayList<Note>();
	}

	public void init() {
		int startPage = 0;
		Note note = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String sql = "SELECT id,date,index,content FROM " + TABLE
				+ " WHERE nid=? ORDER BY date,id";
		String selectionArgs[] = new String[] { this.nid };
		Cursor result = this.db.rawQuery(sql, selectionArgs);
		for (result.moveToFirst(); !result.isAfterLast(); result.moveToNext()) {
			try {
				note = new Note(result.getInt(0));
				note.setDate(sdf.parse(result.getString(1)));
				note.setIndex(result.getString(2));
				note.setContent(result.getString(3));
				note.setStartPage(startPage);
				startPage += note.getLength();
				this.notes.add(note);
			} catch (Exception e) {
			}
		}
		this.db.close();
	}

	public int add(Note note) {
		ContentValues cv = new ContentValues();
		cv.put("date",
				(new SimpleDateFormat("yyyy-MM-dd")).format(note.getDate()));
		cv.put("index", note.getIndex());
		cv.put("content", note.getContent());
		this.db.insert(TABLE, null, cv);
		this.db.close();
		for (int i = this.notes.size(); i > 0; i--)
			if (note.getDate().compareTo(this.notes.get(i - 1).getDate()) >= 0) {
				this.notes.add(i, note);
				for (int j = i + 1; j < this.notes.size(); j++)
					this.notes.get(j).increaseStartPage(note.getLength());
				return i;
			}
		this.notes.add(0, note);
		for (int j = 1; j < this.notes.size(); j++)
			this.notes.get(j).increaseStartPage(note.getLength());
		return 0;
	}

	public Note search(int id) {
		for (Note note : this.notes)
			if (note.getId() == id)
				return note;
		return null;
	}

	public void remove(Note note) {
		String whereClause = "id=?";
		String whereArgs[] = new String[] { String.valueOf(note.getId()) };
		this.db.delete(TABLE, whereClause, whereArgs);
		this.db.close();
		int i = this.notes.indexOf(note);
		this.notes.remove(i);
		for (; i < this.notes.size(); i++)
			this.notes.get(i).increaseStartPage(-note.getLength());
	}

}
