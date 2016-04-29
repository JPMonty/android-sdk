package com.reign.ast.sdk.fragments;

import com.reign.ast.sdk.AstPayActivity;
import com.reign.ast.sdk.BaseActivity;
import com.reign.ast.sdk.manager.Constants;

/**
 * 移动充值卡
 * @author zhouwenjia
 *
 */
public class MobileFragment extends BaseCardFragment {
	protected int getCardType() {
		return Constants.PayMethod.MOBILE.getValue();
	}

	protected int getCardIconResource() {
		return BaseActivity.getDrawbleIdByName(getActivity(), "ast_mob_sdk_pay_mobile");
	}

	protected String tag() {
		return AstPayActivity.MOBILE_TAG;
	}

	protected String logTag() {
		return MobileFragment.class.getSimpleName();
	}
}
