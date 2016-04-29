package com.reign.ast.sdk.fragments;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.reign.ast.sdk.AstPayActivity;
import com.reign.ast.sdk.BaseActivity;
import com.reign.ast.sdk.http.HttpCallback;
import com.reign.ast.sdk.http.handler.PayCardHandler;
import com.reign.ast.sdk.http.handler.QueryChannelHandler;
import com.reign.ast.sdk.listener.PayCallbackListener;
import com.reign.ast.sdk.manager.AstGamePlatform;
import com.reign.ast.sdk.manager.Constants;
import com.reign.ast.sdk.manager.UserManager;
import com.reign.ast.sdk.pojo.CardChannel;
import com.reign.ast.sdk.pojo.Order;
import com.reign.ast.sdk.pojo.UserInfo;
import com.reign.ast.sdk.util.GameUtil;
import com.reign.ast.sdk.util.Logger;

/**
 * 19pay充值卡
 * @author zhouwenjia
 *
 */
public abstract class BaseCardFragment extends Fragment {
	protected String TAG = logTag();
	private PayCallbackListener mListener;
	private String productName;
	private String currency;
	private String extraAppData;
	private float rate;
	private int amount;
	private String roleId;
	private String serverId;
	private String user;
	private boolean amountEditable;
	private Context mContext;
	private int cardType;
	private String tag;
	private ImageView ivCardIcon;
	private TextView tvUser;
	private TextView tvMoney;
	private TextView tvGoods;
	private Spinner mSelectCard;
	private EditText etCardNumber;
	private EditText etCardPassward;
	private Button btPay;
	private String userFormat;
	private String amountFormat;
	private String productNameFormat;
	private String[] mounts;
	private String yx;
	private String extra;
	
	private String cardAmount;
	private String cardNumber;
	private String cardPassward;
	
	/** 消息handler */
	private Handler mHandler = new Handler() {
		public void dispatchMessage(Message msg) {
			switch (msg.what) {
			case 0:
				Toast.makeText(BaseCardFragment.this.mContext, "请选择充值卡金额", Toast.LENGTH_LONG).show();
				BaseCardFragment.this.btPay.setClickable(true);
				break;
			case 1:
				Toast.makeText(BaseCardFragment.this.mContext, "请输入充值卡卡号", Toast.LENGTH_LONG).show();
				BaseCardFragment.this.btPay.setClickable(true);
				break;
			case 2:
				Toast.makeText(BaseCardFragment.this.mContext, "请输入充值卡密码", Toast.LENGTH_LONG).show();
				BaseCardFragment.this.btPay.setClickable(true);
				break;
			case 3:
				Toast.makeText(BaseCardFragment.this.mContext, "请输入1-100000内的金额", Toast.LENGTH_LONG).show();
				BaseCardFragment.this.btPay.setClickable(true);
				break;
			case 4:
				Toast.makeText(BaseCardFragment.this.mContext, "恭喜，订单创建成功，请耐心等待", Toast.LENGTH_LONG).show();
				BaseCardFragment.this.btPay.setClickable(true);
				break;
			case 5:
				Toast.makeText(BaseCardFragment.this.mContext, "支付出错了，请重试", Toast.LENGTH_LONG).show();
				BaseCardFragment.this.btPay.setClickable(true);
				break;
			case 6:
				BaseCardFragment.this.btPay.setClickable(true);
				break;
			case 7:
				Toast.makeText(BaseCardFragment.this.mContext, "支付成功，请及时完善账号", Toast.LENGTH_LONG).show();
				BaseCardFragment.this.getActivity().finish();
			}

			super.dispatchMessage(msg);
		}
	};
	
	/** 查询channel回调 */
	private HttpCallback queryChannelCallBack = new HttpCallback() {
		public void onSuccess(int code, String msg, Object data) {
			// 成功回调
			super.onSuccess(code, msg, data);
			CardChannel cardChannel = (CardChannel) data;
			Map<String, String> dataMap = new HashMap<String, String>();
			dataMap.put("cardType", getCardType() + "");
			dataMap.put("pm_id", cardChannel.getPm_id());
			dataMap.put("pc_id", cardChannel.getPc_id());
			dataMap.put("amount", amount + "");
			dataMap.put("cardNum", cardNumber);
			dataMap.put("cardPassword", cardPassward);
			dataMap.put("selectAmount", cardAmount);
			dataMap.put("gold", GameUtil.converFloat(Float.parseFloat(cardAmount) * rate));
			dataMap.put("gameId", String.valueOf(AstGamePlatform.getInstance().getAppInfo().gameId));
			dataMap.put("serverId", serverId);
			dataMap.put("roleId", roleId);
			dataMap.put("userId", String.valueOf(AstGamePlatform.getInstance().getUserInfo().getUserId()));
			dataMap.put("yx", yx);
			dataMap.put("extra", extra);
			
			new PayCardHandler(AstGamePlatform.getInstance().isDebugMode(), cardPayCallback, dataMap).post();
		}

		public void onFailure(int code, String msg, Object data) {
			// 失败回调
			super.onFailure(2, msg, data);
			Logger.e(BaseCardFragment.this.TAG, "查询渠道失败");
			BaseCardFragment.this.setPayClickable();
			BaseCardFragment.this.mListener.payFail(code, null, extraAppData);
		}
	};

