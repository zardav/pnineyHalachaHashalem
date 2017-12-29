package com.rafraph.pnineyHalachaHashalem;


import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.PopupMenu;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.view.*;

/*jsoup*/
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

@SuppressLint("SetJavaScriptEnabled")
public class textMain extends ActionBarActivity implements View.OnClickListener//, OnGestureListener
{
	private static final int BRACHOT      	= 0;
	private static final int HAAMVEHAAREZ 	= 1;
	private static final int ZMANIM    		= 2;
	private static final int YAMIM    		= 3;
	private static final int LIKUTIM_A 		= 4;
	private static final int LIKUTIM_B 		= 5;
	private static final int LIKUTIM_C 		= 6;
	private static final int MOADIM    		= 7;
	private static final int SUCOT			= 8;
	private static final int PESACH			= 9;
	private static final int SHVIIT			= 10;
	private static final int SHABAT			= 11;
	private static final int SIMCHAT		= 12;	
	private static final int TEFILA			= 13;
	private static final int TEFILAT_NASHIM	= 14;
	private static final int HAR_BRACHOT    = 15;
	private static final int HAR_YAMIM      = 16;
	private static final int HAR_MOADIM     = 17;
	private static final int HAR_SUCOT      = 18;
	private static final int HAR_SHABAT     = 19;
	private static final int HAR_SIMCHAT    = 20;
	private static final int BOOKS_HEB_NUMBER	= 21;
	private static final int E_TEFILA       = 21;
	private static final int E_PESACH       = 22;
	private static final int E_ZMANIM       = 23;
	private static final int E_WOMEN_PRAYER = 24;
	private static final int E_SHABAT       = 25;
	private static final int F_TEFILA       = 26;
	private static final int BOOKS_NUMBER	= 27;

	/*							0	1	2	3	4	5	6	7	8	9  10  11  12  13  14  15  16  17  18 19  20  21  22  23  24  25  26*/
	public int[] lastChapter = {17, 11, 17, 10, 13, 17, 16, 13, 8, 16, 11, 30, 10, 26, 24, 17, 10, 12, 8, 30, 10, 26, 16, 15, 24, 30, 26};

    private static final int HEBREW	 = 0;
    private static final int ENGLISH = 1;
    private static final int RUSSIAN = 2;
    private static final int SPANISH = 3;
    private static final int FRENCH = 4;

    WebView webview;
	public static int[] book_chapter = new int[2];
	boolean cameFromSearch = false, firstTime = true, ChangeChapter = false;
	String searchPosition = null, sectionsForToast = null;
	ImageButton bParagraphs, bFullScreen, bNext_sec, bPrevious_sec, bNext_page, bPrevious_page, bFindNext, bFindPrevious;
	LinearLayout llMainLayout;
	String stHeadersArr;
	Elements headers;
	String fileName, fileNameOnly, lastFileName = null;
	String[][] chaptersFiles = new String[BOOKS_NUMBER][31];
	private LinearLayout lnrOptions, lnrFindOptions;
	public static final String PREFS_NAME = "MyPrefsFile";
	static SharedPreferences mPrefs;
	SharedPreferences.Editor shPrefEditor;
	int scrollY = 0;
	public int BlackBackground=0, SleepScreen=1, cbFullScreen=1, cbAssistButtons=1, SimchatMailPswd=0;
	boolean bookmark = false;
	Document doc = null;
	static MenuInflater inflater;
	static public ActionBar textActionBar;
	public String query, title;
	public String note_id;
	public Resources resources;
	static byte fullScreenFlag = 0;
	public static byte rotate = 0; 
	public String noteStr = "0";
    public int MyLanguage;

	/*for bookmarks*/
	public List<String> bookmarks_array_names = new ArrayList<String>();
	public EditText result;
	public Spinner spinner1, spinnerAutoScroll;
	public EditText BookmarkName, TextToSearch, TextToDecode;
	public Dialog bookmarkDialog, innerSearchDialog, acronymsDialog, autoScrollDialog, simchatDialog;
	String[][] chaptersNames = new String[BOOKS_NUMBER][31];
	String innerSearchText, acronymsText;

	//	static int odd=1;
	public int API;
	static public boolean jumpToSectionFlag = false;

	public int fontSize;

