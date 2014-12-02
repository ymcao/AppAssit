package com.appassit.widget.pieView;

import java.util.ArrayList;
import java.util.List;

import android.view.View;

import com.appassit.widget.pieView.PieMenu.PieView;

/**
 * Pie menu item
 */
public class PieItem {

	private View mView;
	private PieView mPieView;
	private int level;
	private float start;
	private float sweep;
	private float animate;
	private int inner;
	private int outer;
	private boolean mSelected;
	private boolean mEnabled;
	private List<PieItem> mItems;

	public PieItem(View view, int level) {
		mView = view;
		this.level = level;
		mEnabled = true;
		setAnimationAngle(getAnimationAngle());
		setAlpha(getAlpha());
	}

	public PieItem(View view, int level, PieView sym) {
		mView = view;
		this.level = level;
		mPieView = sym;
		mEnabled = false;
	}

	public boolean hasItems() {
		return mItems != null;
	}

	public List<PieItem> getItems() {
		return mItems;
	}

	public void addItem(PieItem item) {
		if (mItems == null) {
			mItems = new ArrayList<PieItem>();
		}
		mItems.add(item);
	}

	public void setAlpha(float alpha) {
		if (mView != null) {
			mView.setAlpha(alpha);
		}
	}

	public float getAlpha() {
		if (mView != null) {
			return mView.getAlpha();
		}
		return 1;
	}

	public void setAnimationAngle(float a) {
		animate = a;
	}

	public float getAnimationAngle() {
		return animate;
	}

	public void setEnabled(boolean enabled) {
		mEnabled = enabled;
	}

	public void setSelected(boolean s) {
		mSelected = s;
		if (mView != null) {
			mView.setSelected(s);
		}
	}

	public boolean isSelected() {
		return mSelected;
	}

	public int getLevel() {
		return level;
	}

	public void setGeometry(float st, float sw, int inside, int outside) {
		start = st;
		sweep = sw;
		inner = inside;
		outer = outside;
	}

	public float getStart() {
		return start;
	}

	public float getStartAngle() {
		return start + animate;
	}

	public float getSweep() {
		return sweep;
	}

	public int getInnerRadius() {
		return inner;
	}

	public int getOuterRadius() {
		return outer;
	}

	public boolean isPieView() {
		return (mPieView != null);
	}

	public View getView() {
		return mView;
	}

	public void setPieView(PieView sym) {
		mPieView = sym;
	}

	public PieView getPieView() {
		if (mEnabled) {
			return mPieView;
		}
		return null;
	}

}
