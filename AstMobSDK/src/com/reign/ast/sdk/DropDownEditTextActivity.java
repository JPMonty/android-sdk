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
	// PopupWindow����
	private PopupWindow selectPopupWindow = null;
	// �Զ���Adapter
	private OptionsAdapter optionsAdapter = null;
	// ������ѡ������Դ
	protected ArrayList<String> datas = new ArrayList<String>();;
	// �������������
	protected RelativeLayout parent;
	// ���������������ȣ�Ҳ����Ϊ������Ŀ��
	private int pwidth;
	// �ı���
	protected EditText et;
	// ������ͷͼƬ���
	protected ImageView image;
	// չʾ��������ѡ���ListView
	private ListView listView = null;
	// ��������ѡ�л���ɾ����������Ϣ
	private Handler handler;
	// �Ƿ��ʼ����ɱ�־
	private boolean flag = true;

	/**
	 * û����onCreate�����е���initWedget()��������onWindowFocusChanged�����е��ã�
	 * ����ΪinitWedget()����Ҫ��ȡPopupWindow���������������������ȣ���onCreate���������޷���ȡ���ÿ�ȵ�
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
	 * ��ʼ������ؼ�
	 */
	protected void initWedget(Dialog dialog) {
		// ��ʼ��Handler,����������Ϣ
		handler = new Handler(this);

		// ��ʼ���������
		// ��ȡ������������������
		int width = et.getWidth();
		pwidth = 500;
		et.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				popupWindwShowing();
				return true;
			}
		});

		// ���õ��������ͷͼƬ�¼����������PopupWindow����������
		image.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (flag) {
					// ��ʾPopupWindow����
					popupWindwShowing();
				}
			}
		});

		// ��ʼ��PopupWindow
		initPopuWindow();

	}

	/**
	 * ��ʼ��PopupWindow
	 */
	private void initPopuWindow() {

		// PopupWindow���������򲼾�
		View loginwindow = (View) this.getLayoutInflater().inflate(
				R.layout.option, null);
		listView = (ListView) loginwindow.findViewById(R.id.list);

		// �����Զ���Adapter
		optionsAdapter = new OptionsAdapter(this, handler, datas);
		listView.setAdapter(optionsAdapter);

		selectPopupWindow = new PopupWindow(loginwindow,parent.getLayoutParams().width ,
				LayoutParams.WRAP_CONTENT, true);

		selectPopupWindow.setOutsideTouchable(true);

		// ��һ����Ϊ��ʵ�ֵ���PopupWindow�󣬵������Ļ�������ּ�Back��ʱPopupWindow����ʧ��
		// û����һ����Ч�����ܳ�������������Ӱ�챳��
		// ���������������ޣ���������ԭ�򣬻������֡�֪����ָ��һ��
		selectPopupWindow.setBackgroundDrawable(new BitmapDrawable());
	}

	/**
	 * ��ʾPopupWindow����
	 * 
	 * @param popupwindow
	 */
	public void popupWindwShowing() {
		// ��selectPopupWindow��Ϊparent����������ʾ����ָ��selectPopupWindow��Y����������ƫ��3pix��
		// ����Ϊ�˷�ֹ���������ı���֮�������϶��Ӱ���������
		// ���Ƿ�������϶����������϶�Ĵ�С�����ܻ���ݻ��͡�Androidϵͳ�汾��ͬ����ɣ���̫�����
		selectPopupWindow.showAsDropDown(parent, 0, -3);
	}

	/**
	 * PopupWindow��ʧ
	 */
	public void dismiss() {
		selectPopupWindow.dismiss();
	}

	/**
	 * ����Hander��Ϣ
	 */
	@Override
	public boolean handleMessage(Message message) {
		Bundle data = message.getData();
		switch (message.what) {
		case 1:
			// ѡ���������������ʧ
			int selIndex = data.getInt("selIndex");
			et.setText(datas.get(selIndex));
			dismiss();
			break;
		case 2:
			// �Ƴ�����������
			int delIndex = data.getInt("delIndex");
			String removeText = datas.remove(delIndex);
			if (removeText.equals("�ο�")) {
				GameUtil.removeGuest(getApplicationContext());
				removeRememberGuest();
			} else {
				GameUtil.removeByUsername(getApplicationContext(), removeText);
				removeRememberAccount(removeText);
			}
			// ˢ�������б�
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