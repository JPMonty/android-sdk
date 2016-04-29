package com.reign.ast.sdk;

import java.net.URL;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.reign.ast.sdk.http.HttpCallback;
import com.reign.ast.sdk.http.handler.BindQuickUserHandler;
import com.reign.ast.sdk.http.handler.CaptchaHandler;
import com.reign.ast.sdk.http.handler.RegHandler;
import com.reign.ast.sdk.listener.OnRegisterListener;
import com.reign.ast.sdk.listener.PayCallbackListener;
import com.reign.ast.sdk.listener.PerfectAccountListener;
import com.reign.ast.sdk.listener.RegistCallbackListener;
import com.reign.ast.sdk.manager.AstGamePlatform;
import com.reign.ast.sdk.pojo.Captcha;
import com.reign.ast.sdk.pojo.UserInfo;
import com.reign.ast.sdk.util.GameUtil;
import com.reign.ast.sdk.util.Logger;

/**
 * 注册activity
 * @author zhouwenjia
 *
 */
@SuppressLint({ "HandlerLeak", "ValidFragment", "ResourceAsColor" })
public class AstRegActivity extends BaseActivity implements OnClickListener, OnRegisterListener {
	private static final String TAG = AstRegActivity.class.getSimpleName();
	private static RegistCallbackListener mRegCallbackListener;
	private static PayCallbackListener mPayCallbackListener;
	private static PerfectAccountListener mPerfectAccountListener;
	private EditText userNameEditText;
	private EditText passwordEditText;
	private EditText captchaEditText;
	private TextView regMain;
	private static final int MSG_USENAME_EMPTY = 0;
	private static final int MSG_USENAME_NOT_VALID = 1;
	private static final int MSG_PASSWORD_NOT_VALID = 2;
	private static final int MSG_AGREE = 3;
	private static final int LOAD_CAPTCHA = 4;
	private static final int NEED_CAPTCHA = 5;
	private static final int CAPTCHA_ERROR = 6;
	private ImageView captchaView;
	private View captcha;
	private Bitmap bitmap = null;
	private boolean isNeedCaptcha = false;
	private String captchaId;
	private Dialog loadingDialog;
	private FragmentManager fragmentManager;
	private TextView headText;
	private AstGamePlatform mAstGamePlatform = AstGamePlatform.getInstance();
	private int action;
	private String userId;
	private String orderId;
	private String extraAppData;
	
	public String phoneNum;
	public String sourceId = null;
	public String udid = null;

	private Fragment fragment = new TabFragment();

	private boolean isMainAgree = true;
	private Button mainSubmit;
	boolean isCloseBack = true;
	private static final int REGISTE_USER_ACTION_TYPE = 1;
	private static final int PERFECT_USER_PAY_ACTION_TYPE = 2;
	private static final int PERFECT_USER_ACTION_TYPE = 3;
	
	/** 验证码监听 */
	private OnClickListener captchaOnClickListener = new OnClickListener() {
		public void onClick(View v) {
			new CaptchaHandler(AstGamePlatform.getInstance().isDebugMode(), capHttpCallback).get();
		}
	};
	
//	/** 绑定临时账号回调 */
//	private HttpCallback bindHttpCallback = new HttpCallback() {
//		public void onSuccess(int code, String msg, Object data) {
//			// 成功回调
//			super.onSuccess(code, msg, data);
//			// 是否需要登陆 TODO
//		}
//		
//		public void onFailure(int code, String msg, Object data) {
//			// 失败回调
//			super.onFailure(code, msg, data);
//		}
//	};

