package com.giftbox.statistic;
/**
 * 
 * 存db监听
 *
 */
public interface OnInsertDBListener {
	void onBeforeInsertToDB();
	void onInsertToDBFinish();
}
