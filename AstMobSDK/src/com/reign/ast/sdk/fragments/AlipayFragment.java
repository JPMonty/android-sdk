package com.reign.ast.sdk.fragments;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.reign.ast.sdk.AstPayActivity;
import com.reign.ast.sdk.BaseActivity;
import com.reign.ast.sdk.alipay.AlixPay;
import com.reign.ast.sdk.alipay.MobileSecurePayHelper;
import com.reign.ast.sdk.http.HttpCallback;
import com.reign.ast.sdk.http.handler.CreateOrderHandler;
import com.reign.ast.sdk.listener.PayCallbackListener;
import com.reign.ast.sdk.manager.AstGamePlatform;
import com.reign.ast.sdk.manager.UserManager;
import com.reign.ast.sdk.pojo.Order;
import com.reign.ast.sdk.pojo.UserInfo;
import com.reign.ast.sdk.util.GameUtil;
import com.reign.ast.sdk.util.Logger;

/**
 * 支付宝
 * @author zhouwenjia
 *
 */
public class AlipayFragment extends Fragment implements OnClickListener {
	private static final String TAG = AlipayFragment.class.getSimpleName();
	private String productName;
	private String currency;
	private String extraAppData;
	private float rate;
	private float amount;
	private float totalAmount;
	private String user;
	private boolean amountEditable;
	private PayCallbackListener mListener;
	private TextView tvUser;
	private TextView tvAmount;
	private TextView tvProductName;
	private Context mContext;
	private EditText etInputAmount;
	private Button btPay;
	private String userFormat;
	private String productNameFormat;
	private String amountFormat;
	private String orderId;
	private String roleId;
	private String serverId;
	private String yx;
	private String extra;
	
	public static final int PAY_RET_SUCC = 9000;
	public static final int PAY_RET_FAIL = 6001;
	
	/** 充值http回调 */
	private HttpCallback mPayHttpCallback = new HttpCallback() {
		public void onSuccess(int code, String msg, Object data) {
			// 成功回调
			try {
				Order order = (Order) data;
				orderId = order.getOrderId();
				AlixPay alixPay = new AlixPay(getActivity());
				alixPay.pay(mHandler, order.getSign());
//				// reset the status of pay button to enable
//				btPay.setClickable(true);
			} catch (Exception ex) {
				ex.printStackTrace();

				btPay.setClickable(true);
				mListener.payFail(2, orderId, extraAppData);
			}
		}

		public void onFailure(int code, String msg, Object data) {
			// 失败回调
			String tag = "PayHttpCallback failure code:" + code + " msg:" + msg;
			Logger.e(AlipayFragment.TAG, tag);

			Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();
			mListener.payFail(code, orderId, extraAppData);
			btPay.setClickable(true);
		}
	};
	
	/** 消息处理器 */
	private Handler mHandler = new Handler() {
		public void dispatchMessage(Message msg) {
			switch (msg.what) {
			case 1:
				Object obj = msg.obj;
				String strRet = String.valueOf(obj);
				String[] str = strRet.split(";");
				int resultStatus = 0;
				if (null != str) {
					for (String s : str) {
						s = s.trim();
						if (s.startsWith("resultStatus=")) {
							int index = s.indexOf("{");
							s = s.substring(index + 1, s.length() - 1);
							resultStatus = Integer.valueOf(s).intValue();
							break;
						}
					}
				}
				UserInfo user = AstGamePlatform.getInstance().getUserInfo();

				if (resultStatus == PAY_RET_SUCC) {
					// 充值成功
					if ((null != user) && (user.isQuickUser())) {
						// 快速注册用户
						UserManager.getInstance().perfectAccount(mContext, orderId, extraAppData, mListener);
						mHandler.sendEmptyMessage(2);
					} else {
						// 充值成功
						mListener.paySuccess(orderId, extraAppData);
						Toast.makeText(mContext, "充值成功", Toast.LENGTH_LONG).show();
					}
				} else if (resultStatus != PAY_RET_FAIL) {
					// 充值失败
					mListener.payFail(-1, orderId, extraAppData);
					Toast.makeText(mContext, "抱歉，充值失败", Toast.LENGTH_LONG).show();
				} else {
					// TODO
				}
				btPay.setClickable(true);

				break;
			case 2:
				Toast.makeText(mContext, "支付成功，请及时完善账号", Toast.LENGTH_LONG).show();
				getActivity().finish();
			}
			// reset the status of pay button to enable
			btPay.setClickable(true);
			super.dispatchMessage(msg);
		}
	};

