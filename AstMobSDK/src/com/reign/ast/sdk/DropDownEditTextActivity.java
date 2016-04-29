package com.reign.ast.sdk;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.reign.ast.sdk.pojo.GameAccount;
import com.reign.ast.sdk.util.GameUtil;
import com.ta.utdid2.android.utils.StringUtils;

public abstract class DropDownEditTextActivity extends Activity implements
		Callback {
	// PopupWindow对象
	private PopupWindow selectPopupWindow = null;
	// 自定义Adapter
	private OptionsAdapter optionsAdapter = null;
	// 下拉框选项数据源
	protected ArrayList<String> datas = new ArrayList<String>();;
	// 下拉框依附组件
	protected RelativeLayout parent;
	// 下拉框依附组件宽度，也将作为下拉框的宽度
	private int pwidth;
	// 文本框
	protected EditText et;
	// 下拉箭头图片组件
	protected ImageView image;
	// 展示所有下拉选项的ListView
	private ListView listView = null;
	// 用来处理选中或者删除下拉项消息
	private Handler handler;
	// 是否初始化完成标志
	private boolean flag = true;

	/**
	 * 没有在onCreate方法中调用initWedget()，而是在onWindowFocusChanged方法中调用，
	 * 是因为initWedget()中需要获取PopupWindow浮动下拉框依附的组件宽度，在onCreate方法中是无法获取到该宽度的
	 */
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		// ****
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

	}

	/**
	 * 初始化界面控件
	 */
	protected void initWedget(Dialog dialog) {
		// 初始化Handler,用来处理消息
		handler = new Handler(this);

		// 初始化界面组件
		// 获取下拉框依附的组件宽度
		int width = et.getWidth();
		pwidth = 500;
		et.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				popupWindwShowing();
				return true;
			}
		});

		// 设置点击下拉箭头图片事件，点击弹出PopupWindow浮动下拉框
		image.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (flag) {
					// 显示PopupWindow窗口
					popupWindwShowing();
				}
			}
		});

		// 初始化PopupWindow
		initPopuWindow();

	}

	/**
	 * 初始化PopupWindow
	 */
	private void initPopuWindow() {

		// PopupWindow浮动下拉框布局
		View loginwindow = (View) this.getLayoutInflater().inflate(
				R.layout.option, null);
		listView = (ListView) loginwindow.findViewById(R.id.list);

		// 设置自定义Adapter
		optionsAdapter = new OptionsAdapter(this, handler, datas);
		listView.setAdapter(optionsAdapter);

		selectPopupWindow = new PopupWindow(loginwindow,parent.getLayoutParams().width ,
				LayoutParams.WRAP_CONTENT, true);

		selectPopupWindow.setOutsideTouchable(true);

		// 这一句是为了实现弹出PopupWindow后，当点击屏幕其他部分及Back键时PopupWindow会消失，
		// 没有这一句则效果不能出来，但并不会影响背景
		// 本人能力极其有限，不明白其原因，还望高手、知情者指点一下
		selectPopupWindow.setBackgroundDrawable(new BitmapDrawable());
	}

	/**
	 * 显示PopupWindow窗口
	 * 
	 * @param popupwindow
	 */
	public void popupWindwShowing() {
		// 将selectPopupWindow作为parent的下拉框显示，并指定selectPopupWindow在Y方向上向上偏移3pix，
		// 这是为了防止下拉框与文本框之间产生缝隙，影响界面美化
		// （是否会产生缝隙，及产生缝隙的大小，可能会根据机型、Android系统版本不同而异吧，不太清楚）
		selectPopupWindow.showAsDropDown(parent, 0, -3);
	}

	/**
	 * PopupWindow消失
	 */
	public void dismiss() {
		selectPopupWindow.dismiss();
	}

	/**
	 * 处理Hander消息
	 */
	@Override
	public boolean handleMessage(Message message) {
		Bundle data = message.getData();
		switch (message.what) {
		case 1:
			// 选中下拉项，下拉框消失
			int selIndex = data.getInt("selIndex");
			et.setText(datas.get(selIndex));
			dismiss();
			break;
		case 2:
			// 移除下拉项数据
			int delIndex = data.getInt("delIndex");
			String removeText = datas.remove(delIndex);
			if (removeText.equals("游客")) {
				GameUtil.removeGuest(getApplicationContext());
				removeRememberGuest();
			} else {
				GameUtil.removeByUsername(getApplicationContext(), removeText);
				removeRememberAccount(removeText);
			}
			// 刷新下拉列表
			optionsAdapter.notifyDataSetChanged();
			if (et.getText().toString().equals(removeText)) {
				et.setText("");
			}
			if (datas.size() == 0) {
				finish();
				startActivity(new Intent(this, MainLoginActivity.class));
			}
			break;
		}
		return false;
	}

	protected void removeRememberAccount(String username) {
		SharedPreferences pref = getSharedPreferences(
				"login_remember_account_info", 0);
		String string = pref.getString("login_account_info", "");
		if (!StringUtils.isEmpty(string)) {
			String[] split = string.split(":");
			if (username != null && split.length > 1
					&& username.equals(split[1])) {
				SharedPreferences.Editor editor = pref.edit();
				editor.clear();
				editor.commit();
			}
		}

	}

	protected void removeRememberAccount() {
		SharedPreferences pref = getSharedPreferences(
				"login_remember_account_info", 0);
		String string = pref.getString("login_account_info", "");
		SharedPreferences.Editor editor = pref.edit();
		editor.clear();
		editor.commit();
	}

	protected void removeRememberGuest() {
		SharedPreferences pref = getSharedPreferences(
				"login_remember_account_info", 0);
		String string = pref.getString("login_account_info", "");
		if (!StringUtils.isEmpty(string)) {
			String[] split = string.split(":");
			if (split.length > 1 && split[0].equals("" + GameAccount.QUICK)) {
				SharedPreferences.Editor editor = pref.edit();
				editor.clear();
				editor.commit();
			}
		}

	}

}