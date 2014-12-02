package com.appassit.widget.pieView;

import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.appassit.R;
import com.appassit.activitys.BaseActivity;
import com.appassit.widget.material.MaterialDialog;

/**
 * Controller for Quick Controls pie menu
 */
public class PieControl implements PieMenu.PieController, OnClickListener {

	protected BaseActivity mActivity;
	protected PieMenu mPie;
	protected int mItemSize;
	private PieItem mOptions;
	private PieItem mClose;
	private PieItem mInfo;
	private PieItem mShare;
	private MaterialDialog materialDialog;

	public PieControl(BaseActivity activity, PieViewControllee controller) {
		mActivity = activity;
		mItemSize = (int) activity.getResources().getDimension(R.dimen.qc_item_size);
	}

	public void attachToContainer(FrameLayout container) {
		if (mPie == null) {
			mPie = new PieMenu(mActivity);
			LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			mPie.setLayoutParams(lp);
			populateMenu();
			mPie.setController(this);
		}
		container.addView(mPie);
	}

	public void removeFromContainer(FrameLayout container) {
		container.removeView(mPie);
	}

	protected void forceToTop(FrameLayout container) {
		if (mPie.getParent() != null) {
			container.removeView(mPie);
			container.addView(mPie);
		}
	}

	protected void setClickListener(OnClickListener listener, PieItem... items) {
		for (PieItem item : items) {
			item.getView().setOnClickListener(listener);
		}
	}

	protected void populateMenu() {
		mClose = makeItem(R.drawable.ic_close_window_holo_dark, 1);
		mInfo = makeItem(android.R.drawable.ic_menu_info_details, 1);
		mShare = makeItem(R.drawable.ic_share_holo_dark, 1);
		mOptions = makeItem(R.drawable.ic_settings_holo_dark, 1);
		// PieStackView stack = new PieStackView(mActivity);
		// stack.setLayoutListener(new OnLayoutListener() {
		// @Override
		// public void onLayout(int ax, int ay, boolean left) {
		// }
		// });
		// // stack.setOnCurrentListener(mTabAdapter);
		// stack.setAdapter(mTabAdapter);
		setClickListener(this, mClose, mOptions, mShare, mInfo);
		// level 1
		mPie.addItem(mClose);
		mPie.addItem(mOptions);
		// mOptions.addItem(makeFiller());
		// /mOptions.addItem(makeFiller());
		// mOptions.addItem(makeFiller());
		mPie.addItem(mShare);
		mPie.addItem(mInfo);
	}

	@Override
	public void onClick(View v) {
		if (mShare.getView() == v) {
		} else if (mClose.getView() == v) {
			exitApp();
		} else if (mInfo.getView() == v) {
			aboutUs();
		} else if (mOptions.getView() == v) {
		}
	}

	protected PieItem makeItem(int image, int l) {
		ImageView view = new ImageView(mActivity);
		view.setImageResource(image);
		view.setMinimumWidth(mItemSize);
		view.setMinimumHeight(mItemSize);
		view.setScaleType(ScaleType.CENTER);
		LayoutParams lp = new LayoutParams(mItemSize, mItemSize);
		view.setLayoutParams(lp);
		return new PieItem(view, l);
	}

	protected PieItem makeFiller() {
		return new PieItem(null, 1);
	}

	// // static class TabAdapter extends BaseAdapter implements OnCurrentListener {
	//
	// LayoutInflater mInflater;
	// private List<Tab> mTabs;
	// private int mCurrent;
	//
	// public TabAdapter(Context ctx) {
	// mInflater = LayoutInflater.from(ctx);
	// mCurrent = -1;
	// }
	//
	// @Override
	// public int getCount() {
	// return mTabs.size();
	// }
	//
	// @Override
	// public Tab getItem(int position) {
	// return mTabs.get(position);
	// }
	//
	// @Override
	// public long getItemId(int position) {
	// return position;
	// }
	//
	// @Override
	// public View getView(int position, View convertView, ViewGroup parent) {
	// final Tab tab = mTabs.get(position);
	// View view = mInflater.inflate(R.layout.qc_tab,
	// null);
	// ImageView thumb = (ImageView) view.findViewById(R.id.thumb);
	// TextView title1 = (TextView) view.findViewById(R.id.title1);
	// TextView title2 = (TextView) view.findViewById(R.id.title2);
	// Bitmap b = tab.getScreenshot();
	// if (b != null) {
	// thumb.setImageBitmap(b);
	// }
	// if (position > mCurrent) {
	// title1.setVisibility(View.GONE);
	// title2.setText(tab.getTitle());
	// } else {
	// title2.setVisibility(View.GONE);
	// title1.setText(tab.getTitle());
	// }
	// view.setOnClickListener(new OnClickListener() {
	// @Override
	// public void onClick(View v) {
	// mUiController.switchToTab(tab);
	// }
	// });
	// return view;
	// }
	//
	// @Override
	// public void onSetCurrent(int index) {
	// mCurrent = index;
	// }
	//
	// }

	@Override
	public boolean onOpen() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void stopEditingUrl() {
		// TODO Auto-generated method stub

	}

	private void exitApp() {
		materialDialog = new MaterialDialog(mActivity);
		materialDialog.setMessage(R.string.quit_context).setNegativeButton(android.R.string.no, new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				materialDialog.dismiss();
			}

		}).setPositiveButton(android.R.string.yes, new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				materialDialog.dismiss();
				mActivity.onExitApp();
			}
		});
		materialDialog.show();
	}

	private void aboutUs() {
		materialDialog = new MaterialDialog(mActivity);
		materialDialog.setTitle(R.string.about).setMessage(R.string.about_context).setNegativeButton(android.R.string.no, new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				materialDialog.dismiss();
			}

		});
		materialDialog.show();
	}

}
