package com.reign.ast.sdk;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.reign.ast.sdk.http.HttpCallback;
import com.reign.ast.sdk.http.handler.AstLoginHandler;
import com.reign.ast.sdk.listener.LoginCallbackListener;
import com.reign.ast.sdk.listener.PerfectAccountListener;
import com.reign.ast.sdk.listener.RegistCallbackListener;
import com.reign.ast.sdk.listener.SwitchUserListener;
import com.reign.ast.sdk.manager.AstGamePlatform;
import com.reign.ast.sdk.manager.UserManager;
import com.reign.ast.sdk.pojo.GameAccount;
import com.reign.ast.sdk.pojo.UserInfo;
import com.reign.ast.sdk.util.DisplayUtil;
import com.reign.ast.sdk.util.GameUtil;
import com.reign.ast.sdk.util.Logger;

/**
 * 登陆 activity
 * 
 * @author zhouwenjia
 *
 */
public class AstLoginActivity extends BaseActivity {
	private static final String TAG = AstLoginActivity.class.getSimpleName();
	private static final int SWITCH_USER_ACTION_TYPE = 1;
	private static final int SWITCH_USER_FLOAT_ACTION_TYPE = 2;
	private static final int USER_LOGIN_ACTION_TYPE = 3;
	private static final int LOGIN_ACTION_TYPE = 4;
	private static final int LOGIN_USER_PWD_ACTION_TYPE = 5;
	private Dialog dialog;
	private TextView tips;
	private AstGamePlatform mAstGamePlatform = AstGamePlatform.getInstance();

