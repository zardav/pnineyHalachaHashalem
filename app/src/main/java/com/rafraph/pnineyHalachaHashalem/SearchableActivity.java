package com.rafraph.pnineyHalachaHashalem;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Menu;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;

public class SearchableActivity extends Activity 
{
	private static final int BRACHOT      	= 0;
	private static final int HAAMVEHAAREZ 	= 1;
	private static final int ZMANIM    		= 2;
	private static final int YAMIM    		= 3;
	private static final int KASHRUT 		= 4;
	private static final int LIKUTIM_A 		= 5;
	private static final int LIKUTIM_B 		= 6;
	private static final int MOADIM    		= 7;
	private static final int MISHPACHA   	= 8;
	private static final int SUCOT			= 9;
	private static final int PESACH			= 10;
	private static final int SHVIIT			= 11;
	private static final int SHABAT			= 12;
	private static final int SIMCHAT		= 13;
	private static final int TEFILA			= 14;
	private static final int TEFILAT_NASHIM	= 15;
	private static final int HAR_BRACHOT    = 16;
	private static final int HAR_YAMIM      = 17;
	private static final int HAR_MOADIM     = 18;
	private static final int HAR_SUCOT      = 19;
	private static final int HAR_SHABAT     = 20;
	private static final int HAR_SIMCHAT    = 21;
	private static final int BOOKS_NUMBER	= 22;

	/*							0	1	2	3	4	5	6	7	8	9  10  11  12  13  14  15  16  17  18 19  20  21  */
	public int[] lastChapter = {17, 11, 17, 10, 19, 11, 16, 13, 10, 8, 16, 11, 30, 10, 26, 24, 17, 10, 12, 8, 30, 10};