	/** cardpay 回调 */
	private HttpCallback cardPayCallback = new HttpCallback() {
		String orderId;

		public void onSuccess(int code, String msg, Object data) {
			// 成功回调
			super.onSuccess(code, msg, data);
			Order order = (Order) data;
			this.orderId = order.getOrderId();
			Logger.d(BaseCardFragment.this.TAG, "@@@orderId: " + this.orderId);
			UserInfo user = AstGamePlatform.getInstance().getUserInfo();
			if ((null != user) && (user.isQuickUser())) {
				// 快速注册用户
				UserManager.getInstance().perfectAccount(mContext, orderId, extraAppData, mListener);
				BaseCardFragment.this.mHandler.sendEmptyMessage(7);
			} else {
				BaseCardFragment.this.mListener.paySuccess(this.orderId, BaseCardFragment.this.extraAppData);
				BaseCardFragment.this.mHandler.sendEmptyMessage(4);
			}

		}

		public void onFailure(int code, String msg, Object data) {
			// 失败回调
			super.onFailure(code, msg, data);
			BaseCardFragment.this.mListener.payFail(2, this.orderId, BaseCardFragment.this.extraAppData);
			BaseCardFragment.this.mHandler.sendEmptyMessage(5);
			Logger.e(BaseCardFragment.this.TAG, "充值卡充值失败");
		}
	};