	/**
	 * onCreate
	 */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		Bundle args = getArguments();
		if (null != args) {
			this.productName = args.getString("product_name");
			this.currency = args.getString("currency");
			this.extraAppData = args.getString("extraAppData");
			this.user = args.getString("user");
			this.rate = args.getFloat("rate");
			this.amount = args.getFloat("amount");
			this.roleId = args.getString("roleId");
			this.serverId = args.getString("serverId");
			this.amountEditable = args.getBoolean("amountEditable");
			this.yx = args.getString("yx");
			this.extra = args.getString("extra");
			// TODO
//			this.amount = 0.1f;
			Logger.d(TAG, "Receive parameters:productName:" + this.productName
					+ "currency:" + this.currency + "extraAppData:" + this.extraAppData
					+ "rate:" + this.rate + "amount:" + this.amount + ", roleId:" + this.roleId + ", serverId:" + this.serverId
					+ " amountEditable:" + this.amountEditable);
		}
	}

	/**
	 * onAttach
	 */
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			this.mListener = ((PayCallbackListener) activity);
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + "must implement PayCallbackListener");
		}
	}

	/**
	 * onCreateView
	 */
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		this.mContext = getActivity();
		View view = null;
		if (BaseActivity.isLandscape()) {
			int id = BaseActivity.getResIdByName(this.mContext, "layout", "ast_mob_sdk_pay_alipay_land");
			view = inflater.inflate(id, container, false);
		} else {
			int id = BaseActivity.getResIdByName(this.mContext, "layout", "ast_mob_sdk_pay_alipay");
			view = inflater.inflate(id, container, false);
		}
		this.userFormat = getResources().getString(BaseActivity.getStringIdByName(this.mContext, "ast_mob_sdk_pay_alipay_user"));
		this.amountFormat = getResources().getString(BaseActivity.getStringIdByName(this.mContext, "ast_mob_sdk_pay_alipay_money"));
		this.productNameFormat = getResources().getString(BaseActivity.getStringIdByName(this.mContext, "ast_mob_sdk_pay_alipay_goods"));

		this.tvUser = ((TextView) view.findViewById(BaseActivity.getResIdByName(this.mContext, "ast_mob_sdk_alipay_user")));
		this.tvUser.setText(String.format(this.userFormat, new Object[] { this.user }));

		this.tvAmount = ((TextView) view.findViewById(BaseActivity.getResIdByName(this.mContext, "ast_mob_sdk_alipay_money")));
		this.tvAmount.setText(Html.fromHtml(String.format(this.amountFormat, new Object[] { GameUtil.redFontString(GameUtil.converFloat(this.amount)) })));

		this.tvProductName = ((TextView) view.findViewById(BaseActivity.getResIdByName(this.mContext, "ast_mob_sdk_alipay_goods")));
		this.tvProductName.setText(Html.fromHtml(String.format(this.productNameFormat, new Object[] {GameUtil.redFontString(GameUtil.converFloat(this.amount * this.rate)), this.currency })));

		this.etInputAmount = ((EditText) view.findViewById(BaseActivity.getResIdByName(this.mContext, "ast_mob_sdk_alipay_input")));
		this.etInputAmount.setEnabled(this.amountEditable);
		this.etInputAmount.setOnEditorActionListener(new TextView.OnEditorActionListener() {
					public boolean onEditorAction(TextView arg0, int arg1, KeyEvent arg2) {
						if (arg1 == 2) {
							pay();
							return true;
						}
						return false;
					}
				});
		this.totalAmount = this.amount;

		if (this.amountEditable) {
			this.etInputAmount.addTextChangedListener(new TextWatcher() {
				public void onTextChanged(CharSequence s, int start,int before, int count) {
				}

				public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				}

				public void afterTextChanged(Editable s) {
					String content = s.toString();
					if (!"".equals(content)) {
						Logger.d(AlipayFragment.TAG, "Total amount: text changed:" + content);
						totalAmount = Integer.parseInt(content);
						setRedAmount(totalAmount);
					} else {
						totalAmount = amount;
						setRedAmount(totalAmount);
						Logger.d(AlipayFragment.TAG, "Total amount: text changed:" + amount);
					}
				}
			});
		} else {
			this.etInputAmount.setVisibility(View.GONE);
		}

		this.btPay = ((Button) view.findViewById(BaseActivity.getResIdByName(this.mContext, "ast_mob_sdk_alipay_pay_ok")));
		this.btPay.setOnClickListener(this);
		this.btPay.setClickable(true);

		return view;
	}

	/**
	 * pay
	 */
	private void pay() {

		// after click pay button, set the status of pay button to disable, in case of double click
		this.btPay.setClickable(false);
		
		AstPayActivity.setPayChannel(this.mContext, AstPayActivity.ALIPAY_TAG);

		if (this.totalAmount <= 0) {
			Toast.makeText(this.mContext, "请输入1-100000内的金额", Toast.LENGTH_LONG).show();
			this.btPay.setClickable(true);
			return;
		}

		MobileSecurePayHelper mspHelper = new MobileSecurePayHelper(this.mContext);
		boolean isMobile_spExist = mspHelper.checkAlipayAppExist();
		if (!isMobile_spExist) {
			// 未找到com.alipay.android.app
			this.btPay.setClickable(true);
			return;
		}
		
		// 创建订单
		Map<String, String> dataMap = new HashMap<String, String>();
		dataMap.put("userId", String.valueOf(AstGamePlatform.getInstance().getUserInfo().getUserId()));
		dataMap.put("gameId", String.valueOf(AstGamePlatform.getInstance().getAppInfo().gameId));
		dataMap.put("serverId", this.serverId);
		dataMap.put("roleId", this.roleId);
		dataMap.put("money", String.valueOf(this.totalAmount));
		dataMap.put("gold", GameUtil.converFloat(this.totalAmount * this.rate));
		dataMap.put("yx", yx);
		dataMap.put("gameName", AstGamePlatform.getInstance().getAppInfo().gameName);
		dataMap.put("extraAppData", extraAppData);
		dataMap.put("extra", extra);
		
		new CreateOrderHandler(AstGamePlatform.getInstance().isDebugMode(), this.mPayHttpCallback, dataMap).post();
	}

	/**
	 * 设置红色数量
	 * @param amount
	 */
	private void setRedAmount(float amount) {
		this.tvAmount.setText(Html.fromHtml(String.format(this.amountFormat, new Object[] { GameUtil.redFontString(GameUtil.converFloat(amount)) })));
		this.tvProductName.setText(Html.fromHtml(String.format(this.productNameFormat, new Object[] {GameUtil.redFontString(GameUtil.converFloat(amount * this.rate)), this.currency })));
	}

	/**
	 * click
	 */
	public void onClick(View v) {
		pay();
	}
}
