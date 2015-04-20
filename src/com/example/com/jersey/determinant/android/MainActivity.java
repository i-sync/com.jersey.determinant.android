package com.example.com.jersey.determinant.android;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
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
	private TextView tvResult;
	// private EditText etDynamic;
	private int order;

	private DialogFrag dialog;
	private boolean isConnected;// sign the network is connected

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			dialog.dismiss();// hiden dialog
			if (msg.what == 1)// success
			{
				Determinant deter = (Determinant) msg.obj;
				glLayout.removeAllViews();
				createEditText(glLayout,deter.getArray());
				//Toast.makeText(getApplicationContext(), String.format("the determinant value is %.2f",deter.getValue()), Toast.LENGTH_LONG).show();
				tvResult.setText(String.format("%s %.2f",getResources().getString(R.string.tv_activity_main_determinant),deter.getValue()));
			} else {
				Toast.makeText(getApplicationContext(), "calc error...",
						Toast.LENGTH_LONG).show();
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// check network is connected
		isConnected = NetworkManager.getInstance().isNetworkConnected(getApplicationContext());
		dialog= DialogFrag.getInstance();
		etOrder = (EditText) this.findViewById(R.id.et_main_order);
		btnConfirm = (Button) this.findViewById(R.id.btn_main_confirm);
		btnSubmit = (Button) this.findViewById(R.id.btn_main_submit);
		tvResult =(TextView)this.findViewById(R.id.tv_main_result);
		glLayout = (GridLayout) this.findViewById(R.id.gl_layout_array);
		llLayout = (LinearLayout) this.findViewById(R.id.ll_layout_submit);
		// etDynamic
		// =(EditText)this.findViewById(R.id.et_activity_main_dynamic);

		
		etOrder.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				// TODO Auto-generated method stub
				if(actionId==EditorInfo.IME_ACTION_DONE)
				{
					btnConfirm.callOnClick();
					return true;
				}
				return false;
			}
		});
		/*
		 * btnConfirm Click Event
		 */
		btnConfirm.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String orderStr = etOrder.getText().toString().trim();
				if (orderStr.equals("")) {
					Toast.makeText(MainActivity.this,
							"please input the order!", Toast.LENGTH_SHORT)
							.show();
					return;
				}
				order = Integer.parseInt(orderStr);
				if (order < 2 || order > 6) {
					Toast.makeText(MainActivity.this,
							"please input a number bewteen 2 and 6!",
							Toast.LENGTH_SHORT).show();
					return;
				}
				glLayout.removeAllViews();
				// setting the relative layout height equal width
				// glLayout.setLayoutParams(new GridLayout.LayoutParams(new
				// ViewGroup.LayoutParams(glLayout.getMeasuredWidth(),glLayout.getMeasuredWidth())));
				glLayout.setColumnCount(order);
				glLayout.setRowCount(order);

				glLayout.setVisibility(View.VISIBLE);
				llLayout.setVisibility(View.VISIBLE);

				createEditText(glLayout,null);
				tvResult.setText(R.string.tv_activity_main_determinant);
			}
		});

		/*
		 * get the number create 2d array
		 */
		btnSubmit.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				
				// if network is not connected ,return
				if (!isConnected) {
					Toast.makeText(MainActivity.this,
							"network is unavailable, please check the network!",
							Toast.LENGTH_LONG).show();
					return;
				}

				// TODO Auto-generated method stub
				double[][] array = new double[order][order];
				int index = 0;
				EditText et;
				for (int i = 0; i < order; i++)
					for (int j = 0; j < order; j++) {
						et = (EditText) glLayout.getChildAt(index++);
						array[i][j] = Double.parseDouble(et.getText()
								.toString().trim().equals("") ? "0" : et
								.getText().toString());
					}
				
				/*double result = deter(array);
				glLayout.removeAllViews();
				createEditText(glLayout,array);
				Toast.makeText(getApplicationContext(), String.format("the determinant value is %.2f",result), Toast.LENGTH_LONG).show();
				*/
				Determinant deter = new Determinant();
				deter.setOrder(order);
				deter.setArray(array);

				// start query and show dialog
				//dialog = DialogFrag.getInstance();
				dialog.show(getFragmentManager(), "dialog");
				
				new Thread(new calcDeter(deter)).start();
				
				
				/*
				 * StringBuilder result =new StringBuilder(""); for(int
				 * i=0;i<order;i++) { for(int j=0;j<order;j++) {
				 * result.append(String.format("\t%.2f", array[i][j])); }
				 * result.append("\r\n"); } Toast.makeText(MainActivity.this,
				 * result.toString(), Toast.LENGTH_LONG).show();
				 */
			}
		});

	}

	public void createEditText(GridLayout layout,double[][] array) {
		for (int i = 0; i < order; i++) {
			for (int j = 0; j < order; j++) {
				EditText et = new EditText(getApplicationContext());
				GridLayout.LayoutParams params = new GridLayout.LayoutParams(
						new ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT,
								LayoutParams.WRAP_CONTENT));
				params.rowSpec = GridLayout.spec(i);
				params.columnSpec = GridLayout.spec(j);
				params.height = params.width = layout.getWidth() / order;
				et.getBackground().setColorFilter(Color.BLUE, Mode.SRC_ATOP);
				et.setTextColor(Color.BLACK);
				int inputType= InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL|InputType.TYPE_NUMBER_FLAG_SIGNED;
				et.setInputType(inputType);
				// et.setId(i);
				et.setHintTextColor(Color.GRAY);
				et.setHint("0");
				et.setImeOptions(EditorInfo.IME_ACTION_NEXT);
				if(array!=null)
					et.setText(String.valueOf(array[i][j]));
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
	 * calculate the determinant value
	 */
	public double deter(double[][] array)
	{
		int flag =1;
		double sum=1;
		int len = array.length;
		double temp;
		for(int i=0,j;i<len-1;i++)
		{
			j=i;
			if(array[i][j]==0)
			{
				boolean b=false;
				for(int k=i+1;k<len;k++)
				{
					if(array[k][j]!=0)//找到一行不为0的,然后换行 
					{
						for(int l=j;l<len;l++)
						{
							temp=array[k][l];
							array[k][l]=array[i][l];
							array[i][l]=temp;
						}
						flag*=-1;
						b=true;
						break;
					}
				}
				if(!b)
				{
					return 0;
				}
				i--;
				continue;
			}
			for(;j<len-1;j++)
			{
				if(array[j+1][i]==0)continue;
				temp = -array[j+1][i]/array[i][i];
				for(int k=i;k<len;k++)
					array[j+1][k]+=array[i][k]*temp;
			}
		}
		
		for(int i=0;i<len;i++)
			sum*=array[i][i];
		return sum*flag;
	}

	/*
	 * call the remote server to calc the determinant value
	 */
	private class calcDeter implements Runnable {
		private Determinant deter;

		public calcDeter(Determinant deter) {
			this.deter = deter;
		}

		@Override
		public void run() {
			
			String json = new Gson().toJson(deter);
			Log.i("json:--->", json);
			String path = "/rest/deter/simple";
			HttpClient client = new DefaultHttpClient();
			HttpPost post = new HttpPost(restUrl+path);
			post.setHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
			post.setHeader("Accept", "application/json");	
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("json",json));
			

			Message msg = Message.obtain();
			try {
				post.setEntity(new UrlEncodedFormEntity(params));
			} catch (UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				msg.what=0;
				handler.sendMessage(msg);
				return ;
			}
			
			try {
				HttpResponse response = client.execute(post);
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(
								response.getEntity().getContent(), "UTF-8"));
				StringBuilder builder = new StringBuilder();
				for (String line = null; (line = reader.readLine()) != null;) {
					builder.append(line).append("\n");
				}
				Log.i("result--->", builder.toString());
				deter = new Gson().fromJson(builder.toString(),
						Determinant.class);
				msg.what=1;
				msg.obj= deter;
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				msg.what=0;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				msg.what=0;
			}
			handler.sendMessage(msg);
			/*
			HttpPost post = new HttpPost(restUrl + path);
			post.setHeader("Content-Type", "application/json");
			post.setHeader("Accept", "application/json");			
			
			StringEntity entity = null;
			try {
				entity = new StringEntity(json);
				//entity.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE,"application/json"));
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			post.setEntity(entity);
			HttpResponse response = null;
			Message msg = Message.obtain();
			try {
				response = client.execute(post);
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(
								response.getEntity().getContent(), "UTF-8"));
				StringBuilder builder = new StringBuilder();
				for (String line = null; (line = reader.readLine()) != null;) {
					builder.append(line).append("\n");
				}
				Log.i("result--->", builder.toString());
				deter = new Gson().fromJson(builder.toString(),
						Determinant.class);

				StringBuilder result = new StringBuilder("");
				for (int i = 0; i < order; i++) {
					for (int j = 0; j < order; j++) {
						result.append(String.format("\t%.2f",
								deter.getArray()[i][j]));
					}
					result.append("\r\n");
				}
				Toast.makeText(MainActivity.this, result.toString(),
						Toast.LENGTH_LONG).show();

				msg.what = 1;
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				msg.what = 0;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				msg.what = 0;
			}
			msg.obj = response;
			handler.sendMessage(msg);
			*/
		}
	}
}
