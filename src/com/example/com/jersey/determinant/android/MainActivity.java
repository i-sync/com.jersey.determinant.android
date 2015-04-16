package com.example.com.jersey.determinant.android;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class MainActivity extends Activity {

	private EditText etOrder;
	private Button btnConfirm;
	private Button btnSubmit;
	private RelativeLayout rlLayout;
	private LinearLayout llLayout;
	private EditText etDynamic;
	private int order;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		etOrder=(EditText) this.findViewById(R.id.et_main_order);
		btnConfirm = (Button)this.findViewById(R.id.btn_main_confirm);
		btnSubmit = (Button)this.findViewById(R.id.btn_main_submit);
		rlLayout = (RelativeLayout)this.findViewById(R.id.rl_layout_array);
		llLayout = (LinearLayout)this.findViewById(R.id.ll_layout_submit);
		etDynamic =(EditText)this.findViewById(R.id.et_activity_main_dynamic);
		
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
				//setting the relative layout height equal width
				rlLayout.setLayoutParams(new LayoutParams(rlLayout.getMeasuredWidth(),rlLayout.getMeasuredWidth()));
				createEditText(rlLayout);
				
				rlLayout.setVisibility(View.VISIBLE);
				llLayout.setVisibility(View.VISIBLE);
				
			}
		});
		
	}
	
	public void createEditText(RelativeLayout layout)
	{
		for(int i=0;i<order;i++)
		{
			EditText et = new EditText(getApplicationContext());
			et.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT));
			//et.setId(i);
			layout.addView(et);
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
}
