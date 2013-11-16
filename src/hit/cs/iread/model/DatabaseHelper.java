package hit.cs.iread.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
	private static final String DATABASE = "iread.db";
	private static final int VERSION = 1;

	public DatabaseHelper(Context context) {
		super(context, DATABASE, null, VERSION);
	}

	public void onCreate(SQLiteDatabase db) {
		String sql = "CREATE TABLE IF NOT EXISTS " + "book" + "("
				+ "iSBN INTEGER PRIMARY KEY," + "title VARCHAR(50),"
				+ "author VARCHAR(50)," + "publisher VARCHAR(50),"
				+ "pubDate VARCHAR(50)," + "pages INTEGER," + "rating FLOAT,"
				+ "summary TEXT," + "bitmap BLOB)";
		db.execSQL(sql);
		sql = "CREATE TABLE IF NOT EXISTS " + "note" + "(" + "nid INTEGER,"
				+ "ID INTEGER AUTOINCREMENT," + "date DATE,"
				+ "index VARCHAR(50)," + "content TEXT,"
				+ "PRIMARY KEY(nid,id))";
		db.execSQL(sql);
	}

	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		String sql = "DROP TABLE IF EXISTS " + "book";
		db.execSQL(sql);
		sql = "DROP TABLE IF EXISTS " + "note";
		db.execSQL(sql);
		this.onCreate(db);
	}

}
