package hit.cs.iread.model;

import java.util.Date;

public class Note {
	private static final int WORD_NUMBER = 180;
	private int id;
	private Date date;
	private String index;
	private String content;
	private int startPage;
	private int length;

	public Note(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getIndex() {
		return index;
	}

	public void setIndex(String index) {
		this.index = index;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
		this.length = this.content.length() / WORD_NUMBER;
	}

	public int getStartPage() {
		return startPage;
	}

	public void setStartPage(int startPage) {
		this.startPage = startPage;
	}

	public void increaseStartPage(int increment) {
		this.startPage += increment;
	}

	public int getLength() {
		return length;
	}

}
