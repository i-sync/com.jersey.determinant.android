package com.example.com.jersey.determinant.android;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jersey.determinant.Determinant;

public class MainActivity extends Activity {

	private static String restUrl = "http://www.contacts09.tk/com.jersey.determinant";
	private EditText etOrder;
	private Button btnConfirm;
	private Button btnSubmit;
	private GridLayout glLayout;
	private LinearLayout llLayout;
	//private EditText etDynamic;
	private int order;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		etOrder=(EditText) this.findViewById(R.id.et_main_order);
		btnConfirm = (Button)this.findViewById(R.id.btn_main_confirm);
		btnSubmit = (Button)this.findViewById(R.id.btn_main_submit);
		glLayout = (GridLayout)this.findViewById(R.id.gl_layout_array);
		llLayout = (LinearLayout)this.findViewById(R.id.ll_layout_submit);
		//etDynamic =(EditText)this.findViewById(R.id.et_activity_main_dynamic);
		
		/*
		 * btnConfirm Click Event
		 */
		btnConfirm.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String orderStr = etOrder.getText().toString().trim();
				if(orderStr.equals(""))
				{
					Toast.makeText(MainActivity.this, "please input the order!", Toast.LENGTH_SHORT).show();
					return;
				}
				order = Integer.parseInt(orderStr);
				if(order<2 || order >6)
				{
					Toast.makeText(MainActivity.this, "please input a number bewteen 2 and 6!", Toast.LENGTH_SHORT).show();
					return;
				}
				glLayout.removeAllViews();
				//setting the relative layout height equal width
				//glLayout.setLayoutParams(new GridLayout.LayoutParams(new ViewGroup.LayoutParams(glLayout.getMeasuredWidth(),glLayout.getMeasuredWidth())));
				glLayout.setColumnCount(order);
				glLayout.setRowCount(order);
				
				glLayout.setVisibility(View.VISIBLE);
				llLayout.setVisibility(View.VISIBLE);
				
				createEditText(glLayout);
			}
		});
		
		/*
		 * get the number create 2d array
		 */
		btnSubmit.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				double [][] array = new double[order][order];
				int index=0;
				EditText et;
				for(int i=0;i<order;i++)
					for(int j=0;j<order;j++)
					{
						et= (EditText)glLayout.getChildAt(index++);
						array[i][j]= Double.parseDouble(et.getText().toString().trim().equals("")?"0":et.getText().toString());
					}
				
				
				Determinant deter = new Determinant();
				deter.setOrder(order);
				deter.setArray(array);
				new Thread(new calcDeter(deter)).start(); 
				
				/*
				StringBuilder result =new StringBuilder("");
				for(int i=0;i<order;i++)
				{
					for(int j=0;j<order;j++)
					{
						result.append(String.format("\t%.2f", array[i][j]));
					}
					result.append("\r\n");
				}	
				Toast.makeText(MainActivity.this, result.toString(), Toast.LENGTH_LONG).show();
				*/
			}
		});
		
	}
	
	public void createEditText(GridLayout layout)
	{
		for(int i=0;i<order;i++)
		{
			for(int j=0;j<order;j++)
			{
				EditText et = new EditText(getApplicationContext());
				GridLayout.LayoutParams params = new GridLayout.LayoutParams(new ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
				params.rowSpec= GridLayout.spec(i);
				params.columnSpec = GridLayout.spec(j);
				params.height = params.width = layout.getWidth()/order;
				et.getBackground().setColorFilter(Color.BLUE, Mode.SRC_ATOP);
				et.setTextColor(Color.BLACK);
				et.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
				//et.setId(i);
				et.setHintTextColor(Color.GRAY);
				et.setHint("0");
				layout.addView(et, params);
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/*
	 * call the remote server to calc the determinant value
	 */
	private class calcDeter implements Runnable{
		private Determinant deter;
		public calcDeter (Determinant deter)
		{
			this.deter= deter;
		}
		@Override
		public void run()
		{
			String path="/rest/deter/calc";
			HttpClient client = new DefaultHttpClient();
			HttpPost post = new HttpPost(restUrl+path);
			post.setHeader("Content-Type", "application/json");
			post.setHeader("Accept", "application/json");
			
			String json = new Gson().toJson(deter);
			Log.i("json:--->",json);
			StringEntity entity=null;
			try {
				entity = new StringEntity(json);
				entity.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			post.setEntity(entity);
			try {
				HttpResponse response = client .execute(post);
				BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
				StringBuilder builder = new StringBuilder();
				for (String line = null; (line = reader.readLine()) != null;) {
				    builder.append(line).append("\n");
				}
				Log.i("result--->",builder.toString());
				deter = new Gson().fromJson(builder.toString(), Determinant.class);
				
				StringBuilder result =new StringBuilder("");
				for(int i=0;i<order;i++)
				{
					for(int j=0;j<order;j++)
					{
						result.append(String.format("\t%.2f", deter.getArray()[i][j]));
					}
					result.append("\r\n");
				}	
				Toast.makeText(MainActivity.this, result.toString(), Toast.LENGTH_LONG).show();
				
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
