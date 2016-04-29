package com.reign.ast.sdk.task;

import java.util.TimerTask;

import com.reign.ast.sdk.floatMenu.FloatMenuIcon;

/**
 * 悬浮icon半隐藏task
 * @author zhouwenjia
 *
 */
public class FloatIconHalfHideTask extends TimerTask {

	/** 是否是左align */
	private boolean leftAlign;
	
	/** floatMenuIcon */
	private FloatMenuIcon floatMenuIcon;
	
	/** 
	 * 构造函数
	 * @param floatMenuIcon
	 * @param leftAlign
	 */
	public FloatIconHalfHideTask(FloatMenuIcon floatMenuIcon, boolean leftAlign) {
		this.floatMenuIcon = floatMenuIcon;
		this.leftAlign = leftAlign;
	}
	
	@Override
	public void run() {
		if (null != floatMenuIcon) {
			floatMenuIcon.halfHideFloatIcon(leftAlign);
		}
	}

}
