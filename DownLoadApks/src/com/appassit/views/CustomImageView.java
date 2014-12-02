package com.appassit.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ImageView;

public class CustomImageView extends ImageView {
	private int mWidth;
	private int mHeight;
	public static final int TYPE_NORMAL = 0;
	public static final int TYPE_CYCLE = 1;
	private int mType = TYPE_NORMAL;
	private final int mBackColor = 0xFFF0F0F0;

	public CustomImageView(Context context) {
		super(context);
		init();
	}

	public CustomImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public CustomImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	private void init() {
		setBackgroundColor(mBackColor);
	}

	public void setImageSize(int width, int height) {
		setImageSize(width, height, TYPE_NORMAL);
	}

	public void setImageSize(int width, int height, int type) {
		mWidth = width;
		mHeight = height;
		this.mType = type;
		if (mType == TYPE_CYCLE) {
			setBackgroundColor(Color.TRANSPARENT);
		}
	}

	public int getCustomDefineWidth() {
		return mWidth <= 0 ? 100 : mWidth;
	}

	public int getCustomDefineHeight() {
		return mHeight <= 0 ? 100 : mHeight;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		if (mWidth > 0 && mHeight > 0) {
			setMeasuredDimension(mWidth, mHeight);
			return;
		}
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	@Override
	public void setImageBitmap(Bitmap bm) {
		if (TYPE_CYCLE == mType) {
			bm = convert2CycleBitmap(bm, getCustomDefineWidth(), getCustomDefineHeight(), mBackColor);
		}
		super.setImageBitmap(bm);
	}

	public static Bitmap convert2CycleBitmap(Bitmap bmp, int width, int height, int backColor) {
		int r = Math.min(width, height) / 2;
		int doubleR = r * 2;
		Bitmap output = Bitmap.createBitmap(doubleR, doubleR, Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		canvas.drawColor(Color.TRANSPARENT);
		final Paint paint = new Paint();
		paint.setColor(backColor);
		paint.setAntiAlias(true);
		final Rect rect = new Rect(0, 0, doubleR, doubleR);
		canvas.drawCircle(r, r, r, paint);
		if (bmp != null) {
			int bW = bmp.getWidth();
			int bH = bmp.getHeight();
			float minScale = 1f;
			if (bW > doubleR && bH > doubleR) {
				minScale = 1f * Math.min(bW, bH) / doubleR;
			}
			paint.setAlpha(255);
			paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
			rect.set(0, 0, bW, bH);
			int tX = (int) ((minScale * doubleR - bW) / 2);
			int tY = (int) ((minScale * doubleR - bH) / 2);
			int saveCount = canvas.save();
			canvas.scale(1 / minScale, 1 / minScale);
			canvas.translate(tX, tY);
			canvas.drawBitmap(bmp, rect, rect, paint);
			canvas.restoreToCount(saveCount);
		}
		return output;
	}
}