	/**
	 * onAttach
	 */
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			this.mListener = ((PayCallbackListener) activity);
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + "必须实现PayCallbackListener接口");
		}
	}

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
			this.amount = args.getInt("amount");
			this.roleId = args.getString("roleId");
			this.serverId = args.getString("serverId");
			this.amountEditable = args.getBoolean("amountEditable");
			this.yx = args.getString("yx");
			this.extra = args.getString("extra");
			Logger.d(this.TAG, "Receive parameters:productName:"
					+ this.productName + "currency:" + this.currency
					+ "extraAppData:" + this.extraAppData + "rate:" + this.rate
					+ "amount:" + this.amount + " amountEditable:"
					+ this.amountEditable);
		}
		this.mContext = getActivity();
		this.cardType = getCardType();
		this.tag = getTag();
	}

	/**
	 * onCreateView
	 */
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = null;
		if (BaseActivity.isLandscape()) {
			int id = BaseActivity.getResIdByName(getActivity(), "layout", "ast_mob_sdk_pay_card_land");
			view = inflater.inflate(id, container, false);
		} else {
			int id = BaseActivity.getResIdByName(getActivity(), "layout", "ast_mob_sdk_pay_card");
			view = inflater.inflate(id, container, false);
		}

		this.ivCardIcon = ((ImageView) view.findViewById(BaseActivity.getResIdByName(getActivity(), "ast_mob_sdk_card_icon")));
		this.ivCardIcon.setBackgroundResource(getCardIconResource());

		this.userFormat = getResources().getString(BaseActivity.getStringIdByName(this.mContext, "ast_mob_sdk_pay_alipay_user"));
		this.amountFormat = getResources().getString(BaseActivity.getStringIdByName(this.mContext, "ast_mob_sdk_pay_alipay_money"));
		this.productNameFormat = getResources().getString(BaseActivity.getStringIdByName(this.mContext, "ast_mob_sdk_pay_alipay_goods"));

		this.tvUser = ((TextView) view.findViewById(BaseActivity.getResIdByName(this.mContext, "ast_mob_sdk_pay_user")));
		this.tvUser.setText(String.format(this.userFormat, new Object[] { this.user }));

		this.tvMoney = ((TextView) view.findViewById(BaseActivity.getResIdByName(getActivity(), "ast_mob_sdk_pay_card_money")));
		this.tvMoney.setText(String.format(this.amountFormat, new Object[] { Integer.valueOf(this.amount) }));

		this.tvGoods = ((TextView) view.findViewById(BaseActivity.getResIdByName(getActivity(), "ast_mob_sdk_pay_card_goods")));
		this.tvGoods.setText(String.format(this.productNameFormat, new Object[] { GameUtil.converFloat(this.amount * this.rate), this.currency }));

		this.mSelectCard = ((Spinner) view.findViewById(BaseActivity.getResIdByName(getActivity(), "ast_mob_sdk_pay_spinner_field_item_content")));
		this.mSelectCard.setPrompt("请选择充值金额");
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.mContext, BaseActivity.getResIdByName(getActivity(), "layout", "ast_mob_sdk_pay_simple_spinner_item"));
		this.mounts = ((String[]) Constants.amountMap.get(Integer.valueOf(this.cardType)));

		int curPosition = 0;
		if (this.mounts != null) {
			for (int i = 0; i < this.mounts.length; i++) {
				adapter.add(this.mounts[i] + "元");
				if (this.mounts[i].equalsIgnoreCase(this.amount + "")) {
					curPosition = i;
				}
			}
		}
		adapter.setDropDownViewResource(17367049);
		this.mSelectCard.setAdapter(adapter);
		this.mSelectCard.setSelection(curPosition);
		this.mSelectCard.setOnItemSelectedListener(new OnItemSelectedListener() {
					public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
						Logger.d(BaseCardFragment.this.TAG, "onItemSelected");
						Integer money = Integer.valueOf(Integer.parseInt(BaseCardFragment.this.mounts[position]));
						if (money.intValue() > Constants.MAX_MONEY) {
							Toast.makeText(BaseCardFragment.this.mContext, "请输入1-100000内的金额", Toast.LENGTH_LONG).show();
							BaseCardFragment.this.btPay.setClickable(true);
							return;
						}
						BaseCardFragment.this.cardAmount = String.valueOf(money);
						BaseCardFragment.this.selectCardChanged(money.intValue());
					}

					public void onNothingSelected(AdapterView<?> parent) {
						Logger.d(BaseCardFragment.this.TAG, "onNothingSelected");
					}
				});
		this.etCardNumber = ((EditText) view.findViewById(BaseActivity.getResIdByName(getActivity(), "ast_mob_sdk_card_number")));
		this.etCardPassward = ((EditText) view.findViewById(BaseActivity.getResIdByName(getActivity(), "ast_mob_sdk_card_pwd")));
		this.etCardPassward.setOnEditorActionListener(new TextView.OnEditorActionListener() {
					public boolean onEditorAction(TextView arg0, int arg1, KeyEvent arg2) {
						if (arg1 == 2) {
							BaseCardFragment.this.pay();
							return true;
						}
						return false;
					}
				});
		this.btPay = ((Button) view.findViewById(BaseActivity.getResIdByName(getActivity(), "ast_mob_sdk_card_pay_ok")));
		this.btPay.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				BaseCardFragment.this.pay();
			}
		});

		return view;
	}

	/**
	 * getView
	 */
	public View getView() {
		return super.getView();
	}

	/**
	 * pay
	 */
	private void pay() {
		Logger.d(this.TAG, "Start pay...\ntag:" + this.tag);
		this.btPay.setClickable(false);

		AstPayActivity.setPayChannel(this.mContext, this.tag);
		this.cardNumber = this.etCardNumber.getEditableText().toString();
		this.cardPassward = this.etCardPassward.getEditableText().toString();

		if ((this.cardAmount == null) || ("".equals(this.cardAmount.trim()))) {
			this.mHandler.sendEmptyMessage(0);
			return;
		}
		if ((this.cardNumber == null) || ("".equals(this.cardNumber.trim()))) {
			this.mHandler.sendEmptyMessage(1);
			return;
		}
		if ((this.cardPassward == null) || ("".equals(this.cardPassward.trim()))) {
			this.mHandler.sendEmptyMessage(2);
			return;
		}
		int cardType = this.getCardType();
		String merchantId = AstGamePlatform.getInstance().getAppInfo().merchantId;
		new QueryChannelHandler(AstGamePlatform.getInstance().isDebugMode(), this.queryChannelCallBack, merchantId, cardType).get();
	}

	private void setPayClickable() {
		this.mHandler.sendEmptyMessage(6);
	}

	/**
	 * 选择card改变
	 * @param money
	 */
	private void selectCardChanged(int money) {
		this.tvMoney.setText(Html.fromHtml(String.format(this.amountFormat, new Object[] { GameUtil.redFontString(Integer.valueOf(money)) })));
		this.tvGoods.setText(Html.fromHtml(String.format(this.productNameFormat, new Object[] {GameUtil.redFontString(GameUtil.converFloat(money * this.rate)), this.currency })));
	}
	
	protected abstract int getCardType();

	protected abstract int getCardIconResource();

	protected abstract String logTag();

	protected abstract String tag();
}
