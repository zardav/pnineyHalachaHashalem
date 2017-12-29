package com.rafraph.pnineyHalachaHashalem;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class BookmarkActivity extends Activity 
{
	static SharedPreferences mPrefs;
//	SharedPreferences.Editor shPrefEditor;
	public ListView bookmarksListView = null;
	public List<String> listBookmarksNames = new ArrayList<String>();
	public String Bookmarks;
	public static final String PREFS_NAME = "MyPrefsFile";
	public static int[] book_chapter = new int[2];
	ArrayAdapter  adapter;
	SharedPreferences.Editor shPrefEditor;
	Button buttonDeleteAll;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bookmarks);
		bookmarksListView = (ListView) findViewById(R.id.Bookmarkslist);
		buttonDeleteAll = (Button) findViewById(R.id.buttonDeleteAll);
	//	shPrefEditor = mPrefs.edit();
		
		TextView textView = new TextView(this);
		textView.setText("סימניות");
		textView.setTextSize(30);
		bookmarksListView.addHeaderView(textView);
		mPrefs = getSharedPreferences(PREFS_NAME, 0);
		shPrefEditor = mPrefs.edit();
		Bookmarks = mPrefs.getString("Bookmarks", "");
		
		fillBookmarksNames();
		showBookmarksList();

		final Context context = this;
		
		bookmarksListView.setOnItemClickListener(new OnItemClickListener() 
		{

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) 
			{
				try
				{
					Class ourClass = Class.forName("com.rafraph.pnineyHalachaHashalem.textMain");
					Intent ourIntent = new Intent(BookmarkActivity.this, ourClass);
					int i, index = 1/*to skip the first comma*/, index_end=0;
					int bookmarkScrollY, fontSize;
					
					for(i=0;i<((position-1)*5)+1;i++)/*skip to the book of the right bookmark*/
						index = Bookmarks.indexOf("," , index) + 1;
					
					/*book*/
					index_end = Bookmarks.indexOf("," , index);
					book_chapter[0] = Integer.parseInt(Bookmarks.substring(index, index_end));
					
					/*chapter*/
					index = index_end+1;
					index_end = Bookmarks.indexOf("," , index);
					book_chapter[1] = Integer.parseInt(Bookmarks.substring(index, index_end));					
					ourIntent.putExtra("book_chapter", book_chapter);
					
					/*scroll*/
					index = index_end+1;
					index_end = Bookmarks.indexOf("," , index);
					bookmarkScrollY = Integer.parseInt(Bookmarks.substring(index, index_end));
					ourIntent.putExtra("bookmarkScrollY", bookmarkScrollY);
					
					/*font size*/
					index = index_end+1;
					index_end = Bookmarks.indexOf("," , index);
					if(index_end == -1)/*last bookmark*/
						index_end = Bookmarks.length();
					fontSize = Integer.parseInt(Bookmarks.substring(index, index_end));
					shPrefEditor.putInt("fontSize", fontSize);
					shPrefEditor.commit();
					
					ourIntent.putExtra("fromBookmarks", 1);
					startActivity(ourIntent);
				}
				catch (ClassNotFoundException e)
				{
					e.printStackTrace();
				}  
			}
		});
	
		bookmarksListView.setOnItemLongClickListener( new AdapterView.OnItemLongClickListener ()
		{ 
			@Override 
			public boolean onItemLongClick(AdapterView<?> av, View v, int pos, long id) 
			{ 
				bookmarksListView.setOnItemClickListener(new OnItemClickListener() 
				{
					public void onItemClick(AdapterView<?> a, View v, int position, long id) 
					{
						AlertDialog.Builder adb=new AlertDialog.Builder(context);
						adb.setTitle("Delete?");
						adb.setMessage("Are you sure you want to delete " + position);
						final int positionToRemove = position-1;
						adb.setNegativeButton("Cancel", null);
						adb.setPositiveButton("Ok", new AlertDialog.OnClickListener() 
						{
							public void onClick(DialogInterface dialog, int which) 
							{
								int index = 0, index_end = 0;
								listBookmarksNames.remove(positionToRemove);
								adapter.notifyDataSetChanged();
								/*remove the bookmark detailes from Bookmark variable*/
								for(int i=0;i<(positionToRemove*5)+1;i++)/*skip to the book of the right bookmark*/
									index = Bookmarks.indexOf("," , index) + 1;
								index_end = index;
								if(index != 0)
									index--;//in order to delete the comma "," (except in case we want to delete the first item)
								for(int i=0;i<5;i++)/*find the end index of this bookmark*/
									index_end = Bookmarks.indexOf("," , index_end) + 1;
								if(index_end == 0)
									index_end = Bookmarks.length();
								else
									index_end--;//We don't want to delete the last comma
								String strToDelete = Bookmarks.substring(index, index_end);
								Bookmarks = Bookmarks.substring(0, index) + Bookmarks.substring(index_end, Bookmarks.length());
								String strBookmark = Bookmarks;
								shPrefEditor.putString("Bookmarks", Bookmarks);
								shPrefEditor.commit();
							}});
						adb.show();
					}
				});
				/*Listener for the "delete all button"*/
				buttonDeleteAll.setOnClickListener(new OnClickListener()
				{
					@SuppressLint("NewApi")
					@Override
					public void onClick(View v) 
					{
						AlertDialog.Builder adb=new AlertDialog.Builder(context);
						adb.setTitle("Delete?");
						adb.setMessage("Are you sure you want to delete all?");
						adb.setNegativeButton("Cancel", null);
						adb.setPositiveButton("Ok", new AlertDialog.OnClickListener() 
						{
							public void onClick(DialogInterface dialog, int which) 
							{
								Bookmarks = "";
								shPrefEditor.putString("Bookmarks", Bookmarks);
								shPrefEditor.commit();
							}});
						adb.show();
					}
					
				});
				return false; 
			} 
		}); 	
	}



	private void fillBookmarksNames()
	{
		int i, index = 0, index_end=0;
		String strBookmark = Bookmarks;
		while((index = Bookmarks.indexOf("," , index)) != -1)
		{
			index++;
			index_end = Bookmarks.indexOf("," , index);
			listBookmarksNames.add(Bookmarks.substring(index, index_end));
			for(i=0;i<4;i++)/*skip all other fields*/
				index = Bookmarks.indexOf("," , index) + 1;
		}
	}
	
	public void showBookmarksList()
	{
		adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, listBookmarksNames);
		bookmarksListView.setAdapter(adapter);
	}	

}
