package com.rafraph.pnineyHalachaHashalem;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class Feedback extends Activity implements View.OnClickListener
{
	Button sendEmail;
	EditText EmailHeader, EmailContent;
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_feedback);
		EmailHeader = (EditText) findViewById(R.id.etHeader);
		EmailContent = (EditText) findViewById(R.id.etContent);

		sendEmail = (Button) findViewById(R.id.bSendEmail);
		sendEmail.setOnClickListener(this);

		Button linkForFix = (Button) findViewById(R.id.bContentFix);
		linkForFix.setOnClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.feedback, menu);
		return true;
	}

	public void onClick(View v) 
	{
		// TODO Auto-generated method stub
		switch(v.getId())
		{
		case R.id.bSendEmail:
			String emailaddress[] = { "janer.solutions@gmail.com" };
			String header;
			String message;

			header = EmailHeader.getText().toString();
			header = "לגבי \"פניני הלכה\": " + EmailHeader.getText().toString();
			message = EmailContent.getText().toString();

			Intent emailIntent = new Intent (android.content.Intent.ACTION_SEND);
			emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, emailaddress);
			emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, header);
			emailIntent.setType("plain/text");
			emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, message);
			startActivity(emailIntent);
			break;
			
		case R.id.bContentFix:
			Intent intent = new Intent();
			intent.setAction(Intent.ACTION_VIEW);
			intent.addCategory(Intent.CATEGORY_BROWSABLE);
			intent.setData(Uri.parse("http://yhb.org.il/?page_id=1194"));
			startActivity(intent);
			break;
		}
	}

}
