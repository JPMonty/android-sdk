package com.reign.ast.sdk.alipay;


import com.reign.ast.sdk.AstPayActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

/**
 * aplix pay
 * @author zhouwenjia
 *
 */
@SuppressLint("HandlerLeak")
public class AlixPay {

	private Activity mActivity = null;
	private ProgressDialog mProgress = null;
	private Handler callBackParam = null;

	/**
	 * 构造函数
	 * @param activity
	 */
	public AlixPay(Activity activity) {
		mActivity = activity;
	}

	/** 异常handler */
	private Handler errorHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == 1) {
				new AlertDialog.Builder(mActivity).setTitle("提示").setMessage("连接网络失败")
						.setPositiveButton("确定",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog, int which) {
										Intent intent = new Intent("android.settings.WIFI_SETTINGS");
										mActivity.startActivity(intent);
									}
								}).create().show();
			}
		};
	};

	/** 充值回调 */
	private Handler payCallBack = new Handler() {
		public void handleMessage(Message msg) {
			try {
				String content = (String) msg.obj;
				switch (msg.what) {
				case AlixId.RQF_PAY: {
					//
					closeProgress();
					// 处理交易结果
					try {
						// 获取交易状态码，具体状态代码请参看文档
						String tradeStatus = "resultStatus={";  
                        int imemoStart = content.indexOf ("resultStatus=");  
                        imemoStart += tradeStatus.length ();  
                        int imemoEnd = content.indexOf ("};memo=");  
                        tradeStatus = content.substring (imemoStart, imemoEnd); 

						// 先验签通知
						ResultChecker resultChecker = new ResultChecker(content);
						int retVal = resultChecker.checkSign();
						if (retVal == ResultChecker.RESULT_CHECK_SIGN_FAILED) {
							BaseHelper.showDialog(mActivity, "提示", "您的订单信息已被非法篡改", android.R.drawable.ic_dialog_alert);
						} else {
							if (tradeStatus.equals("9000")) {
								// 支付成功
								Toast.makeText(mActivity, "支付成功", Toast.LENGTH_LONG).show();
								Log.i("result of this pay:", "successful");
							} else if (tradeStatus.equals("6001")) {
								Toast.makeText(mActivity, "操作已经取消", Toast.LENGTH_LONG).show();
								Log.e("alixPay", "取消交易");
							} else if (tradeStatus.equals("4000")) {
								// 系统繁忙或未安装支付宝钱包
//								Toast.makeText(mActivity, "亲，请先安装支付宝钱包哦", Toast.LENGTH_LONG).show();
								new AlertDialog.Builder(mActivity).setTitle("提示").setMessage("检测到您当前未安装支付宝钱包，是否跳转到市场下载？")
								.setPositiveButton("确定",
										new DialogInterface.OnClickListener() {
											@Override
											public void onClick(DialogInterface dialog, int which) {
												Intent intent = new Intent(Intent.ACTION_VIEW);
//												intent.setData(Uri.parse("market://search?q=支付宝"));
												intent.setData(Uri.parse("market://details?id=com.eg.android.AlipayGphone"));
//												intent.setData(Uri.parse("market://details"));
												intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
												mActivity.startActivity(intent);
											}
										}).setNegativeButton("取消", null).create().show();
								Log.e("alixPay", "系统繁忙或未安装支付宝钱包");
							} else {
								// 支付失败
								Toast.makeText(mActivity, "支付失败, 交易状态码为: " + tradeStatus, Toast.LENGTH_LONG).show();
								Log.e("result of this pay", "falied");
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
					break;
				}
				super.handleMessage(msg);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			try {
				if (null != callBackParam) {
					Message msg2 = new Message ();  
                    msg2.what = msg.what;  
                    msg2.obj = msg.obj;  
					callBackParam.sendMessage(msg2);
                    callBackParam.handleMessage(msg2);;
					callBackParam.dispatchMessage(msg2);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};
	
	/**
	 * 支付
	 * @param callBack
	 * @param orderSign
	 */
	public void pay(Handler callBack, String orderSign) {
		// 支付宝支付必须依赖网络，所以在这里必须加网络判定
		if (!checkCanAccessNet(mActivity)) {
			// 不可访问网络
			errorHandler.sendEmptyMessage(1);
			return;
		}
		
		MobileSecurePayHelper mspHelper = new MobileSecurePayHelper(mActivity);
		boolean alipayAppExist = mspHelper.checkAlipayAppExist();
		if (!alipayAppExist) {
			return;
		}
		// 根据订单信息开始进行支付
		try {
			// 调用pay方法进行支付
			MobileSecurePayer msp = new MobileSecurePayer();
			boolean bRet = msp.pay(orderSign, payCallBack, AlixId.RQF_PAY, mActivity);
			if (bRet) {
				// 显示 正在支付 进度条
				closeProgress();
				mProgress = BaseHelper.showProgress(mActivity, null, "正在支付", false, true);
				// if (null != mActivity && (mActivity instanceof AstPayActivity)) {
				// 	((AstPayActivity) mActivity).attachProgressDialog(mProgress);
				// }
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * 判断是否可以访问网络
	 * @param context
	 * @return
	 */
	public boolean checkCanAccessNet(final Context context) {
		try {
			ConnectivityManager manger = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo info = manger.getActiveNetworkInfo();
			return (null != info && info.isConnected());
		} catch (Exception e) {
			return false;
		}
	}
	
	/**
	 * 获得回调url
	 * @return
	 */
	public String getNotifyUrl() {
		return "http://testos.mobile.aoshitang.com/v2/v2/alipayReceive.action";
	}
	
	/**
	 * 关闭进度条
	 */
	private void closeProgress() {
		try {
			if (null != mProgress) {
				mProgress.dismiss();
				mProgress = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/** on cancel监听 */
	public static class AlixOnCancelListener implements OnCancelListener {
		Activity mcontext;

		AlixOnCancelListener(Activity context) {
			mcontext = context;
		}

		public void onCancel(DialogInterface dialog) {
			mcontext.onKeyDown(KeyEvent.KEYCODE_BACK, null);
		}
	}

}
