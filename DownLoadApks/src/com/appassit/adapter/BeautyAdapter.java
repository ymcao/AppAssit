package com.appassit.adapter;

import java.util.Calendar;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.appassit.R;
import com.appassit.activitys.SLAppication;
import com.appassit.common.BitmapHelp;
import com.appassit.model.BeautyInfo;
import com.appassit.tools.AndroidOSUtils;
import com.appassit.views.CustomImageView;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.callback.BitmapLoadFrom;
import com.lidroid.xutils.bitmap.callback.DefaultBitmapLoadCallBack;

public class BeautyAdapter extends ViewAdapter<BeautyInfo> {

	private static final String TAG = BeautyAdapter.class.getSimpleName();

	private static final int ALBUM_PAST_WIDTH;
	private static final int ALBUM_PAST_HEIGHT;
	private static final int ALBUM_IMAGE_WIDTH;
	private static final int ALBUM_IMAGE_HEIGHT;
	private static BitmapUtils bitmapUtils;
	static {
		ALBUM_PAST_WIDTH = (AndroidOSUtils.getDisplayWidth() - 2 * (2 * AndroidOSUtils.getDimens(R.dimen.album_margin) + AndroidOSUtils
				.getDimens(R.dimen.album_past_space))) / 3;
		ALBUM_PAST_HEIGHT = ALBUM_PAST_WIDTH * 922 / 534;
		ALBUM_IMAGE_WIDTH = AndroidOSUtils.getDisplayWidth() - 4 * AndroidOSUtils.getDimens(R.dimen.album_margin);
		ALBUM_IMAGE_HEIGHT = ALBUM_IMAGE_WIDTH * 488 / 1000;

	}

	public BeautyAdapter(OnClickListener listener) {
		super(listener);
		bitmapUtils = BitmapHelp.getBitmapUtils(SLAppication.getContext());
		bitmapUtils.configDefaultLoadingImage(R.drawable.wallpapermgr_thumb_default);
		bitmapUtils.configDefaultLoadFailedImage(R.drawable.wallpapermgr_thumb_default);
		bitmapUtils.configDefaultBitmapConfig(Bitmap.Config.ARGB_8888);
	}

	@Override
	public BeautyInfo getItem(int position) {
		return super.getItem(position);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(SLAppication.getContext()).inflate(R.layout.layout_album_view, null);
			holder = new ViewHolder();
			holder.init(convertView);
			convertView.setTag(holder);
		} else {
			Object tag = (ViewHolder) convertView.getTag();
			if (tag != null && tag instanceof ViewHolder) {
				holder = (ViewHolder) tag;
			} else {
				holder = new ViewHolder();
				holder.init(convertView);
				convertView.setTag(holder);
			}
		}
		BeautyInfo ri = getItem(position);
		if (ri.flag == 1)
			holder.setHeadData(ri);
		else
			holder.setItemData(ri);
		return convertView;
	}

	class ViewHolder {

		RelativeLayout mTitle;
		TextView mDate, mMonth, mName;
		LinearLayout mLink;
		LinearLayout mPast;
		CustomImageView[] mImages = new CustomImageView[3];

		CustomImageView mImage;

		void init(View parent) {
			mTitle = (RelativeLayout) parent.findViewById(R.id.album_title);
			mDate = (TextView) parent.findViewById(R.id.album_date);
			mMonth = (TextView) parent.findViewById(R.id.album_month);
			mName = (TextView) parent.findViewById(R.id.album_name);
			mLink = (LinearLayout) parent.findViewById(R.id.album_link);
			mPast = (LinearLayout) parent.findViewById(R.id.album_past);
			mImage = (CustomImageView) parent.findViewById(R.id.album_image);
			mImage.setImageSize(ALBUM_IMAGE_WIDTH, ALBUM_IMAGE_HEIGHT);
			mImages[0] = (CustomImageView) parent.findViewById(R.id.album_image1);
			mImages[1] = (CustomImageView) parent.findViewById(R.id.album_image2);
			mImages[2] = (CustomImageView) parent.findViewById(R.id.album_image3);
			for (CustomImageView image : mImages) {
				image.setImageSize(ALBUM_PAST_WIDTH, ALBUM_PAST_HEIGHT);
			}
		}

		void setHeadData(final BeautyInfo ri) {
			mTitle.setVisibility(View.GONE);
			mPast.setVisibility(View.GONE);
			mImage.setVisibility(View.VISIBLE);
			// bitmapUtils.configDefaultBitmapMaxSize(ALBUM_PAST_WIDTH, ALBUM_PAST_HEIGHT);
			bitmapUtils.display(mImage, ri.url1, new CustomBitmapLoadCallBack());
			// Picasso.with(SLAppication.getContext()).load(ri.url1).placeholder(R.drawable.album_images_default).resize(ALBUM_PAST_WIDTH,
			// ALBUM_PAST_HEIGHT).centerCrop().into(mImage);
			mImage.setTag(ri);
			mImage.setOnClickListener(getOnClickListener());
		}

		void setItemData(BeautyInfo ri) {
			mTitle.setVisibility(View.VISIBLE);
			mPast.setVisibility(View.VISIBLE);
			mImage.setImageDrawable(null);
			mImage.setVisibility(View.GONE);

			mLink.setTag(ri);
			mLink.setOnClickListener(getOnClickListener());

			setTextInfo(ri.recomTime, ri.girlName);

			String urls[] = { ri.url1, ri.url2, ri.url3 };
			for (int i = 0; i < mImages.length; i++) {
				final CustomImageView image = mImages[i];
				final String url = urls[i];
				// /bitmapUtils.configDefaultBitmapMaxSize(ALBUM_IMAGE_WIDTH, ALBUM_IMAGE_HEIGHT);
				bitmapUtils.display(image, url, new CustomBitmapLoadCallBack());
				image.setTag(ri);
				image.setOnClickListener(getOnClickListener());
			}
		}

		private void setTextInfo(String recomTime, String girlName) {
			mName.setText(girlName);
			long time = Long.parseLong(recomTime);
			// Logger.Error(TAG, time + "--->" + System.currentTimeMillis());
			Calendar c = Calendar.getInstance();
			c.setTimeInMillis(time * 1000);
			int day = c.get(Calendar.DAY_OF_MONTH);
			String sDate = day < 10 ? ("0" + day) : ("" + day);
			mDate.setText(sDate);
			int month = c.get(Calendar.MONTH);
			String sMonth = month < 9 ? ("0" + (month + 1)) : ("" + (month + 1));
			mMonth.setText("/" + sMonth + "月");
		}

		public class CustomBitmapLoadCallBack extends DefaultBitmapLoadCallBack<ImageView> {

			public CustomBitmapLoadCallBack() {
			}

			@Override
			public void onLoading(ImageView container, String uri, BitmapDisplayConfig config, long total, long current) {
			}

			@Override
			public void onLoadCompleted(ImageView container, String uri, Bitmap bitmap, BitmapDisplayConfig config, BitmapLoadFrom from) {
				// super.onLoadCompleted(container, uri, bitmap, config, from);
				fadeInDisplay(container, bitmap);
			}
		}
	}

	private static final ColorDrawable TRANSPARENT_DRAWABLE = new ColorDrawable(android.R.color.transparent);

	private void fadeInDisplay(ImageView imageView, Bitmap bitmap) {
		final TransitionDrawable transitionDrawable = new TransitionDrawable(new Drawable[] { TRANSPARENT_DRAWABLE,
				new BitmapDrawable(imageView.getResources(), bitmap) });
		imageView.setImageDrawable(transitionDrawable);
		transitionDrawable.startTransition(500);
	}

}
