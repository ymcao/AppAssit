package com.appassit.widget.pieView;

import android.content.Context;
import android.graphics.Canvas;
import android.view.View;

import com.appassit.R;

/**
 * shows views in a stack
 */
public class PieStackView extends BasePieView {

	private static final int SLOP = 5;

	private OnCurrentListener mCurrentListener;
	private int mMinHeight;

	public interface OnCurrentListener {
		public void onSetCurrent(int index);
	}

	public PieStackView(Context ctx) {
		mMinHeight = (int) ctx.getResources().getDimension(R.dimen.qc_tab_title_height);
	}

	public void setOnCurrentListener(OnCurrentListener l) {
		mCurrentListener = l;
	}

	@Override
	public void setCurrent(int ix) {
		super.setCurrent(ix);
		if (mCurrentListener != null) {
			mCurrentListener.onSetCurrent(ix);
		}
	}

	/**
	 * this will be called before the first draw call
	 */
	@Override
	public void layout(int anchorX, int anchorY, boolean left, float angle, int pHeight) {
		super.layout(anchorX, anchorY, left, angle, pHeight);
		buildViews();
		mWidth = mChildWidth;
		mHeight = mChildHeight + (mViews.size() - 1) * mMinHeight;
		mLeft = anchorX + (left ? SLOP : -(SLOP + mChildWidth));
		mTop = anchorY - mHeight / 2;
		if (mViews != null) {
			layoutChildrenLinear();
		}
	}

	private void layoutChildrenLinear() {
		final int n = mViews.size();
		int top = mTop;
		int dy = (n == 1) ? 0 : (mHeight - mChildHeight) / (n - 1);
		for (View view : mViews) {
			int x = mLeft;
			view.layout(x, top, x + mChildWidth, top + mChildHeight);
			top += dy;
		}
	}

	@Override
	public void draw(Canvas canvas) {
		if ((mViews != null) && (mCurrent > -1)) {
			final int n = mViews.size();
			for (int i = 0; i < mCurrent; i++) {
				drawView(mViews.get(i), canvas);
			}
			for (int i = n - 1; i > mCurrent; i--) {
				drawView(mViews.get(i), canvas);
			}
			drawView(mViews.get(mCurrent), canvas);
		}
	}

	@Override
	protected int findChildAt(int y) {
		final int ix = (y - mTop) * mViews.size() / mHeight;
		return ix;
	}

}