	@TargetApi(Build.VERSION_CODES.KITKAT)
	@SuppressLint("JavascriptInterface")
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		loadActivity();

	}//onCreate

	private void loadActivity() 
	{
		mPrefs = getSharedPreferences(PREFS_NAME, 0);
		shPrefEditor = mPrefs.edit();
		cbAssistButtons = mPrefs.getInt("cbAssistButtons", 1);
		SimchatMailPswd = mPrefs.getInt("SimchatMailPswd", 0);
        MyLanguage = mPrefs.getInt("MyLanguage", 0);

		if(cbAssistButtons==0)
			setContentView(R.layout.text_main);
		else
			setContentView(R.layout.text_main_down);
		
		firstTime = true;
		book_chapter[0] = -1;
		book_chapter[1] = -1;
		int fromBookmarks = 0;
		lnrOptions = (LinearLayout) findViewById(R.id.lnrOptions);
		lnrFindOptions = (LinearLayout) findViewById(R.id.lnrFindOptions);
		final Context context = this;
		webview = (WebView) findViewById(R.id.webView1);
		WebSettings webSettings = webview.getSettings();
		webSettings.setDefaultTextEncodingName("utf-8");
		webSettings.setJavaScriptEnabled(true);
		webSettings.setSupportZoom(true);
		API = android.os.Build.VERSION.SDK_INT;
		if(API < 19)
			webSettings.setBuiltInZoomControls(true);

		resources = getResources();

		webview.requestFocusFromTouch();

		webview.setWebViewClient(new MyWebViewClient());

		bParagraphs    = (ImageButton) findViewById(R.id.ibChapters);
		bFullScreen    = (ImageButton) findViewById(R.id.ibFullScreen);
		bNext_sec      = (ImageButton) findViewById(R.id.ibNext);
		bPrevious_sec  = (ImageButton) findViewById(R.id.ibPrevious);
		bNext_page     = (ImageButton) findViewById(R.id.ibNextPage);
		bPrevious_page = (ImageButton) findViewById(R.id.ibPreviousPage);
		llMainLayout   = (LinearLayout) findViewById(R.id.llMainLayout);
		lnrOptions     = (LinearLayout) findViewById(R.id.lnrOptions);
		bFindNext      = (ImageButton) findViewById(R.id.ibFindNext);
		bFindPrevious  = (ImageButton) findViewById(R.id.ibFindPrevious);

		bParagraphs.setOnClickListener(this);
		bFullScreen.setOnClickListener(this);
		bNext_sec.setOnClickListener(this);
		bPrevious_sec.setOnClickListener(this);
		bNext_page.setOnClickListener(this);
		bPrevious_page.setOnClickListener(this);
		bFindNext.setOnClickListener(this);
		bFindPrevious.setOnClickListener(this);

		jumpToSectionFlag = false;

		final Runnable runnable = new Runnable()
		{
			public void run()
			{
				// your code here
				String note, content = null;
				int intNoteId;
				final Dialog dialog = new Dialog(context);
				WebView webviewNote;
				WebSettings webSettingsNote;
				BlackBackground = mPrefs.getInt("BlackBackground", 0);
				dialog.setContentView(R.layout.note);

				intNoteId = Integer.parseInt(note_id)-1000;
				note_id = Integer.toString(intNoteId);
				dialog.setTitle("        הערה "+note_id);

				webviewNote = (WebView) dialog.findViewById(R.id.webViewNote1);
				webSettingsNote = webviewNote.getSettings();
				webSettingsNote.setDefaultTextEncodingName("utf-8");
				webviewNote.requestFocusFromTouch();
				if(API < 19)
					webSettingsNote.setBuiltInZoomControls(true);

				content =  "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>"+
						"<html><head>"+
						"<meta http-equiv=\"content-type\" content=\"text/html; charset=utf-8\" />"+
						"<head>";
				if(BlackBackground == 0)
					content += "<body>";//White background
				else if(BlackBackground == 1)
					content += "<body style=\"background-color:black;color:white\">";//Black background
				ParseTheDoc();
				headers = doc.select("div#ftn"+note_id);
				note = headers.get(0).text();
				if (book_chapter[0] < BOOKS_HEB_NUMBER)/*if this is a hebrew book*/
				{
					note = note.substring(6);//in order to remove the prefix of the note. something like [1]
					content += "<p dir=\"RTL\">" + note + "</p> </body></html>";
				}
				else
				{
					note = note.substring(3);//in order to remove the prefix of the note. something like [1]
					content += "<p dir=\"LTR\">" + note + "</p> </body></html>";
				}

				webviewNote.loadData(content, "text/html; charset=utf-8", "UTF-8");
				webSettingsNote.setDefaultFontSize(fontSize);
				dialog.show();
				
				dialog.setOnCancelListener(new DialogInterface.OnCancelListener()
				{
				    @Override
				    public void onCancel(DialogInterface dialog) 
				    {
				        //do whatever you want the back key to do
				    	dialog.dismiss();
				    	scrollSpeed = mPrefs.getInt("scrollSpeed", 2);
				    }
				});
			}
		};

		webview.addJavascriptInterface(new Object()
		{
			@JavascriptInterface
			public void performClick(String id)
			{
				scrollSpeed=0;
				setId(id);
				runOnUiThread(runnable);
			}
		}, "ok");

		fillChaptersFiles();


		BlackBackground = mPrefs.getInt("BlackBackground", 0);
		cbFullScreen = mPrefs.getInt("cbFullScreen", 1);
		
		inflater = getMenuInflater();
		textActionBar = getSupportActionBar();

		Bundle extras = getIntent().getExtras();
		if (extras != null) 
		{
			cameFromSearch = extras.getBoolean("cameFromSearch",false);
			searchPosition = extras.getString("searchPosition");
			if(extras.getIntArray("book_chapter") != null)
				book_chapter = extras.getIntArray("book_chapter");
			sectionsForToast = extras.getString("sectionsForToast");

			if(cameFromSearch == true)
			{
				query = extras.getString("query");
				findBookAndChapter();
				webview.loadUrl(chaptersFiles[book_chapter[0]][book_chapter[1]]);
				scrollY = 0;
				lnrFindOptions.setVisibility(View.VISIBLE);
			}
			else
			{
				lnrFindOptions.setVisibility(View.GONE);
				book_chapter = extras.getIntArray("book_chapter");
				fromBookmarks = extras.getInt("fromBookmarks");
				if(fromBookmarks == 1)/*came from bookmarks*/
				{
					webview.loadUrl(chaptersFiles[book_chapter[0]][book_chapter[1]]);
					scrollY = extras.getInt("bookmarkScrollY");
				}
				else if(book_chapter != null)
				{
					if(book_chapter[0] == 0xFFFF || book_chapter[1] == 0xFFFF)/*go to the last location*/
					{
						bookmark = true;
						book_chapter[0] = mPrefs.getInt("book", 0);
						book_chapter[1] = mPrefs.getInt("chapter", 0);
						webview.loadUrl(chaptersFiles[book_chapter[0]][book_chapter[1]]);
						scrollY = mPrefs.getInt("scrollY", 0);
					}
					else/*the regular choice of chapter*/
					{
						bookmark = false;
						scrollY = 0;
						webview.loadUrl(chaptersFiles[book_chapter[0]][book_chapter[1]]);
					}
				}
			}
		}
		fontSize = mPrefs.getInt("fontSize", 20);
		if(fontSize > 50)
			fontSize = 20;
		webSettings.setDefaultFontSize(fontSize);

		if(book_chapter[1] == lastChapter[book_chapter[0]])
			bNext_sec.setEnabled(false);
		else if(book_chapter[1] == 0)
			bPrevious_sec.setEnabled(false);

		webview.setWebChromeClient(new WebChromeClient() 
		{
			@Override
			public void onProgressChanged(WebView view, int progress) 
			{
				if ( view.getProgress()==100)
				{
					if(jumpToSectionFlag == false)
						jumpToY( scrollY );
				}
			}
		});

		final WebView wv = new WebView(this);
		wv.post(new Runnable() {
			@Override
			public void run() {
				wv.loadUrl(fileName);
			}
		});

		
	}
	
	public void  setId(String id)
	{
		note_id=id;
	}	

	private void jumpToY ( int yLocation )
	{
		webview.postDelayed( new Runnable ()
		{
			public void run()
			{ 
				if(scrollY != 0)
					webview.scrollTo(0, scrollY);
			}
		}, 400);/*how much time to delay*/
	}

	private void finddelay (final String query  )
	{
		webview.postDelayed( new Runnable ()
		{
			public void run()
			{ 
				int a =webview.findAll(query);
				try
				{
					Method m = WebView.class.getMethod("setFindIsUp", Boolean.TYPE);
					m.invoke(webview, true);
				}
				catch (Throwable ignored){}
			}
		}, 400);/*how much time to delay*/
	}
	
	private void WhiteTextAfterDelay (  )
	{
		webview.postDelayed( new Runnable ()
		{
			public void run()
			{ 
				webview.loadUrl("javascript:function myFunction() {var x = document.body;x.style.color = \"white\";} myFunction(); ");
				webview.findAll(query);
				try
				{
					Method m = WebView.class.getMethod("setFindIsUp", Boolean.TYPE);
					m.invoke(webview, true);
				}
				catch (Throwable ignored){}
			}
		}, 400);/*how much time to delay*/
	}
	
	public void ParseTheDoc()
	{
		String prefix;
		InputStream is;
		int size;
		byte[] buffer;
		String input;

		fileName = getClearUrl();
		if ((fileName.equals(lastFileName) == false))
		{
			lastFileName = fileName;
			prefix = "file:///android_asset/";
			fileNameOnly = fileName.substring(prefix.length());
			try 
			{
				is = getAssets().open(fileNameOnly);
				size = is.available();
				buffer = new byte[size];
				is.read(buffer);
				is.close();
				input = new String(buffer);
				doc = Jsoup.parse(input);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	protected void onStart() 
	{
		super.onStart();
		// The activity is about to become visible.

	}//onStart

	protected void onResume() 
	{
		super.onResume();
		// The activity has become visible (it is now "resumed").

		supportInvalidateOptionsMenu();
		BlackBackground = mPrefs.getInt("BlackBackground", 0);
		SleepScreen = mPrefs.getInt("SleepScreen", 1);

		if(SleepScreen == 0)
		{
			webview.setKeepScreenOn (false);
		}
		else if(SleepScreen == 1)
		{
			webview.setKeepScreenOn (true);
		}
		
		if(cbAssistButtons != mPrefs.getInt("cbAssistButtons", 1))
		{
			loadActivity();
		}
	}//onResume

	protected void onPause()
	{
		super.onPause();

		scrollY = webview.getScrollY();
		shPrefEditor.putInt("book", book_chapter[0]);
		shPrefEditor.putInt("chapter", book_chapter[1]);
		shPrefEditor.putInt("scrollY", scrollY);
		shPrefEditor.putInt("fontSize", fontSize);

		shPrefEditor.commit();
	}//onPaused

	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		// TODO Auto-generated method stub
		BlackBackground = mPrefs.getInt("BlackBackground", 0);

		if(book_chapter[1]==0)
			title = convertBookIdToName(book_chapter[0]);
		else
			title = convertBookIdToName(book_chapter[0]) + ": " + convertAnchorIdToSection(book_chapter[1]); 

		if(BlackBackground == 1)
		{
			textActionBar.setBackgroundDrawable(new ColorDrawable(Color.BLACK));
			inflater.inflate(R.menu.actionbar_textmain_black, menu);
			webview.loadUrl("javascript:function myFunction() {var x = document.body;x.style.color = \"white\";} myFunction(); ");
			webview.setBackgroundColor(0xFFFFFF);//black
			llMainLayout.setBackgroundColor(Color.BLACK);
			textActionBar.setTitle(Html.fromHtml("<font color=\"#ffffff\">" + title + "</font>"));
			bParagraphs.setImageDrawable(resources.getDrawable(R.drawable.ic_action_view_as_list));
			bFullScreen.setImageDrawable(resources.getDrawable(R.drawable.ic_action_full_screen));
			bNext_sec.setImageDrawable(resources.getDrawable(R.drawable.ic_action_next_item));
			bPrevious_sec.setImageDrawable(resources.getDrawable(R.drawable.ic_action_previous_item));
			bNext_page.setImageDrawable(resources.getDrawable(R.drawable.ic_action_down));
			bPrevious_page.setImageDrawable(resources.getDrawable(R.drawable.ic_action_up));
			if(cameFromSearch == true)
			{
				bFindNext.setImageDrawable(resources.getDrawable(R.drawable.ic_action_down_black));
				bFindPrevious.setImageDrawable(resources.getDrawable(R.drawable.ic_action_up_black));
			}

		} else {
			inflater.inflate(R.menu.actionbar_textmain, menu);
			textActionBar.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
			webview.loadUrl("javascript:function myFunction() {var x = document.body;x.style.color = \"black\";} myFunction(); ");
			webview.setBackgroundColor(0x000000);//white
			llMainLayout.setBackgroundColor(Color.WHITE);
			textActionBar.setTitle(Html.fromHtml("<font color=\"black\">" + title + "</font>"));
			bParagraphs.setImageDrawable(resources.getDrawable(R.drawable.ic_action_view_as_list));
			bFullScreen.setImageDrawable(resources.getDrawable(R.drawable.ic_action_full_screen));
			bNext_sec.setImageDrawable(resources.getDrawable(R.drawable.ic_action_next_item));
			bPrevious_sec.setImageDrawable(resources.getDrawable(R.drawable.ic_action_previous_item));
			bNext_page.setImageDrawable(resources.getDrawable(R.drawable.ic_action_down));
			bPrevious_page.setImageDrawable(resources.getDrawable(R.drawable.ic_action_up));
			if(cameFromSearch == true)
			{
				bFindNext.setImageDrawable(resources.getDrawable(R.drawable.ic_action_down));
				bFindPrevious.setImageDrawable(resources.getDrawable(R.drawable.ic_action_up));
			}
		}
		
		MenuInflater DownMenu = getMenuInflater();
        if(MyLanguage == ENGLISH)
		    DownMenu.inflate(R.menu.down_menu_english, menu);
		else if(MyLanguage == RUSSIAN)
			DownMenu.inflate(R.menu.down_menu_russian, menu);
		else if(MyLanguage == SPANISH)
			DownMenu.inflate(R.menu.down_menu_spanish, menu);
		else if(MyLanguage == FRENCH)
			DownMenu.inflate(R.menu.down_menu_french, menu);
        else
            DownMenu.inflate(R.menu.down_menu, menu);
		return true;
	}//onCreateOptionsMenu

	public void onBackPressed() 
	{
		if(fullScreenFlag == 1)
		{
			fullScreenFlag = 0;
			getSupportActionBar().show();
			lnrOptions.setVisibility(View.VISIBLE);
		}
		else
		{
			super.onBackPressed();
		}
	}

	public void onConfigurationChanged(Configuration newConfig) 
	{
		super.onConfigurationChanged(newConfig);
		// Checks the orientation of the screen
		if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) 
		{
			rotate=2;
		} 
		else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) 
		{
			rotate=1;
		}
	}
	
	 @Override
	 public boolean dispatchKeyEvent(KeyEvent event) 
	 {
		 int keyCode = event.getKeyCode();
		 int keyAction = event.getAction();
		 switch (keyCode) 
		 {
		 case KeyEvent.KEYCODE_VOLUME_UP:
			 if(keyAction == KeyEvent.ACTION_UP)
			 {
				 webview.pageUp(false);
			 }
			 return true;
		 case KeyEvent.KEYCODE_VOLUME_DOWN:
			 if(keyAction == KeyEvent.ACTION_UP)
			 {
				 webview.pageDown(false);
			 }
			 return true;
		 default:
			 return super.dispatchKeyEvent(event);
		 }
	 }

	 int scrollSpeed=1;
	 private Handler mHandler=new Handler();
	 public Runnable mScrollDown = new Runnable()
	 {
		 public void run()
		 {
			 if(scrollSpeed == 0) // in case of note opened
			 {
				 mHandler.postDelayed(this, 200);
			 }
			 else if(scrollSpeed == -1) // in case that "stop" pressed
			 {
				 webview.scrollBy(0, 0);
			 }
			 else
			 {
				 webview.scrollBy(0, 1);
				 mHandler.postDelayed(this, 200/scrollSpeed);
			 }
		 }
	 };
	 	 
	@SuppressWarnings("deprecation")
	@Override
	public void onClick(View view) 
	{
		String currentChapter;
		// TODO Auto-generated method stub
		switch(view.getId())
		{
		case R.id.ibChapters:
			findHeaders();
			showPopupMenu(view);
			break;

		case R.id.ibFullScreen:
			cbFullScreen = mPrefs.getInt("cbFullScreen", 1);
			if(cbFullScreen == 0)
				lnrOptions.setVisibility(View.GONE);
			getSupportActionBar().hide();
			Toast.makeText(getApplicationContext(), "לחץ על כפתור 'חזור' כדי לצאת ממסך מלא", Toast.LENGTH_LONG).show();
			fullScreenFlag = 1;
			break;
			
		case R.id.ibNext:
			cameFromSearch = false;
			scrollY = 0;/*In order to jump to the beginning of the chapter*/
			currentChapter = getClearUrl();
			getTheArrayLocation(currentChapter);
			if(checkIfChapterAllowed(book_chapter[1]+1) == false)/*check if this chapter need password*/
				break;
			book_chapter[1] += 1;
			webview.loadUrl(chaptersFiles[book_chapter[0]][book_chapter[1]]);
			title = convertBookIdToName(book_chapter[0]) + ": " + convertAnchorIdToSection(book_chapter[1]);
			if(book_chapter[1] == lastChapter[book_chapter[0]])
				bNext_sec.setEnabled(false);
			else
				bPrevious_sec.setEnabled(true);
			ChangeChapter = true;

			shPrefEditor.putInt("fontSize", fontSize);/*in order to keep the fontSize when moving to next chapter*/

			break;
			
		case R.id.ibPrevious:
			cameFromSearch = false;
			scrollY = 0;/*In order to jump to the beginning of the chapter*/
			currentChapter = getClearUrl();
			getTheArrayLocation(currentChapter);
			if(checkIfChapterAllowed(book_chapter[1]-1) == false)/*check if this chapter need password*/
				break;
			book_chapter[1] -= 1;
			webview.loadUrl(chaptersFiles[book_chapter[0]][book_chapter[1]]);
			if(book_chapter[1] == 0)
				title = convertBookIdToName(book_chapter[0]);
			else
				title = convertBookIdToName(book_chapter[0]) + ": " + convertAnchorIdToSection(book_chapter[1]);
			if(book_chapter[1] == 0)
				bPrevious_sec.setEnabled(false);
			else
				bNext_sec.setEnabled(true);
			ChangeChapter = true;

			shPrefEditor.putInt("fontSize", fontSize);/*in order to keep the fontSize when moving to next chapter*/
			break;
		
		case R.id.ibNextPage:
			webview.pageDown(false);
			break;
		
		case R.id.ibPreviousPage:
			webview.pageUp(false);
			break;
		
		case R.id.ibFindNext:
			webview.findNext(true);
			break;
		
		case R.id.ibFindPrevious:
			webview.findNext(false);
			break;
		}

	}//onClick

	public String strBookmark, Bookmarks;
	@SuppressLint("NewApi")
	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		final Context context = this;
		
		// TODO Auto-generated method stub
		switch (item.getItemId()) 
		{
		case R.id.action_search:
			innerSearch();
			break;
		case R.id.action_add_bookmark:
			// custom dialog
			bookmarkDialog = new Dialog(context);
			if(MyLanguage == ENGLISH)
				bookmarkDialog.setContentView(R.layout.add_bookmark_english);
			else if(MyLanguage == RUSSIAN)
				bookmarkDialog.setContentView(R.layout.add_bookmark_russian);
			else if(MyLanguage == SPANISH)
				bookmarkDialog.setContentView(R.layout.add_bookmark_spanish);
			else if(MyLanguage == FRENCH)
				bookmarkDialog.setContentView(R.layout.add_bookmark_french);
			else
				bookmarkDialog.setContentView(R.layout.add_bookmark);
			bookmarkDialog.setTitle("הוסף סימניה");

			Button dialogButton = (Button) bookmarkDialog.findViewById(R.id.dialogButtonOK);
			spinner1 = (Spinner) bookmarkDialog.findViewById(R.id.spinner1);
			BookmarkName = (EditText) bookmarkDialog.findViewById(R.id.editTextBookmarkName);

			// if button is clicked, close the custom dialog
			dialogButton.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v) 
				{
					int index = 0, index_end = 0;
					String bookmarkText = BookmarkName.getText().toString();
					bookmarkText.replaceAll(",", "-");/*if the user insert comma, replace it with "-"*/
					/*		      bookmark name			book					chapter						scroll							fontSize*/
					strBookmark = bookmarkText + "," + book_chapter[0] + "," + book_chapter[1] + "," + webview.getScrollY() + "," + (int) (fontSize)/*(webview.getScale()*100)*/;

					Bookmarks = mPrefs.getString("Bookmarks", "");
					if((index = Bookmarks.indexOf(bookmarkText))!=-1)/*if there is already bookmark with the same name override it*/
					{
						index_end = index;
						for(int i=0; i<5; i++)
						{
							if(Bookmarks.indexOf(",", index_end+1) != -1)
								index_end = Bookmarks.indexOf(",", index_end + 1);
							else/*in case that this is the last bookmark*/
								index_end = Bookmarks.length();							
						}
						Bookmarks = Bookmarks.substring(0, index) + strBookmark + Bookmarks.substring(index_end, Bookmarks.length());
						if(MyLanguage == ENGLISH)
							Toast.makeText(getApplicationContext(),	"Existing bookmark updated", Toast.LENGTH_SHORT).show();
						else if(MyLanguage == RUSSIAN)
							Toast.makeText(getApplicationContext(),	"Текущая закладка обновлена", Toast.LENGTH_SHORT).show();
						else if(MyLanguage == SPANISH)
							Toast.makeText(getApplicationContext(),	"Marcador existente actualizado", Toast.LENGTH_SHORT).show();
						else if(MyLanguage == FRENCH)
							Toast.makeText(getApplicationContext(),	"Le signet existant est mis à jour", Toast.LENGTH_SHORT).show();
						else
							Toast.makeText(getApplicationContext(),	"הסימניה הקיימת עודכנה", Toast.LENGTH_SHORT).show();
					}
					else
					{
						Bookmarks += "," + strBookmark;
						if(MyLanguage == ENGLISH)
							Toast.makeText(getApplicationContext(),	"New bookmark created", Toast.LENGTH_SHORT).show();
						else if(MyLanguage == RUSSIAN)
							Toast.makeText(getApplicationContext(),	"Создана новая закладка", Toast.LENGTH_SHORT).show();
						else if(MyLanguage == SPANISH)
							Toast.makeText(getApplicationContext(),	"Nuevo marcador creado", Toast.LENGTH_SHORT).show();
						else if(MyLanguage == FRENCH)
							Toast.makeText(getApplicationContext(),	"Nouveau signet créé", Toast.LENGTH_SHORT).show();
						else
							Toast.makeText(getApplicationContext(),	"סימניה חדשה נוצרה", Toast.LENGTH_SHORT).show();
					}
					shPrefEditor.putString("Bookmarks", Bookmarks);
					shPrefEditor.commit();
					bookmarkDialog.dismiss();
				}
			});

			fillChaptersNames();
			BookmarkName.setText(chaptersNames[book_chapter[0]][book_chapter[1]]);

			addItemsOnSpinner();

			spinner1.setOnItemSelectedListener(new OnItemSelectedListener() 
			{
				boolean first=true;
				public void onItemSelected(AdapterView<?> parent, View view, int pos,long id) 
				{
					if (first==false)
						BookmarkName.setText(parent.getItemAtPosition(pos).toString());
					first = false;
				}

				public void onNothingSelected(AdapterView<?> arg0) 
				{
					// do nothing   
				}
			});     

			bookmarkDialog.show();			

			break;
		case R.id.action_config:
			showPopupMenuSettings(findViewById(R.id.action_config));
			break;

		case R.id.play:
			scrollSpeed = mPrefs.getInt("scrollSpeed", 2);
			runOnUiThread(mScrollDown);
			break;
			
		case R.id.stop:
			scrollSpeed = -1;
			break;

		case R.id.autoScrollSpeed:
			autoScrollSpeedDialog();	
			break;
		default:
			break;
		}

		return true;
		//return super.onOptionsItemSelected(item);
	}

	public void addItemsOnSpinner() 
	{		 
		List<String> list = new ArrayList<String>();
		int i, index = 0, index_end=0;

		Bookmarks = mPrefs.getString("Bookmarks", "");
		list.add("");/*this is for the first item that need to be hidden in order to have the ability to choose the first item*/

		while((index = Bookmarks.indexOf("," , index)) != -1)
		{
			index++;
			index_end = Bookmarks.indexOf("," , index);
			list.add(Bookmarks.substring(index, index_end));
			for(i=0;i<4;i++)/*skip all other fields*/
				index = Bookmarks.indexOf("," , index) + 1;
		}

		int hidingItemIndex = 0;
		CustomSpinnerAdapter dataAdapter = new CustomSpinnerAdapter(this, android.R.layout.simple_spinner_item, list, hidingItemIndex);

		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		spinner1.setAdapter(dataAdapter);
	}

	private void showPopupMenu(View v)
	{
		PopupMenu popupMenu = new PopupMenu(textMain.this, v);

		//popupMenu.
		for(int i = 0; i < headers.size(); i++)//fill the menu list
		{
			popupMenu.getMenu().add(0,i,i,headers.get(i).text());
		}

		popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() 
		{
			@Override
			public boolean onMenuItemClick(MenuItem item)
			{
				int id = item.getItemId()+1;
				String s=fileName+ "#" + id;
				String s2=fileName+ "#" + (id+1);
				webview.loadUrl(s2);/*Workaround to fix the bug of jumping to same anchor twice*/
				webview.loadUrl(s);
				jumpToSectionFlag = true;
				return true;
			}
		});

		popupMenu.show();
	}

	private void findHeaders()
	{
		String prefix;
		fileName = getClearUrl();
		prefix = "file:///android_asset/";
		fileNameOnly = fileName.substring(prefix.length());
		try 
		{
			InputStream is = getAssets().open(fileNameOnly);
			int size = is.available();
			byte[] buffer = new byte[size];
			is.read(buffer);
			is.close();
			String input = new String(buffer);

			Document doc = Jsoup.parse(input);
			headers = doc.select("h2");				


		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void getTheArrayLocation(String Chapter)
	{
		int perek, seif;
		for (perek = 0; perek < chaptersFiles.length; perek++)
		{
			for (seif = 0; seif < chaptersFiles[perek].length; seif++)
			{
				if(Chapter.equals(chaptersFiles[perek][seif]) == true)
				{
					book_chapter[0] = perek;
					book_chapter[1] = seif;
					return;
				}
			}
		}
	}

	private String getClearUrl()
	{
		String ClearUrl;
		ClearUrl = webview.getUrl();  
		ClearUrl = ClearUrl.substring(0, ClearUrl.indexOf(".html")+5);
		return ClearUrl;		  
	}



	private void fillChaptersFiles()/*list of all assets*/
	{
		/*BRACHOT*/
		chaptersFiles[BRACHOT][0] = "file:///android_asset/brachot_tochen.html";
		chaptersFiles[BRACHOT][1] = "file:///android_asset/brachot_1.html";
		chaptersFiles[BRACHOT][2] = "file:///android_asset/brachot_2.html";
		chaptersFiles[BRACHOT][3] = "file:///android_asset/brachot_3.html";
		chaptersFiles[BRACHOT][4] = "file:///android_asset/brachot_4.html";
		chaptersFiles[BRACHOT][5] = "file:///android_asset/brachot_5.html";
		chaptersFiles[BRACHOT][6] = "file:///android_asset/brachot_6.html";
		chaptersFiles[BRACHOT][7] = "file:///android_asset/brachot_7.html";
		chaptersFiles[BRACHOT][8] = "file:///android_asset/brachot_8.html";
		chaptersFiles[BRACHOT][9] = "file:///android_asset/brachot_9.html";
		chaptersFiles[BRACHOT][10] = "file:///android_asset/brachot_10.html";
		chaptersFiles[BRACHOT][11] = "file:///android_asset/brachot_11.html";
		chaptersFiles[BRACHOT][12] = "file:///android_asset/brachot_12.html";
		chaptersFiles[BRACHOT][13] = "file:///android_asset/brachot_13.html";
		chaptersFiles[BRACHOT][14] = "file:///android_asset/brachot_14.html";
		chaptersFiles[BRACHOT][15] = "file:///android_asset/brachot_15.html";
		chaptersFiles[BRACHOT][16] = "file:///android_asset/brachot_16.html";
		chaptersFiles[BRACHOT][17] = "file:///android_asset/brachot_17.html";
		/*HAAMVEHAAREZ*/
		chaptersFiles[HAAMVEHAAREZ][0] = "file:///android_asset/haamvehaarez_tochen.html";
		chaptersFiles[HAAMVEHAAREZ][1] = "file:///android_asset/haamvehaarez_1.html";
		chaptersFiles[HAAMVEHAAREZ][2] = "file:///android_asset/haamvehaarez_2.html";
		chaptersFiles[HAAMVEHAAREZ][3] = "file:///android_asset/haamvehaarez_3.html";
		chaptersFiles[HAAMVEHAAREZ][4] = "file:///android_asset/haamvehaarez_4.html";
		chaptersFiles[HAAMVEHAAREZ][5] = "file:///android_asset/haamvehaarez_5.html";
		chaptersFiles[HAAMVEHAAREZ][6] = "file:///android_asset/haamvehaarez_6.html";
		chaptersFiles[HAAMVEHAAREZ][7] = "file:///android_asset/haamvehaarez_7.html";
		chaptersFiles[HAAMVEHAAREZ][8] = "file:///android_asset/haamvehaarez_8.html";
		chaptersFiles[HAAMVEHAAREZ][9] = "file:///android_asset/haamvehaarez_9.html";
		chaptersFiles[HAAMVEHAAREZ][10] = "file:///android_asset/haamvehaarez_10.html";
		chaptersFiles[HAAMVEHAAREZ][11] = "file:///android_asset/haamvehaarez_11.html";
		/*ZMANIM*/
		chaptersFiles[ZMANIM][0] = "file:///android_asset/zmanim_tochen.html";
		chaptersFiles[ZMANIM][1] = "file:///android_asset/zmanim_1.html";
		chaptersFiles[ZMANIM][2] = "file:///android_asset/zmanim_2.html";
		chaptersFiles[ZMANIM][3] = "file:///android_asset/zmanim_3.html";
		chaptersFiles[ZMANIM][4] = "file:///android_asset/zmanim_4.html";
		chaptersFiles[ZMANIM][5] = "file:///android_asset/zmanim_5.html";
		chaptersFiles[ZMANIM][6] = "file:///android_asset/zmanim_6.html";
		chaptersFiles[ZMANIM][7] = "file:///android_asset/zmanim_7.html";
		chaptersFiles[ZMANIM][8] = "file:///android_asset/zmanim_8.html";
		chaptersFiles[ZMANIM][9] = "file:///android_asset/zmanim_9.html";
		chaptersFiles[ZMANIM][10] = "file:///android_asset/zmanim_10.html";
		chaptersFiles[ZMANIM][11] = "file:///android_asset/zmanim_11.html";
		chaptersFiles[ZMANIM][12] = "file:///android_asset/zmanim_12.html";
		chaptersFiles[ZMANIM][13] = "file:///android_asset/zmanim_13.html";
		chaptersFiles[ZMANIM][14] = "file:///android_asset/zmanim_14.html";
		chaptersFiles[ZMANIM][15] = "file:///android_asset/zmanim_15.html";
		chaptersFiles[ZMANIM][16] = "file:///android_asset/zmanim_16.html";
		chaptersFiles[ZMANIM][17] = "file:///android_asset/zmanim_17.html";
		/*YAMIM*/
		chaptersFiles[YAMIM][0] = "file:///android_asset/yamim_tochen.html";
		chaptersFiles[YAMIM][1] = "file:///android_asset/yamim_1.html";
		chaptersFiles[YAMIM][2] = "file:///android_asset/yamim_2.html";
		chaptersFiles[YAMIM][3] = "file:///android_asset/yamim_3.html";
		chaptersFiles[YAMIM][4] = "file:///android_asset/yamim_4.html";
		chaptersFiles[YAMIM][5] = "file:///android_asset/yamim_5.html";
		chaptersFiles[YAMIM][6] = "file:///android_asset/yamim_6.html";
		chaptersFiles[YAMIM][7] = "file:///android_asset/yamim_7.html";
		chaptersFiles[YAMIM][8] = "file:///android_asset/yamim_8.html";
		chaptersFiles[YAMIM][9] = "file:///android_asset/yamim_9.html";
		chaptersFiles[YAMIM][10] = "file:///android_asset/yamim_10.html";		
		/*LIKUTIM_A*/
		chaptersFiles[LIKUTIM_A][0] = "file:///android_asset/likutim_a_tochen.html";
		chaptersFiles[LIKUTIM_A][1] = "file:///android_asset/likutim_a_1.html";
		chaptersFiles[LIKUTIM_A][2] = "file:///android_asset/likutim_a_2.html";
		chaptersFiles[LIKUTIM_A][3] = "file:///android_asset/likutim_a_3.html";
		chaptersFiles[LIKUTIM_A][4] = "file:///android_asset/likutim_a_4.html";
		chaptersFiles[LIKUTIM_A][5] = "file:///android_asset/likutim_a_5.html";
		chaptersFiles[LIKUTIM_A][6] = "file:///android_asset/likutim_a_6.html";
		chaptersFiles[LIKUTIM_A][7] = "file:///android_asset/likutim_a_7.html";
		chaptersFiles[LIKUTIM_A][8] = "file:///android_asset/likutim_a_8.html";
		chaptersFiles[LIKUTIM_A][9] = "file:///android_asset/likutim_a_9.html";
		chaptersFiles[LIKUTIM_A][10] = "file:///android_asset/likutim_a_10.html";
		chaptersFiles[LIKUTIM_A][11] = "file:///android_asset/likutim_a_11.html";
		chaptersFiles[LIKUTIM_A][12] = "file:///android_asset/likutim_a_12.html";
		chaptersFiles[LIKUTIM_A][13] = "file:///android_asset/likutim_a_13.html";
		/*LIKUTIM_B*/
		chaptersFiles[LIKUTIM_B][0] = "file:///android_asset/likutim_b_tochen.html";
		chaptersFiles[LIKUTIM_B][1] = "file:///android_asset/likutim_b_1.html";
		chaptersFiles[LIKUTIM_B][2] = "file:///android_asset/likutim_b_2.html";
		chaptersFiles[LIKUTIM_B][3] = "file:///android_asset/likutim_b_3.html";
		chaptersFiles[LIKUTIM_B][4] = "file:///android_asset/likutim_b_4.html";
		chaptersFiles[LIKUTIM_B][5] = "file:///android_asset/likutim_b_5.html";
		chaptersFiles[LIKUTIM_B][6] = "file:///android_asset/likutim_b_6.html";
		chaptersFiles[LIKUTIM_B][7] = "file:///android_asset/likutim_b_7.html";
		chaptersFiles[LIKUTIM_B][8] = "file:///android_asset/likutim_b_8.html";
		chaptersFiles[LIKUTIM_B][9] = "file:///android_asset/likutim_b_9.html";
		chaptersFiles[LIKUTIM_B][10] = "file:///android_asset/likutim_b_10.html";
		chaptersFiles[LIKUTIM_B][11] = "file:///android_asset/likutim_b_11.html";
		chaptersFiles[LIKUTIM_B][12] = "file:///android_asset/likutim_b_12.html";
		chaptersFiles[LIKUTIM_B][13] = "file:///android_asset/likutim_b_13.html";
		chaptersFiles[LIKUTIM_B][14] = "file:///android_asset/likutim_b_14.html";
		chaptersFiles[LIKUTIM_B][15] = "file:///android_asset/likutim_b_15.html";
		chaptersFiles[LIKUTIM_B][16] = "file:///android_asset/likutim_b_16.html";
		/*LIKUTIM_C*/
		chaptersFiles[LIKUTIM_C][0] = "file:///android_asset/likutim_c_tochen.html";
		chaptersFiles[LIKUTIM_C][1] = "file:///android_asset/likutim_c_1.html";
		chaptersFiles[LIKUTIM_C][2] = "file:///android_asset/likutim_c_2.html";
		chaptersFiles[LIKUTIM_C][3] = "file:///android_asset/likutim_c_3.html";
		chaptersFiles[LIKUTIM_C][4] = "file:///android_asset/likutim_c_4.html";
		chaptersFiles[LIKUTIM_C][5] = "file:///android_asset/likutim_c_5.html";
		chaptersFiles[LIKUTIM_C][6] = "file:///android_asset/likutim_c_6.html";
		chaptersFiles[LIKUTIM_C][7] = "file:///android_asset/likutim_c_7.html";
		chaptersFiles[LIKUTIM_C][8] = "file:///android_asset/likutim_c_8.html";
		chaptersFiles[LIKUTIM_C][9] = "file:///android_asset/likutim_c_9.html";
		chaptersFiles[LIKUTIM_C][10] = "file:///android_asset/likutim_c_10.html";
		chaptersFiles[LIKUTIM_C][11] = "file:///android_asset/likutim_c_11.html";
		chaptersFiles[LIKUTIM_C][12] = "file:///android_asset/likutim_c_12.html";
		chaptersFiles[LIKUTIM_C][13] = "file:///android_asset/likutim_c_13.html";
		chaptersFiles[LIKUTIM_C][14] = "file:///android_asset/likutim_c_14.html";
		chaptersFiles[LIKUTIM_C][15] = "file:///android_asset/likutim_c_15.html";
		chaptersFiles[LIKUTIM_C][16] = "file:///android_asset/likutim_c_16.html";
		chaptersFiles[LIKUTIM_C][17] = "file:///android_asset/likutim_c_17.html";
		/*MOADIM*/
		chaptersFiles[MOADIM][0] = "file:///android_asset/moadim_tochen.html";
		chaptersFiles[MOADIM][1] = "file:///android_asset/moadim_1.html";
		chaptersFiles[MOADIM][2] = "file:///android_asset/moadim_2.html";
		chaptersFiles[MOADIM][3] = "file:///android_asset/moadim_3.html";
		chaptersFiles[MOADIM][4] = "file:///android_asset/moadim_4.html";
		chaptersFiles[MOADIM][5] = "file:///android_asset/moadim_5.html";
		chaptersFiles[MOADIM][6] = "file:///android_asset/moadim_6.html";
		chaptersFiles[MOADIM][7] = "file:///android_asset/moadim_7.html";
		chaptersFiles[MOADIM][8] = "file:///android_asset/moadim_8.html";
		chaptersFiles[MOADIM][9] = "file:///android_asset/moadim_9.html";
		chaptersFiles[MOADIM][10] = "file:///android_asset/moadim_10.html";
		chaptersFiles[MOADIM][11] = "file:///android_asset/moadim_11.html";
		chaptersFiles[MOADIM][12] = "file:///android_asset/moadim_12.html";
		chaptersFiles[MOADIM][13] = "file:///android_asset/moadim_13.html";
		/*SUCOT*/
		chaptersFiles[SUCOT][0] = "file:///android_asset/sucot_tochen.html";
		chaptersFiles[SUCOT][1] = "file:///android_asset/sucot_1.html";
		chaptersFiles[SUCOT][2] = "file:///android_asset/sucot_2.html";
		chaptersFiles[SUCOT][3] = "file:///android_asset/sucot_3.html";
		chaptersFiles[SUCOT][4] = "file:///android_asset/sucot_4.html";
		chaptersFiles[SUCOT][5] = "file:///android_asset/sucot_5.html";
		chaptersFiles[SUCOT][6] = "file:///android_asset/sucot_6.html";
		chaptersFiles[SUCOT][7] = "file:///android_asset/sucot_7.html";
		chaptersFiles[SUCOT][8] = "file:///android_asset/sucot_8.html";
		/*PESACH*/
		chaptersFiles[PESACH][0] = "file:///android_asset/pesach_tochen.html";
		chaptersFiles[PESACH][1] = "file:///android_asset/pesach_1.html";
		chaptersFiles[PESACH][2] = "file:///android_asset/pesach_2.html";
		chaptersFiles[PESACH][3] = "file:///android_asset/pesach_3.html";
		chaptersFiles[PESACH][4] = "file:///android_asset/pesach_4.html";
		chaptersFiles[PESACH][5] = "file:///android_asset/pesach_5.html";
		chaptersFiles[PESACH][6] = "file:///android_asset/pesach_6.html";
		chaptersFiles[PESACH][7] = "file:///android_asset/pesach_7.html";
		chaptersFiles[PESACH][8] = "file:///android_asset/pesach_8.html";
		chaptersFiles[PESACH][9] = "file:///android_asset/pesach_9.html";
		chaptersFiles[PESACH][10] = "file:///android_asset/pesach_10.html";
		chaptersFiles[PESACH][11] = "file:///android_asset/pesach_11.html";
		chaptersFiles[PESACH][12] = "file:///android_asset/pesach_12.html";
		chaptersFiles[PESACH][13] = "file:///android_asset/pesach_13.html";
		chaptersFiles[PESACH][14] = "file:///android_asset/pesach_14.html";
		chaptersFiles[PESACH][15] = "file:///android_asset/pesach_15.html";
		chaptersFiles[PESACH][16] = "file:///android_asset/pesach_16.html";
		/*SHVIIT*/
		chaptersFiles[SHVIIT][0] = "file:///android_asset/shviit_tochen.html";
		chaptersFiles[SHVIIT][1] = "file:///android_asset/shviit_1.html";
		chaptersFiles[SHVIIT][2] = "file:///android_asset/shviit_2.html";
		chaptersFiles[SHVIIT][3] = "file:///android_asset/shviit_3.html";
		chaptersFiles[SHVIIT][4] = "file:///android_asset/shviit_4.html";
		chaptersFiles[SHVIIT][5] = "file:///android_asset/shviit_5.html";
		chaptersFiles[SHVIIT][6] = "file:///android_asset/shviit_6.html";
		chaptersFiles[SHVIIT][7] = "file:///android_asset/shviit_7.html";
		chaptersFiles[SHVIIT][8] = "file:///android_asset/shviit_8.html";
		chaptersFiles[SHVIIT][9] = "file:///android_asset/shviit_9.html";
		chaptersFiles[SHVIIT][10] = "file:///android_asset/shviit_10.html";
		chaptersFiles[SHVIIT][11] = "file:///android_asset/shviit_11.html";
		/*SHABAT*/
		chaptersFiles[SHABAT][0] = "file:///android_asset/shabat_tochen.html";
		chaptersFiles[SHABAT][1] = "file:///android_asset/shabat_1.html";
		chaptersFiles[SHABAT][2] = "file:///android_asset/shabat_2.html";
		chaptersFiles[SHABAT][3] = "file:///android_asset/shabat_3.html";
		chaptersFiles[SHABAT][4] = "file:///android_asset/shabat_4.html";
		chaptersFiles[SHABAT][5] = "file:///android_asset/shabat_5.html";
		chaptersFiles[SHABAT][6] = "file:///android_asset/shabat_6.html";
		chaptersFiles[SHABAT][7] = "file:///android_asset/shabat_7.html";
		chaptersFiles[SHABAT][8] = "file:///android_asset/shabat_8.html";
		chaptersFiles[SHABAT][9] = "file:///android_asset/shabat_9.html";
		chaptersFiles[SHABAT][10] = "file:///android_asset/shabat_10.html";
		chaptersFiles[SHABAT][11] = "file:///android_asset/shabat_11.html";
		chaptersFiles[SHABAT][12] = "file:///android_asset/shabat_12.html";
		chaptersFiles[SHABAT][13] = "file:///android_asset/shabat_13.html";
		chaptersFiles[SHABAT][14] = "file:///android_asset/shabat_14.html";
		chaptersFiles[SHABAT][15] = "file:///android_asset/shabat_15.html";
		chaptersFiles[SHABAT][16] = "file:///android_asset/shabat_16.html";
		chaptersFiles[SHABAT][17] = "file:///android_asset/shabat_17.html";
		chaptersFiles[SHABAT][18] = "file:///android_asset/shabat_18.html";
		chaptersFiles[SHABAT][19] = "file:///android_asset/shabat_19.html";
		chaptersFiles[SHABAT][20] = "file:///android_asset/shabat_20.html";
		chaptersFiles[SHABAT][21] = "file:///android_asset/shabat_21.html";
		chaptersFiles[SHABAT][22] = "file:///android_asset/shabat_22.html";
		chaptersFiles[SHABAT][23] = "file:///android_asset/shabat_23.html";
		chaptersFiles[SHABAT][24] = "file:///android_asset/shabat_24.html";
		chaptersFiles[SHABAT][25] = "file:///android_asset/shabat_25.html";
		chaptersFiles[SHABAT][26] = "file:///android_asset/shabat_26.html";
		chaptersFiles[SHABAT][27] = "file:///android_asset/shabat_27.html";
		chaptersFiles[SHABAT][28] = "file:///android_asset/shabat_28.html";
		chaptersFiles[SHABAT][29] = "file:///android_asset/shabat_29.html";
		chaptersFiles[SHABAT][30] = "file:///android_asset/shabat_30.html";
		/*SIMCHAT*/
		chaptersFiles[SIMCHAT][0] = "file:///android_asset/simchat_tochen.html";
		chaptersFiles[SIMCHAT][1] = "file:///android_asset/simchat_1.html";
		chaptersFiles[SIMCHAT][2] = "file:///android_asset/simchat_2.html";
		chaptersFiles[SIMCHAT][3] = "file:///android_asset/simchat_3.html";
		chaptersFiles[SIMCHAT][4] = "file:///android_asset/simchat_4.html";
		chaptersFiles[SIMCHAT][5] = "file:///android_asset/simchat_5.html";
		chaptersFiles[SIMCHAT][6] = "file:///android_asset/simchat_6.html";
		chaptersFiles[SIMCHAT][7] = "file:///android_asset/simchat_7.html";
		chaptersFiles[SIMCHAT][8] = "file:///android_asset/simchat_8.html";
		chaptersFiles[SIMCHAT][9] = "file:///android_asset/simchat_9.html";
		chaptersFiles[SIMCHAT][10] = "file:///android_asset/simchat_10.html";

		/*TEFILA*/
		chaptersFiles[TEFILA][0] = "file:///android_asset/tefila_tochen.html";
		chaptersFiles[TEFILA][1] = "file:///android_asset/tefila_1.html";
		chaptersFiles[TEFILA][2] = "file:///android_asset/tefila_2.html";
		chaptersFiles[TEFILA][3] = "file:///android_asset/tefila_3.html";
		chaptersFiles[TEFILA][4] = "file:///android_asset/tefila_4.html";
		chaptersFiles[TEFILA][5] = "file:///android_asset/tefila_5.html";
		chaptersFiles[TEFILA][6] = "file:///android_asset/tefila_6.html";
		chaptersFiles[TEFILA][7] = "file:///android_asset/tefila_7.html";
		chaptersFiles[TEFILA][8] = "file:///android_asset/tefila_8.html";
		chaptersFiles[TEFILA][9] = "file:///android_asset/tefila_9.html";
		chaptersFiles[TEFILA][10] = "file:///android_asset/tefila_10.html";
		chaptersFiles[TEFILA][11] = "file:///android_asset/tefila_11.html";
		chaptersFiles[TEFILA][12] = "file:///android_asset/tefila_12.html";
		chaptersFiles[TEFILA][13] = "file:///android_asset/tefila_13.html";
		chaptersFiles[TEFILA][14] = "file:///android_asset/tefila_14.html";
		chaptersFiles[TEFILA][15] = "file:///android_asset/tefila_15.html";
		chaptersFiles[TEFILA][16] = "file:///android_asset/tefila_16.html";
		chaptersFiles[TEFILA][17] = "file:///android_asset/tefila_17.html";
		chaptersFiles[TEFILA][18] = "file:///android_asset/tefila_18.html";
		chaptersFiles[TEFILA][19] = "file:///android_asset/tefila_19.html";
		chaptersFiles[TEFILA][20] = "file:///android_asset/tefila_20.html";
		chaptersFiles[TEFILA][21] = "file:///android_asset/tefila_21.html";
		chaptersFiles[TEFILA][22] = "file:///android_asset/tefila_22.html";
		chaptersFiles[TEFILA][23] = "file:///android_asset/tefila_23.html";
		chaptersFiles[TEFILA][24] = "file:///android_asset/tefila_24.html";
		chaptersFiles[TEFILA][25] = "file:///android_asset/tefila_25.html";
		chaptersFiles[TEFILA][26] = "file:///android_asset/tefila_26.html";
		/*TEFILAT_NASHIM*/
		chaptersFiles[TEFILAT_NASHIM][0] = "file:///android_asset/tefilat_nashim_tochen.html";
		chaptersFiles[TEFILAT_NASHIM][1] = "file:///android_asset/tefilat_nashim_1.html";
		chaptersFiles[TEFILAT_NASHIM][2] = "file:///android_asset/tefilat_nashim_2.html";
		chaptersFiles[TEFILAT_NASHIM][3] = "file:///android_asset/tefilat_nashim_3.html";
		chaptersFiles[TEFILAT_NASHIM][4] = "file:///android_asset/tefilat_nashim_4.html";
		chaptersFiles[TEFILAT_NASHIM][5] = "file:///android_asset/tefilat_nashim_5.html";
		chaptersFiles[TEFILAT_NASHIM][6] = "file:///android_asset/tefilat_nashim_6.html";
		chaptersFiles[TEFILAT_NASHIM][7] = "file:///android_asset/tefilat_nashim_7.html";
		chaptersFiles[TEFILAT_NASHIM][8] = "file:///android_asset/tefilat_nashim_8.html";
		chaptersFiles[TEFILAT_NASHIM][9] = "file:///android_asset/tefilat_nashim_9.html";
		chaptersFiles[TEFILAT_NASHIM][10] = "file:///android_asset/tefilat_nashim_10.html";
		chaptersFiles[TEFILAT_NASHIM][11] = "file:///android_asset/tefilat_nashim_11.html";
		chaptersFiles[TEFILAT_NASHIM][12] = "file:///android_asset/tefilat_nashim_12.html";
		chaptersFiles[TEFILAT_NASHIM][13] = "file:///android_asset/tefilat_nashim_13.html";
		chaptersFiles[TEFILAT_NASHIM][14] = "file:///android_asset/tefilat_nashim_14.html";
		chaptersFiles[TEFILAT_NASHIM][15] = "file:///android_asset/tefilat_nashim_15.html";
		chaptersFiles[TEFILAT_NASHIM][16] = "file:///android_asset/tefilat_nashim_16.html";
		chaptersFiles[TEFILAT_NASHIM][17] = "file:///android_asset/tefilat_nashim_17.html";
		chaptersFiles[TEFILAT_NASHIM][18] = "file:///android_asset/tefilat_nashim_18.html";
		chaptersFiles[TEFILAT_NASHIM][19] = "file:///android_asset/tefilat_nashim_19.html";
		chaptersFiles[TEFILAT_NASHIM][20] = "file:///android_asset/tefilat_nashim_20.html";
		chaptersFiles[TEFILAT_NASHIM][21] = "file:///android_asset/tefilat_nashim_21.html";
		chaptersFiles[TEFILAT_NASHIM][22] = "file:///android_asset/tefilat_nashim_22.html";
		chaptersFiles[TEFILAT_NASHIM][23] = "file:///android_asset/tefilat_nashim_23.html";
		chaptersFiles[TEFILAT_NASHIM][24] = "file:///android_asset/tefilat_nashim_24.html";
		/*HAR_BRACHOT*/
		chaptersFiles[HAR_BRACHOT][0] = "file:///android_asset/har_brachot_tochen.html";
		chaptersFiles[HAR_BRACHOT][1] = "file:///android_asset/har_brachot_1.html";
		chaptersFiles[HAR_BRACHOT][2] = "file:///android_asset/har_brachot_2.html";
		chaptersFiles[HAR_BRACHOT][3] = "file:///android_asset/har_brachot_3.html";
		chaptersFiles[HAR_BRACHOT][4] = "file:///android_asset/har_brachot_4.html";
		chaptersFiles[HAR_BRACHOT][5] = "file:///android_asset/har_brachot_5.html";
		chaptersFiles[HAR_BRACHOT][6] = "file:///android_asset/har_brachot_6.html";
		chaptersFiles[HAR_BRACHOT][7] = "file:///android_asset/har_brachot_7.html";
		chaptersFiles[HAR_BRACHOT][8] = "file:///android_asset/har_brachot_8.html";
		chaptersFiles[HAR_BRACHOT][9] = "file:///android_asset/har_brachot_9.html";
		chaptersFiles[HAR_BRACHOT][10] = "file:///android_asset/har_brachot_10.html";
		chaptersFiles[HAR_BRACHOT][11] = "file:///android_asset/har_brachot_11.html";
		chaptersFiles[HAR_BRACHOT][12] = "file:///android_asset/har_brachot_12.html";
		chaptersFiles[HAR_BRACHOT][13] = "file:///android_asset/har_brachot_13.html";
		chaptersFiles[HAR_BRACHOT][14] = "file:///android_asset/har_brachot_14.html";
		chaptersFiles[HAR_BRACHOT][15] = "file:///android_asset/har_brachot_15.html";
		chaptersFiles[HAR_BRACHOT][16] = "file:///android_asset/har_brachot_16.html";
		chaptersFiles[HAR_BRACHOT][17] = "file:///android_asset/har_brachot_17.html";
		/*HAR_YAMIM*/
		chaptersFiles[HAR_YAMIM][0] = "file:///android_asset/har_yamim_tochen.html";
		chaptersFiles[HAR_YAMIM][1] = "file:///android_asset/har_yamim_1.html";
		chaptersFiles[HAR_YAMIM][2] = "file:///android_asset/har_yamim_2.html";
		chaptersFiles[HAR_YAMIM][3] = "file:///android_asset/har_yamim_3.html";
		chaptersFiles[HAR_YAMIM][4] = "file:///android_asset/har_yamim_4.html";
		chaptersFiles[HAR_YAMIM][5] = "file:///android_asset/har_yamim_5.html";
		chaptersFiles[HAR_YAMIM][6] = "file:///android_asset/har_yamim_6.html";
		chaptersFiles[HAR_YAMIM][7] = "file:///android_asset/har_yamim_7.html";
		chaptersFiles[HAR_YAMIM][8] = "file:///android_asset/har_yamim_8.html";
		chaptersFiles[HAR_YAMIM][9] = "file:///android_asset/har_yamim_9.html";
		chaptersFiles[HAR_YAMIM][10] = "file:///android_asset/har_yamim_10.html";
		/*HAR_MOADIM*/
		chaptersFiles[HAR_MOADIM][0] = "file:///android_asset/har_moadim_tochen.html";
		chaptersFiles[HAR_MOADIM][1] = "file:///android_asset/har_moadim_1.html";
		chaptersFiles[HAR_MOADIM][2] = "file:///android_asset/har_moadim_2.html";
		chaptersFiles[HAR_MOADIM][3] = "file:///android_asset/har_moadim_3.html";
		chaptersFiles[HAR_MOADIM][4] = "file:///android_asset/har_moadim_4.html";
		chaptersFiles[HAR_MOADIM][5] = "file:///android_asset/har_moadim_5.html";
		chaptersFiles[HAR_MOADIM][6] = "file:///android_asset/har_moadim_6.html";
		chaptersFiles[HAR_MOADIM][7] = "file:///android_asset/har_moadim_7.html";
		chaptersFiles[HAR_MOADIM][8] = "file:///android_asset/har_moadim_8.html";
		//chaptersFiles[HAR_MOADIM][9] = "file:///android_asset/har_moadim_9.html"; //currently there is no chapter 9
		chaptersFiles[HAR_MOADIM][9] = "file:///android_asset/har_moadim_10.html";
		chaptersFiles[HAR_MOADIM][10] = "file:///android_asset/har_moadim_11.html";
		chaptersFiles[HAR_MOADIM][11] = "file:///android_asset/har_moadim_12.html";
		chaptersFiles[HAR_MOADIM][12] = "file:///android_asset/har_moadim_13.html";
		/*HAR_SUCOT*/
		chaptersFiles[HAR_SUCOT][0] = "file:///android_asset/sucot_tochen.html";
		chaptersFiles[HAR_SUCOT][1] = "file:///android_asset/har_sucot_1.html";
		chaptersFiles[HAR_SUCOT][2] = "file:///android_asset/har_sucot_2.html";
		chaptersFiles[HAR_SUCOT][3] = "file:///android_asset/har_sucot_3.html";
		chaptersFiles[HAR_SUCOT][4] = "file:///android_asset/har_sucot_4.html";
		chaptersFiles[HAR_SUCOT][5] = "file:///android_asset/har_sucot_5.html";
		chaptersFiles[HAR_SUCOT][6] = "file:///android_asset/har_sucot_6.html";
		chaptersFiles[HAR_SUCOT][7] = "file:///android_asset/har_sucot_7.html";
		chaptersFiles[HAR_SUCOT][8] = "file:///android_asset/har_sucot_8.html";
		/*HAR_SHABAT*/
		chaptersFiles[HAR_SHABAT][0] = "file:///android_asset/har_shabat_tochen.html";
		chaptersFiles[HAR_SHABAT][1] = "file:///android_asset/har_shabat_1.html";
		chaptersFiles[HAR_SHABAT][2] = "file:///android_asset/har_shabat_2.html";
		chaptersFiles[HAR_SHABAT][3] = "file:///android_asset/har_shabat_3.html";
		chaptersFiles[HAR_SHABAT][4] = "file:///android_asset/har_shabat_4.html";
		chaptersFiles[HAR_SHABAT][5] = "file:///android_asset/har_shabat_5.html";
		chaptersFiles[HAR_SHABAT][6] = "file:///android_asset/har_shabat_6.html";
		chaptersFiles[HAR_SHABAT][7] = "file:///android_asset/har_shabat_7.html";
		chaptersFiles[HAR_SHABAT][8] = "file:///android_asset/har_shabat_8.html";
		chaptersFiles[HAR_SHABAT][9] = "file:///android_asset/har_shabat_9.html";
		chaptersFiles[HAR_SHABAT][10] = "file:///android_asset/har_shabat_10.html";
		chaptersFiles[HAR_SHABAT][11] = "file:///android_asset/har_shabat_11.html";
		chaptersFiles[HAR_SHABAT][12] = "file:///android_asset/har_shabat_12.html";
		chaptersFiles[HAR_SHABAT][13] = "file:///android_asset/har_shabat_13.html";
		chaptersFiles[HAR_SHABAT][14] = "file:///android_asset/har_shabat_14.html";
		chaptersFiles[HAR_SHABAT][15] = "file:///android_asset/har_shabat_15.html";
		chaptersFiles[HAR_SHABAT][16] = "file:///android_asset/har_shabat_16.html";
		chaptersFiles[HAR_SHABAT][17] = "file:///android_asset/har_shabat_17.html";
		chaptersFiles[HAR_SHABAT][18] = "file:///android_asset/har_shabat_18.html";
		chaptersFiles[HAR_SHABAT][19] = "file:///android_asset/har_shabat_19.html";
		chaptersFiles[HAR_SHABAT][20] = "file:///android_asset/har_shabat_20.html";
		chaptersFiles[HAR_SHABAT][21] = "file:///android_asset/har_shabat_21.html";
		chaptersFiles[HAR_SHABAT][22] = "file:///android_asset/har_shabat_22.html";
		chaptersFiles[HAR_SHABAT][23] = "file:///android_asset/har_shabat_23.html";
		chaptersFiles[HAR_SHABAT][24] = "file:///android_asset/har_shabat_24.html";
		chaptersFiles[HAR_SHABAT][25] = "file:///android_asset/har_shabat_25.html";
		chaptersFiles[HAR_SHABAT][26] = "file:///android_asset/har_shabat_26.html";
		chaptersFiles[HAR_SHABAT][27] = "file:///android_asset/har_shabat_27.html";
		chaptersFiles[HAR_SHABAT][28] = "file:///android_asset/har_shabat_28.html";
		chaptersFiles[HAR_SHABAT][29] = "file:///android_asset/har_shabat_29.html";
		chaptersFiles[HAR_SHABAT][30] = "file:///android_asset/har_shabat_30.html";
		/*HAR_SIMCHAT*/
		chaptersFiles[HAR_SIMCHAT][0] = "file:///android_asset/har_simchat_tochen.html";
		chaptersFiles[HAR_SIMCHAT][1] = "file:///android_asset/har_simchat_1.html";
		chaptersFiles[HAR_SIMCHAT][2] = "file:///android_asset/har_simchat_2.html";
		chaptersFiles[HAR_SIMCHAT][3] = "file:///android_asset/har_simchat_3.html";
		chaptersFiles[HAR_SIMCHAT][4] = "file:///android_asset/har_simchat_4.html";
		chaptersFiles[HAR_SIMCHAT][5] = "file:///android_asset/har_simchat_5.html";
		chaptersFiles[HAR_SIMCHAT][6] = "file:///android_asset/har_simchat_6.html";
		chaptersFiles[HAR_SIMCHAT][7] = "file:///android_asset/har_simchat_7.html";
		chaptersFiles[HAR_SIMCHAT][8] = "file:///android_asset/har_simchat_8.html";
		chaptersFiles[HAR_SIMCHAT][9] = "file:///android_asset/har_simchat_9.html";
		chaptersFiles[HAR_SIMCHAT][10] = "file:///android_asset/har_simchat_10.html";
		/*E_TEFILA*/
		chaptersFiles[E_TEFILA][0] = "file:///android_asset/E_tefila_contents.html";
		chaptersFiles[E_TEFILA][1] = "file:///android_asset/E_tefila_1.html";
		chaptersFiles[E_TEFILA][2] = "file:///android_asset/E_tefila_2.html";
		chaptersFiles[E_TEFILA][3] = "file:///android_asset/E_tefila_3.html";
		chaptersFiles[E_TEFILA][4] = "file:///android_asset/E_tefila_4.html";
		chaptersFiles[E_TEFILA][5] = "file:///android_asset/E_tefila_5.html";
		chaptersFiles[E_TEFILA][6] = "file:///android_asset/E_tefila_6.html";
		chaptersFiles[E_TEFILA][7] = "file:///android_asset/E_tefila_7.html";
		chaptersFiles[E_TEFILA][8] = "file:///android_asset/E_tefila_8.html";
		chaptersFiles[E_TEFILA][9] = "file:///android_asset/E_tefila_9.html";
		chaptersFiles[E_TEFILA][10] = "file:///android_asset/E_tefila_10.html";
		chaptersFiles[E_TEFILA][11] = "file:///android_asset/E_tefila_11.html";
		chaptersFiles[E_TEFILA][12] = "file:///android_asset/E_tefila_12.html";
		chaptersFiles[E_TEFILA][13] = "file:///android_asset/E_tefila_13.html";
		chaptersFiles[E_TEFILA][14] = "file:///android_asset/E_tefila_14.html";
		chaptersFiles[E_TEFILA][15] = "file:///android_asset/E_tefila_15.html";
		chaptersFiles[E_TEFILA][16] = "file:///android_asset/E_tefila_16.html";
		chaptersFiles[E_TEFILA][17] = "file:///android_asset/E_tefila_17.html";
		chaptersFiles[E_TEFILA][18] = "file:///android_asset/E_tefila_18.html";
		chaptersFiles[E_TEFILA][19] = "file:///android_asset/E_tefila_19.html";
		chaptersFiles[E_TEFILA][20] = "file:///android_asset/E_tefila_20.html";
		chaptersFiles[E_TEFILA][21] = "file:///android_asset/E_tefila_21.html";
		chaptersFiles[E_TEFILA][22] = "file:///android_asset/E_tefila_22.html";
		chaptersFiles[E_TEFILA][23] = "file:///android_asset/E_tefila_23.html";
		chaptersFiles[E_TEFILA][24] = "file:///android_asset/E_tefila_24.html";
		chaptersFiles[E_TEFILA][25] = "file:///android_asset/E_tefila_25.html";
		chaptersFiles[E_TEFILA][26] = "file:///android_asset/E_tefila_26.html";
		/*E_PESACH*/		
		chaptersFiles[E_PESACH][0] = "file:///android_asset/E_pesach_contents.html";
		chaptersFiles[E_PESACH][1] = "file:///android_asset/E_pesach_1.html";
		chaptersFiles[E_PESACH][2] = "file:///android_asset/E_pesach_2.html";
		chaptersFiles[E_PESACH][3] = "file:///android_asset/E_pesach_3.html";
		chaptersFiles[E_PESACH][4] = "file:///android_asset/E_pesach_4.html";
		chaptersFiles[E_PESACH][5] = "file:///android_asset/E_pesach_5.html";
		chaptersFiles[E_PESACH][6] = "file:///android_asset/E_pesach_6.html";
		chaptersFiles[E_PESACH][7] = "file:///android_asset/E_pesach_7.html";
		chaptersFiles[E_PESACH][8] = "file:///android_asset/E_pesach_8.html";
		chaptersFiles[E_PESACH][9] = "file:///android_asset/E_pesach_9.html";
		chaptersFiles[E_PESACH][10] = "file:///android_asset/E_pesach_10.html";
		chaptersFiles[E_PESACH][11] = "file:///android_asset/E_pesach_11.html";
		chaptersFiles[E_PESACH][12] = "file:///android_asset/E_pesach_12.html";
		chaptersFiles[E_PESACH][13] = "file:///android_asset/E_pesach_13.html";
		chaptersFiles[E_PESACH][14] = "file:///android_asset/E_pesach_14.html";
		chaptersFiles[E_PESACH][15] = "file:///android_asset/E_pesach_15.html";
		chaptersFiles[E_PESACH][16] = "file:///android_asset/E_pesach_16.html";
		/*E_ZMANIM*/
		chaptersFiles[E_ZMANIM][0] = "file:///android_asset/E_zmanim_contents.html";
		chaptersFiles[E_ZMANIM][1] = "file:///android_asset/E_zmanim_1.html";
		chaptersFiles[E_ZMANIM][2] = "file:///android_asset/E_zmanim_2.html";
		chaptersFiles[E_ZMANIM][3] = "file:///android_asset/E_zmanim_3.html";
		chaptersFiles[E_ZMANIM][4] = "file:///android_asset/E_zmanim_4.html";
		chaptersFiles[E_ZMANIM][5] = "file:///android_asset/E_zmanim_5.html";
		chaptersFiles[E_ZMANIM][6] = "file:///android_asset/E_zmanim_6.html";
		chaptersFiles[E_ZMANIM][7] = "file:///android_asset/E_zmanim_7.html";
		chaptersFiles[E_ZMANIM][8] = "file:///android_asset/E_zmanim_8.html";
		chaptersFiles[E_ZMANIM][9] = "file:///android_asset/E_zmanim_9.html";
		chaptersFiles[E_ZMANIM][10] = "file:///android_asset/E_zmanim_10.html";
		chaptersFiles[E_ZMANIM][11] = "file:///android_asset/E_zmanim_11.html";
		chaptersFiles[E_ZMANIM][12] = "file:///android_asset/E_zmanim_12.html";
		chaptersFiles[E_ZMANIM][13] = "file:///android_asset/E_zmanim_13.html";
		chaptersFiles[E_ZMANIM][14] = "file:///android_asset/E_zmanim_14.html";
		chaptersFiles[E_ZMANIM][15] = "file:///android_asset/E_zmanim_15.html";
		/*E_WOMEN_PRAYER*/
		chaptersFiles[E_WOMEN_PRAYER][0] = "file:///android_asset/e_w_prayer_contents.html";
		chaptersFiles[E_WOMEN_PRAYER][1] = "file:///android_asset/e_w_prayer_1.html";
		chaptersFiles[E_WOMEN_PRAYER][2] = "file:///android_asset/e_w_prayer_2.html";
		chaptersFiles[E_WOMEN_PRAYER][3] = "file:///android_asset/e_w_prayer_3.html";
		chaptersFiles[E_WOMEN_PRAYER][4] = "file:///android_asset/e_w_prayer_4.html";
		chaptersFiles[E_WOMEN_PRAYER][5] = "file:///android_asset/e_w_prayer_5.html";
		chaptersFiles[E_WOMEN_PRAYER][6] = "file:///android_asset/e_w_prayer_6.html";
		chaptersFiles[E_WOMEN_PRAYER][7] = "file:///android_asset/e_w_prayer_7.html";
		chaptersFiles[E_WOMEN_PRAYER][8] = "file:///android_asset/e_w_prayer_8.html";
		chaptersFiles[E_WOMEN_PRAYER][9] = "file:///android_asset/e_w_prayer_9.html";
		chaptersFiles[E_WOMEN_PRAYER][10] = "file:///android_asset/e_w_prayer_10.html";
		chaptersFiles[E_WOMEN_PRAYER][11] = "file:///android_asset/e_w_prayer_11.html";
		chaptersFiles[E_WOMEN_PRAYER][12] = "file:///android_asset/e_w_prayer_12.html";
		chaptersFiles[E_WOMEN_PRAYER][13] = "file:///android_asset/e_w_prayer_13.html";
		chaptersFiles[E_WOMEN_PRAYER][14] = "file:///android_asset/e_w_prayer_14.html";
		chaptersFiles[E_WOMEN_PRAYER][15] = "file:///android_asset/e_w_prayer_15.html";
		chaptersFiles[E_WOMEN_PRAYER][16] = "file:///android_asset/e_w_prayer_16.html";
		chaptersFiles[E_WOMEN_PRAYER][17] = "file:///android_asset/e_w_prayer_17.html";
		chaptersFiles[E_WOMEN_PRAYER][18] = "file:///android_asset/e_w_prayer_18.html";
		chaptersFiles[E_WOMEN_PRAYER][19] = "file:///android_asset/e_w_prayer_19.html";
		chaptersFiles[E_WOMEN_PRAYER][20] = "file:///android_asset/e_w_prayer_20.html";
		chaptersFiles[E_WOMEN_PRAYER][21] = "file:///android_asset/e_w_prayer_21.html";
		chaptersFiles[E_WOMEN_PRAYER][22] = "file:///android_asset/e_w_prayer_22.html";
		chaptersFiles[E_WOMEN_PRAYER][23] = "file:///android_asset/e_w_prayer_23.html";
		chaptersFiles[E_WOMEN_PRAYER][24] = "file:///android_asset/e_w_prayer_24.html";
		/*E_SHABAT*/
		chaptersFiles[E_SHABAT][0] = "file:///android_asset/e_shabbat_contents.html";
		chaptersFiles[E_SHABAT][1] = "file:///android_asset/e_shabbat_1.html";
		chaptersFiles[E_SHABAT][2] = "file:///android_asset/e_shabbat_2.html";
		chaptersFiles[E_SHABAT][3] = "file:///android_asset/e_shabbat_3.html";
		chaptersFiles[E_SHABAT][4] = "file:///android_asset/e_shabbat_4.html";
		chaptersFiles[E_SHABAT][5] = "file:///android_asset/e_shabbat_5.html";
		chaptersFiles[E_SHABAT][6] = "file:///android_asset/e_shabbat_6.html";
		chaptersFiles[E_SHABAT][7] = "file:///android_asset/e_shabbat_7.html";
		chaptersFiles[E_SHABAT][8] = "file:///android_asset/e_shabbat_8.html";
		chaptersFiles[E_SHABAT][9] = "file:///android_asset/e_shabbat_9.html";
		chaptersFiles[E_SHABAT][10] = "file:///android_asset/e_shabbat_10.html";
		chaptersFiles[E_SHABAT][11] = "file:///android_asset/e_shabbat_11.html";
		chaptersFiles[E_SHABAT][12] = "file:///android_asset/e_shabbat_12.html";
		chaptersFiles[E_SHABAT][13] = "file:///android_asset/e_shabbat_13.html";
		chaptersFiles[E_SHABAT][14] = "file:///android_asset/e_shabbat_14.html";
		chaptersFiles[E_SHABAT][15] = "file:///android_asset/e_shabbat_15.html";
		chaptersFiles[E_SHABAT][16] = "file:///android_asset/e_shabbat_16.html";
		chaptersFiles[E_SHABAT][17] = "file:///android_asset/e_shabbat_17.html";
		chaptersFiles[E_SHABAT][18] = "file:///android_asset/e_shabbat_18.html";
		chaptersFiles[E_SHABAT][19] = "file:///android_asset/e_shabbat_19.html";
		chaptersFiles[E_SHABAT][20] = "file:///android_asset/e_shabbat_20.html";
		chaptersFiles[E_SHABAT][21] = "file:///android_asset/e_shabbat_21.html";
		chaptersFiles[E_SHABAT][22] = "file:///android_asset/e_shabbat_22.html";
		chaptersFiles[E_SHABAT][23] = "file:///android_asset/e_shabbat_23.html";
		chaptersFiles[E_SHABAT][24] = "file:///android_asset/e_shabbat_24.html";
		chaptersFiles[E_SHABAT][25] = "file:///android_asset/e_shabbat_25.html";
		chaptersFiles[E_SHABAT][26] = "file:///android_asset/e_shabbat_26.html";
		chaptersFiles[E_SHABAT][27] = "file:///android_asset/e_shabbat_27.html";
		chaptersFiles[E_SHABAT][28] = "file:///android_asset/e_shabbat_28.html";
		chaptersFiles[E_SHABAT][29] = "file:///android_asset/e_shabbat_29.html";
		chaptersFiles[E_SHABAT][30] = "file:///android_asset/e_shabbat_30.html";
		/*F_TEFILA*/
		chaptersFiles[F_TEFILA][0] = "file:///android_asset/F_tefila_contents.html";
		chaptersFiles[F_TEFILA][1] = "file:///android_asset/F_tefila_1.html";
		chaptersFiles[F_TEFILA][2] = "file:///android_asset/F_tefila_2.html";
		chaptersFiles[F_TEFILA][3] = "file:///android_asset/F_tefila_3.html";
		chaptersFiles[F_TEFILA][4] = "file:///android_asset/F_tefila_4.html";
		chaptersFiles[F_TEFILA][5] = "file:///android_asset/F_tefila_5.html";
		chaptersFiles[F_TEFILA][6] = "file:///android_asset/F_tefila_6.html";
		chaptersFiles[F_TEFILA][7] = "file:///android_asset/F_tefila_7.html";
		chaptersFiles[F_TEFILA][8] = "file:///android_asset/F_tefila_8.html";
		chaptersFiles[F_TEFILA][9] = "file:///android_asset/F_tefila_9.html";
		chaptersFiles[F_TEFILA][10] = "file:///android_asset/F_tefila_10.html";
		chaptersFiles[F_TEFILA][11] = "file:///android_asset/F_tefila_11.html";
		chaptersFiles[F_TEFILA][12] = "file:///android_asset/F_tefila_12.html";
		chaptersFiles[F_TEFILA][13] = "file:///android_asset/F_tefila_13.html";
		chaptersFiles[F_TEFILA][14] = "file:///android_asset/F_tefila_14.html";
		chaptersFiles[F_TEFILA][15] = "file:///android_asset/F_tefila_15.html";
		chaptersFiles[F_TEFILA][16] = "file:///android_asset/F_tefila_16.html";
		chaptersFiles[F_TEFILA][17] = "file:///android_asset/F_tefila_17.html";
		chaptersFiles[F_TEFILA][18] = "file:///android_asset/F_tefila_18.html";
		chaptersFiles[F_TEFILA][19] = "file:///android_asset/F_tefila_19.html";
		chaptersFiles[F_TEFILA][20] = "file:///android_asset/F_tefila_20.html";
		chaptersFiles[F_TEFILA][21] = "file:///android_asset/F_tefila_21.html";
		chaptersFiles[F_TEFILA][22] = "file:///android_asset/F_tefila_22.html";
		chaptersFiles[F_TEFILA][23] = "file:///android_asset/F_tefila_23.html";
		chaptersFiles[F_TEFILA][24] = "file:///android_asset/F_tefila_24.html";
		chaptersFiles[F_TEFILA][25] = "file:///android_asset/F_tefila_25.html";
		chaptersFiles[F_TEFILA][26] = "file:///android_asset/F_tefila_26.html";

	}


	public boolean onDown(MotionEvent e) {
		return false;
	}

	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		return false;
	}

	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
		return false;
	}

	public boolean onSingleTapUp(MotionEvent e) {
		return false;
	}

	private void showPopupMenuSettings(View v)
	{
		PopupMenu popupMenu = new PopupMenu(textMain.this, v);

        String configHeaders[] = new String[7];
        if(MyLanguage == ENGLISH) {
            configHeaders[0] = "Settings";
            configHeaders[1] = "About";
            configHeaders[2] = "Feedback";
            configHeaders[3] = "Explanation of search results";
            configHeaders[4] = "Acronyms";
            configHeaders[5] = "Zoom in";
            configHeaders[6] = "Zoom out";
        }
        else if(MyLanguage == RUSSIAN) {
            configHeaders[0] = "Настройки";
            configHeaders[1] = "Около";
            configHeaders[2] = "Обратная связь";
            configHeaders[3] = "Объяснение результатов поиска";
            configHeaders[4] = "Абревиатуры";
            configHeaders[5] = "Увеличить шрифт";
            configHeaders[6] = "Уменьшить шрифт";
        }
        else if(MyLanguage == SPANISH) {
            configHeaders[0] = "Ajustes";
            configHeaders[1] = "Acerca de";
            configHeaders[2] = "Comentarios";
            configHeaders[3] = "Explicacion del resultado de la busqueda";
            configHeaders[4] = "Acronimos";
            configHeaders[5] = "Aumentar enfoque";
            configHeaders[6] = "Disminuir enfoque";
        }
        else if(MyLanguage == FRENCH) {
            configHeaders[0] = "Definitions";
            configHeaders[1] = "A Propos de…";
            configHeaders[2] = "Commentaires";
            configHeaders[3] = "Explication de la recherche";
            configHeaders[4] = "Acronymes";
            configHeaders[5] = "Zoom avant";
            configHeaders[6] = "Zoom arrière";
        }
        else {/*this is the default*/
            configHeaders[0] = "הגדרות";
            configHeaders[1] = "אודות";
            configHeaders[2] = "משוב";
            configHeaders[3] = "הסבר על החיפוש";
            configHeaders[4] = "ראשי תיבות";
            configHeaders[5] = "הגדל טקסט";
            configHeaders[6] = "הקטן טקסט";
        }

		popupMenu.getMenu().add(0,0,0,configHeaders[0]);//(int groupId, int itemId, int order, int titleRes)
		popupMenu.getMenu().add(0,1,1,configHeaders[1]);
		popupMenu.getMenu().add(0,2,2,configHeaders[2]);
		popupMenu.getMenu().add(0,3,3,configHeaders[3]);
		popupMenu.getMenu().add(0,4,4,configHeaders[4]);
		if(API >= 19)
		{
			popupMenu.getMenu().add(0,5,5,configHeaders[5]);
			popupMenu.getMenu().add(0,6,6,configHeaders[6]);
		}

		popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() 
		{
			@Override
			public boolean onMenuItemClick(MenuItem item)
			{
				WebSettings webSettings = webview.getSettings();
				fontSize = webSettings.getDefaultFontSize();
				switch (item.getItemId())
				{
				case 0:/*settings*/
					try
					{
						Class ourClass = Class.forName("com.rafraph.pnineyHalachaHashalem.Settings");
						Intent ourIntent = new Intent(textMain.this, ourClass);
						startActivity(ourIntent);
					}
					catch (ClassNotFoundException e)
					{
						e.printStackTrace();
					}
					break;
				case 1:/*about*/
					try
					{
						Class ourClass = Class.forName("com.rafraph.pnineyHalachaHashalem.About");
						Intent ourIntent = new Intent(textMain.this, ourClass);
						startActivity(ourIntent);
					}
					catch (ClassNotFoundException e)
					{
						e.printStackTrace();
					}

					break;
				case 2:/*Feedback*/
					try
					{
						Class ourClass = Class.forName("com.rafraph.pnineyHalachaHashalem.Feedback");
						Intent ourIntent = new Intent(textMain.this, ourClass);
						startActivity(ourIntent);
					}
					catch (ClassNotFoundException e)
					{
						e.printStackTrace();
					}
					break;
				case 3:/*Explanation for Search*/
					try
					{
						Class ourClass = Class.forName("com.rafraph.pnineyHalachaHashalem.SearchHelp");
						Intent ourIntent = new Intent(textMain.this, ourClass);
						startActivity(ourIntent);
					}
					catch (ClassNotFoundException e)
					{
						e.printStackTrace();
					}
					break;
				case 4:/*acronyms*/
					acronymsDecode();
					break;
				case 5:/*increase text*/
					if(fontSize <= 47) {
						fontSize += 3;
						webSettings.setDefaultFontSize(fontSize);
						shPrefEditor.putInt("fontSize", fontSize);
						shPrefEditor.commit();
						switch (MyLanguage){
							case ENGLISH:
								Toast.makeText(getApplicationContext(),	"Font size - "+fontSize, Toast.LENGTH_SHORT).show();
								break;
							case RUSSIAN:
								Toast.makeText(getApplicationContext(),	"Размер шрифта - "+fontSize, Toast.LENGTH_SHORT).show();
								break;
							case SPANISH:
								Toast.makeText(getApplicationContext(),	"Tamaño de fuente - "+fontSize, Toast.LENGTH_SHORT).show();
								break;
							case FRENCH:
								Toast.makeText(getApplicationContext(),	"Taille de police - "+fontSize, Toast.LENGTH_SHORT).show();
								break;
							default:
								Toast.makeText(getApplicationContext(),	"גודל גופן - "+fontSize, Toast.LENGTH_SHORT).show();
						}
					}
					else{
						switch (MyLanguage){
							case ENGLISH:
								Toast.makeText(getApplicationContext(),	"Maximum font size - "+fontSize, Toast.LENGTH_SHORT).show();
								break;
							case RUSSIAN:
								Toast.makeText(getApplicationContext(),	"Максимальный размер шрифта - "+fontSize, Toast.LENGTH_SHORT).show();
								break;
							case SPANISH:
								Toast.makeText(getApplicationContext(),	"Tamaño máximo de la fuente - "+fontSize, Toast.LENGTH_SHORT).show();
								break;
							case FRENCH:
								Toast.makeText(getApplicationContext(),	"Taille maximale de la police - "+fontSize, Toast.LENGTH_SHORT).show();
								break;
							default:
								Toast.makeText(getApplicationContext(),	"גודל גופן מקסימלי", Toast.LENGTH_SHORT).show();
						}
					}
					break;
				case 6:/*decrease text*/
				if(fontSize >= 10 ) {
					fontSize -= 3;
					webSettings.setDefaultFontSize(fontSize);
					shPrefEditor.putInt("fontSize", fontSize);
					shPrefEditor.commit();
					switch (MyLanguage){
						case ENGLISH:
							Toast.makeText(getApplicationContext(),	"Font size - "+fontSize, Toast.LENGTH_SHORT).show();
							break;
						case RUSSIAN:
							Toast.makeText(getApplicationContext(),	"Размер шрифта - "+fontSize, Toast.LENGTH_SHORT).show();
							break;
						case SPANISH:
							Toast.makeText(getApplicationContext(),	"Tamaño de fuente - "+fontSize, Toast.LENGTH_SHORT).show();
							break;
						case FRENCH:
							Toast.makeText(getApplicationContext(),	"Taille de police - "+fontSize, Toast.LENGTH_SHORT).show();
							break;
						default:
							Toast.makeText(getApplicationContext(),	"גודל גופן - "+fontSize, Toast.LENGTH_SHORT).show();
					}
				}
				else{
					switch (MyLanguage){
						case ENGLISH:
							Toast.makeText(getApplicationContext(),	"Minimum font size - "+fontSize, Toast.LENGTH_SHORT).show();
							break;
						case RUSSIAN:
							Toast.makeText(getApplicationContext(),	"Минимальный размер шрифта - "+fontSize, Toast.LENGTH_SHORT).show();
							break;
						case SPANISH:
							Toast.makeText(getApplicationContext(),	"Tamaño mínimo de fuente - "+fontSize, Toast.LENGTH_SHORT).show();
							break;
						case FRENCH:
							Toast.makeText(getApplicationContext(),	"Taille de police minimale - "+fontSize, Toast.LENGTH_SHORT).show();
							break;
						default:
							Toast.makeText(getApplicationContext(),	"גודל גופן מינימלי", Toast.LENGTH_SHORT).show();
					}
				}
					break;
				default:
					break;
				}
				return true;
			}
		});

		popupMenu.show();
	}//showPopupMenuSettings

	private class MyWebViewClient extends WebViewClient 
	{
		@SuppressLint("NewApi")
		@Override
		public void onPageFinished(WebView view, String url) 
		{
			// TODO Auto-generated method stub
			super.onPageFinished(view, url);
			String strToastSearch;
			WebSettings webSettings = webview.getSettings();

			if(firstTime == true || ChangeChapter == true)
			{
				firstTime = false;

				webSettings.setDefaultFontSize(fontSize);

				if(cameFromSearch == true)
				{
					webview.loadUrl(searchPosition);
					if(noteStr != "0")/*if all the results are in notes*/
					{
						query = "הערה " + noteStr;
						strToastSearch = "תוצאות החיפוש נמצאות בהערות: " + sectionsForToast;
					}
					else
					{
						strToastSearch = "תוצאות החיפוש נמצאות גם בהערות: " + sectionsForToast;
					}

					if(API < 16) 
					{
						if(BlackBackground == 1)
							WhiteTextAfterDelay();
						else
							finddelay(query);
					} else {
						webview.findAllAsync(query);
					}

					if (sectionsForToast.compareTo("") != 0)
						Toast.makeText(getApplicationContext(), strToastSearch, Toast.LENGTH_LONG).show();
				}
				if(BlackBackground == 1)
				{
					webview.loadUrl("javascript:function myFunction() {var x = document.body;x.style.color = \"white\";} myFunction(); ");
					llMainLayout.setBackgroundColor(Color.BLACK);
					webview.setBackgroundColor(0xFFFFFF);//black
				//	textActionBar.setTitle(Html.fromHtml("<font color=\"white\">" + title + "</font>"));
				}
				else
				{
					webview.loadUrl("javascript:function myFunction() {var x = document.body;x.style.color = \"black\";} myFunction(); ");
					llMainLayout.setBackgroundColor(Color.WHITE);
					webview.setBackgroundColor(0x000000);//white
				//	textActionBar.setTitle(Html.fromHtml("<font color=\"black\">" + title + "</font>"));
				}
			}
		}
	}

	public void findBookAndChapter()
	{
		String bookAndChapter;

		int length= searchPosition.lastIndexOf("#"); 
		if(length == -1)/*it means that all the results are in notes*/
		{
			length = searchPosition.lastIndexOf(":");
			noteStr = searchPosition.substring(length+1,searchPosition.length());
			searchPosition = searchPosition.substring(0, length);
			bookAndChapter = searchPosition;
		}
		else
		{
			length= searchPosition.lastIndexOf("#");
			bookAndChapter = searchPosition.substring(0, length);
		}

		book_chapter = new int[2];
		for (int i=0; i<=BOOKS_HEB_NUMBER; i++)
			for (int j=1; j<=lastChapter[i]; j++)
				if(bookAndChapter.equals(chaptersFiles[i][j]))
				{
					book_chapter[0] = i;
					book_chapter[1] = j;
					return;
				}
	}

	public String convertAnchorIdToSection(int Id)
	{
		switch (Id)
		{
		case 0:
			return "פתיחה";
		case 1:
			return "א";
		case 2:
			return "ב";
		case 3:
			return "ג";
		case 4:
			return "ד";
		case 5:
			return "ה";
		case 6:
			return "ו";
		case 7:
			return "ז";
		case 8:
			return "ח";
		case 9:
			return "ט";
		case 10:
			return "י";
		case 11:
			return "יא";
		case 12:
			return "יב";
		case 13:
			return "יג";
		case 14:
			return "יד";
		case 15:
			return "טו";
		case 16:
			return "טז";
		case 17:
			return "יז";
		case 18:
			return "יח";
		case 19:
			return "יט";
		case 20:
			return "כ";
		case 21:
			return "כא";
		case 22:
			return "כב";
		case 23:
			return "כג";
		case 24:
			return "כד";
		case 25:
			return "כה";
		case 26:
			return "כו";
		case 27:
			return "כז";
		case 28:
			return "כח";
		case 29:
			return "כט";
		case 30:
			return "ל";
		case 31:
			return "לא";
		case 32:
			return "לב";
		case 33:
			return "לג";
		case 34:
			return "לד";
		case 35:
			return "לה";
		case 36:
			return "לו";
		case 37:
			return "לז";
		case 38:
			return "לח";
		case 39:
			return "לט";
		case 40:
			return "מ";
		default:
			return "תת";
		}
	}

	public String convertBookIdToName(int bookId)
	{
		switch (bookId)
		{
		case BRACHOT:
			return "ברכות";
		case HAAMVEHAAREZ:
			return "העם והא'";
		case ZMANIM:
			return "זמנים";
		case YAMIM:
			return "ימים נוראים";
		case LIKUTIM_A:
			return "ליקוטים א";
		case LIKUTIM_B:
			return "ליקוטים ב";
		case LIKUTIM_C:
			return "ליקוטים ג";
		case MOADIM:
			return "מועדים";
		case SUCOT:
			return "סוכות";
		case PESACH:
			return "פסח";
		case SHVIIT:
			return "שביעית";
		case SHABAT:
			return "שבת";
		case SIMCHAT:
			return "שמחת הבית";
		case TEFILA:
			return "תפילה";
		case TEFILAT_NASHIM:
			return "תפילת נש'";
		case HAR_MOADIM:
			return "הר' מועדים";
		case HAR_SUCOT:
			return "הר' סוכות";
		case HAR_SHABAT:
			return "הר' שבת";
		case HAR_SIMCHAT:
			return "הר' שמחת הבית";
		case HAR_YAMIM:
			return "הר' ימים נוראים";
		case HAR_BRACHOT:
			return "הר' ברכות";
		case E_TEFILA:
			return "Tefila";
		case E_PESACH:
			return "Pesach";
		case E_ZMANIM:
			return "Zmanim";
		case E_WOMEN_PRAYER:
			return "Women’s Prayer";
		case E_SHABAT:
			return "Shabbat";
		case F_TEFILA:
			return "La prière d’Israël";
		default:
			return "לא ידוע";
		}
	}

	private void fillChaptersNames()
	{
		/*BRACHOT*/
		chaptersNames[BRACHOT][1] = "ברכות: א - פתיחה";
		chaptersNames[BRACHOT][2] = "ברכות: ב - נטילת ידיים לסעודה";
		chaptersNames[BRACHOT][3] = "ברכות: ג - ברכת המוציא";
		chaptersNames[BRACHOT][4] = "ברכות: ד - ברכת המזון";
		chaptersNames[BRACHOT][5] = "ברכות: ה - זימון";
		chaptersNames[BRACHOT][6] = "ברכות: ו - חמשת מיני דגן";
		chaptersNames[BRACHOT][7] = "ברכות: ז - ברכת היין";
		chaptersNames[BRACHOT][8] = "ברכות: ח - ברכת הפירות ושהכל";
		chaptersNames[BRACHOT][9] = "ברכות: ט - כללי ברכה ראשונה";
		chaptersNames[BRACHOT][10] = "ברכות: י - ברכה אחרונה";
		chaptersNames[BRACHOT][11] = "ברכות: יא - עיקר וטפל";
		chaptersNames[BRACHOT][12] = "ברכות: יב - כללי ברכות";
		chaptersNames[BRACHOT][13] = "ברכות: יג - דרך ארץ";
		chaptersNames[BRACHOT][14] = "ברכות: יד - ברכת הריח";
		chaptersNames[BRACHOT][15] = "ברכות: טו - ברכות הראייה";
		chaptersNames[BRACHOT][16] = "ברכות: טז - ברכת הגומל";
		chaptersNames[BRACHOT][17] = "ברכות: יז - ברכות ההודאה והשמחה";
		/*HAAMVEHAAREZ*/
		chaptersNames[HAAMVEHAAREZ][1] = "העם והארץ: א - מעלת הארץ";
		chaptersNames[HAAMVEHAAREZ][2] = "העם והארץ: ב - קודש וחול ביישוב הארץ";
		chaptersNames[HAAMVEHAAREZ][3] = "העם והארץ: ג - מצוות יישוב הארץ";
		chaptersNames[HAAMVEHAAREZ][4] = "העם והארץ: ד - מהלכות צבא ומלחמה";
		chaptersNames[HAAMVEHAAREZ][5] = "העם והארץ: ה - שמירת הארץ";
		chaptersNames[HAAMVEHAAREZ][6] = "העם והארץ: ו - מהלכות מדינה";
		chaptersNames[HAAMVEHAAREZ][7] = "העם והארץ: ז - ערבות הדדית";
		chaptersNames[HAAMVEHAAREZ][8] = "העם והארץ: ח - עבודה עברית";
		chaptersNames[HAAMVEHAAREZ][9] = "העם והארץ: ט - זכר למקדש";
		chaptersNames[HAAMVEHAAREZ][10] = "העם והארץ: י - הלכות גרים";
		chaptersNames[HAAMVEHAAREZ][11] = "העם והארץ: יא - נספח: תשובות מאת הרב גורן ומרבנים נוספים";
		/*ZMANIM*/
		chaptersNames[ZMANIM][1] = "זמנים: א - ראש חודש";
		chaptersNames[ZMANIM][2] = "זמנים: ב - הלכות ספירת העומר";
		chaptersNames[ZMANIM][3] = "זמנים: ג - מנהגי אבילות בספירת העומר";
		chaptersNames[ZMANIM][4] = "זמנים: ד - יום העצמאות";
		chaptersNames[ZMANIM][5] = "זמנים: ה - לג בעומר";
		chaptersNames[ZMANIM][6] = "זמנים: ו - ארבעת צומות החורבן";
		chaptersNames[ZMANIM][7] = "זמנים: ז - דיני הצומות הקלים";
		chaptersNames[ZMANIM][8] = "זמנים: ח - מנהגי שלושת השבועות";
		chaptersNames[ZMANIM][9] = "זמנים: ט - ערב תשעה באב";
		chaptersNames[ZMANIM][10] = "זמנים: י - הלכות תשעה באב";
		chaptersNames[ZMANIM][11] = "זמנים: יא - ימי החנוכה";
		chaptersNames[ZMANIM][12] = "זמנים: יב - הדלקת נרות חנוכה";
		chaptersNames[ZMANIM][13] = "זמנים: יג - דיני המקום והזמן";
		chaptersNames[ZMANIM][14] = "זמנים: יד - חודש אדר";
		chaptersNames[ZMANIM][15] = "זמנים: טו - פורים ומקרא מגילה";
		chaptersNames[ZMANIM][16] = "זמנים: טז - מצוות השמחה והחסד";
		chaptersNames[ZMANIM][17] = "זמנים: יז - דיני פרזים ומוקפים";
		/*YAMIM*/
		chaptersNames[YAMIM][1] = "ימים נוראים: א - הדין השכר והעונש";
		chaptersNames[YAMIM][2] = "ימים נוראים: ב - סליחות ותפילות";
		chaptersNames[YAMIM][3] = "ימים נוראים: ג - ראש השנה";
		chaptersNames[YAMIM][4] = "ימים נוראים: ד - מצוות השופר";
		chaptersNames[YAMIM][5] = "ימים נוראים: ה - עשרת ימי תשובה";
		chaptersNames[YAMIM][6] = "ימים נוראים: ו - יום הכיפורים";
		chaptersNames[YAMIM][7] = "ימים נוראים: ז - הלכות יום הכיפורים";
		chaptersNames[YAMIM][8] = "ימים נוראים: ח - דיני התענית";
		chaptersNames[YAMIM][9] = "ימים נוראים: ט - שאר עינויים";
		chaptersNames[YAMIM][10] = "ימים נוראים: י - עבודת יום הכיפורים";
		/*LIKUTIM_A*/
		chaptersNames[LIKUTIM_A][1] = "ליקוטים א: א - הלכות תלמוד תורה";
		chaptersNames[LIKUTIM_A][2] = "ליקוטים א: ב - החינוך לתורה";
		chaptersNames[LIKUTIM_A][3] = "ליקוטים א: ג - הלכות ספר תורה";
		chaptersNames[LIKUTIM_A][4] = "ליקוטים א: ד - הלכות קריאת התורה";
		chaptersNames[LIKUTIM_A][5] = "ליקוטים א: ה - כבוד ספר תורה ושמות קדושים";
		chaptersNames[LIKUTIM_A][6] = "ליקוטים א: ו - הלכות בית כנסת";
		chaptersNames[LIKUTIM_A][7] = "ליקוטים א: ז - הלכות כיפה";
		chaptersNames[LIKUTIM_A][8] = "ליקוטים א: ח - מהלכות ציצית";
		chaptersNames[LIKUTIM_A][9] = "ליקוטים א: ט - תפילין";
		chaptersNames[LIKUTIM_A][10] = "ליקוטים א: י - מהלכות מזוזה";
		chaptersNames[LIKUTIM_A][11] = "ליקוטים א: יא - הלכות כהנים";
		chaptersNames[LIKUTIM_A][12] = "ליקוטים א: יב - תרומות ומעשרות";
		chaptersNames[LIKUTIM_A][13] = "ליקוטים א: יג - מתנות מן החי";
		/*LIKUTIM_B*/
		chaptersNames[LIKUTIM_B][1] = "ליקוטים ב: א - בין אדם לחברו";
		chaptersNames[LIKUTIM_B][2] = "ליקוטים ב: ב - הלכות אמירת אמת";
		chaptersNames[LIKUTIM_B][3] = "ליקוטים ב: ג - הלכות גניבת דעת";
		chaptersNames[LIKUTIM_B][4] = "ליקוטים ב: ד - הלכות גניבה";
		chaptersNames[LIKUTIM_B][5] = "ליקוטים ב: ה - מצוות הלוואה";
		chaptersNames[LIKUTIM_B][6] = "ליקוטים ב: ו - מהלכות צדקה";
		chaptersNames[LIKUTIM_B][7] = "ליקוטים ב: ז - הכנסת אורחים";
		chaptersNames[LIKUTIM_B][8] = "ליקוטים ב: ח - הלכות רוצח ומתאבד";
		chaptersNames[LIKUTIM_B][9] = "ליקוטים ב: ט - הלכות שמירת הנפש";
		chaptersNames[LIKUTIM_B][10] = "ליקוטים ב: י - נהיגה זהירה ותפילת הדרך";
		chaptersNames[LIKUTIM_B][11] = "ליקוטים ב: יא - הלכות הצלת נפשות";
		chaptersNames[LIKUTIM_B][12] = "ליקוטים ב: יב - הפסקת הריון";
		chaptersNames[LIKUTIM_B][13] = "ליקוטים ב: יג - הלכות ניתוחי מתים";
		chaptersNames[LIKUTIM_B][14] = "ליקוטים ב: יד - השתלת אברים";
		chaptersNames[LIKUTIM_B][15] = "ליקוטים ב: טו - הלכות הנוטה למות";
		chaptersNames[LIKUTIM_B][16] = "ליקוטים ב: טז - ליקוטים";
		chaptersNames[LIKUTIM_B][17] = "ליקוטים ב: יז - חברה ושליחות";
		/*LIKUTIM_C*/
		chaptersNames[LIKUTIM_C][1] = "ליקוטים ג: א - כיבוד הורים";
		chaptersNames[LIKUTIM_C][2] = "ליקוטים ג: ב - לקראת נישואין";
		chaptersNames[LIKUTIM_C][3] = "ליקוטים ג: ג - הלכות נישואין";
		chaptersNames[LIKUTIM_C][4] = "ליקוטים ג: ד - הלכות החתונה ומנהגיה";
		chaptersNames[LIKUTIM_C][5] = "ליקוטים ג: ה - איסורי עריות";
		chaptersNames[LIKUTIM_C][6] = "ליקוטים ג: ו - הלכות צניעות";
		chaptersNames[LIKUTIM_C][7] = "ליקוטים ג: ז - ברית מילה";
		chaptersNames[LIKUTIM_C][8] = "ליקוטים ג: ח - פדיון בכורות";
		chaptersNames[LIKUTIM_C][9] = "ליקוטים ג: ט - צער בעלי חיים";
		chaptersNames[LIKUTIM_C][10] = "ליקוטים ג: י - מצוות שילוח הקן";
		chaptersNames[LIKUTIM_C][11] = "ליקוטים ג: יא - כלאיים באילן ובהמה";
		chaptersNames[LIKUTIM_C][12] = "ליקוטים ג: יב - הלכות שמירת עצי פרי";
		chaptersNames[LIKUTIM_C][13] = "ליקוטים ג: יג - בל תשחית";
		chaptersNames[LIKUTIM_C][14] = "ליקוטים ג: יד - הלכות תולעים";
		chaptersNames[LIKUTIM_C][15] = "ליקוטים ג: טו - הלכות טבילת כלים";
		chaptersNames[LIKUTIM_C][16] = "ליקוטים ג: טז - ליקוטים בכשרות";
		/*MOADIM*/
		chaptersNames[MOADIM][1] = "מועדים: א - פתיחה";
		chaptersNames[MOADIM][2] = "מועדים: ב - דיני עשה ביום טוב";
		chaptersNames[MOADIM][3] = "מועדים: ג - כללי המלאכות";
		chaptersNames[MOADIM][4] = "מועדים: ד - מלאכות המאכלים";
		chaptersNames[MOADIM][5] = "מועדים: ה - הבערה כיבוי וחשמל";
		chaptersNames[MOADIM][6] = "מועדים: ו - הוצאה ומוקצה";
		chaptersNames[MOADIM][7] = "מועדים: ז - מדיני יום טוב";
		chaptersNames[MOADIM][8] = "מועדים: ח - עירוב תבשילין";
		chaptersNames[MOADIM][9] = "מועדים: ט - יום טוב שני של גלויות";
		chaptersNames[MOADIM][10] = "מועדים: י - מצוות חול המועד";
		chaptersNames[MOADIM][11] = "מועדים: יא - מלאכת חול המועד";
		chaptersNames[MOADIM][12] = "מועדים: יב - היתרי עבודה במועד";
		chaptersNames[MOADIM][13] = "מועדים: יג - חג שבועות";
		/*SUCOT*/
		chaptersNames[SUCOT][1] = "סוכות: א - חג הסוכות";
		chaptersNames[SUCOT][2] = "סוכות: ב - הלכות סוכה";
		chaptersNames[SUCOT][3] = "סוכות: ג - ישיבה בסוכה";
		chaptersNames[SUCOT][4] = "סוכות: ד - ארבעת המינים";
		chaptersNames[SUCOT][5] = "סוכות: ה - נטילת לולב";
		chaptersNames[SUCOT][6] = "סוכות: ו - הושענא רבה";
		chaptersNames[SUCOT][7] = "סוכות: ז - שמיני עצרת";
		chaptersNames[SUCOT][8] = "סוכות: ח - הקהל";
		/*PESACH*/
		chaptersNames[PESACH][1] = "פסח: א - משמעות החג";
		chaptersNames[PESACH][2] = "פסח: ב - כללי איסור חמץ";
		chaptersNames[PESACH][3] = "פסח: ג - מצוות השבתת חמץ";
		chaptersNames[PESACH][4] = "פסח: ד - בדיקת חמץ";
		chaptersNames[PESACH][5] = "פסח: ה - ביטול חמץ וביעורו";
		chaptersNames[PESACH][6] = "פסח: ו - מכירת חמץ";
		chaptersNames[PESACH][7] = "פסח: ז - תערובת חמץ";
		chaptersNames[PESACH][8] = "פסח: ח - מהלכות כשרות לפסח";
		chaptersNames[PESACH][9] = "פסח: ט - מנהג איסור קטניות";
		chaptersNames[PESACH][10] = "פסח: י - כללי הגעלת כלים";
		chaptersNames[PESACH][11] = "פסח: יא - הכשרת המטבח לפסח";
		chaptersNames[PESACH][12] = "פסח: יב - הלכות מצה";
		chaptersNames[PESACH][13] = "פסח: יג - הלכות ערב פסח ומנהגיו";
		chaptersNames[PESACH][14] = "פסח: יד - ערב פסח שחל בשבת";
		chaptersNames[PESACH][15] = "פסח: טו - ההגדה";
		chaptersNames[PESACH][16] = "פסח: טז - ליל הסדר";
		/*SHVIIT*/
		chaptersNames[SHVIIT][1] = "שביעית: א - מצוות השביעית";
		chaptersNames[SHVIIT][2] = "שביעית: ב - מצוות השביתה";
		chaptersNames[SHVIIT][3] = "שביעית: ג - השמטת הפירות";
		chaptersNames[SHVIIT][4] = "שביעית: ד - פירות השביעית";
		chaptersNames[SHVIIT][5] = "שביעית: ה - הזמן המקום והאדם";
		chaptersNames[SHVIIT][6] = "שביעית: ו - שמיטת כספים";
		chaptersNames[SHVIIT][7] = "שביעית: ז - היתר המכירה";
		chaptersNames[SHVIIT][8] = "שביעית: ח - אוצר בית דין";
		chaptersNames[SHVIIT][9] = "שביעית: ט - קניית פירות בשביעית";
		chaptersNames[SHVIIT][10] = "שביעית: י - מצוות היובל";
		chaptersNames[SHVIIT][11] = "שביעית: יא - חזון השביעית";		
		/*SHABAT*/
		chaptersNames[SHABAT][1] = "שבת: א - פתיחה";
		chaptersNames[SHABAT][2] = "שבת: ב - הכנות לשבת";
		chaptersNames[SHABAT][3] = "שבת: ג - זמני השבת";
		chaptersNames[SHABAT][4] = "שבת: ד - הדלקת נרות שבת";
		chaptersNames[SHABAT][5] = "שבת: ה - תורה ותפילה בשבת";
		chaptersNames[SHABAT][6] = "שבת: ו - הלכות קידוש";
		chaptersNames[SHABAT][7] = "שבת: ז - סעודות השבת ומלווה מלכה";
		chaptersNames[SHABAT][8] = "שבת: ח - הבדלה ומוצאי שבת";
		chaptersNames[SHABAT][9] = "שבת: ט - כללי המלאכות";
		chaptersNames[SHABAT][10] = "שבת: י - בישול";
		chaptersNames[SHABAT][11] = "שבת: יא - בורר";
		chaptersNames[SHABAT][12] = "שבת: יב - הכנת מאכלים";
		chaptersNames[SHABAT][13] = "שבת: יג - מלאכות הבגד";
		chaptersNames[SHABAT][14] = "שבת: יד - הטיפול בגוף";
		chaptersNames[SHABAT][15] = "שבת: טו - בונה סותר בבית וכלים";
		chaptersNames[SHABAT][16] = "שבת: טז - מבעיר ומכבה";
		chaptersNames[SHABAT][17] = "שבת: יז - חשמל ןמכשיריו";
		chaptersNames[SHABAT][18] = "שבת: יח - כותב מוחק וצובע";
		chaptersNames[SHABAT][19] = "שבת: יט - מלאכות שבצומח";
		chaptersNames[SHABAT][20] = "שבת: כ - בעלי חיים";
		chaptersNames[SHABAT][21] = "שבת: כא - הלכות הוצאה";
		chaptersNames[SHABAT][22] = "שבת: כב - צביון השבת";
		chaptersNames[SHABAT][23] = "שבת: כג - מוקצה";
		chaptersNames[SHABAT][24] = "שבת: כד - דיני קטן";
		chaptersNames[SHABAT][25] = "שבת: כה - מלאכת גוי";
		chaptersNames[SHABAT][26] = "שבת: כו - מעשה שבת ולפני עיוור";
		chaptersNames[SHABAT][27] = "שבת: כז - פיקוח נפש וחולה";
		chaptersNames[SHABAT][28] = "שבת: כח - חולה שאינו מסוכן";
		chaptersNames[SHABAT][29] = "שבת: כט - עירובין";
		chaptersNames[SHABAT][30] = "שבת: ל - תחומי שבת";
		/*SIMCHAT*/
		chaptersNames[SIMCHAT][1] = "שמחת הבית וברכתו: א - מצוות עונה";
		chaptersNames[SIMCHAT][2] = "שמחת הבית וברכתו: ב - הלכות עונה";
		chaptersNames[SIMCHAT][3] = "שמחת הבית וברכתו: ג - קדושה וכוונה";
		chaptersNames[SIMCHAT][4] = "שמחת הבית וברכתו: ד - שמירת הברית";
		chaptersNames[SIMCHAT][5] = "שמחת הבית וברכתו: ה - פרו ורבו";
		chaptersNames[SIMCHAT][6] = "שמחת הבית וברכתו: ו - קשיים ועקרות";
		chaptersNames[SIMCHAT][7] = "שמחת הבית וברכתו: ז - סריס והשחתה";
		chaptersNames[SIMCHAT][8] = "שמחת הבית וברכתו: ח - נחמת חשוכי ילדים";
		chaptersNames[SIMCHAT][9] = "שמחת הבית וברכתו: ט - הפסקת הריון";
		chaptersNames[SIMCHAT][10] = "שמחת הבית וברכתו: י - האיש והאשה";
		/*TEFILA*/
		chaptersNames[TEFILA][1] = "תפילה: א - יסודות הלכות תפילה";
		chaptersNames[TEFILA][2] = "תפילה: ב - המניין";
		chaptersNames[TEFILA][3] = "תפילה: ג - מקום התפילה";
		chaptersNames[TEFILA][4] = "תפילה: ד - החזן וקדיש של אבלים";
		chaptersNames[TEFILA][5] = "תפילה: ה - הכנות לתפילה";
		chaptersNames[TEFILA][6] = "תפילה: ו - הנוסחים ומנהגי העדות";
		chaptersNames[TEFILA][7] = "תפילה: ז - השקמת הבוקר";
		chaptersNames[TEFILA][8] = "תפילה: ח - נטילת ידיים שחרית";
		chaptersNames[TEFILA][9]  = "תפילה: ט - ברכות השחר";
		chaptersNames[TEFILA][10] = "תפילה: י - ברכת התורה";
		chaptersNames[TEFILA][11] = "תפילה: יא - זמן ק\"ש ותפילת שחרית";
		chaptersNames[TEFILA][12] = "תפילה: יב - לקראת תפילת שחרית";
		chaptersNames[TEFILA][13] = "תפילה: יג - סדר קרבנות";
		chaptersNames[TEFILA][14] = "תפילה: יד - פסוקי דזמרה";
		chaptersNames[TEFILA][15] = "תפילה: טו - קריאת שמע";
		chaptersNames[TEFILA][16] = "תפילה: טז - ברכות קריאת שמע";
		chaptersNames[TEFILA][17] = "תפילה: יז - תפילת עמידה";
		chaptersNames[TEFILA][18] = "תפילה: יח - טעויות הזכרות ושכחה";
		chaptersNames[TEFILA][19] = "תפילה: יט - חזרת הש\"ץ";
		chaptersNames[TEFILA][20] = "תפילה: כ - ברכת כהנים";
		chaptersNames[TEFILA][21] = "תפילה: כא - נפילת אפיים ותחנונים";
		chaptersNames[TEFILA][22] = "תפילה: כב - מדיני קריאת התורה";
		chaptersNames[TEFILA][23] = "תפילה: כג - סיום שחרית ודיני קדיש";
		chaptersNames[TEFILA][24] = "תפילה: כד - תפילת מנחה";
		chaptersNames[TEFILA][25] = "תפילה: כה - תפילת מעריב";
		chaptersNames[TEFILA][26] = "תפילה: כו - קריאת שמע על המיטה"; 
		/*TEFILAT_NASHIM*/
		chaptersNames[TEFILAT_NASHIM][1] = "תפילת נשים: א - יסודות הלכות תפילה";
		chaptersNames[TEFILAT_NASHIM][2] = "תפילת נשים: ב - מצוות תפילה לנשים";
		chaptersNames[TEFILAT_NASHIM][3] = "תפילת נשים: ג - טעמי מצוות הנשים";
		chaptersNames[TEFILAT_NASHIM][4] = "תפילת נשים: ד - השכמת הבוקר";
		chaptersNames[TEFILAT_NASHIM][5] = "תפילת נשים: ה - נטילת ידיים שחרית";
		chaptersNames[TEFILAT_NASHIM][6] = "תפילת נשים: ו - ברכות השחר";
		chaptersNames[TEFILAT_NASHIM][7] = "תפילת נשים: ז - ברכות התורה";
		chaptersNames[TEFILAT_NASHIM][8] = "תפילת נשים: ח - תפילת שחרית והדינים שלפניה";
		chaptersNames[TEFILAT_NASHIM][9]  = "תפילת נשים: ט - הכנת הגוף";
		chaptersNames[TEFILAT_NASHIM][10] = "תפילת נשים: י - הכנת הנפש והלבוש";
		chaptersNames[TEFILAT_NASHIM][11] = "תפילת נשים: יא - מקום התפילה";
		chaptersNames[TEFILAT_NASHIM][12] = "תפילת נשים: יב - תפילת עמידה";
		chaptersNames[TEFILAT_NASHIM][13] = "תפילת נשים: יג - הזכרת גשמים ובקשתם";
		chaptersNames[TEFILAT_NASHIM][14] = "תפילת נשים: יד - כבוד התפילה";
		chaptersNames[TEFILAT_NASHIM][15] = "תפילת נשים: טו - קרבנות ופסוקי דזמרה";
		chaptersNames[TEFILAT_NASHIM][16] = "תפילת נשים: טז - קריאת שמע וברכותיה";
		chaptersNames[TEFILAT_NASHIM][17] = "תפילת נשים: יז - התפילות שלאחר עמידה";
		chaptersNames[TEFILAT_NASHIM][18] = "תפילת נשים: יח - מנחה וערכית";
		chaptersNames[TEFILAT_NASHIM][19] = "תפילת נשים: יט - קריאת שמע על המיטה";
		chaptersNames[TEFILAT_NASHIM][20] = "תפילת נשים: כ - מהלכות התפילה במניין";
		chaptersNames[TEFILAT_NASHIM][21] = "תפילת נשים: כא - מהלכות בית הכנסת";
		chaptersNames[TEFILAT_NASHIM][22] = "תפילת נשים: כב - תפילה וקידוש בשבת";
		chaptersNames[TEFILAT_NASHIM][23] = "תפילת נשים: כג - מהלכות חגים ומועדים";
		chaptersNames[TEFILAT_NASHIM][24] = "תפילת נשים: כד - נוסחי התפלה ומנהגי העדות";
		/*HAR_MOADIM*/
		chaptersNames[HAR_MOADIM][1]  = "הר' מועדים: א - פתיחה";
		chaptersNames[HAR_MOADIM][2]  = "הר' מועדים: ב - דיני עשה ביום טוב";
		chaptersNames[HAR_MOADIM][3]  = "הר' מועדים: ג - כללי המלאכות";
		chaptersNames[HAR_MOADIM][4]  = "הר' מועדים: ד - מלאכות המאכלים";
		chaptersNames[HAR_MOADIM][5]  = "הר' מועדים: ה - הבערה כיבוי וחשמל";
		chaptersNames[HAR_MOADIM][6]  = "הר' מועדים: ו - הוצאה ומוקצה";
		chaptersNames[HAR_MOADIM][7]  = "הר' מועדים: ז - מדיני יום טוב";
		chaptersNames[HAR_MOADIM][8]  = "הר' מועדים: ח - עירוב תבשילין";
		chaptersNames[HAR_MOADIM][9]  = "הר' מועדים: ט - יום טוב שני של גלויות";
		chaptersNames[HAR_MOADIM][10] = "הר' מועדים: י - מצוות חול המועד";
		chaptersNames[HAR_MOADIM][11] = "הר' מועדים: יא - מלאכת חול המועד";
		chaptersNames[HAR_MOADIM][12] = "הר' מועדים: יב - היתרי עבודה במועד";
		/*HAR_SUCOT*/
		chaptersNames[HAR_SUCOT][1]  = "א -חג הסוכות";
		chaptersNames[HAR_SUCOT][2]  = "ב - הלכות סוכה";
		chaptersNames[HAR_SUCOT][3]  = "ג - ישיבה בסוכה";
		chaptersNames[HAR_SUCOT][4]  = "ד - ארבעת המינים";
		chaptersNames[HAR_SUCOT][5]  = "ה - נטילת לולב";
		chaptersNames[HAR_SUCOT][6]  = "ו - הושענא רבה";
		chaptersNames[HAR_SUCOT][7]  = "ז - שמיני עצרת";
		chaptersNames[HAR_SUCOT][8]  = "ח - הקהל";
		/*HAR_SHABAT*/
		chaptersNames[HAR_SHABAT][1]  = "הר' שבת: א - פתיחה";
		chaptersNames[HAR_SHABAT][2]  = "הר' שבת: ב - הכנות לשבת";
		chaptersNames[HAR_SHABAT][3]  = "הר' שבת: ג - זמני השבת";
		chaptersNames[HAR_SHABAT][4]  = "הר' שבת: ד - הדלקת נרות שבת";
		chaptersNames[HAR_SHABAT][5]  = "הר' שבת: ה - תורה ותפילה בשבת";
		chaptersNames[HAR_SHABAT][6]  = "הר' שבת: ו - הלכות קידוש";
		chaptersNames[HAR_SHABAT][7]  = "הר' שבת: ז - סעודות השבת ומלווה מלכה";
		chaptersNames[HAR_SHABAT][8]  = "הר' שבת: ח - הבדלה ומוצאי שבת";
		chaptersNames[HAR_SHABAT][9]  = "הר' שבת: ט - כללי המלאכות";
		chaptersNames[HAR_SHABAT][10] = "הר' שבת: י - בישול";
		chaptersNames[HAR_SHABAT][11] = "הר' שבת: יא - בורר";
		chaptersNames[HAR_SHABAT][12] = "הר' שבת: יב - הכנת מאכלים";
		chaptersNames[HAR_SHABAT][13] = "הר' שבת: יג - מלאכות הבגד";
		chaptersNames[HAR_SHABAT][14] = "הר' שבת: יד - הטיפול בגוף";
		chaptersNames[HAR_SHABAT][15] = "הר' שבת: טו - בונה סותר בבית וכלים";
		chaptersNames[HAR_SHABAT][16] = "הר' שבת: טז - מבעיר ומכבה";
		chaptersNames[HAR_SHABAT][17] = "הר' שבת: יז - חשמל ומכשיריו";
		chaptersNames[HAR_SHABAT][18] = "הר' שבת: יח - כותב מוחק וצובע";
		chaptersNames[HAR_SHABAT][19] = "הר' שבת: יט - מלאכות שבצומח";
		chaptersNames[HAR_SHABAT][20] = "הר' שבת: כ - בעלי חיים";
		chaptersNames[HAR_SHABAT][21] = "הר' שבת: כא - הלכות הוצאה";
		chaptersNames[HAR_SHABAT][22] = "הר' שבת: כב - צביון השבת";
		chaptersNames[HAR_SHABAT][23] = "הר' שבת: כג - מוקצה";
		chaptersNames[HAR_SHABAT][24] = "הר' שבת: כד - דיני קטן";
		chaptersNames[HAR_SHABAT][25] = "הר' שבת: כה - מלאכת גוי";
		chaptersNames[HAR_SHABAT][26] = "הר' שבת: כו - מעשה שבת ולפני עיוור";
		chaptersNames[HAR_SHABAT][27] = "הר' שבת: כז - פיקוח נפש וחולה";
		chaptersNames[HAR_SHABAT][28] = "הר' שבת: כח - חולה שאינו מסוכן";
		chaptersNames[HAR_SHABAT][29] = "הר' שבת: כט - עירובין";
		chaptersNames[HAR_SHABAT][30] = "הר' שבת: ל - תחומי שבת";
		/*HAR_SIMCHAT*/
		chaptersNames[HAR_SIMCHAT][1]  = "הר' שמחת: א - מצוות עונה";
		chaptersNames[HAR_SIMCHAT][2]  = "הר' שמחת: ב - הלכות עונה";
		chaptersNames[HAR_SIMCHAT][3]  = "הר' שמחת: ג - קדושה וכוונה";
		chaptersNames[HAR_SIMCHAT][4]  = "הר' שמחת: ד - שמירת הברית";
		chaptersNames[HAR_SIMCHAT][5]  = "הר' שמחת: ה - פרו ורבו";
		chaptersNames[HAR_SIMCHAT][6]  = "הר' שמחת: ו - קשיים ועקרות";
		chaptersNames[HAR_SIMCHAT][7]  = "הר' שמחת: ז - סריס והשחתה";
		chaptersNames[HAR_SIMCHAT][8]  = "הר' שמחת: ח - נחמת חשוכי ילדים";
		chaptersNames[HAR_SIMCHAT][9]  = "הר' שמחת: ט - הפסקת הריון";
		chaptersNames[HAR_SIMCHAT][10] = "הר' שמחת: י - האיש והאשה";
		/*HAR_BRACHOT*/
		chaptersNames[HAR_BRACHOT][1]  = "הר' ברכות: א - פתיחה";
		chaptersNames[HAR_BRACHOT][2]  = "הר' ברכות: ב - נטילת ידיים לסעודה";
		chaptersNames[HAR_BRACHOT][3]  = "הר' ברכות: ג - ברכת המוציא";
		chaptersNames[HAR_BRACHOT][4]  = "הר' ברכות: ד - ברכת המזון";
		chaptersNames[HAR_BRACHOT][5]  = "הר' ברכות: ה - זימון";
		chaptersNames[HAR_BRACHOT][6]  = "הר' ברכות: ו - חמשת מיני דגן";
		chaptersNames[HAR_BRACHOT][7]  = "הר' ברכות: ז - ברכת היין";
		chaptersNames[HAR_BRACHOT][8]  = "הר' ברכות: ח - ברכת הפירות ושהכל";
		chaptersNames[HAR_BRACHOT][9]  = "הר' ברכות: ט - כללי ברכה ראשונה";
		chaptersNames[HAR_BRACHOT][10] = "הר' ברכות: י - ברכה אחרונה";
		chaptersNames[HAR_BRACHOT][11] = "הר' ברכות: יא - עיקר וטפל";
		chaptersNames[HAR_BRACHOT][12] = "הר' ברכות: יב - כללי ברכות";
		chaptersNames[HAR_BRACHOT][13] = "הר' ברכות: יג - דרך ארץ";
		chaptersNames[HAR_BRACHOT][14] = "הר' ברכות: יד - ברכת הריח";
		chaptersNames[HAR_BRACHOT][15] = "הר' ברכות: טו - ברכות הראייה";
		chaptersNames[HAR_BRACHOT][16] = "הר' ברכות: טז - ברכת הגומל";
		chaptersNames[HAR_BRACHOT][17] = "הר' ברכות: יז - ברכות ההודאה והשמחה";
		/*HAR_YAMIM*/
		chaptersNames[HAR_YAMIM][1] = "הר' ימים נוראים: א - הדין השכר והעונש";
		chaptersNames[HAR_YAMIM][2] = "הר' ימים נוראים: ב - סליחות ותפילות";
		chaptersNames[HAR_YAMIM][3] = "הר' ימים נוראים: ג - ראש השנה";
		chaptersNames[HAR_YAMIM][4] = "הר' ימים נוראים: ד - מצוות השופר";
		chaptersNames[HAR_YAMIM][5] = "הר' ימים נוראים: ה - עשרת ימי תשובה";
		chaptersNames[HAR_YAMIM][6] = "הר' ימים נוראים: ו - יום הכיפורים";
		chaptersNames[HAR_YAMIM][7] = "הר' ימים נוראים: ז - הלכות יום הכיפורים";
		chaptersNames[HAR_YAMIM][8] = "הר' ימים נוראים: ח - דיני התענית";
		chaptersNames[HAR_YAMIM][9] = "הר' ימים נוראים: ט - שאר עינויים";
		chaptersNames[HAR_YAMIM][10] = "הר' ימים נוראים: י - עבודת יום הכיפורים";
	}

	void innerSearch()
	{
		final Context context = this;

		// custom dialog
		innerSearchDialog = new Dialog(context);
		innerSearchDialog.setContentView(R.layout.inner_search);
		innerSearchDialog.setTitle("חיפוש בפרק הנוכחי");

		Button dialogButton = (Button) innerSearchDialog.findViewById(R.id.dialogButtonOK);
		TextToSearch = (EditText) innerSearchDialog.findViewById(R.id.editTextTextToSearch );

		// if button is clicked
		dialogButton.setOnClickListener(new OnClickListener()
		{
			@SuppressLint("NewApi")
			@Override
			public void onClick(View v) 
			{
				innerSearchText = TextToSearch.getText().toString();

				innerSearchDialog.dismiss();
				lnrFindOptions.setVisibility(View.VISIBLE);
				if(API < 16) 
				{
					int a=webview.findAll(/*"כל"*/innerSearchText);
					/*to highlight the searched text*/
					try
					{
						Method m = WebView.class.getMethod("setFindIsUp", Boolean.TYPE);
						m.invoke(webview, true);
					}
					catch (Throwable ignored){}
				} 
				else 
				{
					webview.findAllAsync(/*"כל"*/innerSearchText);
				}
			}
		});
		innerSearchDialog.show();
	}

	void acronymsDecode()
	{
		final Context context = this;

		// custom dialog
		acronymsDialog = new Dialog(context);
		acronymsDialog.setContentView(R.layout.acronyms);
		acronymsDialog.setTitle("פענוח ראשי תיבות");

		Button dialogButtonExit = (Button) acronymsDialog.findViewById(R.id.dialogButtonExit);
		Button dialogButtonDecode = (Button) acronymsDialog.findViewById(R.id.dialogButtonDecode);
		final TextView decodedText = (TextView) acronymsDialog.findViewById(R.id.textViewDecodedText);
		//final byte[] buffer;
		//final int size;
		
		TextToDecode = (EditText) acronymsDialog.findViewById(R.id.editTextAcronyms );

		// if button is clicked
		dialogButtonExit.setOnClickListener(new OnClickListener() {
			@SuppressLint("NewApi")
			@Override
			public void onClick(View v) {
				acronymsDialog.dismiss();
			}
		});
		
		dialogButtonDecode.setOnClickListener(new OnClickListener() {
			@SuppressLint("NewApi")
			@Override
			public void onClick(View v) {
				acronymsText = "\r\n" + /*"י\"א" */TextToDecode.getText().toString() + " - ";
				acronymsText = acronymsText.replace("\"", "");
				acronymsText = acronymsText.replace("'", "");
				InputStream is;
				String r = "לא נמצאו תוצאות";
				int index = 0, index_end = 0, first = 1;
				try {
					is = getAssets().open("acronyms.txt");
					int size = is.available();
					byte[] buffer = new byte[size];
					is.read(buffer);
					is.close();
					String strText = new String(buffer);

					while (strText.indexOf(acronymsText, index_end) != -1) {
						index = strText.indexOf(acronymsText, index);
						index = strText.indexOf("-", index + 1) + 2;
						index_end = strText.indexOf("\r\n", index);
						if (first == 1) {
							r = strText.substring(index, index_end);
							first = 0;
						} else
							r += ", " + strText.substring(index, index_end);
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				decodedText.setText(TextToDecode.getText().toString() + " - " + r);

			}
		});
		acronymsDialog.show();	
	}
	
	void autoScrollSpeedDialog()
	{
		final Context context = this;

		// custom dialog
		autoScrollDialog = new Dialog(context);
		autoScrollDialog.setContentView(R.layout.auto_scroll);
		autoScrollDialog.setTitle("מהירות גלילה אוטומטית");

		Button dialogButton = (Button) autoScrollDialog.findViewById(R.id.dialogButtonOK);
		
		// if button is clicked
		dialogButton.setOnClickListener(new OnClickListener()
		{
			@SuppressLint("NewApi")
			@Override
			public void onClick(View v) 
			{
				autoScrollDialog.dismiss();
			}
		});
		spinnerAutoScroll = (Spinner) autoScrollDialog.findViewById(R.id.spinner_auto_scroll);
		scrollSpeed = mPrefs.getInt("scrollSpeed", 2);
		spinnerAutoScroll.setSelection((scrollSpeed / 2) - 1);
		spinnerAutoScroll.setOnItemSelectedListener(new OnItemSelectedListener() {
			boolean first = true;

			public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
				scrollSpeed = (pos + 1) * 2;
				shPrefEditor.putInt("scrollSpeed", scrollSpeed);
				shPrefEditor.commit();
			}

			public void onNothingSelected(AdapterView<?> arg0) {
				// do nothing   
			}
		});
		autoScrollDialog.show();	

	}

	boolean checkIfChapterAllowed(int chapter)
	{
		SimchatMailPswd = mPrefs.getInt("SimchatMailPswd", 0);
		if((book_chapter[0] == SIMCHAT || book_chapter[0] == HAR_SIMCHAT) && chapter == 2 && SimchatMailPswd != 1)
		{
			if(SimchatMailPswd == 1)
				return true;
			else
			{
				startSimchatDialog();
				return false;
			}
		}
		else
			return true;
	}

	void startSimchatDialog()
	{
		final Context context = this;

		simchatDialog = new Dialog(context);
		simchatDialog.setContentView(R.layout.simchatdialog);
		simchatDialog.setTitle("שמחת הבית וברכתו - פרק ב");

		final CheckBox cbOver18 = (CheckBox) simchatDialog.findViewById(R.id.checkBoxOver18);
		final Button buttonSend = (Button) simchatDialog.findViewById(R.id.buttonSend);
		final TextView textView1 = (TextView) simchatDialog.findViewById(R.id.textView1);
		final TextView textView2 = (TextView) simchatDialog.findViewById(R.id.textView2);
		final Button buttonOk = (Button) simchatDialog.findViewById(R.id.buttonOk);
		final int pswd = mPrefs.getInt("pswd", 61377);
		final EditText textPswd = (EditText) simchatDialog.findViewById(R.id.editPswd);

		cbOver18.setOnClickListener(new OnClickListener()
		{
			@SuppressLint("NewApi")
			@Override
			public void onClick(View v)
			{
				if(cbOver18.isChecked())
					buttonSend.setEnabled(true);
				else
					buttonSend.setEnabled(false);
			}
		});

		// if button is clicked
		buttonSend.setOnClickListener(new OnClickListener()
		{
			@SuppressLint("NewApi")
			@Override
			public void onClick(View v)
			{
				String emailaddress[] = { "janer.solutions@gmail.com" };
				String header;
				String message;

				header = "סיסמה לפרק ב בשמחת הבית וברכתו";
				message = "הריני מאשר שאני מעל גיל 18. נא לשלוח לי סיסמה לפרק ב בספר שמחת הבית וברכתו.";

				Intent emailIntent = new Intent (android.content.Intent.ACTION_SEND);
				emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, emailaddress);
				emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, header);
				emailIntent.setType("plain/text");
				emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, message);
				startActivity(emailIntent);

				Calendar date = Calendar.getInstance();
				int pswd = date.get(Calendar.DATE) * date.get(Calendar.YEAR) + 613;

				textPswd.setText(Integer.toString(pswd));
				shPrefEditor.putInt("pswd", pswd);
				shPrefEditor.commit();
				simchatDialog.dismiss();
			}
		});

		buttonOk.setOnClickListener(new OnClickListener()
		{
			@SuppressLint("NewApi")
			@Override
			public void onClick(View v)
			{
				if(textPswd.getText().toString().equals(Integer.toString(pswd)))
				{
					shPrefEditor.putInt("SimchatMailPswd", 1);
					shPrefEditor.commit();
					Toast.makeText(getApplicationContext(), "פרק ב זמין כעת", Toast.LENGTH_SHORT).show();
				}
				else
					Toast.makeText(getApplicationContext(), "הסיסמה שגויה", Toast.LENGTH_SHORT).show();
				simchatDialog.dismiss();
			}
		});

		simchatDialog.show();
	}
}
