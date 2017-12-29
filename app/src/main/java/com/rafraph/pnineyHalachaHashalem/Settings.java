package com.rafraph.pnineyHalachaHashalem;

import android.os.Bundle;
import android.app.Activity;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;

public class Settings extends Activity 
{
	public static final String PREFS_NAME = "MyPrefsFile";
	static SharedPreferences mPrefs;
	SharedPreferences.Editor shPrefEditor;
	CheckBox cbBlackBackground, cbSleepScreen, cbFullScreen, cbAssistButtons, cbLastLocation;

    private static final int HEBREW	 = 0;
    private static final int ENGLISH = 1;
    private static final int RUSSIAN = 2;
    private static final int SPANISH = 3;
    private static final int FRENCH = 4;

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		
		cbSleepScreen 		= (CheckBox) findViewById(R.id.checkBoxSleepScreen);
		cbBlackBackground 	= (CheckBox) findViewById(R.id.checkBoxBlackBackground);
		cbFullScreen 		= (CheckBox) findViewById(R.id.checkBoxFullScreen);
		cbAssistButtons 	= (CheckBox) findViewById(R.id.checkBoxAssistButtons);
		cbLastLocation 		= (CheckBox) findViewById(R.id.checkBoxLastLocation);

		mPrefs = getSharedPreferences(PREFS_NAME, 0);
		shPrefEditor = mPrefs.edit();

		int MyLanguage = mPrefs.getInt("MyLanguage", 0);

		if(MyLanguage != HEBREW)
		{
			ChangeTextLengauge(MyLanguage);
		}


		if (mPrefs.getInt("SleepScreen", 1) ==1)
		{
			cbSleepScreen.setChecked(true);
		}
		else
		{
			cbSleepScreen.setChecked(false);
		}
		
		if (mPrefs.getInt("BlackBackground", 0) == 1)
		{
			cbBlackBackground.setChecked(true);
		}
		else
		{
			cbBlackBackground.setChecked(false);
		}
		
		if (mPrefs.getInt("cbFullScreen", 1) == 1)
		{
			cbFullScreen.setChecked(true);
		}
		else
		{
			cbFullScreen.setChecked(false);
		}
		
		if (mPrefs.getInt("cbAssistButtons", 1) == 1)
		{
			cbAssistButtons.setChecked(true);
		}
		else
		{
			cbAssistButtons.setChecked(false);
		}

		if (mPrefs.getInt("StartInLastLocation", 1) == 1)
		{
			cbLastLocation.setChecked(true);
		}
		else
		{
			cbLastLocation.setChecked(false);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.settings, menu);
		return true;
	}

	public void onCheckboxClicked(View view) 
	{
		
		//shPrefEditor.putInt("SleepScreen", 1);
	    // Is the view now checked?
	    boolean checked = ((CheckBox) view).isChecked();
	    
	    // Check which checkbox was clicked
	    switch(view.getId()) 
	    {
	        case R.id.checkBoxSleepScreen:
	            if (checked == true)
	            	shPrefEditor.putInt("SleepScreen", 1);
	            else
	            	shPrefEditor.putInt("SleepScreen", 0);
	            break;
	        case R.id.checkBoxBlackBackground:
	            if (checked == true)
	            	shPrefEditor.putInt("BlackBackground", 1);
	            else
	            	shPrefEditor.putInt("BlackBackground", 0);
	            break;
	        case R.id.checkBoxFullScreen:
	            if (checked == true)
	            	shPrefEditor.putInt("cbFullScreen", 1);
	            else
	            	shPrefEditor.putInt("cbFullScreen", 0);
	            break;
	        case R.id.checkBoxAssistButtons:
	            if (checked == true)
	            	shPrefEditor.putInt("cbAssistButtons", 1);
	            else
	            	shPrefEditor.putInt("cbAssistButtons", 0);
	            break;
			case R.id.checkBoxLastLocation:
				if (checked == true)
					shPrefEditor.putInt("StartInLastLocation", 1);
				else
					shPrefEditor.putInt("StartInLastLocation", 0);
				break;
	    }
	    shPrefEditor.commit();
	}

	public void ChangeTextLengauge(int language)
	{
        if(language == ENGLISH) {
            cbSleepScreen.setText("Cancel monitor sleep");
            cbBlackBackground.setText("Black background");
            cbFullScreen.setText("Don't remove buttons in full screen mode");
            cbAssistButtons.setText("Locate the buttons in the bottom part of the screen");
            cbLastLocation.setText("Jump to the last location when application start");
        }
        else if(language == RUSSIAN) {
            cbSleepScreen.setText("");
            cbBlackBackground.setText("Чёрный фон");
            cbFullScreen.setText("");
            cbAssistButtons.setText("");
            cbLastLocation.setText("");
        }
        else if(language == SPANISH) {
            cbSleepScreen.setText("Cancelar el sueño del monitor");
            cbBlackBackground.setText("Fondo negro");
            cbFullScreen.setText("No remover los botones en modo de pantalla entera");
            cbAssistButtons.setText("Localiza los botones en la parte baja de la pantalla");
            cbLastLocation.setText("Saltar a la ultima locacion cuando la aplicacion comience");
        }
        else if(language == FRENCH  ) {
            cbSleepScreen.setText("Annuler mode veille");
            cbBlackBackground.setText("Fond noir");
            cbFullScreen.setText("Ne pas retirer les boutons en mode plein écran");
            cbAssistButtons.setText("Placer les boutons dans la partie inférieure de l'écran");
            cbLastLocation.setText("Au demarrage revenir a la precedente location");
        }
    }
}
