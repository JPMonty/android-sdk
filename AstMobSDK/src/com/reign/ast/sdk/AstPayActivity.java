package com.reign.ast.sdk;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.TextView;

import com.reign.ast.sdk.fragments.AlipayFragment;
import com.reign.ast.sdk.fragments.MobileFragment;
import com.reign.ast.sdk.fragments.TelcomFragment;
import com.reign.ast.sdk.fragments.UnicomFragment;
import com.reign.ast.sdk.listener.PayCallbackListener;
import com.reign.ast.sdk.util.Logger;

/**
 * 充值
 * @author zhouwenjia
 *
 */
@SuppressLint({ "ValidFragment" })
public class AstPayActivity extends BaseActivity implements PayCallbackListener {
	private static final String TAG = AstPayActivity.class.getSimpleName();
	
	/** 充值回调 */
	private static PayCallbackListener mPayCallbackListener;
	private TextView alipay;
	private TextView unicom;
	private TextView telcom;
	private TextView mobile;
	private String mLastTag;
	private List<TextView> tags = new ArrayList<TextView>();
	private Map<String, Class<?>> classMap = new HashMap<String, Class<?>>();

	/** 参数bundle */
	private static Bundle mBundle = new Bundle();
	private static String extraAppData;
	public static final int CLEAN_DATA_AND_CLOSE_ACTION = 1;
	
	public static final String ALIPAY_TAG = "ast_mob_sdk_pay_left_side_1";
	public static final String UNICOM_TAG = "ast_mob_sdk_pay_left_side_2";
	public static final String TELCOM_TAG = "ast_mob_sdk_pay_left_side_3";
	public static final String MOBILE_TAG = "ast_mob_sdk_pay_left_side_4";
	
	private static final String BG_RES_SELECTED = "ast_mob_sdk_pay_left_side_bg_s";
	private static final String BG_RES_UNSELECTED = "ast_mob_sdk_pay_left_side_bg";
	
	/** 进度提示框 */
	private ProgressDialog mProgress = null;
	
