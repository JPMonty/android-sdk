package com.reign.ast.sdk.manager;

/**
 * 异常code处理
 * @author zhouwenjia
 *
 */
public class ErrorCodeTransfer {

	public static final int ERROR_PARSE_DATA = -1;
	
	/**
	 * 获得异常消息
	 * @param code
	 * @return
	 */
	public static final String getErrorMsg(int code) {
		String errorMsg = "";
		switch (code) {
		case 0: errorMsg = "请求成功"; break;
		case -1: errorMsg = "数据解析失败"; break;
		case -2: errorMsg = "连接服务器失败"; break;
		case -3: errorMsg = "网络未连接"; break;
		case -4: errorMsg = "请求失败"; break;
		
		case 1: errorMsg = "成功"; break;
		case 2: errorMsg = "请求参数包含空或空值"; break;
		case 3: errorMsg = "请求没有响应"; break;
		case 4: errorMsg = "非法请求"; break;
		case 5: errorMsg = "游戏平台来源不存在"; break;
		case 201: errorMsg = "签名过期"; break;
		case 202: errorMsg = "签名不一致"; break;
		case 301: errorMsg = "用户不存在"; break;
		case 302: errorMsg = "用户已经存在"; break;
		case 303: errorMsg = "密码错误"; break;
		case 304: errorMsg = "临时用户不存在"; break;
		case 305: errorMsg = "临时用户登录时设备信息与注册时不一致"; break;
		case 306: errorMsg = "临时账号已绑定"; break;
		case 307: errorMsg = "用户注册异常"; break;
		case 308: errorMsg = "用户名格式错误"; break;
		case 309: errorMsg = "密码格式错误"; break;
		case 310: errorMsg = "绑定用户异常"; break;
		case 311: errorMsg = "临时用户游戏信息不一致"; break;
		case 401: errorMsg = "设备ID为空"; break;
		case 501: errorMsg = "充值卡卡号格式错误"; break;
		case 502: errorMsg = "充值卡密码格式错误"; break;
		case 601: errorMsg = "游戏id为空"; break;
		case 602: errorMsg = "游戏id无效"; break;
		}
		
		return errorMsg;
	}
}