	/** reg main回调 */
	private HttpCallback regMainHttpCallback = new HttpCallback() {
		public void onSuccess(int code, String msg, Object data) {
			// 成功回调
			super.onSuccess(code, msg, data);

			mainSubmit.setClickable(true);

			if (null != loadingDialog) {
				AstRegActivity.dismissDialog(loadingDialog);
			}

			String userName = userNameEditText.getEditableText().toString();
			String password = passwordEditText.getEditableText().toString();
			if ((PERFECT_USER_PAY_ACTION_TYPE == action) || (PERFECT_USER_ACTION_TYPE == action)) {
				Toast.makeText(AstRegActivity.this, "完善账号成功", Toast.LENGTH_LONG).show();
				if (null != AstRegActivity.mPayCallbackListener) {
					AstRegActivity.mPayCallbackListener.paySuccess(orderId, extraAppData);
				}
				// 非快速注册账号, 自动登陆
//				UserManager.getInstance().simpleAutoLogin(AstRegActivity.this, userName, password);
			
				AstGamePlatform.getInstance().setIsQuickUser(false);
				if (null != AstRegActivity.mPerfectAccountListener) {
					AstRegActivity.mPerfectAccountListener.success();
				}
			} else if (null != AstRegActivity.mRegCallbackListener) {
				AstRegActivity.mRegCallbackListener.regSucess(code, msg, userName, password);
			}

			finish();
		}

		public void onFailure(int code, String msg, Object data) {
			// 失败回调
			super.onFailure(code, msg, data);

			new CaptchaHandler(AstGamePlatform.getInstance().isDebugMode(), capHttpCallback).get();

			if (code == AstRegActivity.CAPTCHA_ERROR) {
				new CaptchaHandler(AstGamePlatform.getInstance().isDebugMode(), capHttpCallback).get();
				mHandler.sendEmptyMessage(AstRegActivity.CAPTCHA_ERROR);
			} else if (null != AstRegActivity.mRegCallbackListener) {
				AstRegActivity.mRegCallbackListener.regFail(code, msg);
			}
			mainSubmit.setClickable(true);
			if (loadingDialog != null) {
				AstRegActivity.dismissDialog(loadingDialog);
			}
			if ((PERFECT_USER_PAY_ACTION_TYPE == action) || (PERFECT_USER_ACTION_TYPE == action)) {
				Toast.makeText(AstRegActivity.this, "".equals(msg) ? "完善账号失败" : msg, Toast.LENGTH_LONG).show();
			}
		}
	};

	/**
	 * 验证码回调
	 */
	private HttpCallback capHttpCallback = new HttpCallback() {
		public void onSuccess(int code, String msg, Object data) {
			// 成功回调
			super.onSuccess(code, msg, data);

			if (null != data) {
				isNeedCaptcha = true;
				Captcha cp = (Captcha) data;
				captchaId = cp.getCaptchaId();
				captcha.setVisibility(View.VISIBLE);
				Thread th = new Thread(new AstRegActivity.ThirdRunnable(cp));
				th.start();
				Log.d(TAG, "captchaId: " + captchaId);
			}
		}

		public void onFailure(int code, String msg, Object data) {
			// 失败回调
			super.onFailure(code, msg, data);
		}
	};

	/**
	 * 消息处理handler
	 */
	private Handler mHandler = new Handler() {
		public void dispatchMessage(Message msg) {
			switch (msg.what) {
			case MSG_USENAME_EMPTY:
				Toast.makeText(AstRegActivity.this, "用户名不能为空", Toast.LENGTH_LONG).show();
				break;
			case MSG_USENAME_NOT_VALID:
				Toast.makeText(AstRegActivity.this, "用户名5-20个大小写英文字母或数字", Toast.LENGTH_LONG).show();
				break;
			case MSG_PASSWORD_NOT_VALID:
				Toast.makeText(AstRegActivity.this, "密码6-20个字符，不含空格及引号", Toast.LENGTH_LONG).show();
				break;
			case MSG_AGREE:
				Toast.makeText(AstRegActivity.this, "请勾选同意条款", Toast.LENGTH_LONG).show();
				break;
			case LOAD_CAPTCHA:
				captchaView.setImageBitmap(bitmap);
				passwordEditText.setImeOptions(5);
				break;
			case NEED_CAPTCHA:
				Toast.makeText(AstRegActivity.this, "请输入验证码", Toast.LENGTH_LONG).show();
				break;
			case CAPTCHA_ERROR:
				Toast.makeText(AstRegActivity.this, "验证码错误", Toast.LENGTH_LONG).show();
				break;
			case 7:
				Toast.makeText(AstRegActivity.this, AstRegActivity.this .getStringByRes("ast_mob_sdk_reg_succeed"), Toast.LENGTH_LONG).show();
				break;
			case 8:
				goMainRegister();
				break;
			}

			super.dispatchMessage(msg);
		}
	};