	String[][] chaptersFiles = new String[BOOKS_NUMBER][31];
	String[][] chaptersNames = new String[BOOKS_NUMBER][31];
	public List<String> listBookLocation = new ArrayList<String>();
	public List<String> listStrAnchor = new ArrayList<String>();
	public ListView searchListView = null;
	public String query;
	public static final String PREFS_NAME = "MyPrefsFile";
	public String sectionsForToast = null;
	public int i = 0;
	public String hebCharacter = "אבגדהוזחטיכלמנסעפצקרשתםןץףך -'\"";
	public boolean validQuery = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_searchable);
		final SharedPreferences mPrefs;
		mPrefs = getSharedPreferences(PREFS_NAME, 0);

		// Get the intent, verify the action and get the query
		Intent intent = getIntent();
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) 
		{
			query = intent.getStringExtra(SearchManager.QUERY);
			//query = "ו";
			for (i=0; i<query.length(); i++)
			{
				validQuery = hebCharacter.contains(query.substring(i, i+1));
				if(validQuery == false)
				{
					break;
				}
			}

			if(validQuery == true)		
			{
				searchListView = (ListView) findViewById(R.id.list);
				fillChaptersFiles();
				fillChaptersNames();
				doMySearch();
				showResults();

				searchListView.setOnItemClickListener(new OnItemClickListener() 
				{
					boolean cameFromSearch = false;
					String searchPosition = null;
					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position, long id) 
					{
						try
						{
							Class ourClass = Class.forName("com.rafraph.pnineyHalachaHashalem.textMain");
							Intent ourIntent = new Intent(SearchableActivity.this, ourClass);

							searchPosition = listStrAnchor.get(position-1);
							if (searchPosition.contains("simchat_2.html") == false || mPrefs.getInt("SimchatMailPswd", 0) == 1)
							{
								cameFromSearch = true;

								ourIntent.putExtra("cameFromSearch", cameFromSearch);
								ourIntent.putExtra("searchPosition", searchPosition);
								ourIntent.putExtra("query", query);
								sectionsForToast = listBookLocation.get(position - 1);
								if (sectionsForToast.indexOf("הערות:") != -1) {
									sectionsForToast = sectionsForToast.substring(sectionsForToast.indexOf("הערות: ") + 7, sectionsForToast.indexOf(")"));
								} else {
									sectionsForToast = "";
								}
								ourIntent.putExtra("sectionsForToast", sectionsForToast);

								startActivity(ourIntent);
							}
							else
							{
								Toast.makeText(getApplicationContext(), "פרק ב דורש סיסמה, לחץ עליו דרך התוכן הראשי לצורך קבלת סיסמה", Toast.LENGTH_SHORT).show();
							}
						}
						catch (ClassNotFoundException e)
						{
							e.printStackTrace();
						}  
					}
				});
			}
			else
			{
				final Context context = this;
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
						context);

				// set title
				alertDialogBuilder.setTitle("חיפוש לא חוקי");

				// set dialog message
				alertDialogBuilder
				.setMessage("הסימן "+query.substring(i, i+1)+" אינו חוקי")
				.setCancelable(false)
				.setPositiveButton("חזור",new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int id) {
						// if this button is clicked, close current activity
						SearchableActivity.this.finish();
					}
				});

				// create alert dialog
				AlertDialog alertDialog = alertDialogBuilder.create();

				// show it
				alertDialog.show();
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.searchable, menu);
		return true;
	}

	public boolean doMySearch()
	{
		InputStream is;
		int size, i, j, index, index_anchor_start, index_anchor_end, anchorId=0, lastanchorId=0, globalCounter=0, chapterCounter=0, noteIndex = 0;
		byte[] buffer;
		String strText = null, strAnchor=null, section=null, sections=null; 
		String prefixAnchor="<a name=" ;

		for(i=0; i<BOOKS_NUMBER; i++)
		{			
			for(j=1; j<=lastChapter[i]; j++)//starts from 1 since I don't need to search in "tochen" files
			{
				try 
				{
					chapterCounter = 0;
					sections="";
					is = getAssets().open(chaptersFiles[i][j]);
					size = is.available(); 
					buffer = new byte[size];
					is.read(buffer);
					is.close();
					strText  = new String(buffer);

					index = 0;
					index_anchor_start = 0;
					index_anchor_end = 0;
					noteIndex = strText.indexOf("<div style=\"display:none;\">", 0);
					while(index != (-1))
					{
						//System.out.println("i="+i+" j="+j+" chapterCounter="+chapterCounter);/*for checking crash with searching*/
						index = strText.indexOf(query, index+1);
						if(index != (-1))
						{
							if((noteIndex != -1) && (noteIndex < index))/*find in note*/
							{
								index_anchor_end = strText.lastIndexOf("</a>", index);
								index_anchor_end = strText.lastIndexOf("]", index_anchor_end);
								index_anchor_start = strText.lastIndexOf("[", index_anchor_end) + 1;
								strAnchor = strText.substring (index_anchor_start, index_anchor_end);
								anchorId = Integer.parseInt(strAnchor);//convert the anchor ID from string to int
								section = strAnchor;
								if (sections.indexOf("הערות") == -1)//if this is the first find in note make lastanchorId = -1. otherwise don't do it to prevent mentioning of the same note 
								{
									lastanchorId = -1;//to separate the anchor ID if the main text and the notes
									if (sections.compareTo("") == 0)
										section =  "הערות: " + strAnchor;
									else
										section =  " הערות: " + strAnchor;
								}
							}
							else
							{
								index_anchor_start = strText.lastIndexOf(prefixAnchor, index);
								index_anchor_start += prefixAnchor.length()+1;
								index_anchor_end = strText.indexOf("\"", index_anchor_start);
								strAnchor = strText.substring (index_anchor_start, index_anchor_end);
								anchorId = Integer.parseInt(strAnchor);//convert the anchor ID from string to int
								section = convertAnchorIdToSection(anchorId);
							}

							if(chapterCounter==0)/*the first is the link*/
							{
								sections += section;
								if(noteIndex < index)/*find in note*/
									listStrAnchor.add("file:///android_asset/" + chaptersFiles[i][j]+":"+strAnchor);/*if all the results are in notes so the link will be to the first note*/
								else
									listStrAnchor.add("file:///android_asset/" + chaptersFiles[i][j] + "#" + anchorId);
							}

							else if(lastanchorId != anchorId)
							{
								sections += ","+section;
							}
							globalCounter++;
							chapterCounter++;
							lastanchorId = anchorId;
						}
					}
					if(chapterCounter > 0)
					{
						listBookLocation.add("["+chapterCounter+"] "+chaptersNames[i][j]+ " (" + sections+ ")");/*only one item in the list per chapter*/
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}				
			}			
		}
		TextView textView = new TextView(this);
		textView.setText(query + ": נמצאו "+globalCounter+" תוצאות");
		textView.setTextSize(30);
		searchListView.addHeaderView(textView);

		return true;
	}

	public void showResults()
	{
		ArrayAdapter  adapter;
		adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, listBookLocation);
		searchListView.setAdapter(adapter);
	}

	public String convertAnchorIdToSection(int Id)
	{
		switch (Id)
		{
		case 98:
		case 99:
		case 100:
		case 0:
			return "כותרת";
		case 101:
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

	private void fillChaptersFiles()/*list of all assets*/
	{
		/*BRACHOT*/
		chaptersFiles[BRACHOT][0] = "brachot_tochen.html";
		chaptersFiles[BRACHOT][1] = "brachot_1.html";
		chaptersFiles[BRACHOT][2] = "brachot_2.html";
		chaptersFiles[BRACHOT][3] = "brachot_3.html";
		chaptersFiles[BRACHOT][4] = "brachot_4.html";
		chaptersFiles[BRACHOT][5] = "brachot_5.html";
		chaptersFiles[BRACHOT][6] = "brachot_6.html";
		chaptersFiles[BRACHOT][7] = "brachot_7.html";
		chaptersFiles[BRACHOT][8] = "brachot_8.html";
		chaptersFiles[BRACHOT][9] = "brachot_9.html";
		chaptersFiles[BRACHOT][10] = "brachot_10.html";
		chaptersFiles[BRACHOT][11] = "brachot_11.html";
		chaptersFiles[BRACHOT][12] = "brachot_12.html";
		chaptersFiles[BRACHOT][13] = "brachot_13.html";
		chaptersFiles[BRACHOT][14] = "brachot_14.html";
		chaptersFiles[BRACHOT][15] = "brachot_15.html";
		chaptersFiles[BRACHOT][16] = "brachot_16.html";
		chaptersFiles[BRACHOT][17] = "brachot_17.html";
		/*HAAMVEHAAREZ*/
		chaptersFiles[HAAMVEHAAREZ][0] = "haamvehaarez_tochen.html";
		chaptersFiles[HAAMVEHAAREZ][1] = "haamvehaarez_1.html";
		chaptersFiles[HAAMVEHAAREZ][2] = "haamvehaarez_2.html";
		chaptersFiles[HAAMVEHAAREZ][3] = "haamvehaarez_3.html";
		chaptersFiles[HAAMVEHAAREZ][4] = "haamvehaarez_4.html";
		chaptersFiles[HAAMVEHAAREZ][5] = "haamvehaarez_5.html";
		chaptersFiles[HAAMVEHAAREZ][6] = "haamvehaarez_6.html";
		chaptersFiles[HAAMVEHAAREZ][7] = "haamvehaarez_7.html";
		chaptersFiles[HAAMVEHAAREZ][8] = "haamvehaarez_8.html";
		chaptersFiles[HAAMVEHAAREZ][9] = "haamvehaarez_9.html";
		chaptersFiles[HAAMVEHAAREZ][10] = "haamvehaarez_10.html";
		chaptersFiles[HAAMVEHAAREZ][11] = "haamvehaarez_11.html";
		/*ZMANIM*/
		chaptersFiles[ZMANIM][0] = "zmanim_tochen.html";
		chaptersFiles[ZMANIM][1] = "zmanim_1.html";
		chaptersFiles[ZMANIM][2] = "zmanim_2.html";
		chaptersFiles[ZMANIM][3] = "zmanim_3.html";
		chaptersFiles[ZMANIM][4] = "zmanim_4.html";
		chaptersFiles[ZMANIM][5] = "zmanim_5.html";
		chaptersFiles[ZMANIM][6] = "zmanim_6.html";
		chaptersFiles[ZMANIM][7] = "zmanim_7.html";
		chaptersFiles[ZMANIM][8] = "zmanim_8.html";
		chaptersFiles[ZMANIM][9] = "zmanim_9.html";
		chaptersFiles[ZMANIM][10] = "zmanim_10.html";
		chaptersFiles[ZMANIM][11] = "zmanim_11.html";
		chaptersFiles[ZMANIM][12] = "zmanim_12.html";
		chaptersFiles[ZMANIM][13] = "zmanim_13.html";
		chaptersFiles[ZMANIM][14] = "zmanim_14.html";
		chaptersFiles[ZMANIM][15] = "zmanim_15.html";
		chaptersFiles[ZMANIM][16] = "zmanim_16.html";
		chaptersFiles[ZMANIM][17] = "zmanim_17.html";
		/*YAMIM*/
		chaptersFiles[YAMIM][0] = "yamim_tochen.html";
		chaptersFiles[YAMIM][1] = "yamim_1.html";
		chaptersFiles[YAMIM][2] = "yamim_2.html";
		chaptersFiles[YAMIM][3] = "yamim_3.html";
		chaptersFiles[YAMIM][4] = "yamim_4.html";
		chaptersFiles[YAMIM][5] = "yamim_5.html";
		chaptersFiles[YAMIM][6] = "yamim_6.html";
		chaptersFiles[YAMIM][7] = "yamim_7.html";
		chaptersFiles[YAMIM][8] = "yamim_8.html";
		chaptersFiles[YAMIM][9] = "yamim_9.html";
		chaptersFiles[YAMIM][10] = "yamim_10.html";
		/*KASHRUT*/
		chaptersFiles[KASHRUT][0] = "kashrut_tochen.html";
		chaptersFiles[KASHRUT][1] = "kashrut_1.html";
		chaptersFiles[KASHRUT][2] = "kashrut_2.html";
		chaptersFiles[KASHRUT][3] = "kashrut_3.html";
		chaptersFiles[KASHRUT][4] = "kashrut_4.html";
		chaptersFiles[KASHRUT][5] = "kashrut_5.html";
		chaptersFiles[KASHRUT][6] = "kashrut_6.html";
		chaptersFiles[KASHRUT][7] = "kashrut_7.html";
		chaptersFiles[KASHRUT][8] = "kashrut_8.html";
		chaptersFiles[KASHRUT][9] = "kashrut_9.html";
		chaptersFiles[KASHRUT][10] = "kashrut_10.html";
		chaptersFiles[KASHRUT][11] = "kashrut_11.html";
		chaptersFiles[KASHRUT][12] = "kashrut_12.html";
		chaptersFiles[KASHRUT][13] = "kashrut_13.html";
		chaptersFiles[KASHRUT][14] = "kashrut_14.html";
		chaptersFiles[KASHRUT][15] = "kashrut_15.html";
		chaptersFiles[KASHRUT][16] = "kashrut_16.html";
		chaptersFiles[KASHRUT][17] = "kashrut_17.html";
		chaptersFiles[KASHRUT][18] = "kashrut_18.html";
		chaptersFiles[KASHRUT][19] = "kashrut_19.html";
		/*LIKUTIM_A*/
		chaptersFiles[LIKUTIM_A][0] = "likutim_a_tochen.html";
		chaptersFiles[LIKUTIM_A][1] = "likutim_a_1.html";
		chaptersFiles[LIKUTIM_A][2] = "likutim_a_2.html";
		chaptersFiles[LIKUTIM_A][3] = "likutim_a_3.html";
		chaptersFiles[LIKUTIM_A][4] = "likutim_a_4.html";
		chaptersFiles[LIKUTIM_A][5] = "likutim_a_5.html";
		chaptersFiles[LIKUTIM_A][6] = "likutim_a_6.html";
		chaptersFiles[LIKUTIM_A][7] = "likutim_a_7.html";
		chaptersFiles[LIKUTIM_A][8] = "likutim_a_8.html";
		chaptersFiles[LIKUTIM_A][9] = "likutim_a_9.html";
		chaptersFiles[LIKUTIM_A][10] = "likutim_a_10.html";
		chaptersFiles[LIKUTIM_A][11] = "likutim_a_11.html";
		/*LIKUTIM_B*/
		chaptersFiles[LIKUTIM_B][0] = "likutim_b_tochen.html";
		chaptersFiles[LIKUTIM_B][1] = "likutim_b_1.html";
		chaptersFiles[LIKUTIM_B][2] = "likutim_b_2.html";
		chaptersFiles[LIKUTIM_B][3] = "likutim_b_3.html";
		chaptersFiles[LIKUTIM_B][4] = "likutim_b_4.html";
		chaptersFiles[LIKUTIM_B][5] = "likutim_b_5.html";
		chaptersFiles[LIKUTIM_B][6] = "likutim_b_6.html";
		chaptersFiles[LIKUTIM_B][7] = "likutim_b_7.html";
		chaptersFiles[LIKUTIM_B][8] = "likutim_b_8.html";
		chaptersFiles[LIKUTIM_B][9] = "likutim_b_9.html";
		chaptersFiles[LIKUTIM_B][10] = "likutim_b_10.html";
		chaptersFiles[LIKUTIM_B][11] = "likutim_b_11.html";
		chaptersFiles[LIKUTIM_B][12] = "likutim_b_12.html";
		chaptersFiles[LIKUTIM_B][13] = "likutim_b_13.html";
		chaptersFiles[LIKUTIM_B][14] = "likutim_b_14.html";
		chaptersFiles[LIKUTIM_B][15] = "likutim_b_15.html";
		chaptersFiles[LIKUTIM_B][16] = "likutim_b_16.html";
		chaptersFiles[LIKUTIM_B][17] = "likutim_b_17.html";
		/*MOADIM*/
		chaptersFiles[MOADIM][0] = "moadim_tochen.html";
		chaptersFiles[MOADIM][1] = "moadim_1.html";
		chaptersFiles[MOADIM][2] = "moadim_2.html";
		chaptersFiles[MOADIM][3] = "moadim_3.html";
		chaptersFiles[MOADIM][4] = "moadim_4.html";
		chaptersFiles[MOADIM][5] = "moadim_5.html";
		chaptersFiles[MOADIM][6] = "moadim_6.html";
		chaptersFiles[MOADIM][7] = "moadim_7.html";
		chaptersFiles[MOADIM][8] = "moadim_8.html";
		chaptersFiles[MOADIM][9] = "moadim_9.html";
		chaptersFiles[MOADIM][10] = "moadim_10.html";
		chaptersFiles[MOADIM][11] = "moadim_11.html";
		chaptersFiles[MOADIM][12] = "moadim_12.html";
		chaptersFiles[MOADIM][13] = "moadim_13.html";
		/*MISHPACHA*/
		chaptersFiles[MISHPACHA][0] = "mishpacha_tochen.html";
		chaptersFiles[MISHPACHA][1] = "mishpacha_1.html";
		chaptersFiles[MISHPACHA][2] = "mishpacha_2.html";
		chaptersFiles[MISHPACHA][3] = "mishpacha_3.html";
		chaptersFiles[MISHPACHA][4] = "mishpacha_4.html";
		chaptersFiles[MISHPACHA][5] = "mishpacha_5.html";
		chaptersFiles[MISHPACHA][6] = "mishpacha_6.html";
		chaptersFiles[MISHPACHA][7] = "mishpacha_7.html";
		chaptersFiles[MISHPACHA][8] = "mishpacha_8.html";
		chaptersFiles[MISHPACHA][9] = "mishpacha_9.html";
		chaptersFiles[MISHPACHA][10] = "mishpacha_10.html";
		/*SUCOT*/
		chaptersFiles[SUCOT][0] = "sucot_tochen.html";
		chaptersFiles[SUCOT][1] = "sucot_1.html";
		chaptersFiles[SUCOT][2] = "sucot_2.html";
		chaptersFiles[SUCOT][3] = "sucot_3.html";
		chaptersFiles[SUCOT][4] = "sucot_4.html";
		chaptersFiles[SUCOT][5] = "sucot_5.html";
		chaptersFiles[SUCOT][6] = "sucot_6.html";
		chaptersFiles[SUCOT][7] = "sucot_7.html";
		chaptersFiles[SUCOT][8] = "sucot_8.html";
		/*PESACH*/
		chaptersFiles[PESACH][0] = "pesach_tochen.html";
		chaptersFiles[PESACH][1] = "pesach_1.html";
		chaptersFiles[PESACH][2] = "pesach_2.html";
		chaptersFiles[PESACH][3] = "pesach_3.html";
		chaptersFiles[PESACH][4] = "pesach_4.html";
		chaptersFiles[PESACH][5] = "pesach_5.html";
		chaptersFiles[PESACH][6] = "pesach_6.html";
		chaptersFiles[PESACH][7] = "pesach_7.html";
		chaptersFiles[PESACH][8] = "pesach_8.html";
		chaptersFiles[PESACH][9] = "pesach_9.html";
		chaptersFiles[PESACH][10] = "pesach_10.html";
		chaptersFiles[PESACH][11] = "pesach_11.html";
		chaptersFiles[PESACH][12] = "pesach_12.html";
		chaptersFiles[PESACH][13] = "pesach_13.html";
		chaptersFiles[PESACH][14] = "pesach_14.html";
		chaptersFiles[PESACH][15] = "pesach_15.html";
		chaptersFiles[PESACH][16] = "pesach_16.html";
		/*SHVIIT*/
		chaptersFiles[SHVIIT][0] = "shviit_tochen.html";
		chaptersFiles[SHVIIT][1] = "shviit_1.html";
		chaptersFiles[SHVIIT][2] = "shviit_2.html";
		chaptersFiles[SHVIIT][3] = "shviit_3.html";
		chaptersFiles[SHVIIT][4] = "shviit_4.html";
		chaptersFiles[SHVIIT][5] = "shviit_5.html";
		chaptersFiles[SHVIIT][6] = "shviit_6.html";
		chaptersFiles[SHVIIT][7] = "shviit_7.html";
		chaptersFiles[SHVIIT][8] = "shviit_8.html";
		chaptersFiles[SHVIIT][9] = "shviit_9.html";
		chaptersFiles[SHVIIT][10] = "shviit_10.html";
		chaptersFiles[SHVIIT][11] = "shviit_11.html";
		/*SHABAT*/
		chaptersFiles[SHABAT][0] = "shabat_tochen.html";
		chaptersFiles[SHABAT][1] = "shabat_1.html";
		chaptersFiles[SHABAT][2] = "shabat_2.html";
		chaptersFiles[SHABAT][3] = "shabat_3.html";
		chaptersFiles[SHABAT][4] = "shabat_4.html";
		chaptersFiles[SHABAT][5] = "shabat_5.html";
		chaptersFiles[SHABAT][6] = "shabat_6.html";
		chaptersFiles[SHABAT][7] = "shabat_7.html";
		chaptersFiles[SHABAT][8] = "shabat_8.html";
		chaptersFiles[SHABAT][9] = "shabat_9.html";
		chaptersFiles[SHABAT][10] = "shabat_10.html";
		chaptersFiles[SHABAT][11] = "shabat_11.html";
		chaptersFiles[SHABAT][12] = "shabat_12.html";
		chaptersFiles[SHABAT][13] = "shabat_13.html";
		chaptersFiles[SHABAT][14] = "shabat_14.html";
		chaptersFiles[SHABAT][15] = "shabat_15.html";
		chaptersFiles[SHABAT][16] = "shabat_16.html";
		chaptersFiles[SHABAT][17] = "shabat_17.html";
		chaptersFiles[SHABAT][18] = "shabat_18.html";
		chaptersFiles[SHABAT][19] = "shabat_19.html";
		chaptersFiles[SHABAT][20] = "shabat_20.html";
		chaptersFiles[SHABAT][21] = "shabat_21.html";
		chaptersFiles[SHABAT][22] = "shabat_22.html";
		chaptersFiles[SHABAT][23] = "shabat_23.html";
		chaptersFiles[SHABAT][24] = "shabat_24.html";
		chaptersFiles[SHABAT][25] = "shabat_25.html";
		chaptersFiles[SHABAT][26] = "shabat_26.html";
		chaptersFiles[SHABAT][27] = "shabat_27.html";
		chaptersFiles[SHABAT][28] = "shabat_28.html";
		chaptersFiles[SHABAT][29] = "shabat_29.html";
		chaptersFiles[SHABAT][30] = "shabat_30.html";
		/*SIMCHAT*/
		chaptersFiles[SIMCHAT][0] = "simchat_tochen.html";
		chaptersFiles[SIMCHAT][1] = "simchat_1.html";
		chaptersFiles[SIMCHAT][2] = "simchat_2.html";
		chaptersFiles[SIMCHAT][3] = "simchat_3.html";
		chaptersFiles[SIMCHAT][4] = "simchat_4.html";
		chaptersFiles[SIMCHAT][5] = "simchat_5.html";
		chaptersFiles[SIMCHAT][6] = "simchat_6.html";
		chaptersFiles[SIMCHAT][7] = "simchat_7.html";
		chaptersFiles[SIMCHAT][8] = "simchat_8.html";
		chaptersFiles[SIMCHAT][9] = "simchat_9.html";
		chaptersFiles[SIMCHAT][10] = "simchat_10.html";
		/*TEFILA*/
		chaptersFiles[TEFILA][0] = "tefila_tochen.html";
		chaptersFiles[TEFILA][1] = "tefila_1.html";
		chaptersFiles[TEFILA][2] = "tefila_2.html";
		chaptersFiles[TEFILA][3] = "tefila_3.html";
		chaptersFiles[TEFILA][4] = "tefila_4.html";
		chaptersFiles[TEFILA][5] = "tefila_5.html";
		chaptersFiles[TEFILA][6] = "tefila_6.html";
		chaptersFiles[TEFILA][7] = "tefila_7.html";
		chaptersFiles[TEFILA][8] = "tefila_8.html";
		chaptersFiles[TEFILA][9] = "tefila_9.html";
		chaptersFiles[TEFILA][10] = "tefila_10.html";
		chaptersFiles[TEFILA][11] = "tefila_11.html";
		chaptersFiles[TEFILA][12] = "tefila_12.html";
		chaptersFiles[TEFILA][13] = "tefila_13.html";
		chaptersFiles[TEFILA][14] = "tefila_14.html";
		chaptersFiles[TEFILA][15] = "tefila_15.html";
		chaptersFiles[TEFILA][16] = "tefila_16.html";
		chaptersFiles[TEFILA][17] = "tefila_17.html";
		chaptersFiles[TEFILA][18] = "tefila_18.html";
		chaptersFiles[TEFILA][19] = "tefila_19.html";
		chaptersFiles[TEFILA][20] = "tefila_20.html";
		chaptersFiles[TEFILA][21] = "tefila_21.html";
		chaptersFiles[TEFILA][22] = "tefila_22.html";
		chaptersFiles[TEFILA][23] = "tefila_23.html";
		chaptersFiles[TEFILA][24] = "tefila_24.html";
		chaptersFiles[TEFILA][25] = "tefila_25.html";
		chaptersFiles[TEFILA][26] = "tefila_26.html";
		/*TEFILAT_NASHIM*/
		chaptersFiles[TEFILAT_NASHIM][0] = "tefilat_nashim_tochen.html";
		chaptersFiles[TEFILAT_NASHIM][1] = "tefilat_nashim_1.html";
		chaptersFiles[TEFILAT_NASHIM][2] = "tefilat_nashim_2.html";
		chaptersFiles[TEFILAT_NASHIM][3] = "tefilat_nashim_3.html";
		chaptersFiles[TEFILAT_NASHIM][4] = "tefilat_nashim_4.html";
		chaptersFiles[TEFILAT_NASHIM][5] = "tefilat_nashim_5.html";
		chaptersFiles[TEFILAT_NASHIM][6] = "tefilat_nashim_6.html";
		chaptersFiles[TEFILAT_NASHIM][7] = "tefilat_nashim_7.html";
		chaptersFiles[TEFILAT_NASHIM][8] = "tefilat_nashim_8.html";
		chaptersFiles[TEFILAT_NASHIM][9] = "tefilat_nashim_9.html";
		chaptersFiles[TEFILAT_NASHIM][10] = "tefilat_nashim_10.html";
		chaptersFiles[TEFILAT_NASHIM][11] = "tefilat_nashim_11.html";
		chaptersFiles[TEFILAT_NASHIM][12] = "tefilat_nashim_12.html";
		chaptersFiles[TEFILAT_NASHIM][13] = "tefilat_nashim_13.html";
		chaptersFiles[TEFILAT_NASHIM][14] = "tefilat_nashim_14.html";
		chaptersFiles[TEFILAT_NASHIM][15] = "tefilat_nashim_15.html";
		chaptersFiles[TEFILAT_NASHIM][16] = "tefilat_nashim_16.html";
		chaptersFiles[TEFILAT_NASHIM][17] = "tefilat_nashim_17.html";
		chaptersFiles[TEFILAT_NASHIM][18] = "tefilat_nashim_18.html";
		chaptersFiles[TEFILAT_NASHIM][19] = "tefilat_nashim_19.html";
		chaptersFiles[TEFILAT_NASHIM][20] = "tefilat_nashim_20.html";
		chaptersFiles[TEFILAT_NASHIM][21] = "tefilat_nashim_21.html";
		chaptersFiles[TEFILAT_NASHIM][22] = "tefilat_nashim_22.html";
		chaptersFiles[TEFILAT_NASHIM][23] = "tefilat_nashim_23.html";
		chaptersFiles[TEFILAT_NASHIM][24] = "tefilat_nashim_24.html";
		/*HAR_BRACHOT*/
		chaptersFiles[HAR_BRACHOT][0] = "har_brachot_tochen.html";
		chaptersFiles[HAR_BRACHOT][1] = "har_brachot_1.html";
		chaptersFiles[HAR_BRACHOT][2] = "har_brachot_2.html";
		chaptersFiles[HAR_BRACHOT][3] = "har_brachot_3.html";
		chaptersFiles[HAR_BRACHOT][4] = "har_brachot_4.html";
		chaptersFiles[HAR_BRACHOT][5] = "har_brachot_5.html";
		chaptersFiles[HAR_BRACHOT][6] = "har_brachot_6.html";
		chaptersFiles[HAR_BRACHOT][7] = "har_brachot_7.html";
		chaptersFiles[HAR_BRACHOT][8] = "har_brachot_8.html";
		chaptersFiles[HAR_BRACHOT][9] = "har_brachot_9.html";
		chaptersFiles[HAR_BRACHOT][10] = "har_brachot_10.html";
		chaptersFiles[HAR_BRACHOT][11] = "har_brachot_11.html";
		chaptersFiles[HAR_BRACHOT][12] = "har_brachot_12.html";
		chaptersFiles[HAR_BRACHOT][13] = "har_brachot_13.html";
		chaptersFiles[HAR_BRACHOT][14] = "har_brachot_14.html";
		chaptersFiles[HAR_BRACHOT][15] = "har_brachot_15.html";
		chaptersFiles[HAR_BRACHOT][16] = "har_brachot_16.html";
		chaptersFiles[HAR_BRACHOT][17] = "har_brachot_17.html";
		/*HAR_YAMIM*/
		chaptersFiles[HAR_YAMIM][0] = "har_yamim_tochen.html";
		chaptersFiles[HAR_YAMIM][1] = "har_yamim_1.html";
		chaptersFiles[HAR_YAMIM][2] = "har_yamim_2.html";
		chaptersFiles[HAR_YAMIM][3] = "har_yamim_3.html";
		chaptersFiles[HAR_YAMIM][4] = "har_yamim_4.html";
		chaptersFiles[HAR_YAMIM][5] = "har_yamim_5.html";
		chaptersFiles[HAR_YAMIM][6] = "har_yamim_6.html";
		chaptersFiles[HAR_YAMIM][7] = "har_yamim_7.html";
		chaptersFiles[HAR_YAMIM][8] = "har_yamim_8.html";
		chaptersFiles[HAR_YAMIM][9] = "har_yamim_9.html";
		chaptersFiles[HAR_YAMIM][10] = "har_yamim_10.html";
		/*HAR_MOADIM*/
		chaptersFiles[HAR_MOADIM][0] = "har_moadim_tochen.html";
		chaptersFiles[HAR_MOADIM][1] = "har_moadim_1.html";
		chaptersFiles[HAR_MOADIM][2] = "har_moadim_2.html";
		chaptersFiles[HAR_MOADIM][3] = "har_moadim_3.html";
		chaptersFiles[HAR_MOADIM][4] = "har_moadim_4.html";
		chaptersFiles[HAR_MOADIM][5] = "har_moadim_5.html";
		chaptersFiles[HAR_MOADIM][6] = "har_moadim_6.html";
		chaptersFiles[HAR_MOADIM][7] = "har_moadim_7.html";
		chaptersFiles[HAR_MOADIM][8] = "har_moadim_8.html";
		//chaptersFiles[HAR_MOADIM][9] = "har_moadim_9.html"; //currently there is no chapter 9
		chaptersFiles[HAR_MOADIM][9] = "har_moadim_10.html";
		chaptersFiles[HAR_MOADIM][10] = "har_moadim_11.html";
		chaptersFiles[HAR_MOADIM][11] = "har_moadim_12.html";
		chaptersFiles[HAR_MOADIM][12] = "har_moadim_13.html";
		/*HAR_SUCOT*/
		chaptersFiles[HAR_SUCOT][0] = "sucot_tochen.html";
		chaptersFiles[HAR_SUCOT][1] = "har_sucot_1.html";
		chaptersFiles[HAR_SUCOT][2] = "har_sucot_2.html";
		chaptersFiles[HAR_SUCOT][3] = "har_sucot_3.html";
		chaptersFiles[HAR_SUCOT][4] = "har_sucot_4.html";
		chaptersFiles[HAR_SUCOT][5] = "har_sucot_5.html";
		chaptersFiles[HAR_SUCOT][6] = "har_sucot_6.html";
		chaptersFiles[HAR_SUCOT][7] = "har_sucot_7.html";
		chaptersFiles[HAR_SUCOT][8] = "har_sucot_8.html";
		/*HAR_SHABAT*/
		chaptersFiles[HAR_SHABAT][0] = "har_shabat_tochen.html";
		chaptersFiles[HAR_SHABAT][1] = "har_shabat_1.html";
		chaptersFiles[HAR_SHABAT][2] = "har_shabat_2.html";
		chaptersFiles[HAR_SHABAT][3] = "har_shabat_3.html";
		chaptersFiles[HAR_SHABAT][4] = "har_shabat_4.html";
		chaptersFiles[HAR_SHABAT][5] = "har_shabat_5.html";
		chaptersFiles[HAR_SHABAT][6] = "har_shabat_6.html";
		chaptersFiles[HAR_SHABAT][7] = "har_shabat_7.html";
		chaptersFiles[HAR_SHABAT][8] = "har_shabat_8.html";
		chaptersFiles[HAR_SHABAT][9] = "har_shabat_9.html";
		chaptersFiles[HAR_SHABAT][10] = "har_shabat_10.html";
		chaptersFiles[HAR_SHABAT][11] = "har_shabat_11.html";
		chaptersFiles[HAR_SHABAT][12] = "har_shabat_12.html";
		chaptersFiles[HAR_SHABAT][13] = "har_shabat_13.html";
		chaptersFiles[HAR_SHABAT][14] = "har_shabat_14.html";
		chaptersFiles[HAR_SHABAT][15] = "har_shabat_15.html";
		chaptersFiles[HAR_SHABAT][16] = "har_shabat_16.html";
		chaptersFiles[HAR_SHABAT][17] = "har_shabat_17.html";
		chaptersFiles[HAR_SHABAT][18] = "har_shabat_18.html";
		chaptersFiles[HAR_SHABAT][19] = "har_shabat_19.html";
		chaptersFiles[HAR_SHABAT][20] = "har_shabat_20.html";
		chaptersFiles[HAR_SHABAT][21] = "har_shabat_21.html";
		chaptersFiles[HAR_SHABAT][22] = "har_shabat_22.html";
		chaptersFiles[HAR_SHABAT][23] = "har_shabat_23.html";
		chaptersFiles[HAR_SHABAT][24] = "har_shabat_24.html";
		chaptersFiles[HAR_SHABAT][25] = "har_shabat_25.html";
		chaptersFiles[HAR_SHABAT][26] = "har_shabat_26.html";
		chaptersFiles[HAR_SHABAT][27] = "har_shabat_27.html";
		chaptersFiles[HAR_SHABAT][28] = "har_shabat_28.html";
		chaptersFiles[HAR_SHABAT][29] = "har_shabat_29.html";
		chaptersFiles[HAR_SHABAT][30] = "har_shabat_30.html";
		/*HAR_SIMCHAT*/
		chaptersFiles[HAR_SIMCHAT][0] = "har_simchat_tochen.html";
		chaptersFiles[HAR_SIMCHAT][1] = "har_simchat_1.html";
		chaptersFiles[HAR_SIMCHAT][2] = "har_simchat_2.html";
		chaptersFiles[HAR_SIMCHAT][3] = "har_simchat_3.html";
		chaptersFiles[HAR_SIMCHAT][4] = "har_simchat_4.html";
		chaptersFiles[HAR_SIMCHAT][5] = "har_simchat_5.html";
		chaptersFiles[HAR_SIMCHAT][6] = "har_simchat_6.html";
		chaptersFiles[HAR_SIMCHAT][7] = "har_simchat_7.html";
		chaptersFiles[HAR_SIMCHAT][8] = "har_simchat_8.html";
		chaptersFiles[HAR_SIMCHAT][9] = "har_simchat_9.html";
		chaptersFiles[HAR_SIMCHAT][10] = "har_simchat_10.html";
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
		/*KASHRUT*/
		chaptersNames[KASHRUT][1] = "כשרות א: א - חדש";
		chaptersNames[KASHRUT][2] = "כשרות א: ב - ערלה ורבעי";
		chaptersNames[KASHRUT][3] = "כשרות א: ג - כלאי בהמה ואילן";
		chaptersNames[KASHRUT][4] = "כשרות א: ד - כלאי זרעים";
		chaptersNames[KASHRUT][5] = "כשרות א: ה - כלאי הכרם";
		chaptersNames[KASHRUT][6] = "כשרות א: ו - מתנות עניים";
		chaptersNames[KASHRUT][7] = "כשרות א: ז - תרומות ומעשרות";
		chaptersNames[KASHRUT][8] = "כשרות א: ח - החייב והפטור";
		chaptersNames[KASHRUT][9] = "כשרות א: ט - כללי המצווה";
		chaptersNames[KASHRUT][10] ="כשרות א: י - סדר ההפרשה למעשה";
		chaptersNames[KASHRUT][11] ="כשרות א: יא - חלה";
		chaptersNames[KASHRUT][12] ="כשרות א: יב - מצוות התלויות בארץ";
		chaptersNames[KASHRUT][13] ="כשרות א: יג - עצי פרי ובל תשחית";
		chaptersNames[KASHRUT][14] ="כשרות א: יד - אכילת בשר";
		chaptersNames[KASHRUT][15] ="כשרות א: טו - צער בעלי חיים";
		chaptersNames[KASHRUT][16] ="כשרות א: טז - שילוח הקן";
		chaptersNames[KASHRUT][17] ="כשרות א: יז - כשרות בעלי חיים";
		chaptersNames[KASHRUT][18] ="כשרות א: יח - הלכות שחיטה";
		chaptersNames[KASHRUT][19] ="כשרות א: יט - מתנות כהונה מהחי";
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
		/*MISHPACHA*/
		chaptersNames[MISHPACHA][1] = "משפחה: א - כיבוד הורים";
		chaptersNames[MISHPACHA][2] = "משפחה: ב - מצוות הנישואין";
		chaptersNames[MISHPACHA][3] = "משפחה: ג - שידוכים";
		chaptersNames[MISHPACHA][4] = "משפחה: ד - קידושין וכתובה";
		chaptersNames[MISHPACHA][5] = "משפחה: ה - החתונה ומנהגיה";
		chaptersNames[MISHPACHA][6] = "משפחה: ו - איסורי עריות";
		chaptersNames[MISHPACHA][7] = "משפחה: ז - מהלכות צניעות";
		chaptersNames[MISHPACHA][8] = "משפחה: ח - ברית מילה";
		chaptersNames[MISHPACHA][9] = "משפחה: ט - פדיון הבן";
		chaptersNames[MISHPACHA][10] ="משפחה: י - אבלות";
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

}
