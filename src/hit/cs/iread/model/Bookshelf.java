package hit.cs.iread.model;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class Bookshelf {
	private static final String TABLE = "book";
	private SQLiteDatabase db;
	private ArrayList<Book> books;

	public Bookshelf(SQLiteDatabase db) {
		this.db = db;
		this.books = new ArrayList<Book>();
	}

	public void init() {
		Book book = null;
		String sql = "SELECT * FROM " + TABLE;
		Cursor result = this.db.rawQuery(sql, null);
		for (result.moveToFirst(); !result.isAfterLast(); result.moveToNext()) {
			book = new Book(String.valueOf(result.getLong(0)));
			book.setTitle(result.getString(1));
			book.setAuthor(result.getString(2));
			book.setPublisher(result.getString(3));
			book.setPubDate(result.getString(4));
			book.setPages(result.getString(5));
			book.setRating(result.getString(6));
			book.setSummary(result.getString(7));
			book.setBitmap(BitmapFactory.decodeByteArray(result.getBlob(8), 0,
					result.getBlob(8).length));
			this.books.add(book);
		}
		this.db.close();
	}

	public void add(Book book) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ContentValues cv = new ContentValues();
		cv.put("iSBN", Long.valueOf(book.getISBN()));
		cv.put("title", book.getTitle());
		cv.put("author", book.getAuthor());
		cv.put("publisher", book.getPublisher());
		cv.put("pubDate", book.getPubDate());
		cv.put("pages", book.getPages());
		cv.put("rating", book.getRating());
		cv.put("summary", book.getSummary());
		book.getBitmap().compress(Bitmap.CompressFormat.PNG, 100, bos);
		cv.put("bitmap", bos.toByteArray());
		this.db.insert(TABLE, null, cv);
		this.db.close();
		this.books.add(book);
	}

	public Book search(String iSBN) {
		for (Book book : this.books)
			if (book.getISBN().equals(iSBN))
				return book;
		return null;
	}

	public void remove(Book book) {
		String whereClause = "iSBN=?";
		String whereArgs[] = new String[] { book.getISBN() };
		this.db.delete(TABLE, whereClause, whereArgs);
		this.db.close();
		this.books.remove(book);
	}

}
