package com.reign.ast.sdk;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class HasBindedActivity extends Activity implements OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		String mobile = getIntent().getStringExtra("mobile");
		Dialog dialog = new Dialog(this,
				R.style.ast_mob_sdk_loading_FullHeightDialog);
		dialog.show();
		dialog.setCancelable(true);
		
		dialog.setCanceledOnTouchOutside(false);
		
		dialog.setOnCancelListener(new OnCancelListener() {
			
			@Override
			public void onCancel(DialogInterface arg0) {
				// TODO Auto-generated method stub
				finish();
				
			}
		});
		dialog.setContentView(R.layout.ast_mob_sdk_phone_binded);
		dialog.findViewById(R.id.back).setOnClickListener(this);
		TextView tip = (TextView) dialog.findViewById(R.id.tip);
		tip.setText("你绑定的手机号为" + mobile);
	}

	@Override
	public void onClick(View view) {
		if (view.getId() == R.id.back) {
			startActivity(new Intent(getApplicationContext(),
					AccountUserCenterActivity.class));
			finish();
		}
	}

}
