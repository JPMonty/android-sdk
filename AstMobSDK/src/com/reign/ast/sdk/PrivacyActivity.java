package com.reign.ast.sdk;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class PrivacyActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		RelativeLayout l = new RelativeLayout(this);
		l.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));
		WebView view = new WebView(this);
		view.loadUrl("http://privacy.zzsf.com/privacy.html");
		setContentView(l);
		ImageView close = new ImageView(this);
		close.setImageResource(R.drawable.btn_close_big);
		l.addView(view, new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);// 与父容器的左侧对齐
		lp.addRule(RelativeLayout.ALIGN_PARENT_TOP);// 与父容器的上侧对齐
		lp.topMargin = 30;
		lp.rightMargin = 50;
		l.addView(close, lp);
		close.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				onBackPressed();
			}
		});
	}

}
