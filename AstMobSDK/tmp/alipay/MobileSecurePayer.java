package com.reign.ast.sdk.alipay;

import java.util.List;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;

import com.alipay.android.app.IAlixPay;
import com.alipay.android.app.IRemoteServiceCallback;
import com.alipay.sdk.app.PayTask;
import com.reign.ast.sdk.util.Logger;

/**
 * 移动安全充值
 * @author zhouwenjia
 *
 */
public class MobileSecurePayer {
	
	private static final String TAG = "MobileSecurePayer";
	private Integer lock = 0;  
    private IAlixPay mAlixPay = null;  
    private boolean mbPaying = false;  
    private Activity mActivity = null;  
    // 和安全支付服务建立连接   
    private ServiceConnection mAlixPayConnection = new ServiceConnection (){  
        public void onServiceConnected (ComponentName className, IBinder service){  
            // wake up the binder to continue.   
            // 获得通信通道   
            synchronized (lock){  
                mAlixPay = IAlixPay.Stub.asInterface (service);  
                lock.notify ();  
            }  
        }  
          
        public void onServiceDisconnected (ComponentName className){  
            mAlixPay = null;  
        }  
    };  
    // 回调
    private IRemoteServiceCallback mCallback = new IRemoteServiceCallback.Stub () {
        public void startActivity (String packageName, String className, int iCallingPid, Bundle bundle) throws RemoteException {  
            Intent intent = new Intent (Intent.ACTION_MAIN, null);  
            if (null == bundle) {
                bundle = new Bundle ();
            }
            try{  
                bundle.putInt ("CallingPid", iCallingPid);  
                intent.putExtras (bundle);  
            } catch (Exception e){  
                e.printStackTrace ();  
            }  
            intent.setClassName (packageName, className);  
            
//            ComponentName component = new ComponentName(packageName, className);  
////            // Create a new intent. Use the old one for extras and such reuse  
////            Intent newIntent = new Intent(intent);  
////            // Set the component to be explicit  
//            intent.setComponent(component); 
//            
////            mActivity.startActivity (intent);  
            mActivity.startActivity(intent);
        }  


        @Override  
        public boolean isHideLoadingScreen () throws RemoteException{  
            return false;  
        }  

        @Override  
        public void payEnd (boolean arg0, String arg1) throws RemoteException{  
              
        }  
    }; 
    
    public static Intent getExplicitIntent(Context context, Intent implicitIntent) {  
        // Retrieve all services that can match the given intent  
        PackageManager pm = context.getPackageManager();  
        List<ResolveInfo> resolveInfo = pm.queryIntentServices(implicitIntent, 0);  
        // Make sure only one match was found  
        if (resolveInfo == null || resolveInfo.size() != 1) {  
            return null;  
        }  
        // Get component info and create ComponentName  
        ResolveInfo serviceInfo = resolveInfo.get(0);  
        String packageName = serviceInfo.serviceInfo.packageName;  
        String className = serviceInfo.serviceInfo.name;  
        ComponentName component = new ComponentName(packageName, className);  
        // Create a new intent. Use the old one for extras and such reuse  
        Intent explicitIntent = new Intent(implicitIntent);  
        // Set the component to be explicit  
        explicitIntent.setComponent(component);  
        return explicitIntent;  
    }  
    
    /**
     * 支付
     * @param strOrderInfo
     * @param callback
     * @param myWhat
     * @param activity
     * @return
     */
    public boolean pay (final String strOrderInfo, final Handler callback, final int myWhat, final Activity activity){  
        if (mbPaying) {
            return false;
        }
        mbPaying = true;  
        //   
        mActivity = activity;  
        
		Runnable payRunnable = new Runnable() {

			@Override
			public void run() {
				// 构造PayTask 对象
				PayTask alipay = new PayTask(mActivity);
				// 调用支付接口，获取支付结果
				String result = alipay.pay(strOrderInfo);
				mbPaying = false;
				Message msg = new Message();
				msg.what = myWhat;
				msg.obj = result;
				callback.sendMessage(msg);
			}
		};

		// 必须异步调用
		Thread payThread = new Thread(payRunnable);
		payThread.start();
        
//        // bind the service.   
//        // 绑定服务   
//        if (null == mAlixPay) {
//            // 绑定安全支付服务需要获取上下文环境，   
//            // 如果绑定不成功使用mActivity.getApplicationContext().bindService   
//            // 解绑时同理   
//        	Intent intent = new Intent (IAlixPay.class.getName ());
////        	intent.setPackage(activity.getPackageName());
//        	Intent newIntent = getExplicitIntent(mActivity.getApplicationContext(), intent);
////        	Intent newIntent = getExplicitIntent(mActivity, intent);
//            mActivity.getApplicationContext ().bindService (newIntent, mAlixPayConnection, Context.BIND_AUTO_CREATE);  
//        }  
//        // 实例一个线程来进行支付   
//        new Thread (new Runnable () {  
//            public void run () {  
//                try {  
//                    // wait for the service bind operation to completely   
//                    // finished.   
//                    // Note: this is important,otherwise the next mAlixPay.Pay()   
//                    // will fail.   
//                    // 等待安全支付服务绑定操作结束   
//                    // 注意：这里很重要，否则mAlixPay.Pay()方法会失败   
//                    synchronized (lock) {  
//                        if (null == mAlixPay) {
//                            lock.wait ();  
//                        }
//                    }  
//                    // register a Callback for the service.   
//                    // 为安全支付服务注册一个回调   
//                    mAlixPay.registerCallback (mCallback);  
//                    // call the MobileSecurePay service.   
//                    // 调用安全支付服务的pay方法   
//                    String strRet = mAlixPay.Pay (strOrderInfo);
//                    Logger.d(MobileSecurePayer.TAG, "@@@Alipay Pay result: " + strRet);
//                    // set the flag to indicate that we have finished.   
//                    // unregister the Callback, and unbind the service.   
//                    // 将mbPaying置为false，表示支付结束   
//                    // 移除回调的注册，解绑安全支付服务   
//                    mbPaying = false;  
//                    mAlixPay.unregisterCallback (mCallback);  
//                    mActivity.getApplicationContext ().unbindService (mAlixPayConnection);  
//                    // send the result back to caller.   
//                    // 发送交易结果   
//                    Message msg = new Message ();  
//                    msg.what = myWhat;  
//                    msg.obj = strRet;  
//                    callback.sendMessage (msg);  
//                } catch (Exception e) {  
//                    e.printStackTrace ();  
//                    Logger.e(MobileSecurePayer.TAG, "@@@Alipay pay error!");
//                    // send the result back to caller.   
//                    // 发送交易结果   
//                    Message msg = new Message ();  
//                    msg.what = myWhat;  
//                    msg.obj = e.toString ();  
//                    callback.sendMessage (msg);  
//                }  
//            }  
//        }).start ();  
        return true;  
    } 
    
}
