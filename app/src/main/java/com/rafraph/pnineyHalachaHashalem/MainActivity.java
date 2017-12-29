package com.rafraph.pnineyHalachaHashalem;


import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.PopupMenu;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Calendar;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity
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


	private static final int HEBREW	 = 0;
	private static final int ENGLISH = 1;
    private static final int RUSSIAN = 2;
    private static final int SPANISH = 3;
    private static final int FRENCH = 4;
	
	public ExpandableListAdapter listAdapter;
	ExpandableListView expListView;
	LinearLayout LinearLayoutListGroup;
	List<String> listDataHeader;
	HashMap<String, List<String>> listDataChild;
	public static final String PREFS_NAME = "MyPrefsFile";
	static SharedPreferences mPrefs;
	SharedPreferences.Editor shPrefEditor;
	public int BlackBackground=0, SleepScreen=1, SimchatMailPswd=0, MyLanguage = -1;
	public MenuInflater inflater;
	public ActionBar ab;
	public Menu abMenu=null;
	public EditText TextToDecode;
	public Dialog acronymsDialog, newVersionDialog, simchatDialog, languageDialog;
	String acronymsText;
    public int StartInLastLocation = 1;
	public boolean newVersion = false;
	public Context context;
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);  
		
		context = this;
		mPrefs = getSharedPreferences(PREFS_NAME, 0);
		shPrefEditor = mPrefs.edit();
		BlackBackground = mPrefs.getInt("BlackBackground", 0);
		SimchatMailPswd = mPrefs.getInt("SimchatMailPswd", 0);
		StartInLastLocation = mPrefs.getInt("StartInLastLocation", 1);
		MyLanguage = mPrefs.getInt("MyLanguage", -1);


		ab = getSupportActionBar();
		// get the listview
		expListView = (ExpandableListView) findViewById(R.id.lvExp);

		// preparing list data
		prepareListData();

		listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);

		// setting list adapter
		expListView.setAdapter(listAdapter);

		// Listview on child click listener
		expListView.setOnChildClickListener(new OnChildClickListener() 
		{
			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) 
			{
				// TODO Auto-generated method stub
				SimchatMailPswd = mPrefs.getInt("SimchatMailPswd", 0);
				
				if((groupPosition == SIMCHAT || groupPosition == HAR_SIMCHAT) && childPosition == 2 && SimchatMailPswd != 1)
				{
					if(SimchatMailPswd == 0)
						startSimchatDialog();
				}
				
				if(!((groupPosition == SIMCHAT || groupPosition == HAR_SIMCHAT) && childPosition == 2) || (SimchatMailPswd == 1))/*if this is no chapter 2, or the password already inserted*/
				{
					Toast.makeText(getApplicationContext(), listDataHeader.get(groupPosition) + " : " 
							+ listDataChild.get(listDataHeader.get(groupPosition)).get(childPosition), Toast.LENGTH_SHORT).show();
					try
					{
						Class ourClass = Class.forName("com.rafraph.pnineyHalachaHashalem.textMain");
						Intent ourIntent = new Intent(MainActivity.this, ourClass);
						int[] book_chapter = new int[2];
						book_chapter[0] = groupPosition;
						book_chapter[1] = childPosition;
						ourIntent.putExtra("book_chapter", book_chapter);
						startActivity(ourIntent);
					}
					catch (ClassNotFoundException e)
					{
						e.printStackTrace();
					}
				}
				return false;
			}
		});

		/* Choose language*/
		if(MyLanguage == -1)
		{
			languageDialog(context);
		}

		/*display the new features of this version*/
		PackageManager packageManager = context.getPackageManager();
		String packageName = context.getPackageName();
		String version;
		try 
		{
			version = packageManager.getPackageInfo(packageName, 0).versionName;
			
			if(mPrefs.getString("Version", "").equals("2.1") == false)
			{
				newVersion = true;
				shPrefEditor.putString("Version", version);
				shPrefEditor.commit();
				newVersionDialog = new Dialog(context);
				newVersionDialog.setContentView(R.layout.new_version);
				newVersionDialog.setTitle("גרסה " + version);

				Button dialogButtonExit = (Button) newVersionDialog.findViewById(R.id.dialogButtonExit);
				// if button is clicked
				dialogButtonExit.setOnClickListener(new OnClickListener()
				{
					@SuppressLint("NewApi")
					@Override
					public void onClick(View v) 
					{
						newVersionDialog.dismiss();
					}
				});
				newVersionDialog.show();	
			}
		}
		catch (PackageManager.NameNotFoundException e) 
		{
		    e.printStackTrace();
		}
        if(StartInLastLocation == 1 && !(mPrefs.getInt("book", 0) == 0 && mPrefs.getInt("chapter", 0) == 0) && newVersion == false)/*check if book and chapter are 0 so this is the first time the user open the application so don't go to the last location*/
        {
            goToLastLocation();
        }
	}//onCreate


	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		abMenu = menu;
		// Inflate the menu; this adds items to the action bar if it is present.
		inflater = getMenuInflater();

		if(BlackBackground == 1)
		{
			ab.setBackgroundDrawable(new ColorDrawable(Color.BLACK));
			inflater.inflate(R.menu.tochen_actionbar_black, menu);
			ab.setTitle(Html.fromHtml("<font color=\"white\">" + "תוכן" + "</font>"));
			listAdapter.setTextColor(Color.WHITE);//to set the list text color
			expListView.setAdapter(listAdapter);//to set the list text color
		}
		else
		{
			ab.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
			inflater.inflate(R.menu.tochen_actionbar, menu);	
			ab.setTitle(Html.fromHtml("<font color=\"black\">" + "תוכן" + "</font>"));
			listAdapter.setTextColor(Color.BLACK);//to set the list text color
			expListView.setAdapter(listAdapter);//to set the list text color
		}

		return true;
	}//onCreateOptionsMenu

	protected void onResume() 
	{
		// The activity has become visible (it is now "resumed").
		super.onResume();
		BlackBackground = mPrefs.getInt("BlackBackground", 0);
		supportInvalidateOptionsMenu();//This will dump the current menu and call your activity's onCreateOptionsMenu/onPrepareOptionsMenu methods again to rebuild it.
	}//onResume

	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		// TODO Auto-generated method stub
		switch (item.getItemId()) 
		{
		case R.id.action_search:
			onSearchRequested();
			break;
		case R.id.action_bookmarks:
			try
			{
				Class ourClass = Class.forName("com.rafraph.pnineyHalachaHashalem.BookmarkActivity");
				Intent ourIntent = new Intent(MainActivity.this, ourClass);
				startActivity(ourIntent);
			}
			catch (ClassNotFoundException e)
			{
				e.printStackTrace();
			}
			break;
		case R.id.action_place:
			goToLastLocation();
			break;
		case R.id.action_config:
			showPopupMenuSettings(findViewById(R.id.action_config));
			break;
		default:
			break;
		}

		return true;
		//return super.onOptionsItemSelected(item);
	}

	private void showPopupMenuSettings(View v)
	{
		PopupMenu popupMenu = new PopupMenu(MainActivity.this, v);
		//  popupMenu.getMenuInflater().inflate(R.menu.popupmenu, popupMenu.getMenu());

		String configHeaders[] = new String[7];
		if(MyLanguage == ENGLISH) {
			configHeaders[0] = "Settings";
			configHeaders[1] = "About";
			configHeaders[2] = "Feedback";
			configHeaders[3] = "Explanation of search results";
			configHeaders[4] = "Acronyms";
			configHeaders[5] = "Approbations";
			configHeaders[6] = "Language / שפה";
		}
		else if(MyLanguage == RUSSIAN) {
			configHeaders[0] = "Настройки";
			configHeaders[1] = "Около";
			configHeaders[2] = "Обратная связь";
			configHeaders[3] = "Объяснение результатов поиска";
			configHeaders[4] = "Абревиатуры";
			configHeaders[5] = "Апробации";
			configHeaders[6] = "ЯЗЫК / שפה";
		}
		else if(MyLanguage == SPANISH) {
			configHeaders[0] = "Ajustes";
			configHeaders[1] = "Acerca de";
			configHeaders[2] = "Comentarios";
			configHeaders[3] = "Explicacion del resultado de la busqueda";
			configHeaders[4] = "Acronimos";
			configHeaders[5] = "Aprovaciones";
			configHeaders[6] = "Idioma / שפה";
		}
		else if(MyLanguage == FRENCH) {
			configHeaders[0] = "Definitions";
			configHeaders[1] = "A Propos de…";
			configHeaders[2] = "Commentaires";
			configHeaders[3] = "Explication de la recherche";
			configHeaders[4] = "Acronymes";
			configHeaders[5] = "Approbations";
			configHeaders[6] = "Langue / שפה";
		}
		else {/*this is the default*/
			configHeaders[0] = "הגדרות";
			configHeaders[1] = "אודות";
			configHeaders[2] = "משוב";
			configHeaders[3] = "הסבר על החיפוש";
			configHeaders[4] = "ראשי תיבות";
			configHeaders[5] = "הסכמות";
			configHeaders[6] = "Language / שפה";
		}

		popupMenu.getMenu().add(0,0,0,configHeaders[0]);//(int groupId, int itemId, int order, int titleRes)
		popupMenu.getMenu().add(0,1,1,configHeaders[1]);
		popupMenu.getMenu().add(0,2,2,configHeaders[2]);
		popupMenu.getMenu().add(0,3,3,configHeaders[3]);
		popupMenu.getMenu().add(0,4,4,configHeaders[4]);
		popupMenu.getMenu().add(0,5,5,configHeaders[5]);
		popupMenu.getMenu().add(0,6,6,configHeaders[6]);
		
		popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() 
		{
			@Override
			public boolean onMenuItemClick(MenuItem item)
			{
				switch (item.getItemId())
				{
				case 0:/*settings*/
					try
					{
						Class ourClass = Class.forName("com.rafraph.pnineyHalachaHashalem.Settings");
						Intent ourIntent = new Intent(MainActivity.this, ourClass);
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
						Intent ourIntent = new Intent(MainActivity.this, ourClass);
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
						Intent ourIntent = new Intent(MainActivity.this, ourClass);
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
						Intent ourIntent = new Intent(MainActivity.this, ourClass);
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
				case 5:/*hascamot*/
					hascamotDialog();
					break;
				case 6:/*language*/
					languageDialog(context);
					break;
					
				default:
					break;
				}
				return true;
			}
		});

		popupMenu.show();
	}	

	/*Preparing the list data*/
	private void prepareListData() 
	{
		listDataHeader = new ArrayList<String>();
		listDataChild = new HashMap<String, List<String>>();

		// Adding child data
		listDataHeader.add("ברכות");
		listDataHeader.add("העם והארץ");
		listDataHeader.add("זמנים");
		listDataHeader.add("ימים נוראים");
		listDataHeader.add("ליקוטים א");
		listDataHeader.add("ליקוטים ב");
		listDataHeader.add("ליקוטים ג");
		listDataHeader.add("מועדים");
		listDataHeader.add("סוכות");
		listDataHeader.add("פסח");
		listDataHeader.add("שביעית ויובל");
		listDataHeader.add("שבת");
		listDataHeader.add("שמחת הבית וברכתו");
		listDataHeader.add("תפילה");
		listDataHeader.add("תפילת נשים");
		listDataHeader.add("הרחבות ברכות");
		listDataHeader.add("הרחבות ימים נוראים");
		listDataHeader.add("הרחבות מועדים");
		listDataHeader.add("הרחבות סוכות");
		listDataHeader.add("הרחבות שבת");
		listDataHeader.add("הרחבות שמחת הבית וברכתו");
		listDataHeader.add("Tefila");
		listDataHeader.add("Pesaĥ");
		listDataHeader.add("Z’manim");
		listDataHeader.add("Laws of Women’s Prayer");
		listDataHeader.add("Laws of Shabbat");
		listDataHeader.add("La prière d’Israël");


		// Adding child data
		List<String> brachot = new ArrayList<String>();
		brachot.add("תוכן מפורט, מבוא, מפתח");
		brachot.add("א - פתיחה");
		brachot.add("ב - נטילת ידיים לסעודה");
		brachot.add("ג - ברכת המוציא");
		brachot.add("ד - ברכת המזון");
		brachot.add("ה - זימון");
		brachot.add("ו - חמשת מיני דגן");
		brachot.add("ז - ברכת היין");
		brachot.add("ח - ברכת הפירות ושהכל");
		brachot.add("ט - כללי ברכה ראשונה");
		brachot.add("י - ברכה אחרונה");
		brachot.add("יא - עיקר וטפל");
		brachot.add("יב - כללי ברכות");
		brachot.add("יג - דרך ארץ");
		brachot.add("יד - ברכת הריח");
		brachot.add("טו - ברכות הראייה");
		brachot.add("טז - ברכת הגומל");
		brachot.add("יז - ברכות ההודאה והשמחה");

		List<String> haam = new ArrayList<String>();
		haam.add("תוכן מפורט, מבוא");
		haam.add("א - מעלת הארץ");
		haam.add("ב - קודש וחול ביישוב הארץ");
		haam.add("ג - מצוות יישוב הארץ");
		haam.add("ד - מהלכות צבא ומלחמה");
		haam.add("ה - שמירת הארץ");
		haam.add("ו - מהלכות מדינה");
		haam.add("ז - ערבות הדדית");
		haam.add("ח - עבודה עברית");
		haam.add("ט - זכר למקדש");
		haam.add("י - הלכות גרים");
		haam.add("יא - נספח: תשובות מאת הרב גורן ומרבנים נוספים");

		List<String> zmanim = new ArrayList<String>();
		zmanim.add("תוכן מפורט, מבוא");
		zmanim.add("א - ראש חודש");
		zmanim.add("ב - הלכות ספירת העומר");
		zmanim.add("ג - מנהגי אבילות בספירת העומר");
		zmanim.add("ד - יום העצמאות");
		zmanim.add("ה - לג בעומר");
		zmanim.add("ו - ארבעת צומות החורבן");
		zmanim.add("ז - דיני הצומות הקלים");
		zmanim.add("ח - מנהגי שלושת השבועות");
		zmanim.add("ט - ערב תשעה באב");
		zmanim.add("י - הלכות תשעה באב");
		zmanim.add("יא - ימי החנוכה");
		zmanim.add("יב - הדלקת נרות חנוכה");
		zmanim.add("יג - דיני המקום והזמן");
		zmanim.add("יד - חודש אדר");
		zmanim.add("טו - פורים ומקרא מגילה");
		zmanim.add("טז - מצוות השמחה והחסד");
		zmanim.add("יז - דיני פרזים ומוקפים");

		List<String> yamim = new ArrayList<String>();
		yamim.add("תוכן מפורט, מבוא");
		yamim.add("א - הדין השכר והעונש");
		yamim.add("ב - סליחות ותפילות");
		yamim.add("ג - ראש השנה");
		yamim.add("ד - מצוות השופר");
		yamim.add("ה - עשרת ימי תשובה");
		yamim.add("ו - יום הכיפורים");
		yamim.add("ז - הלכות יום הכיפורים");
		yamim.add("ח - דיני התענית");
		yamim.add("ט - שאר עינויים");
		yamim.add("י - עבודת יום הכיפורים");

		List<String> likutimA = new ArrayList<String>();
		likutimA.add("תוכן מפורט, מבוא");
		likutimA.add("א - הלכות תלמוד תורה");
		likutimA.add("ב - החינוך לתורה");
		likutimA.add("ג - הלכות ספר תורה");
		likutimA.add("ד - הלכות קריאת התורה");
		likutimA.add("ה - כבוד ספר תורה ושמות קדושים");
		likutimA.add("ו - הלכות בית כנסת");
		likutimA.add("ז - הלכות כיפה");
		likutimA.add("ח - מהלכות ציצית");
		likutimA.add("ט - תפילין");
		likutimA.add("י - מהלכות מזוזה");
		likutimA.add("יא - הלכות כהנים");
		likutimA.add("יב - תרומות ומעשרות");
		likutimA.add("יג - מתנות מן החי");

		List<String> likutimB = new ArrayList<String>();
		likutimB.add("תוכן מפורט, מבוא");
		likutimB.add("א - בין אדם לחברו");
		likutimB.add("ב - הלכות אמירת אמת");
		likutimB.add("ג - הלכות גניבת דעת");
		likutimB.add("ד - הלכות גניבה");
		likutimB.add("ה - מצוות הלוואה");
		likutimB.add("ו - הלכות צדקה");
		likutimB.add("ז - הכנסת אורחים");
		likutimB.add("ח - הלכות רוצח ומתאבד");
		likutimB.add("ט - הלכות שמירת הנפש");
		likutimB.add("י - נהיגה זהירה ותפילת הדרך");
		likutimB.add("יא - הלכות הצלת נפשות");
		likutimB.add("יב - הלכות ניתוחי מתים");
		likutimB.add("יג - השתלת אברים");
		likutimB.add("יד - הלכות הנוטה למות");
		likutimB.add("טו - ליקוטים");
		likutimB.add("טז - חברה ושליחות");

		List<String> likutimC = new ArrayList<String>();
		likutimC.add("תוכן מפורט, מבוא");
		likutimC.add("א - כיבוד הורים");
		likutimC.add("ב - לקראת נישואין");
		likutimC.add("ג - הלכות נישואין");
		likutimC.add("ד - הלכות החתונה ומנהגיה");
		likutimC.add("ה - איסורי עריות");
		likutimC.add("ו - הלכות צניעות");
		likutimC.add("ז - ברית מילה");
		likutimC.add("ח - פדיון בכורות");
		likutimC.add("ט - צער בעלי חיים");
		likutimC.add("י - מצוות שילוח הקן");
		likutimC.add("יא - כלאיים באילן ובהמה");
		likutimC.add("יב - הלכות שמירת עצי פרי");
		likutimC.add("יג - בל תשחית");
		likutimC.add("יד - הלכות תולעים");
		likutimC.add("טו - הלכות טבילת כלים");
		likutimC.add("טז - ליקוטים בכשרות");

		List<String> moadim = new ArrayList<String>();
		moadim.add("תוכן מפורט, מבוא, מפתח");
		moadim.add("א - פתיחה");
		moadim.add("ב - דיני עשה ביום טוב");
		moadim.add("ג - כללי המלאכות");
		moadim.add("ד - מלאכות המאכלים");
		moadim.add("ה - הבערה כיבוי וחשמל");
		moadim.add("ו - הוצאה ומוקצה");
		moadim.add("ז - מדיני יום טוב");
		moadim.add("ח - עירוב תבשילין");
		moadim.add("ט - יום טוב שני של גלויות");
		moadim.add("י - מצוות חול המועד");
		moadim.add("יא - מלאכת חול המועד");
		moadim.add("יב - היתרי עבודה במועד");
		moadim.add("יג - חג שבועות");

		List<String> sucot = new ArrayList<String>();
		sucot.add("תוכן מפורט, מבוא, מפתח");
		sucot.add("א - חג הסוכות");
		sucot.add("ב - הלכות סוכה");
		sucot.add("ג - ישיבה בסוכה");
		sucot.add("ד - ארבעת המינים");
		sucot.add("ה - נטילת לולב");
		sucot.add("ו - הושענא רבה");
		sucot.add("ז - שמיני עצרת");
		sucot.add("ח - הקהל");

		List<String> pesach = new ArrayList<String>();
		pesach.add("תוכן מפורט, מבוא, מפתח");
		pesach.add("א - משמעות החג");
		pesach.add("ב - כללי איסור חמץ");
		pesach.add("ג - מצוות השבתת חמץ");
		pesach.add("ד - בדיקת חמץ");
		pesach.add("ה - ביטול חמץ וביעורו");
		pesach.add("ו - מכירת חמץ");
		pesach.add("ז - תערובת חמץ");
		pesach.add("ח - מהלכות כשרות לפסח");
		pesach.add("ט - מנהג איסור קטניות");
		pesach.add("י - כללי הגעלת כלים");
		pesach.add("יא - הכשרת המטבח לפסח");
		pesach.add("יב - הלכות מצה");
		pesach.add("יג - הלכות ערב פסח ומנהגיו");
		pesach.add("יד - ערב פסח שחל בשבת");
		pesach.add("טו - ההגדה");
		pesach.add("טז - ליל הסדר");

		List<String> shviit = new ArrayList<String>();
		shviit.add("תוכן מפורט, מבוא, מפתח");
		shviit.add("א - מצוות השביעית");
		shviit.add("ב - מצוות השביתה");
		shviit.add("ג - השמטת הפירות");
		shviit.add("ד - פירות השביעית");
		shviit.add("ה - הזמן המקום והאדם");
		shviit.add("ו - שמיטת כספים");
		shviit.add("ז - היתר המכירה");
		shviit.add("ח - אוצר בית דין");
		shviit.add("ט - קניית פירות בשביעית");
		shviit.add("י - מצוות היובל");
		shviit.add("יא - חזון השביעית");

		List<String> shabat = new ArrayList<String>();
		shabat.add("תוכן מפורט, מבוא, מפתח");
		shabat.add("א - פתיחה");
		shabat.add("ב - הכנות לשבת");
		shabat.add("ג - זמני השבת");
		shabat.add("ד - הדלקת נרות שבת");
		shabat.add("ה - תורה ותפילה בשבת");
		shabat.add("ו - הלכות קידוש");
		shabat.add("ז - סעודות השבת ומלווה מלכה");
		shabat.add("ח - הבדלה ומוצאי שבת");
		shabat.add("ט - כללי המלאכות");
		shabat.add("י - בישול");
		shabat.add("יא - בורר");
		shabat.add("יב - הכנת מאכלים");
		shabat.add("יג - מלאכות הבגד");
		shabat.add("יד - הטיפול בגוף");
		shabat.add("טו - בונה סותר בבית וכלים");
		shabat.add("טז - מבעיר ומכבה");
		shabat.add("יז - חשמל ומכשיריו");
		shabat.add("יח - כותב מוחק וצובע");
		shabat.add("יט - מלאכות שבצומח");
		shabat.add("כ - בעלי חיים");
		shabat.add("כא - הלכות הוצאה");
		shabat.add("כב - צביון השבת");
		shabat.add("כג - מוקצה");
		shabat.add("כד - דיני קטן");
		shabat.add("כה - מלאכת גוי");
		shabat.add("כו - מעשה שבת ולפני עיוור");
		shabat.add("כז - פיקוח נפש וחולה");
		shabat.add("כח - חולה שאינו מסוכן");
		shabat.add("כט - עירובין");
		shabat.add("ל - תחומי שבת");

		List<String> simchat = new ArrayList<String>();
		simchat.add("תוכן מפורט, מבוא, מפתח");
		simchat.add("א - מצוות עונה");
		simchat.add("ב - הלכות עונה");
		simchat.add("ג - קדושה וכוונה");
		simchat.add("ד - שמירת הברית");
		simchat.add("ה - פרו ורבו");
		simchat.add("ו - קשיים ועקרות");
		simchat.add("ז - סריס והשחתה");
		simchat.add("ח - נחמת חשוכי ילדים");
		simchat.add("ט - הפסקת הריון");
		simchat.add("י - האיש והאשה");
				
		List<String> tefila = new ArrayList<String>();
		tefila.add("תוכן מפורט, מבוא, מפתח");
		tefila.add("א - יסודות הלכות תפילה");
		tefila.add("ב - המניין");
		tefila.add("ג - מקום התפילה");
		tefila.add("ד - החזן וקדיש של אבלים");
		tefila.add("ה - הכנות לתפילה");
		tefila.add("ו - הנוסחים ומנהגי העדות");
		tefila.add("ז - השכמת הבוקר");
		tefila.add("ח - נטילת ידיים שחרית");
		tefila.add("ט - ברכות השחר");
		tefila.add("י - ברכת התורה");
		tefila.add("יא - זמן ק\"ש ותפילת שחרית");
		tefila.add("יב - לקראת תפילת שחרית");
		tefila.add("יג - סדר קרבנות");
		tefila.add("יד - פסוקי דזמרה");
		tefila.add("טו - קריאת שמע");
		tefila.add("טז - ברכות קריאת שמע");
		tefila.add("יז - תפילת עמידה");
		tefila.add("יח - טעויות הזכרות ושכחה");
		tefila.add("יט - חזרת הש\"ץ");
		tefila.add("כ - ברכת כהנים");
		tefila.add("כא - נפילת אפיים ותחנונים");
		tefila.add("כב - מדיני קריאת התורה");
		tefila.add("כג - סיום שחרית ודיני קדיש");
		tefila.add("כד - תפילת מנחה");
		tefila.add("כה - תפילת מעריב");
		tefila.add("כו - קריאת שמע על המיטה"); 

		List<String> tefilatNashim = new ArrayList<String>();
		tefilatNashim.add("תוכן מפורט, מבוא, מפתח");
		tefilatNashim.add("א - יסודות הלכות תפילה");
		tefilatNashim.add("ב - מצוות תפילה לנשים");
		tefilatNashim.add("ג - טעמי מצוות הנשים");
		tefilatNashim.add("ד - השכמת הבוקר");
		tefilatNashim.add("ה - נטילת ידיים שחרית");
		tefilatNashim.add("ו - ברכות השחר");
		tefilatNashim.add("ז - ברכות התורה");
		tefilatNashim.add("ח - תפילת שחרית והדינים שלפניה");
		tefilatNashim.add("ט - הכנת הגוף");
		tefilatNashim.add("י - הכנת הנפש והלבוש");
		tefilatNashim.add("יא - מקום התפילה");
		tefilatNashim.add("יב - תפילת עמידה");
		tefilatNashim.add("יג - הזכרת גשמים ובקשתם");
		tefilatNashim.add("יד - כבוד התפילה");
		tefilatNashim.add("טו - קרבנות ופסוקי דזמרה");
		tefilatNashim.add("טז - קריאת שמע וברכותיה");
		tefilatNashim.add("יז - התפילות שלאחר עמידה");
		tefilatNashim.add("יח - מנחה וערבית");
		tefilatNashim.add("יט - קריאת שמע על המיטה");
		tefilatNashim.add("כ - מהלכות התפילה במניין");
		tefilatNashim.add("כא - מהלכות בית הכנסת");
		tefilatNashim.add("כב - תפילה וקידוש בשבת");
		tefilatNashim.add("כג - מהלכות חגים ומועדים");
		tefilatNashim.add("כד - נוסחי התפלה ומנהגי העדות");

		List<String> harchavot_moadim = new ArrayList<String>();
		harchavot_moadim.add("תוכן העניינים, פתח דבר, סוגיות מורחבות");
		harchavot_moadim.add("א - פתיחה");
		harchavot_moadim.add("ב - דיני עשה ביום טוב");
		harchavot_moadim.add("ג - כללי המלאכות");
		harchavot_moadim.add("ד - מלאכות המאכלים");
		harchavot_moadim.add("ה - הבערה כיבוי וחשמל");
		harchavot_moadim.add("ו - הוצאה ומוקצה");
		harchavot_moadim.add("ז - מדיני יום טוב");
		harchavot_moadim.add("ח - עירוב תבשילין");
		harchavot_moadim.add("י - מצוות חול המועד");
		harchavot_moadim.add("יא - מלאכת חול המועד");
		harchavot_moadim.add("יב - היתרי עבודה במועד");
		harchavot_moadim.add("יג - חג השבועות");

		List<String> harchavot_sucot = new ArrayList<String>();
		harchavot_sucot.add("תוכן העניינים, פתח דבר, סוגיות מורחבות");
		harchavot_sucot.add("א -חג הסוכות");
		harchavot_sucot.add("ב - הלכות סוכה");
		harchavot_sucot.add("ג - ישיבה בסוכה");
		harchavot_sucot.add("ד - ארבעת המינים");
		harchavot_sucot.add("ה - נטילת לולב");
		harchavot_sucot.add("ו - הושענא רבה");
		harchavot_sucot.add("ז - שמיני עצרת");
		harchavot_sucot.add("ח - הקהל");

		List<String> harchavot_shabat = new ArrayList<String>();
		harchavot_shabat.add("תוכן העניינים, פתח דבר, סוגיות מורחבות");
		harchavot_shabat.add("א - פתיחה");
		harchavot_shabat.add("ב - הכנות לשבת");
		harchavot_shabat.add("ג - זמני השבת");
		harchavot_shabat.add("ד - הדלקת נרות שבת");
		harchavot_shabat.add("ה - תורה ותפילה בשבת");
		harchavot_shabat.add("ו - הלכות קידוש");
		harchavot_shabat.add("ז - סעודות השבת ומלווה מלכה");
		harchavot_shabat.add("ח - הבדלה ומוצאי שבת");
		harchavot_shabat.add("ט - כללי המלאכות");
		harchavot_shabat.add("י - בישול");
		harchavot_shabat.add("יא - בורר");
		harchavot_shabat.add("יב - הכנת מאכלים");
		harchavot_shabat.add("יג - מלאכות הבגד");
		harchavot_shabat.add("יד - הטיפול בגוף");
		harchavot_shabat.add("טו - בונה סותר בבית וכלים");
		harchavot_shabat.add("טז - מבעיר ומכבה");
		harchavot_shabat.add("יז - חשמל ומכשיריו");
		harchavot_shabat.add("יח - כותב מוחק וצובע");
		harchavot_shabat.add("יט - מלאכות שבצומח");
		harchavot_shabat.add("כ - בעלי חיים");
		harchavot_shabat.add("כא - הלכות הוצאה");
		harchavot_shabat.add("כב - צביון השבת");
		harchavot_shabat.add("כג - מוקצה");
		harchavot_shabat.add("כד - דיני קטן");
		harchavot_shabat.add("כה - מלאכת גוי");
		harchavot_shabat.add("כו - מעשה שבת ולפני עיוור");
		harchavot_shabat.add("כז - פיקוח נפש וחולה");
		harchavot_shabat.add("כח - חולה שאינו מסוכן");
		harchavot_shabat.add("כט - עירובין");
		harchavot_shabat.add("ל - תחומי שבת");

		List<String> harchavot_simchat = new ArrayList<String>();
		harchavot_simchat.add("תוכן מפורט, פתח דבר");
		harchavot_simchat.add("א - מצוות עונה");
		harchavot_simchat.add("ב - הלכות עונה");
		harchavot_simchat.add("ג - קדושה וכוונה");
		harchavot_simchat.add("ד - שמירת הברית");
		harchavot_simchat.add("ה - פרו ורבו");
		harchavot_simchat.add("ו - קשיים ועקרות");
		harchavot_simchat.add("ז - סריס והשחתה");
		harchavot_simchat.add("ח - נחמת חשוכי ילדים");
		harchavot_simchat.add("ט - הפסקת הריון");
		harchavot_simchat.add("י - האיש והאשה");

		List<String> harchavot_brachot = new ArrayList<String>();
		harchavot_brachot.add("פתח דבר");
		harchavot_brachot.add("א - פתיחה");
		harchavot_brachot.add("ב - נטילת ידיים לסעודה");
		harchavot_brachot.add("ג - ברכת המוציא");
		harchavot_brachot.add("ד - ברכת המזון");
		harchavot_brachot.add("ה - זימון");
		harchavot_brachot.add("ו - חמשת מיני דגן");
		harchavot_brachot.add("ז - ברכת היין");
		harchavot_brachot.add("ח - ברכת הפירות ושהכל");
		harchavot_brachot.add("ט - כללי ברכה ראשונה");
		harchavot_brachot.add("י - ברכה אחרונה");
		harchavot_brachot.add("יא - עיקר וטפל");
		harchavot_brachot.add("יב - כללי ברכות");
		harchavot_brachot.add("יג - דרך ארץ");
		harchavot_brachot.add("יד - ברכת הריח");
		harchavot_brachot.add("טו - ברכות הראייה");
		harchavot_brachot.add("טז - ברכת הגומל");
		harchavot_brachot.add("יז - ברכות ההודאה והשמחה");
		
		List<String> harchavot_yamim = new ArrayList<String>();
		harchavot_yamim.add("תוכן מפורט, מבוא");
		harchavot_yamim.add("א - הדין השכר והעונש");
		harchavot_yamim.add("ב - סליחות ותפילות");
		harchavot_yamim.add("ג - ראש השנה");
		harchavot_yamim.add("ד - מצוות השופר");
		harchavot_yamim.add("ה - עשרת ימי תשובה");
		harchavot_yamim.add("ו - יום הכיפורים");
		harchavot_yamim.add("ז - הלכות יום הכיפורים");
		harchavot_yamim.add("ח - דיני התענית");
		harchavot_yamim.add("ט - שאר עינויים");
		harchavot_yamim.add("י - עבודת יום הכיפורים");
		
		List<String> E_tefila = new ArrayList<String>();
		E_tefila.add("Contents, Introduction, Glossary and Index");
		E_tefila.add("1 - Fundamentals of the Laws of Prayer");
		E_tefila.add("2 - Minyan");
		E_tefila.add("3 - The Place of Prayer");
		E_tefila.add("4 - The Chazan and the Mourner's Kaddish");
		E_tefila.add("5 - Preparations for Prayer");
		E_tefila.add("6 - Nusach: Wording of Prayer");
		E_tefila.add("7 - Waking Up in the Morning");
		E_tefila.add("8 - Washing One’s Hands in the Morning");
		E_tefila.add("9 - Birkot HaShachar – The Morning Blessings");
		E_tefila.add("10 - Birkot HaTorah – The Blessings on the Torah");
		E_tefila.add("11 - The Times of Keriat Shema and Shacharit");
		E_tefila.add("12 - Before the Shacharit Prayer");
		E_tefila.add("13 - Korbanot – The Passages of the Sacrificial Offerings");
		E_tefila.add("14 - Pesukei d’Zimrah");
		E_tefila.add("15 - Keriat Shema");
		E_tefila.add("16 - Birkot Keriat Shema");
		E_tefila.add("17 - The Amidah");
		E_tefila.add("18 - Errors, Additions, and Omissions in the Amidah");
		E_tefila.add("19 - The Chazan’s Repetition of the Amidah");
		E_tefila.add("20 - Birkat Kohanim – The Priestly Blessing");
		E_tefila.add("21 - Nefillat Apayim and the Prayers of Supplication");
		E_tefila.add("22 - Several Laws of Torah Reading");
		E_tefila.add("23 - The Conclusion of Shacharit and the Laws of Kaddish");
		E_tefila.add("24 - The Minchah Prayer");
		E_tefila.add("25 - The Ma’ariv Prayer");
		E_tefila.add("26 - The Bedtime Shema");

		List<String> E_pesach = new ArrayList<String>();
		E_pesach.add("Contents, Introduction, Glossary and Index");
		E_pesach.add("1 - The Meaning of the Holiday");
		E_pesach.add("2 - General Rules of the Prohibition against Ĥametz");
		E_pesach.add("3 - The Mitzva of Getting Rid of Ĥametz");
		E_pesach.add("4 - Bedikat Ĥametz – the Search for Ĥametz");
		E_pesach.add("5 - Bitul and Bi’ur Ĥametz");
		E_pesach.add("6 - Mekhirat Ĥametz – the Sale of Ĥametz");
		E_pesach.add("7 - Ĥametz Mixtures");
		E_pesach.add("8 - Pesaĥ Kashrut");
		E_pesach.add("9 - Kitniyot");
		E_pesach.add("10 - The Principles of Hagalat Kelim");
		E_pesach.add("11 - Koshering the Kitchen");
		E_pesach.add("12 - The Laws of Matza");
		E_pesach.add("13 - The Laws and Customs of Erev Pesaĥ");
		E_pesach.add("14 - When Erev Pesaĥ Falls on Shabbat");
		E_pesach.add("15 - The Hagada");
		E_pesach.add("16 - Seder Night");

		List<String> E_zmanim = new ArrayList<String>();
		E_zmanim.add("Contents");
		E_zmanim.add("1 - Rosh Chodesh");
		E_zmanim.add("2 - The Laws of Counting the Omer");
		E_zmanim.add("3 - Customs of Mourning During the Omer Period");
		E_zmanim.add("4 - Yom HaAtzma’ut, Yom Yerushalayim, and Yom HaZikaron");
		E_zmanim.add("5 - Lag B’Omer");
		E_zmanim.add("6 - The Four Fasts Commemorating the Churban");
		E_zmanim.add("7 - The Laws of the Minor Fasts");
		E_zmanim.add("8 - The Customs of the Three Weeks");
		E_zmanim.add("9 - The Eve of Tish’a B’Av");
		E_zmanim.add("10 - The Laws of Tish’a B’Av");
		E_zmanim.add("11 - Chanukah");
		E_zmanim.add("12 - Lighting the Chanukah Candles");
		E_zmanim.add("13 - Unavailable");
		E_zmanim.add("14 - The Month of Adar");
		E_zmanim.add("15 - Purim and the Reading of the Megillah");

		List<String> E_Women_Prayer = new ArrayList<String>();
		E_Women_Prayer.add("Contents, Abbreviations, Preface, Glossary and Index");
		E_Women_Prayer.add("1 - Fundamentals of the Laws of Prayer");
		E_Women_Prayer.add("2 - The Mitzva of Prayer for Women");
		E_Women_Prayer.add("3 - The Reasons behind Women's Mitzvot");
		E_Women_Prayer.add("4 - Waking Up in the Morning");
		E_Women_Prayer.add("5 - Netilat Yadayim in the Morning");
		E_Women_Prayer.add("6 - Birkhot Ha-shaĥar – The Morning Blessings");
		E_Women_Prayer.add("7 - Birkhot Ha-Torah – The Blessings on the Torah");
		E_Women_Prayer.add("8 - The Shaĥarit Prayer and the Laws Prior to its Recitation");
		E_Women_Prayer.add("9 - Preparing the Body for Prayer");
		E_Women_Prayer.add("10 - Mental Preparation and Proper Attire");
		E_Women_Prayer.add("11 - The Place of Prayer");
		E_Women_Prayer.add("12 - The Amida");
		E_Women_Prayer.add("13 - Additions, Errors, and Omissions in the Amida");
		E_Women_Prayer.add("14 - Respect for Prayer");
		E_Women_Prayer.add("15 - Korbanot and Pesukei De-zimra");
		E_Women_Prayer.add("16 - Keri’at Shema and its Berakhot");
		E_Women_Prayer.add("17 - The Prayers after the Amida");
		E_Women_Prayer.add("18 - Minĥa and Ma’ariv");
		E_Women_Prayer.add("19 - The Bedtime Shema");
		E_Women_Prayer.add("20 - Praying with a Minyan");
		E_Women_Prayer.add("21 - Some Laws Concerning the Synagogue, Tzitzit, and Tefilin");
		E_Women_Prayer.add("22 - Shabbat Prayer and Kiddush");
		E_Women_Prayer.add("23 - Some Laws Concerning the Holidays and Festivals");
		E_Women_Prayer.add("24 - Prayer Rites (Nusaĥ)  and Customs of Different Communities");

		List<String> E_Shabat = new ArrayList<String>();
		E_Shabat.add("Contents, Introduction, Glossary and Index");
		E_Shabat.add("1 - Introduction");
		E_Shabat.add("2 - Preparing for Shabbat");
		E_Shabat.add("3 - Shabbat Times");
		E_Shabat.add("4 - Lighting Shabbat Candles");
		E_Shabat.add("5 - Torah Study and Prayer on Shabbat");
		E_Shabat.add("6 - Laws of Kiddush");
		E_Shabat.add("7 - Shabbat Meals and Melaveh Malka");
		E_Shabat.add("8 - Havdala and Saturday Night");
		E_Shabat.add("9 - The Principles of the Melakhot");
		E_Shabat.add("10 - Bishul (Cooking)");
		E_Shabat.add("11 - Borer (Separating)");
		E_Shabat.add("12 - Food Preparation");
		E_Shabat.add("13 - Melakhot Pertaining to Clothing");
		E_Shabat.add("14 - Personal Grooming");
		E_Shabat.add("15 - Boneh and Soter ");
		E_Shabat.add("16 - Mav’ir and Mekhabeh");
		E_Shabat.add("17 - Electricity and Electrical Appliances");
		E_Shabat.add("18 - Kotev, Moĥek, and Tzove’a");
		E_Shabat.add("19 - Agricultural Melakhot (Ĥoresh, Zore’a, Kotzer, and Me’amer)");
		E_Shabat.add("20 - Animals");
		E_Shabat.add("21 - Hotza’ah");
		E_Shabat.add("22 - The Spirit of Shabbat");
		E_Shabat.add("23 - Muktzeh");
		E_Shabat.add("24 - Children");
		E_Shabat.add("25 - Melakha Performed by a Non-Jew");
		E_Shabat.add("26 - Ma’aseh Shabbat and Lifnei Iver");
		E_Shabat.add("27 - Sick People and Saving Lives");
		E_Shabat.add("28 - Illness That Is Not Life-Threatening");
		E_Shabat.add("29 - Eruvin");
		E_Shabat.add("30 - Teĥum Shabbat ");

		List<String> F_tefila = new ArrayList<String>();
		F_tefila.add("Table des matières, Préfaces, Avant-propos, Note du traducteur and Index ");
		F_tefila.add("1 - Fondements des lois de la prière");
		F_tefila.add("2 - Le minyan");
		F_tefila.add("3 - Le lieu de la prière");
		F_tefila.add("4 - L’officiant et le Qaddich des endeuillés");
		F_tefila.add("5 - Préparatifs de la prière");
		F_tefila.add("6 - Rituels et coutumes des communautés");
		F_tefila.add("7 - Le lever matinal");
		F_tefila.add("8 - Nétilat yadaïm, l’ablution des mains");
		F_tefila.add("9 - Birkot hacha’har, les bénédictions du matin");
		F_tefila.add("10 - Birkot Hatorah, les bénédictions de la Torah");
		F_tefila.add("11 - Horaires de la lecture du Chéma et de la prière du matin");
		F_tefila.add("12 - A l’approche de la prière de Cha’harit");
		F_tefila.add("13 - Séder qorbanot, le rappel des sacrifices");
		F_tefila.add("14 - Les Versets de louange (Pessouqé dezimra)");
		F_tefila.add("15 - Lecture du Chéma Israël");
		F_tefila.add("16 - Les bénédictions du Chéma (Birkot qriat Chéma) ");
		F_tefila.add("17 - La ‘Amida");
		F_tefila.add("18 - Erreurs et oublis dans la récitation de la ‘Amida");
		F_tefila.add("19 - Répétition de l’officiant (‘hazara)");
		F_tefila.add("20 - La bénédiction sacerdotale (Birkat Cohanim)");
		F_tefila.add("21 - Nefilat apayima et les supplications (Ta’hanounim)");
		F_tefila.add("22 - Résumé des lois de lecture de la Torah (Qriat Hatorah)");
		F_tefila.add("23 - Conclusion de l’office du matin et règles du Qaddich");
		F_tefila.add("24 - L’office de Min’ha");
		F_tefila.add("25 - L’office d’Arvit");
		F_tefila.add("26 - Prière du coucher");

		listDataChild.put(listDataHeader.get(BRACHOT), brachot); // Header, Child data
		listDataChild.put(listDataHeader.get(HAAMVEHAAREZ), haam);
		listDataChild.put(listDataHeader.get(ZMANIM), zmanim);
		listDataChild.put(listDataHeader.get(YAMIM), yamim);
		listDataChild.put(listDataHeader.get(LIKUTIM_A), likutimA);
		listDataChild.put(listDataHeader.get(LIKUTIM_B), likutimB);
		listDataChild.put(listDataHeader.get(LIKUTIM_C), likutimC);
		listDataChild.put(listDataHeader.get(MOADIM), moadim);
		listDataChild.put(listDataHeader.get(SUCOT), sucot);
		listDataChild.put(listDataHeader.get(PESACH), pesach);
		listDataChild.put(listDataHeader.get(SHVIIT), shviit);
		listDataChild.put(listDataHeader.get(SHABAT), shabat);
		listDataChild.put(listDataHeader.get(SIMCHAT), simchat);
		listDataChild.put(listDataHeader.get(TEFILA), tefila);
		listDataChild.put(listDataHeader.get(TEFILAT_NASHIM), tefilatNashim);
		listDataChild.put(listDataHeader.get(HAR_BRACHOT), harchavot_brachot);
		listDataChild.put(listDataHeader.get(HAR_YAMIM), harchavot_yamim);
		listDataChild.put(listDataHeader.get(HAR_MOADIM), harchavot_moadim);
		listDataChild.put(listDataHeader.get(HAR_SUCOT), harchavot_sucot);
		listDataChild.put(listDataHeader.get(HAR_SHABAT), harchavot_shabat);
		listDataChild.put(listDataHeader.get(HAR_SIMCHAT), harchavot_simchat);
		listDataChild.put(listDataHeader.get(E_TEFILA), E_tefila);
		listDataChild.put(listDataHeader.get(E_PESACH), E_pesach);
		listDataChild.put(listDataHeader.get(E_ZMANIM), E_zmanim);
		listDataChild.put(listDataHeader.get(E_WOMEN_PRAYER), E_Women_Prayer);
		listDataChild.put(listDataHeader.get(E_SHABAT), E_Shabat);
		listDataChild.put(listDataHeader.get(F_TEFILA), F_tefila);
	}//prepareListData

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
		dialogButtonExit.setOnClickListener(new OnClickListener()
		{
			@SuppressLint("NewApi")
			@Override
			public void onClick(View v) 
			{
				acronymsDialog.dismiss();
			}
		});
		
		dialogButtonDecode.setOnClickListener(new OnClickListener()
		{
			@SuppressLint("NewApi")
			@Override
			public void onClick(View v) 
			{
				acronymsText = "\r\n" + /*"י\"א" */TextToDecode.getText().toString() + " - ";
				acronymsText = acronymsText.replace("\"", "");
				acronymsText = acronymsText.replace("'", "");
				InputStream is;
				String r="לא נמצאו תוצאות";
				int index=0, index_end=0, first=1;
				try 
				{
					is = getAssets().open("acronyms.txt");
					int size = is.available();
					byte[] buffer = new byte[size];
					is.read(buffer);
					is.close();
					String strText  = new String(buffer);
					
					while (strText.indexOf(acronymsText, index_end) != -1)
					{
						index = strText.indexOf(acronymsText, index);	
						index = strText.indexOf("-", index+1) + 2;
						index_end = strText.indexOf("\r\n", index);
						if(first==1)
						{
							r = strText.substring (index, index_end);
							first=0;
						}
						else
							r += ", " + strText.substring (index, index_end);
					}
				}
				catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				decodedText.setText(TextToDecode.getText().toString() + " - " + r);

			}
		});
		acronymsDialog.show();	
	}

	@SuppressLint("SetJavaScriptEnabled")
	void hascamotDialog()
	{
		final Context context = this;
		final Dialog dialog = new Dialog(context);
		int fontSize;
		WebView webviewHascmot;
		WebSettings webSettingsHascamot;

		dialog.setContentView(R.layout.note);

		dialog.setTitle(" הסכמות ");

		webviewHascmot = (WebView) dialog.findViewById(R.id.webViewNote1);
		webSettingsHascamot = webviewHascmot.getSettings();
		webSettingsHascamot.setJavaScriptEnabled(true);
		webSettingsHascamot.setDefaultTextEncodingName("utf-8");
		webviewHascmot.requestFocusFromTouch();
	//	if(API < 19)
		//	webSettingsNote.setBuiltInZoomControls(true);

		fontSize = mPrefs.getInt("fontSize", 20);
		webSettingsHascamot.setDefaultFontSize(fontSize);
		int backgroundColor = mPrefs.getInt("BlackBackground", 0);
		webviewHascmot.setBackgroundColor(backgroundColor);
		if(backgroundColor == 0)
			webviewHascmot.loadUrl("javascript:document.body.style.color=\"black\";");
		else
			webviewHascmot.loadUrl("javascript:document.body.style.color=\"white\";");
		webviewHascmot.loadUrl("file:///android_asset/hascamot.html");
		dialog.show();
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

	void languageDialog(Context context)
	{
		languageDialog = new Dialog(context);
		languageDialog.setContentView(R.layout.language);

		Button ButtonSetLanguage = (Button) languageDialog.findViewById(R.id.dialogButtonOK);
		final RadioButton radioHebrew = (RadioButton) languageDialog.findViewById(R.id.radioHebrew);
		final RadioButton radioEnglish = (RadioButton) languageDialog.findViewById(R.id.radioEnglish);
		final RadioButton radioRussian = (RadioButton) languageDialog.findViewById(R.id.radioRussian);
		final RadioButton radioSpanish = (RadioButton) languageDialog.findViewById(R.id.radioSpanish);
		final RadioButton radioFrench = (RadioButton) languageDialog.findViewById(R.id.radioFrench);

		if(MyLanguage == -1)
		{
			MyLanguage = HEBREW; /*default value*/
			shPrefEditor.putInt("MyLanguage", MyLanguage);
			shPrefEditor.commit();
		}
		else
		{
			if(MyLanguage == HEBREW)
				radioHebrew.setChecked(true);
			else if(MyLanguage == ENGLISH)
				radioEnglish.setChecked(true);
			else if(MyLanguage == RUSSIAN)
				radioRussian.setChecked(true);
			else if(MyLanguage == SPANISH)
				radioSpanish.setChecked(true);
			else if(MyLanguage == FRENCH)
				radioFrench.setChecked(true);
		}

		// if button is clicked
		ButtonSetLanguage.setOnClickListener(new OnClickListener()
		{
			@SuppressLint("NewApi")
			@Override
			public void onClick(View v)
			{
				if(radioHebrew.isChecked())
				{
					MyLanguage = HEBREW;
				}
				else if(radioEnglish.isChecked())
				{
					MyLanguage = ENGLISH;
				}
				else if(radioRussian.isChecked())
				{
					MyLanguage = RUSSIAN;
				}
				else if(radioSpanish.isChecked())
				{
					MyLanguage = SPANISH;
				}
				else if(radioFrench.isChecked())
				{
					MyLanguage = FRENCH;
				}

				shPrefEditor.putInt("MyLanguage", MyLanguage);
				shPrefEditor.commit();

				languageDialog.dismiss();
			}
		});

		languageDialog.show();
	}

	void goToLastLocation()
	{
		try
		{
			Class ourClass = Class.forName("com.rafraph.pnineyHalachaHashalem.textMain");
			Intent ourIntent = new Intent(MainActivity.this, ourClass);
			int[] book_chapter = new int[2];
			book_chapter[0] = 0xFFFF;
			book_chapter[1] = 0xFFFF;
			ourIntent.putExtra("book_chapter", book_chapter);
			startActivity(ourIntent);
		}
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}
	}

}