	/**
	 * onCreate
	 */
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentViewByName("ast_mob_sdk_regist_container");
		this.phoneNum = GameUtil.getNativePhoneNumber(this);
		this.sourceId = GameUtil.getSource(this);
		this.fragmentManager = getSupportFragmentManager();
		Fragment f = this.fragmentManager.findFragmentByTag("main");
		if ((f != null) && (!f.isDetached())) {
			FragmentTransaction ft = this.fragmentManager.beginTransaction();
			ft.detach(f);
			ft.commit();
		}
		FragmentTransaction transaction = this.fragmentManager.beginTransaction();
		transaction.add(getResIdByName(this, "ast_mob_sdk_reg_myframe"), this.fragment, "main").commit();

		this.headText = ((TextView) findViewById(getResIdByName(this, "ast_mob_sdk_logo_text")));
		this.headText.setText(getStringIdByName(this, "ast_mob_sdk_reg_title_express"));
		this.backButton = findViewById(getResIdByName(this, "ast_mob_sdk_back_img_button"));
		this.backButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				finish();
				perfectAccountCallback();
			}
		});
		this.regMain = ((TextView) findViewById(getResIdByName(this, "ast_mob_sdk_reg_main")));
		ColorStateList csl = getResources().getColorStateList(getResIdByName(this, "color", "ast_mob_sdk_reg_select_tabs_font_color"));
		this.regMain.setTextColor(csl);
		this.regMain.setOnClickListener(this);

		this.action = getIntent().getIntExtra("action", 0);

		if (PERFECT_USER_PAY_ACTION_TYPE == this.action) {
			this.headText.setText(getStringIdByName(this, "ast_mob_sdk_reg_account_perfect"));
			UserInfo user = this.mAstGamePlatform.getUserInfo();
			if (null != user) {
				this.userId = user.getUserId();
			}

			this.orderId = getIntent().getStringExtra("orderId");
			this.extraAppData = getIntent().getStringExtra("extraAppData");
		}

		if (PERFECT_USER_ACTION_TYPE == this.action) {
			this.headText.setText(getStringIdByName(this, "ast_mob_sdk_reg_account_perfect"));
			UserInfo user = this.mAstGamePlatform.getUserInfo();
			if (null != user) {
				this.userId = user.getUserId();
			}
		}

		if (BaseActivity.isLandscape()) {
			this.isCloseBack = false;
		}
		getWindow().setSoftInputMode(18);
	}

	public void onBackPressed() {
		this.isCloseBack = true;
		super.onBackPressed();
		perfectAccountCallback();
	}

	public void finish() {
		super.finish();
		this.isCloseBack = true;
	}

	/**
	 * onRegister
	 */
	public void onRegister(View view) {
		int id = view.getId();
		Logger.d(TAG, "onRegister:" + id);
		if (id == getResIdByName(this, "ast_mob_sdk_reg_main_submit_btn")) {
			String userName = this.userNameEditText.getEditableText().toString();
			String password = this.passwordEditText.getEditableText().toString();

			if ((null == userName) || (userName.trim().equals(""))) {
				this.mHandler.sendEmptyMessage(MSG_USENAME_EMPTY);
				return;
			}

			if ((userName.length() < 5) || (userName.length() > 20)) {
				this.mHandler.sendEmptyMessage(MSG_USENAME_NOT_VALID);
				return;
			}

//			if (GameUtil.isChinese(userName).booleanValue()) {
//				this.mHandler.sendEmptyMessage(MSG_USENAME_NOT_VALID);
//				return;
//			}

			if ((null == password) || (password.trim().equals(""))) {
				this.mHandler.sendEmptyMessage(MSG_PASSWORD_NOT_VALID);
				return;
			}

			if ((password.length() < 6) || (password.length() > 20)) {
				this.mHandler.sendEmptyMessage(MSG_PASSWORD_NOT_VALID);
				return;
			}

			if (this.isNeedCaptcha) {
				if ((this.captchaEditText.getText() == null) || (this.captchaEditText.getText().toString().trim().length() <= 0)) {
					this.mHandler.sendEmptyMessage(NEED_CAPTCHA);
					return;
				}
			}

			if (!this.isMainAgree) {
				this.mHandler.sendEmptyMessage(MSG_AGREE);
				return;
			}

			userName = GameUtil.addSuffix(userName);
			String gameId = String.valueOf(AstGamePlatform.getInstance().getAppInfo().gameId);
			if ((PERFECT_USER_PAY_ACTION_TYPE == this.action) || (PERFECT_USER_ACTION_TYPE == this.action)) {
				if (null == this.userId) {
					Toast.makeText(this, "当前用户信息为空", Toast.LENGTH_LONG).show();
					return;
				}
				String tmpPlayerId = null;
				UserInfo user = this.mAstGamePlatform.getUserInfo();
				if (null != user && user.isQuickUser()) {
					tmpPlayerId = user.getTmpPlayerId();
				}
				// 直接绑定 TODO
				new BindQuickUserHandler(AstGamePlatform.getInstance().isDebugMode(), regMainHttpCallback, tmpPlayerId, userName, password).post();
//				// 完善账号
//				new RegHandler(AstGamePlatform.getInstance().isDebugMode(), this.regMainHttpCallback, userName, password, gameId, tmpPlayerId).post();
			} else {
				// 正常注册
				new RegHandler(AstGamePlatform.getInstance().isDebugMode(), this.regMainHttpCallback, userName, password, gameId).post();
			}

			this.mainSubmit.setClickable(false);

			this.loadingDialog = createLoadingDiaLog(this, getStringByRes("ast_mob_sdk_reg_tips"));
			this.loadingDialog.show();
		}
	}

	@SuppressLint({ "ResourceAsColor" })
	public void onClick(View v) {
		int id = v.getId();
		ColorStateList csl = getResources().getColorStateList(getResIdByName(this, "color", "ast_mob_sdk_reg_select_tabs_font_color"));
		if (getResIdByName(this, "ast_mob_sdk_reg_main") == id) {
			this.regMain.setTextColor(csl);
		}
	}

	public void goMainRegister() {
		View v = new View(this);
		v.setId(getResIdByName(this, "ast_mob_sdk_reg_main"));
		onClick(v);
	}

	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	/**
	 * onDestory
	 */
	protected void onDestroy() {
		super.onDestroy();

		if ((null != mRegCallbackListener) && (this.isCloseBack)) {
			mRegCallbackListener = null;
		}
		if ((null != mPayCallbackListener) && (this.isCloseBack)) {
			mPayCallbackListener = null;
		}
		if ((null != mPerfectAccountListener) && (this.isCloseBack)) {
			mPerfectAccountListener = null;
		}

		if (null != this.loadingDialog) {
			try {
				this.loadingDialog.dismiss();
			} catch (Throwable e) {
				e.printStackTrace();
			}
			this.loadingDialog = null;
		}
	}

	/**
	 * 注册
	 * @param ctx
	 * @param registCallbackListener
	 */
	public static void register(Context ctx, RegistCallbackListener registCallbackListener) {
		mRegCallbackListener = registCallbackListener;
		Intent intent = new Intent(ctx, AstRegActivity.class);
		intent.putExtra("action", REGISTE_USER_ACTION_TYPE);
		ctx.startActivity(intent);
	}

	/**
	 * 完善账号
	 * @param ctx
	 * @param orderId
	 * @param extraAppData
	 * @param listener
	 */
	public static void perfectAccount(Context ctx, String orderId, String extraAppData, PayCallbackListener listener) {
		mPayCallbackListener = listener;
		Intent intent = new Intent(ctx, AstRegActivity.class);
		intent.putExtra("action", PERFECT_USER_PAY_ACTION_TYPE);
		intent.putExtra("extraAppData", extraAppData);
		intent.putExtra("orderId", orderId);
		ctx.startActivity(intent);
	}

	/**
	 * 完善账号
	 * @param ctx
	 * @param listener
	 */
	public static void perfectAccount(Context ctx, PerfectAccountListener listener) {
		mPerfectAccountListener = listener;
		Intent intent = new Intent(ctx, AstRegActivity.class);
		intent.putExtra("action", PERFECT_USER_ACTION_TYPE);
		ctx.startActivity(intent);
	}

	/**
	 * 完善账号回调
	 */
	private void perfectAccountCallback() {
		if ((PERFECT_USER_PAY_ACTION_TYPE == this.action) && (mPayCallbackListener != null)) {
			mPayCallbackListener.paySuccess(this.orderId, this.extraAppData);
		}

		if ((PERFECT_USER_ACTION_TYPE == this.action) && (mPerfectAccountListener != null)) {
			mPerfectAccountListener.failure(10002);
		}
	}

	/**
	 * onResume
	 */
	protected void onResume() {
		super.onResume();
	}

	/**
	 * 注册页fragment
	 * @author zhouwenjia
	 *
	 */
	public class TabFragment extends Fragment {
		private OnRegisterListener mListener;

		public TabFragment() {
		}

		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setRetainInstance(true);
		}

		public void onResume() {
			super.onResume();
		}

		/**
		 * onCreateView
		 */
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View v = inflater.inflate(AstRegActivity.getResIdByName(getActivity(), "layout", "ast_mob_sdk_regist_main"), container, false);
			userNameEditText = ((EditText) v.findViewById(AstRegActivity.getResIdByName(getActivity(), "ast_mob_sdk_reg_username")));

			userNameEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
						public void onFocusChange(View v, boolean hasFocus) {
							if (hasFocus) {
								// TODO
							}
						}
					});
			passwordEditText = ((EditText) v.findViewById(AstRegActivity.getResIdByName(getActivity(), "ast_mob_sdk_reg_passpword")));
			passwordEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
						public void onFocusChange(View v, boolean hasFocus) {
							if (hasFocus) {
								// TODO
							}
						}
					});
			passwordEditText.setImeOptions(2);

			passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
						public boolean onEditorAction(TextView arg0, int arg1, KeyEvent arg2) {
							if (arg1 == 2) {
								AstRegActivity.TabFragment.this.mListener.onRegister(mainSubmit);
								return true;
							}
							return false;
						}
					});
			captchaView = ((ImageView) v.findViewById(AstRegActivity.getResIdByName(getActivity(), "ast_mob_sdk_reg_captcha")));
			captcha = v.findViewById(AstRegActivity.getResIdByName(getActivity(), "ast_mob_sdk_reg_captcha_bg"));
			captchaEditText = ((EditText) v.findViewById(AstRegActivity.getResIdByName(getActivity(), "ast_mob_sdk_reg_captcha_input")));
			captchaEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
						public boolean onEditorAction(TextView arg0, int arg1, KeyEvent arg2) {
							if (arg1 == 2) {
								TabFragment.this.mListener.onRegister(mainSubmit);
								return true;
							}
							return false;
						}
					});
			captchaView.setOnClickListener(captchaOnClickListener);
			new CaptchaHandler(AstGamePlatform.getInstance().isDebugMode(), capHttpCallback).get();

			mainSubmit = ((Button) v.findViewById(AstRegActivity.getResIdByName(getActivity(), "ast_mob_sdk_reg_main_submit_btn")));
			mainSubmit.setOnClickListener(new View.OnClickListener() {
						public void onClick(View v) {
							TabFragment.this.mListener.onRegister(mainSubmit);
						}
					});
			if ((PERFECT_USER_PAY_ACTION_TYPE == action) || (PERFECT_USER_ACTION_TYPE == action)) {
				mainSubmit.setText(AstRegActivity.getStringIdByName(getActivity(), "ast_mob_sdk_reg_account_perfect_ok"));
			}
			return v;
		}

		public void onAttach(Activity activity) {
			super.onAttach(activity);
			try {
				this.mListener = ((OnRegisterListener) activity);
			} catch (ClassCastException e) {
				throw new ClassCastException(activity.toString() + "must implement OnRegisterListener");
			}
		}

		public String toString() {
			return "TabFragment";
		}
	}

	/**
	 * 下载验证码
	 * @author zhouwenjia
	 *
	 */
	private class ThirdRunnable implements Runnable {
		private Captcha captcha;

		public ThirdRunnable(Captcha captcha) {
			this.captcha = captcha;
		}

		public void run() {
			try {
				URL picUrl = new URL(this.captcha.getUrl());
				bitmap = BitmapFactory.decodeStream(picUrl.openStream());
				mHandler.sendEmptyMessage(4);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
