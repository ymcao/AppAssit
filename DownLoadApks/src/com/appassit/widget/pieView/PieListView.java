package com.appassit.widget.pieView;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

/**
 * shows views in a menu style list
 */
public class PieListView extends BasePieView {

    private Paint mBgPaint;

    public PieListView(Context ctx) {
        mBgPaint = new Paint();
        mBgPaint.setColor(Color.BLUE);
    }

    /**
     * this will be called before the first draw call
     */
    @Override
    public void layout(int anchorX, int anchorY, boolean left, float angle,
            int pHeight) {
        super.layout(anchorX, anchorY, left, angle, pHeight);
        buildViews();
        mWidth = mChildWidth;
        mHeight = mChildHeight * mAdapter.getCount();
        mLeft = anchorX + (left ? 0 : - mChildWidth);
        mTop = Math.max(anchorY - mHeight / 2, 0);
        if (mTop + mHeight > pHeight) {
            mTop = pHeight - mHeight;
        }
        if (mViews != null) {
            layoutChildrenLinear();
        }
    }

    protected void layoutChildrenLinear() {
        final int n = mViews.size();
        int top = mTop;
        for (View view : mViews) {
            view.layout(mLeft, top, mLeft + mChildWidth, top + mChildHeight);
            top += mChildHeight;
        }
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawRect(mLeft, mTop, mLeft + mWidth, mTop + mHeight, mBgPaint);
        if (mViews != null) {
            for (View view : mViews) {
                drawView(view, canvas);
            }
        }
    }

    @Override
    protected int findChildAt(int y) {
        final int ix = (y - mTop) * mViews.size() / mHeight;
        return ix;
    }

}
