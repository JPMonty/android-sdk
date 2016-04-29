package com.reign.ast.sdk.alipay;

import org.json.JSONObject;

import com.reign.ast.sdk.util.GameUtil;

/**
 * 结果检测
 * @author zhouwenjia
 *
 */
public class ResultChecker {
	
	public static final int RESULT_INVALID_PARAM = 0;
	public static final int RESULT_CHECK_SIGN_FAILED = 1;
	public static final int RESULT_CHECK_SIGN_SUCCEED = 2;
	
	private String content;

	/**
	 * 构造函数
	 * @param content
	 */
	public ResultChecker(String content) {
		this.content = content;
	}

	/**
	 * 检测签名
	 * @return
	 */
	public int checkSign() {
		int retVal = RESULT_CHECK_SIGN_SUCCEED;  
        try{  
            JSONObject objContent = BaseHelper.string2JSON (content, ";");  
            String result = objContent.getString ("result");  
            
	        result = result.substring (1, result.length () - 1);  
            if (!GameUtil.isBlankString(result)) {
	            // 返回不为空
	            // 获取待签名数据   
	            int iSignContentEnd = result.indexOf ("&sign_type=");  
	            String signContent = result.substring (0, iSignContentEnd);  
	            // 获取签名   
	            JSONObject objResult = BaseHelper.string2JSON (result, "&");  
	            String signType = objResult.getString ("sign_type");  
	            signType = signType.replace ("\"", "");  
	            String sign = objResult.getString ("sign");  
	            sign = sign.replace ("\"", "");  
	            // 进行验签 返回验签结果   
	            if (signType.equalsIgnoreCase ("RSA")) {  
	                if (!Rsa.doCheck (signContent, sign, PartnerConfig.RSA_ALIPAY_PUBLIC)) {
	                    retVal = RESULT_CHECK_SIGN_FAILED;  
	                }
	            }  
            }
        } catch (Exception e) {  
            retVal = RESULT_INVALID_PARAM;  
            e.printStackTrace ();  
        }  
        return retVal;  
	}

}
