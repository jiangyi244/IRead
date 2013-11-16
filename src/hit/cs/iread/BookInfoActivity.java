package hit.cs.iread;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import hit.cs.iread.model.Book;
import hit.cs.iread.model.Bookshelf;
import hit.cs.iread.model.DatabaseHelper;
import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

public class BookInfoActivity extends Activity {
	private SQLiteOpenHelper helper;
	private Bookshelf bookshelf;
	private Book book;
	private ImageView bookImage;
	private TextView title;
	private TextView author;
	private TextView publisher;
	private TextView pubDate;
	private TextView pages;
	private RatingBar ratingBar;
	private TextView rating;
	private TextView summary;
	private Button button;
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case R.id.path_ready:
				new Thread(new ImageThread(), (String) msg.obj).start();
				break;
			case R.id.info_ready:
				BookInfoActivity.this.showInfo();
				break;
			case R.id.image_ready:
				BookInfoActivity.this.showImage();
				break;
			}
		}
	};

	private class InfoThread implements Runnable {
		public void run() {
			try {
				int num = 0;
				StringBuffer titleBuf = new StringBuffer();
				StringBuffer authorBuf = new StringBuffer();
				StringBuffer publisherBuf = new StringBuffer();
				StringBuffer pubDateBuf = new StringBuffer();
				StringBuffer pagesBuf = new StringBuffer();
				StringBuffer summaryBuf = new StringBuffer();
				URL url = new URL("http://api.douban.com/book/subject/isbn/"
						+ BookInfoActivity.this.book.getISBN());
				InputStream input = url.openStream();
				DocumentBuilderFactory factory = DocumentBuilderFactory
						.newInstance();
				DocumentBuilder builder = factory.newDocumentBuilder();
				Document document = builder.parse(input);
				Element root = document.getDocumentElement();
				NodeList list = root.getChildNodes();
				NodeList childList = null;
				Node node = null;
				for (int i = 0; i < list.getLength(); i++) {
					node = list.item(i);
					if (node.getNodeType() == Node.ELEMENT_NODE) {
						if (node.getNodeName().equals("link"))
							if (((Element) node).getAttribute("rel").equals(
									"image")) {
								String imagePath = ((Element) node)
										.getAttribute("href").replace("spic",
												"lpic");
								Message msg = new Message();
								msg.what = R.id.path_ready;
								msg.obj = imagePath;
								BookInfoActivity.this.handler.sendMessage(msg);
							}
						if (node.getNodeName().equals("title")) {
							childList = node.getChildNodes();
							for (int j = 0; j < childList.getLength(); j++)
								titleBuf.append(childList.item(j)
										.getNodeValue());
							continue;
						} else if (node.getNodeName().equals("author")) {
							childList = node.getChildNodes();
							for (int j = 0; j < childList.getLength()
									&& num < 1; j++) {
								Node childNode = childList.item(j);
								if (childNode.getNodeType() == Node.ELEMENT_NODE)
									if (childNode.getNodeName().equals("name")) {
										NodeList childNodeList = childNode
												.getChildNodes();
										for (int k = 0; k < childNodeList
												.getLength(); k++)
											authorBuf.append(childNodeList
													.item(k).getNodeValue());
									}
							}
							num++;
							continue;
						} else if (node.getNodeName().equals("db:attribute")) {
							Element element = (Element) node;
							childList = node.getChildNodes();
							if (element.getAttribute("name")
									.equals("publisher"))
								for (int j = 0; j < childList.getLength(); j++)
									publisherBuf.append(childList.item(j)
											.getNodeValue());
							else if (element.getAttribute("name").equals(
									"pubdate"))
								for (int j = 0; j < childList.getLength(); j++)
									pubDateBuf.append(childList.item(j)
											.getNodeValue());
							else if (element.getAttribute("name").equals(
									"pages"))
								for (int j = 0; j < childList.getLength(); j++)
									pagesBuf.append(childList.item(j)
											.getNodeValue());
							continue;
						} else if (node.getNodeName().equals("gd:rating"))
							BookInfoActivity.this.book
									.setRating(((Element) node)
											.getAttribute("average"));
						else if (node.getNodeName().equals("summary")) {
							childList = node.getChildNodes();
							for (int j = 0; j < childList.getLength(); j++)
								summaryBuf.append(childList.item(j)
										.getNodeValue());
							continue;
						}
					}
				}
				input.close();
				if (num > 1)
					authorBuf.append("µÈ");
				BookInfoActivity.this.book.setTitle(titleBuf.toString());
				BookInfoActivity.this.book.setAuthor(authorBuf.toString());
				BookInfoActivity.this.book
						.setPublisher(publisherBuf.toString());
				BookInfoActivity.this.book.setPubDate(pubDateBuf.toString());
				BookInfoActivity.this.book.setPages(pagesBuf.toString());
				BookInfoActivity.this.book.setSummary(summaryBuf.toString());
				Message msg = new Message();
				msg.what = R.id.info_ready;
				BookInfoActivity.this.handler.sendMessage(msg);
			} catch (Exception e) {
			}
		}
	}

	private class ImageThread implements Runnable {
		public void run() {
			try {
				URL url = new URL(Thread.currentThread().getName());
				HttpURLConnection con = (HttpURLConnection) url
						.openConnection();
				InputStream input = con.getInputStream();
				BookInfoActivity.this.book.setBitmap(BitmapFactory
						.decodeStream(input));
				input.close();
				con.disconnect();
				Message msg = new Message();
				msg.what = R.id.image_ready;
				BookInfoActivity.this.handler.sendMessage(msg);
			} catch (Exception e) {
			}
		}
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.setContentView(R.layout.bookinfo);
		this.bookImage = (ImageView) super.findViewById(R.id.bookimage);
		this.title = (TextView) super.findViewById(R.id.title);
		this.author = (TextView) super.findViewById(R.id.author);
		this.publisher = (TextView) super.findViewById(R.id.publisher);
		this.pubDate = (TextView) super.findViewById(R.id.pubdate);
		this.pages = (TextView) super.findViewById(R.id.pages);
		this.ratingBar = (RatingBar) super.findViewById(R.id.ratingbar);
		this.rating = (TextView) super.findViewById(R.id.rating);
		this.summary = (TextView) super.findViewById(R.id.summary);
		this.button = (Button) super.findViewById(R.id.button);
		this.helper = new DatabaseHelper(this);
		this.bookshelf = new Bookshelf(this.helper.getWritableDatabase());
		this.bookshelf.init();
		Intent it = super.getIntent();
		this.book = this.bookshelf.search(it.getStringExtra("iSBN"));
		if (this.book != null) {
			this.showInfo();
			this.showImage();
			this.button.setVisibility(View.GONE);
		} else {
			this.book = new Book(it.getStringExtra("iSBN"));
			new Thread(new InfoThread()).start();
		}
		this.button.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				BookInfoActivity.this.bookshelf = new Bookshelf(
						BookInfoActivity.this.helper.getWritableDatabase());
				BookInfoActivity.this.bookshelf.add(BookInfoActivity.this.book);
				BookInfoActivity.this.button.setVisibility(View.GONE);
			}
		});
	}

	private void showInfo() {
		this.title.setText(this.book.getTitle());
		this.author.setText(this.book.getAuthor());
		this.publisher.setText(this.book.getPublisher());
		this.pubDate.setText(this.book.getPubDate());
		this.pages.setText(this.book.getPages());
		this.ratingBar.setRating(Float.valueOf(this.book.getRating()) / 2);
		this.rating.setText(this.book.getRating());
		this.summary.setText("  " + this.book.getSummary());
	}

	private void showImage() {
		this.bookImage.setImageBitmap(Bitmap.createScaledBitmap(
				this.book.getBitmap(), 94, 132, true));
	}

}
