package com.reign.ast.sdk.fragments;

import com.reign.ast.sdk.AstPayActivity;
import com.reign.ast.sdk.BaseActivity;
import com.reign.ast.sdk.manager.Constants;

/**
 * 电信充值卡
 * @author zhouwenjia
 *
 */
public class TelcomFragment extends BaseCardFragment {
	protected int getCardType() {
		return Constants.PayMethod.TELECOM.getValue();
	}

	protected int getCardIconResource() {
		return BaseActivity.getDrawbleIdByName(getActivity(), "ast_mob_sdk_pay_telcom");
	}

	protected String tag() {
		return AstPayActivity.TELCOM_TAG;
	}

	protected String logTag() {
		return MobileFragment.class.getSimpleName();
	}
}
