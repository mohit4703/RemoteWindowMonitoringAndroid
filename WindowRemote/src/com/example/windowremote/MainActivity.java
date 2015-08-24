package com.example.windowremote;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends Activity {

	EditText et1,et2;
	Button start;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN|WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.activity_main);
		et1=(EditText)findViewById(R.id.windowIp);
		et2=(EditText)findViewById(R.id.windowPass);
		start=(Button)findViewById(R.id.button1);
		start.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent in=new Intent(getApplicationContext(),Client_main.class);
				in.putExtra("ip", et1.getText().toString());
				in.putExtra("pass", et2.getText().toString());
				startActivity(in);
			}
		});
	}



}
