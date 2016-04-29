package com.reign.ast.sdk.listener;

/**
 * 浮动菜单icon监听
 * @author zhouwenjia
 *
 */
public abstract interface FloatMenuIconListener {
	
	public abstract void onClick(float x, float y);

	public abstract void onMove();
	
}
