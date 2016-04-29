package com.reign.ast.sdk.fragments;

import com.reign.ast.sdk.AstPayActivity;
import com.reign.ast.sdk.BaseActivity;
import com.reign.ast.sdk.manager.Constants;

/**
 * 联通充值卡
 * @author zhouwenjia
 *
 */
public class UnicomFragment extends BaseCardFragment {
	protected int getCardType() {
		return Constants.PayMethod.UNICOM.getValue();
	}

	protected int getCardIconResource() {
		return BaseActivity.getDrawbleIdByName(getActivity(), "ast_mob_sdk_pay_unicom");
	}

	protected String tag() {
		return AstPayActivity.UNICOM_TAG;
	}

	protected String logTag() {
		return UnicomFragment.class.getSimpleName();
	}
}