	/** 消息处理器 */
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SWITCH_USER_ACTION_TYPE:
				tips.setText(AstLoginActivity
						.getStringIdByName(AstLoginActivity.this,
								"ast_mob_sdk_loading_express_id"));
				break;
			case SWITCH_USER_FLOAT_ACTION_TYPE:
				tips.setText(AstLoginActivity.getStringIdByName(
						AstLoginActivity.this, "ast_mob_sdk_loading_main_id"));
				break;
			case USER_LOGIN_ACTION_TYPE:
				Toast.makeText(AstLoginActivity.this, "用户名或者密码不能为空",
						Toast.LENGTH_LONG).show();
				break;
			case LOGIN_ACTION_TYPE:
				Toast.makeText(AstLoginActivity.this, "登录账号成功",
						Toast.LENGTH_LONG).show();
				break;
			case LOGIN_USER_PWD_ACTION_TYPE:
				Toast.makeText(AstLoginActivity.this, msg.obj.toString(),
						Toast.LENGTH_LONG).show();
				break;
			case 6:
				Toast.makeText(AstLoginActivity.this, "登录账号成功",
						Toast.LENGTH_LONG).show();
				finish();
				break;
			case 7:
				Toast.makeText(AstLoginActivity.this,
						msg.obj != null ? msg.obj.toString() : "注册账号失败",
						Toast.LENGTH_LONG).show();
				break;
			case 8:
				Toast.makeText(AstLoginActivity.this, "注册账号成功",
						Toast.LENGTH_LONG).show();
				break;
			case 9:
				Toast.makeText(AstLoginActivity.this, "切换账号成功",
						Toast.LENGTH_LONG).show();
				break;
			default:
				Toast.makeText(AstLoginActivity.this, (String) msg.obj,
						Toast.LENGTH_LONG).show();
			}
		}
	};
	private EditText etUsername;
	private EditText etPasswd;
	private ImageView ivRemeberPwd;
	// private Button btRegist;
	private Button btLogin;

	private boolean isRemeberPwd = true;

	private String[] accounts = null;
	private String guest = "";
	/** 账号map */
	private Map<String, String> accountMap = new HashMap<String, String>();

	/** onClick监听 */
	private OnClickListener singleOnClickListener = new OnClickListener() {
		public void onClick(DialogInterface dialog, int which) {
			if ((which >= 0) && (null != accounts)) {
				String user = accounts[which];
				etUsername.setText(user);
				String pwd = (String) accountMap.get(accounts[which]);
				etPasswd.setText(pwd);
				dialog.dismiss();
				if (guest.equals(user)) {
					// quickLogin();
					return;
				}

				if ((user == null) || ("".equals(user)) || (pwd == null)
						|| ("".equals(pwd))) {
					return;
				}
				mUserName = GameUtil.addSuffix(user);
				mPassword = pwd;
				login(user, pwd);
			} else if (which != -1) {
				if (which == -2) {
					dialog.dismiss();
				}
			}
		}
	};
	private int action;
	private String user;
	private String password;
	private Dialog loadingDialog;
	private String mUserName;
	private String mPassword;
	/** 回调 */
	private HttpCallback mHttpCallback = new HttpCallback() {
		public void onSuccess(int code, String msg, Object data) {
			// 成功回调
			mUserName = GameUtil.addSuffix(mUserName);
			if (!isRemeberPwd) {
				mPassword = "";
				accountMap.put(mUserName, mPassword);
			}
			UserInfo oldUser = AstGamePlatform.getInstance().getUserInfo();
			UserInfo newUser = (UserInfo) data;

			GameUtil.saveAccountInfo(AstLoginActivity.this, new GameAccount(
					GameAccount.COMMON, newUser.getUserName(), mPassword,
					newUser.getToken()));

			AstGamePlatform.getInstance().setUserInfo(
					newUser.setUserName(newUser.getUserName()));

			UserManager.getInstance().refreshUser(newUser);

			GameUtil.loginTypeMark(AstLoginActivity.this, GameAccount.COMMON);

			btLogin.setClickable(true);

			Message mg = new Message();
			mg.what = 6;
			mHandler.sendMessage(mg);

			finish();

			if (null != loadingDialog) {
				AstLoginActivity.dismissDialog(loadingDialog);
			}
			boolean isSameUser = (null != oldUser && oldUser.getUserId()
					.equalsIgnoreCase(newUser.getUserId()));
			if (!isSameUser) {
				// 切换用户!!
				loginSuccessCallback(code, newUser);
			}
		}

		public void onFailure(int code, String msg, Object data) {
			// 失败回调
			super.onFailure(code, msg, data);

			btLogin.setClickable(true);

			etPasswd.setText("");
			if (null != loadingDialog) {
				AstLoginActivity.dismissDialog(loadingDialog);
			}
			Message mg = new Message();
			mg.obj = msg;
			mg.what = 5;
			mHandler.sendMessage(mg);
			loginFailCallback(code, msg);
		}
	};

	/** 注册回调监听 */
	private RegistCallbackListener mRegistCallbackListener = new RegistCallbackListener() {
		public void regSucess(int code, String msg, String userName,
				String passpword) {
			// 成功回调
			Message mg = new Message();
			mg.obj = msg;
			mg.what = 8;
			mHandler.sendMessage(mg);

			etUsername.setText(userName);
			mUserName = userName;
			mPassword = passpword;

			etPasswd.setText(mPassword);
			Logger.d(AstLoginActivity.TAG, "After registration login:"
					+ mUserName + " : " + mPassword);

			String gameId = String.valueOf(AstGamePlatform.getInstance()
					.getAppInfo().gameId);

			new AstLoginHandler(AstGamePlatform.getInstance().isDebugMode(),
					mHttpCallback, GameUtil.addSuffix(userName), passpword,
					gameId).post();
		}

		public void regFail(int code, String msg) {
			// 失败回调
			Message mg = new Message();
			mg.obj = msg;
			mg.what = 7;
			mHandler.sendMessage(mg);
		}
	};

	private static LoginCallbackListener mLoginCallbackListener;

	private static SwitchUserListener mSwitchUserListener;

	private static AlertDialog mAlertDialog;

	/**
	 * 记住密码
	 * 
	 * @param view
	 */
	public void onClickRemeberPwd(View view) {
		if (this.isRemeberPwd) {
			this.ivRemeberPwd
					.setBackgroundResource(BaseActivity.getDrawbleIdByName(
							this, "ast_mob_sdk_login_remeber_pwd_d"));
			this.isRemeberPwd = false;
		} else {
			this.ivRemeberPwd
					.setBackgroundResource(BaseActivity.getDrawbleIdByName(
							this, "ast_mob_sdk_login_remeber_pwd_s"));
			this.isRemeberPwd = true;
		}
	}

	/**
	 * 获得账号信息
	 */
	private void getAccountInfos() {
		try {
			List<GameAccount> user = GameUtil.getUsers(this);
			int size = user.size();
			if (0 == size) {
				this.accounts = new String[1];
				this.accounts[0] = this.guest;
				return;
			}
			this.accounts = new String[size + 1];

			for (int i = 0; i < size; i++) {
				if (((GameAccount) user.get(i)).type != 0) {
					this.accounts[i] = ((GameAccount) user.get(i)).name;
					String pwd = ((GameAccount) user.get(i)).pwd;
					if (null == pwd) {
						pwd = "";
					}
					this.accountMap.put(this.accounts[i], pwd);
				}
			}
			// 最后默认添加guest账号
			this.accounts[size] = this.guest;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * login drop down
	 * 
	 * @param view
	 */
	public void onLoginDropDown(View view) {
		if ((null != this.accounts) && (this.accounts.length > 0)) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("请选择账号登录");
			builder.setSingleChoiceItems(this.accounts, 0,
					this.singleOnClickListener);
			builder.setNegativeButton("取消", this.singleOnClickListener);
			builder.show();
		}
	}

	// /**
	// * 快速登陆
	// */
	// public void quickLogin() {
	// this.loadingDialog = createLoadingDiaLog(this,
	// getString(getStringIdByName(this, "ast_mob_sdk_login_tips")));
	// this.loadingDialog.show();
	// String gameId = String.valueOf(AstGamePlatform.getInstance()
	// .getAppInfo().gameId);
	// new QuickRegHandler(AstGamePlatform.getInstance().isDebugMode(),
	// new HttpCallback() {
	// public void onSuccess(int code, String msg, Object data) {
	// // 成功回调
	// mHandler.post(new Runnable() {
	// public void run() {
	// if (mAstGamePlatform.isLogin()) {
	// Toast.makeText(AstLoginActivity.this,
	// "切换账号成功", Toast.LENGTH_LONG).show();
	// } else {
	// Toast.makeText(AstLoginActivity.this,
	// "登录账号成功", Toast.LENGTH_LONG).show();
	// }
	// }
	// });
	// UserInfo user = (UserInfo) data;
	// GameAccount account = null;
	// if (user.isQuickUser()) {
	// // 快速注册用户
	// account = new GameAccount(GameAccount.QUICK, String
	// .valueOf(user.getUserId()), "");
	// } else {
	// // 已绑定用户
	// account = new GameAccount(GameAccount.COMMON, user
	// .getUserName(), "");
	// }
	// GameUtil.saveAccountInfo(AstLoginActivity.this, account);
	//
	// UserManager.getInstance().refreshUser(user);
	// GameUtil.loginTypeMark(AstLoginActivity.this,
	// GameAccount.QUICK);
	// UserInfo oldUser = mAstGamePlatform.getUserInfo();
	// mAstGamePlatform.setUserInfo(user);
	// finish();
	// if (null != loadingDialog) {
	// AstLoginActivity.dismissDialog(loadingDialog);
	// }
	//
	// boolean isSameUser = (null != oldUser && oldUser
	// .getUserId().equalsIgnoreCase(user.getUserId()));
	// if (!isSameUser) {
	// loginSuccessCallback(code, user);
	// }
	// Logger.d(AstLoginActivity.TAG, "quick login success.");
	// }
	//
	// public void onFailure(int code, String msg, Object data) {
	// // 失败回调
	// Logger.d(AstLoginActivity.TAG, "quick login failure.");
	// if (null != loadingDialog) {
	// AstLoginActivity.dismissDialog(loadingDialog);
	// }
	// Message mg = new Message();
	// mg.obj = msg;
	// mg.what = 5;
	// mHandler.sendMessage(mg);
	// loginFailCallback(code, msg);
	// }
	// }, gameId).post();
	// }

	/**
	 * onCreate
	 */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		String phoneNumber = getIntent().getStringExtra("phoneNumber");
		if (phoneNumber != null) {
			guest = phoneNumber;
		} else {
			if (AstGamePlatform.getInstance().getCurrentUser() != null) {
				guest = AstGamePlatform.getInstance().getCurrentUser();
			}
		}
		this.action = getIntent().getIntExtra("action", 0);

		if (USER_LOGIN_ACTION_TYPE == this.action) {
			this.user = getIntent().getStringExtra("user");
		}
		if (LOGIN_USER_PWD_ACTION_TYPE == this.action) {
			this.user = getIntent().getStringExtra("user");
			this.password = getIntent().getStringExtra("password");
		}
		if (SWITCH_USER_FLOAT_ACTION_TYPE == this.action) {
			// TODO
		}
		Logger.d(TAG, "density:" + DisplayUtil.getScale(this));

		View view = LayoutInflater.from(this).inflate(
				getLayoutIdByName(this, "ast_mob_sdk_login"),
				(ViewGroup) getWindow().getDecorView(), false);
		view.findViewById(R.id.ast_back_icon).setOnClickListener(
				new android.view.View.OnClickListener() {
					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						finish();
						startActivity(new Intent(AstLoginActivity.this,
								MainLoginActivity.class));
					}
				});

		view.findViewById(R.id.reget_pass_btn).setOnClickListener(
				new android.view.View.OnClickListener() {

					@Override
					public void onClick(View view) {
						// TODO Auto-generated method stub
						startActivity(new Intent(AstLoginActivity.this,
								ForgetPasswdActivity.class));
						finish();
					}
				});

		view.findViewById(R.id.register_btn).setOnClickListener(
				new android.view.View.OnClickListener() {

					@Override
					public void onClick(View view) {
						// TODO Auto-generated method stub
						startActivity(new Intent(AstLoginActivity.this,
								RegisterAstActivity.class));
						finish();
					}
				});

		this.etUsername = ((EditText) view.findViewById(BaseActivity
				.getResIdByName(this, "ast_mob_sdk_login_username_et")));
		this.etUsername.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			public void afterTextChanged(Editable s) {
				// text changed
				String content = s.toString();
				Logger.d(AstLoginActivity.TAG, "User: text changed:" + content);
				if (null != content)
					if (!accountMap.containsKey(content)) {
						if (null != etPasswd) {
							etPasswd.setText(null);
						}
					} else if (null != etPasswd) {
						etPasswd.setText((CharSequence) accountMap.get(content));
					}
			}
		});
		this.etUsername
				.setOnFocusChangeListener(new View.OnFocusChangeListener() {
					public void onFocusChange(View v, boolean hasFocus) {
						if (hasFocus) {
							// TODO
						}
					}
				});
		getAccountInfos();

		this.etPasswd = ((EditText) view.findViewById(BaseActivity
				.getResIdByName(this, "ast_mob_sdk_login_passwd_et")));
		this.etPasswd
				.setOnEditorActionListener(new TextView.OnEditorActionListener() {
					public boolean onEditorAction(TextView arg0, int arg1,
							KeyEvent arg2) {
						if (arg1 == 2) {
							login(arg0);
							return true;
						}
						return false;
					}
				});
		this.etPasswd
				.setOnFocusChangeListener(new View.OnFocusChangeListener() {
					public void onFocusChange(View v, boolean hasFocus) {
						if (hasFocus) {
							// TODO
						}
					}
				});
		// this.ivRemeberPwd = ((ImageView)
		// view.findViewById(BaseActivity.getResIdByName(this,
		// "ast_mob_sdk_login_remeber_btn")));
		this.btLogin = ((Button) view.findViewById(BaseActivity.getResIdByName(
				this, "ast_mob_sdk_login_btn")));
		// this.btRegist = ((Button)
		// view.findViewById(BaseActivity.getResIdByName(this,
		// "ast_mob_sdk_login_regist_btn")));

		ViewGroup.LayoutParams vlp = view.getLayoutParams();
		this.dialog = new Dialog(this, getStyleIdByName(this,
				"ast_mob_sdk_login_FullHeightDialog"));
		this.dialog.getWindow().setBackgroundDrawableResource(17170445);
		this.dialog.setCancelable(false);
		// 显示dialog
		this.dialog.show();
		this.dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
			public void onCancel(DialogInterface dialog) {
				cancel();
			}
		});
		this.dialog.setContentView(view, vlp);
		dialog.setCancelable(true);
		dialog.setCanceledOnTouchOutside(false);
		dialog.setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface arg0) {
				// TODO Auto-generated method stub
				dialog.findViewById(R.id.ast_back_icon).callOnClick();
			}
		});

		if (this.action == USER_LOGIN_ACTION_TYPE) {
			this.etUsername.setText(this.user);
		} else if (this.action == LOGIN_USER_PWD_ACTION_TYPE) {
			this.etUsername.setText(this.user);
			this.etPasswd.setText(this.password);
		} else if ((this.accounts != null) && (this.accounts.length > 0)) {
			this.mUserName = this.accounts[0];
			this.mPassword = ((String) this.accountMap.get(this.mUserName));
			this.etUsername.setText(this.accounts[0]);
			this.etPasswd.setText(this.mPassword);
		}

	}

	/**
	 * 登陆
	 * 
	 * @param v
	 */
	public void login(View v) {
		Logger.d(TAG, "click login button.");
		this.mUserName = this.etUsername.getEditableText().toString();
		this.mPassword = this.etPasswd.getEditableText().toString();
		// if ("guest".equals(this.mUserName)) {
		// // quickLogin();
		// } else {
		if ((this.mUserName == null) || (this.mUserName.trim().equals(""))
				|| (this.mPassword == null)
				|| (this.mPassword.trim().equals(""))) {
			this.mHandler.sendEmptyMessage(3);
			return;
		}
		this.etPasswd.setText(this.mPassword);
		login(this.mUserName, this.mPassword);
		// }
	}

	/**
	 * 登陆
	 * 
	 * @param user
	 * @param pwd
	 */
	private void login(String user, String pwd) {
		this.btLogin.setClickable(false);

		user = GameUtil.addSuffix(user);
		String gameId = String.valueOf(AstGamePlatform.getInstance()
				.getAppInfo().gameId);
		new AstLoginHandler(AstGamePlatform.getInstance().isDebugMode(),
				this.mHttpCallback, user, pwd, gameId).post();
		this.loadingDialog = createLoadingDiaLog(this,
				getString(getStringIdByName(this, "ast_mob_sdk_login_tips")));
		this.loadingDialog.show();
	}

	/**
	 * 登陆成功回调
	 * 
	 * @param code
	 * @param userInfo
	 */
	private void loginSuccessCallback(int code, UserInfo userInfo) {
		if ((this.action == SWITCH_USER_ACTION_TYPE)
				|| (this.action == SWITCH_USER_FLOAT_ACTION_TYPE)) {
			this.mHandler.sendEmptyMessage(9);
		}

		if (null != mSwitchUserListener) {
			mSwitchUserListener.switchSuccess(code, userInfo);
			mSwitchUserListener = null;
			// 切换账号后, 重置menuBar
			AstGamePlatform.getInstance().resetMenuBar();
		}

		if (null != mLoginCallbackListener) {
			mLoginCallbackListener.loginSuccess(code, userInfo);
			mLoginCallbackListener = null;
		}
	}

	/**
	 * 登陆失败回调
	 * 
	 * @param code
	 * @param msg
	 */
	private void loginFailCallback(int code, String msg) {
		if (null != mSwitchUserListener) {
			mSwitchUserListener.switchFail(code, msg);
			mSwitchUserListener = null;
		}

		if (null != mLoginCallbackListener) {
			mLoginCallbackListener.loginFail(code, msg);
			mLoginCallbackListener = null;
		}
	}

	/**
	 * 注册
	 * 
	 * @param view
	 */
	public void register(View view) {
		UserInfo mUserInfo = this.mAstGamePlatform.getUserInfo();

		if ((mUserInfo != null) && (mUserInfo.isQuickUser())) {
			AstRegActivity.perfectAccount(this, new PerfectAccountListener() {
				public void success() {
					// 成功回调
					finish();
				}

				public void failure(int errorCode) {
					// 失败回调
					Log.e(AstLoginActivity.TAG, "perfectAccount errorCode:"
							+ errorCode);
				}
			});
		} else {
			AstRegActivity.register(this, this.mRegistCallbackListener);
		}
	}

	/**
	 * 切换账号
	 * 
	 * @param context
	 * @param switchUserListener
	 */
	public static void switchUser(Context context,
			SwitchUserListener switchUserListener) {
		if (AstGamePlatform.getInstance().isLogin()) {
			UserInfo mUserInfo = AstGamePlatform.getInstance().getUserInfo();
			if (mUserInfo.isQuickUser()) {
				Toast.makeText(context, "快速注册用户，请完善账号", Toast.LENGTH_LONG)
						.show();
			}
		}
		switchUserInner(context, switchUserListener);
	}

	/**
	 * 切换账号
	 * 
	 * @param context
	 * @param switchUserListener
	 */
	private static void switchUserInner(Context context,
			SwitchUserListener switchUserListener) {
		Logger.d(TAG, "switchUser");
		Intent intent = new Intent(context, AstLoginActivity.class);
		intent.putExtra("action", SWITCH_USER_ACTION_TYPE);
		context.startActivity(intent);
		mSwitchUserListener = switchUserListener;

	}

	/**
	 * 切换账号
	 * 
	 * @param context
	 * @param switchUserListener
	 */
	private static void switchUserFloatInner(Context context,
			SwitchUserListener switchUserListener) {
		Logger.d(TAG, "switchUserFloat");
		Intent intent = new Intent(context, AstLoginActivity.class);
		intent.putExtra("action", SWITCH_USER_FLOAT_ACTION_TYPE);
		context.startActivity(intent);
		mSwitchUserListener = switchUserListener;
	}

	/**
	 * 切换账号float
	 * 
	 * @param context
	 * @param switchUserListener
	 */
	public static void switchUserFloat(Context context,
			SwitchUserListener switchUserListener) {
		if (AstGamePlatform.getInstance().isLogin()) {
			UserInfo mUserInfo = AstGamePlatform.getInstance().getUserInfo();
			if (mUserInfo.isQuickUser()) {
				Toast.makeText(context, "快速注册用户，请完善账号", Toast.LENGTH_LONG)
						.show();
			}
		}
		switchUserFloatInner(context, switchUserListener);
	}

	/**
	 * login
	 * 
	 * @param ctx
	 * @param user
	 * @param loginCallbackListener
	 */
	public static void login(Context ctx, String user,
			LoginCallbackListener loginCallbackListener) {
		Logger.d(TAG, "user login");
		Intent intent = new Intent(ctx, AstLoginActivity.class);
		intent.putExtra("action", USER_LOGIN_ACTION_TYPE);
		intent.putExtra("user", user);
		ctx.startActivity(intent);
		mLoginCallbackListener = loginCallbackListener;
	}

	public static void login(Context ctx, String user) {
		Logger.d(TAG, "user login");
		Intent intent = new Intent(ctx, AstLoginActivity.class);
		intent.putExtra("action", USER_LOGIN_ACTION_TYPE);
		intent.putExtra("user", user);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		ctx.startActivity(intent);
	}

	public static void login(Context ctx, String user, String password) {
		Logger.d(TAG, "user login");
		Intent intent = new Intent(ctx, AstLoginActivity.class);
		intent.putExtra("action", LOGIN_USER_PWD_ACTION_TYPE);
		intent.putExtra("user", user);
		intent.putExtra("password", password);
		ctx.startActivity(intent);
	}

	public static void login(Context ctx,
			LoginCallbackListener loginCallbackListener) {
		Logger.d(TAG, "default login");
		Intent intent = new Intent(ctx, AstLoginActivity.class);
		intent.putExtra("action", LOGIN_ACTION_TYPE);
		ctx.startActivity(intent);
		mLoginCallbackListener = loginCallbackListener;
	}

	private void cancel() {
		if (null != mLoginCallbackListener) {
			mLoginCallbackListener.loginFail(1, "login cancel!");
			mLoginCallbackListener = null;
		}
		if (null != mSwitchUserListener) {
			mSwitchUserListener.switchFail(1, "switch user cancel!");
			mSwitchUserListener = null;
		}
		finish();
	}

	public static void startAstLoginActivity(Context ctx,
			SwitchUserListener listener) {
		mSwitchUserListener = listener;
		Intent intent = new Intent(ctx, AstLoginActivity.class);
		ctx.startActivity(intent);
	}

	/**
	 * back
	 */
	public void onBackPressed() {
		super.onBackPressed();
		Logger.d(TAG, "onBackPressed");
		cancel();
	}

	protected void onDestroy() {
		super.onDestroy();
		if (null != this.dialog) {
			this.dialog.dismiss();
		}
		if (null != mAlertDialog)
			mAlertDialog.dismiss();
	}
}