	/** 消息处理器 */
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 1:
				AstPayActivity.this.finish();
				AstPayActivity.this.cleanBundle();
				break;
			}
		}
	};

	/**
	 * back pressed
	 */
	public void onBackPressed() {
		super.onBackPressed();
		if (null != mPayCallbackListener) {
			mPayCallbackListener.payFail(10002, null, extraAppData);
			cleanBundle();
		}
	}

	/**
	 * 左边引导条
	 * @param view
	 */
	public void leftNavigationBar(View view) {
		leftNavigationBar(view.getTag().toString());
	}

	/**
	 * 引导条
	 * @param tag
	 */
	public void leftNavigationBar(String tag) {
		String newTag = tag;
		ColorStateList colorSelect = getResources().getColorStateList(getResIdByName(this, "color", "ast_mob_sdk_pay_left_side_font_color_s"));
		ColorStateList colorDefault = getResources().getColorStateList(getResIdByName(this, "color", "ast_mob_sdk_pay_left_side_font_color_d"));
		if (ALIPAY_TAG.equals(newTag)) {
			// 选择支付宝
			this.alipay.setBackgroundResource(getColorIdByName(this, BG_RES_SELECTED));
			this.alipay.setTextColor(colorSelect);

			this.unicom.setBackgroundResource(getColorIdByName(this, BG_RES_UNSELECTED));
			this.unicom.setTextColor(colorDefault);
			this.telcom.setBackgroundResource(getColorIdByName(this, BG_RES_UNSELECTED));
			this.telcom.setTextColor(colorDefault);
			this.mobile.setBackgroundResource(getColorIdByName(this, BG_RES_UNSELECTED));
			this.mobile.setTextColor(colorDefault);
		}
		if (UNICOM_TAG.equals(newTag)) {
			// 选择联通卡
			this.unicom.setBackgroundResource(getColorIdByName(this, BG_RES_SELECTED));
			this.unicom.setTextColor(colorSelect);

			this.alipay.setBackgroundResource(getColorIdByName(this, BG_RES_UNSELECTED));
			this.alipay.setTextColor(colorDefault);
			this.telcom.setBackgroundResource(getColorIdByName(this, BG_RES_UNSELECTED));
			this.telcom.setTextColor(colorDefault);
			this.mobile.setBackgroundResource(getColorIdByName(this, BG_RES_UNSELECTED));
			this.mobile.setTextColor(colorDefault);
		}
		if (TELCOM_TAG.equals(newTag)) {
			// 选择电信卡
			this.telcom.setBackgroundResource(getColorIdByName(this, BG_RES_SELECTED));
			this.telcom.setTextColor(colorSelect);

			this.unicom.setBackgroundResource(getColorIdByName(this, BG_RES_UNSELECTED));
			this.unicom.setTextColor(colorDefault);
			this.alipay.setBackgroundResource(getColorIdByName(this, BG_RES_UNSELECTED));
			this.alipay.setTextColor(colorDefault);
			this.mobile.setBackgroundResource(getColorIdByName(this, BG_RES_UNSELECTED));
			this.mobile.setTextColor(colorDefault);
		}
		if (MOBILE_TAG.equals(newTag)) {
			// 选择移动卡
			this.mobile.setBackgroundResource(getColorIdByName(this, BG_RES_SELECTED));
			this.mobile.setTextColor(colorSelect);

			this.telcom.setBackgroundResource(getColorIdByName(this, BG_RES_UNSELECTED));
			this.telcom.setTextColor(colorDefault);
			this.unicom.setBackgroundResource(getColorIdByName(this, BG_RES_UNSELECTED));
			this.unicom.setTextColor(colorDefault);
			this.alipay.setBackgroundResource(getColorIdByName(this, BG_RES_UNSELECTED));
			this.alipay.setTextColor(colorDefault);
		}

		if (this.mLastTag != newTag) {
			// 有切换, 判断 TODO
			FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
			if (null != this.mLastTag) {
				Fragment fragment = getSupportFragmentManager().findFragmentByTag(this.mLastTag);
				if (null != fragment) {
					// 隐藏上一个fragment
					ft.hide(fragment);
				}
			}
			if (null != newTag) {
				Fragment fragment = getSupportFragmentManager().findFragmentByTag(newTag);
				if (null == fragment) {
					fragment = Fragment.instantiate(this, ((Class<?>) this.classMap.get(newTag)).getName(), mBundle);
					ft.add(getResIdByName(this, "ast_mob_sdk_pay_myframe"), fragment, newTag);
				} else {
					// 显示新的fragment
					ft.show(fragment);
				}
			}

			this.mLastTag = newTag;
			ft.commit();
			getSupportFragmentManager().executePendingTransactions();
		}
	}
	
	/**
	 * onCreate
	 */
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentViewByName("ast_mob_sdk_pay");
		init();
	}

	/**
	 * 初始化
	 */
	private void init() {
		TextView headText = (TextView) findViewById(getResIdByName(this, "ast_mob_sdk_logo_text"));
		headText.setText(getStringIdByName(this, "ast_mob_sdk_pay_head_title"));

		findViewById(getResIdByName(this, "ast_mob_sdk_back_img_button")).setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						AstPayActivity.this.finish();
						if (AstPayActivity.mPayCallbackListener != null) {
							AstPayActivity.mPayCallbackListener.payFail(10002, null, AstPayActivity.extraAppData);
							AstPayActivity.this.cleanBundle();
						}
					}
				});
		this.alipay = ((TextView) findViewById(getResIdByName(this, ALIPAY_TAG)));
		this.mobile = ((TextView) findViewById(getResIdByName(this, MOBILE_TAG)));
		this.telcom = ((TextView) findViewById(getResIdByName(this, TELCOM_TAG)));
		this.unicom = ((TextView) findViewById(getResIdByName(this, UNICOM_TAG)));

		if ((null != mBundle) && (mBundle.containsKey("hide_channel"))) {
			String payChannel = mBundle.getString("hide_channel");
			// 隐藏充值渠道
			if (!"".equals(payChannel)) {
				try {
					String[] cls = payChannel.split(":");
					for (String c : cls) {
						switch (Integer.parseInt(c)) {
						case 1:
							this.alipay.setVisibility(View.GONE);
							break;
						case 2:
							this.unicom.setVisibility(View.GONE);
							break;
						case 3:
							this.telcom.setVisibility(View.GONE);
							break;
						case 4:
							this.mobile.setVisibility(View.GONE);
							break;
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		// 显示支付宝和19pay
		this.alipay.setVisibility(View.VISIBLE);
		this.mobile.setVisibility(View.VISIBLE);
		this.unicom.setVisibility(View.VISIBLE);
		this.telcom.setVisibility(View.VISIBLE);

		this.tags.add(this.alipay);
		this.tags.add(this.mobile);
		this.tags.add(this.telcom);
		this.tags.add(this.unicom);
		
		this.classMap.put(this.alipay.getTag().toString(), AlipayFragment.class);
		this.classMap.put(this.mobile.getTag().toString(), MobileFragment.class);
		this.classMap.put(this.telcom.getTag().toString(), TelcomFragment.class);
		this.classMap.put(this.unicom.getTag().toString(), UnicomFragment.class);
		for (TextView tv : this.tags) {
			Fragment fragment = getSupportFragmentManager().findFragmentByTag(tv.getTag().toString());
			if ((null != fragment) && (!fragment.isDetached())) {
				FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
				ft.hide(fragment);
				ft.commit();
			}
		}

		SharedPreferences pref = getSharedPreferences("pay_info", 0);
		String payChanel = pref.getString("pay_chanel", "");
		Logger.d(TAG, "payChanel:" + payChanel);
		if ("".equals(payChanel)) {
			leftNavigationBar(ALIPAY_TAG);
		} else {
			leftNavigationBar(payChanel);
		}
	}

	/**
	 * 设置充值渠道
	 * @param context
	 * @param channel
	 * @return
	 */
	public static boolean setPayChannel(Context context, String channel) {
		SharedPreferences pref = context.getSharedPreferences("pay_info", 0);
		SharedPreferences.Editor editor = pref.edit();
		return editor.putString("pay_chanel", channel).commit();
	}

	/**
	 * 充值
	 * @param ctx
	 * @param optionData
	 * @param payCallbackListener
	 * @param amountEditable
	 */
	public static void pay(Context ctx, Map<String, Object> optionData, PayCallbackListener payCallbackListener, boolean amountEditable) {
		Logger.d(TAG, "pay params:" + optionData.toString());
		mPayCallbackListener = payCallbackListener;
		String productName = optionData.containsKey("product_name") ? optionData.get("product_name").toString() : null;
		if (null != productName) {
			// 产品名
			mBundle.putString("product_name", productName);
		}
		String currency = optionData.containsKey("currency") ? optionData.get("currency").toString() : null;
		if (null != currency) {
			// 金额
			mBundle.putString("currency", currency);
		}
		extraAppData = optionData.containsKey("extraAppData") ? optionData.get("extraAppData").toString() : null;
		if (null != extraAppData) {
			// app data
			mBundle.putString("extraAppData", extraAppData);
		}
		
		String roleId = optionData.containsKey("roleId") ? optionData.get("roleId").toString() : null;
		if (null != roleId) {
			mBundle.putString("roleId", roleId);
		}
		
		String serverId = optionData.containsKey("serverId") ? optionData.get("serverId").toString() : null;
		if (null != serverId) {
			mBundle.putString("serverId", serverId);
		}

		float rate = optionData.containsKey("rate") ? Float.parseFloat(optionData.get("rate").toString()) : 0.0F;
		mBundle.putFloat("rate", rate);

//		int amount = optionData.containsKey("amount") ? Integer.parseInt(optionData.get("amount").toString()) : 0;
//		mBundle.putInt("amount", amount);
		float amount = optionData.containsKey("amount") ? Float.parseFloat(optionData.get("amount").toString()) : 0f;
		mBundle.putFloat("amount", amount);

		String user = optionData.containsKey("user") ? optionData.get("user").toString() : null;
		if (null != user) {
			// user
			mBundle.putString("user", user);
		}
		String hideChannel = optionData.containsKey("hide_channel") ? optionData.get("hide_channel").toString() : null;
		if (null != hideChannel) {
			// 需要隐藏的充值渠道
			mBundle.putString("hide_channel", hideChannel);
		}

		String yx = optionData.containsKey("yx") ? optionData.get("yx").toString() : null;
		if (null != yx) {
			// yx
			mBundle.putString("yx", yx);
		}
		String extra = optionData.containsKey("extra") ? optionData.get("extra").toString() : null;
		if (null != yx) {
			// extra
			mBundle.putString("extra", extra);
		} else {
			mBundle.putString("extra", "");
		}
		mBundle.putBoolean("amountEditable", amountEditable);

		Intent intent = new Intent(ctx, AstPayActivity.class);
		ctx.startActivity(intent);
	}

	/**
	 * 充值成功回调
	 */
	public void paySuccess(String orderId, String extraAppData) {
		if (null != mPayCallbackListener) {
			mPayCallbackListener.paySuccess(orderId, extraAppData);
			cleanBundle();
			finish();
		}
	}

	/**
	 * 充值失败回调
	 */
	public void payFail(int code, String orderId, String extraAppData) {
		if (null != mPayCallbackListener) {
			mPayCallbackListener.payFail(code, orderId, extraAppData);
			cleanBundle();
			finish();
		}
	}

	public Handler getHandler() {
		return this.mHandler;
	}
	
	/**
	 * attachProgressDialog
	 * @param progressDialog
	 */
	public void attachProgressDialog(ProgressDialog progressDialog) {
		if (null != mProgress) {
			mProgress.dismiss();
		}
		this.mProgress = progressDialog;
	}

	/**
	 * 清空参数bundle
	 */
	private void cleanBundle() {
		if (mBundle != null) {
			mBundle.clear();
		}
		extraAppData = null;
		mPayCallbackListener = null;
		Logger.d(TAG, "cleanBundle");
	}

	/**
	 * onDestory
	 */
	protected void onDestroy() {
		super.onDestroy();
		Logger.d(TAG, "onDestroy");
		if (null != mProgress) {
			mProgress.dismiss();
		}
	}
}
