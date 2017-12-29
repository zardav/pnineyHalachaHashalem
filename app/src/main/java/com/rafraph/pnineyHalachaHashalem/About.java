package com.rafraph.pnineyHalachaHashalem;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.view.Menu;
import android.view.View.OnClickListener;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class About extends Activity implements View.OnClickListener
{
	Button link;
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
				
		link = (Button) findViewById(R.id.buttonLink);
		link.setOnClickListener(this);
		
		/*version*/
		String myVersionName = "not available"; // initialize String

		Context context = getApplicationContext(); // or activity.getApplicationContext()
		PackageManager packageManager = context.getPackageManager();
		String packageName = context.getPackageName();

		try 
		{
		    myVersionName = "גירסה: " + packageManager.getPackageInfo(packageName, 0).versionName;
		} 
		catch (PackageManager.NameNotFoundException e) 
		{
		    e.printStackTrace();
		}
		TextView tvVersion = (TextView) findViewById(R.id.textViewVersion);
		tvVersion.setText(myVersionName);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.about, menu);
		return true;
	}
	
	public void onClick(View v) 
	{
		// TODO Auto-generated method stub
	
		Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_BROWSABLE);
        intent.setData(Uri.parse("http://shop.yhb.org.il/"));
        startActivity(intent);
	}
}
