package com.appassit.adapter;

import java.io.File;
import java.lang.ref.WeakReference;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.appassit.R;
import com.appassit.activitys.SLAppication;
import com.appassit.common.BitmapHelp;
import com.appassit.model.DownloadInfo;
import com.appassit.service.DownloadManager;
import com.appassit.service.DownloadService;
import com.appassit.tools.CommonUtils;
import com.appassit.tools.FileUtils;
import com.appassit.tools.StorageUtils;
import com.appassit.tools.ToastUtil;
import com.appassit.widget.NumberProgressBar;
import com.appassit.widget.RippleView;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.callback.BitmapLoadFrom;
import com.lidroid.xutils.bitmap.callback.DefaultBitmapLoadCallBack;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

public class RecommendAppAdapter extends ViewAdapter<DownloadInfo> {
	private DownloadManager manager;
	// private static boolean isNotifyDataChange = false;
	private FileUtils fileUtils;
	private static BitmapUtils bitmapUtils;

	/*public RecommendAppAdapter(OnClickListener onClickListener) {
		super(onClickListener);
		imageManager = new ImageManager(SLAppication.getContext());
		imageManager.setPlaceholderResId(R.color.cpb_blue);
	}*/

	public RecommendAppAdapter() {
		super();
		manager = DownloadService.getDownloadManager(SLAppication.getContext());
		fileUtils = new FileUtils();
		bitmapUtils = BitmapHelp.getBitmapUtils(SLAppication.getContext());
		bitmapUtils.configDefaultLoadingImage(R.drawable.wallpapermgr_thumb_default);
		bitmapUtils.configDefaultLoadFailedImage(R.drawable.wallpapermgr_thumb_default);
		bitmapUtils.configDefaultBitmapConfig(Bitmap.Config.ARGB_8888);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder = null;
		DownloadInfo info = getItem(position);
		DownloadInfo infoFromData = manager.getDownloadInfo(info.getDownloadUrl());
		if (infoFromData != null) {
			info = null;
			info = infoFromData;
		}
		if (convertView == null) {
			convertView = LayoutInflater.from(SLAppication.getContext()).inflate(R.layout.layout_down_appitem, null);
			holder = new ViewHolder(info);
			holder.init(convertView);
			convertView.setTag(holder);
			holder.progressBar.setTag(info.getDownloadUrl());
			holder.ivAppIcon.setTag(info.getIconPath());
			holder.refresh(info);
		} else {
			Object tag = (ViewHolder) convertView.getTag();
			if (tag != null && tag instanceof ViewHolder) {
				holder = (ViewHolder) tag;
			}
			holder.progressBar.setTag(info.getDownloadUrl());
			holder.ivAppIcon.setTag(info.getIconPath());
			holder.update(info);
		}
		HttpHandler<File> handler = info.getHandler();
		if (handler != null) {
			RequestCallBack<File> callBack = handler.getRequestCallBack();
			if (callBack instanceof DownloadManager.ManagerCallBack) {
				DownloadManager.ManagerCallBack managerCallBack = (DownloadManager.ManagerCallBack) callBack;
				if (managerCallBack.getBaseCallBack() == null) {
					managerCallBack.setBaseCallBack(new DownloadRequestCallBack());
				}
			}
			holder.refreshDownloadStatus = true;
			callBack.setUserTag(new WeakReference<ViewHolder>(holder));
		}
		return convertView;
	}

	class ViewHolder {
		DownloadInfo downloadInfo = null;
		ImageView ivAppIcon;
		TextView apkNameTxt;
		TextView apkTotalTxt;
		RippleView btDown;
		TextView btStatus;
		NumberProgressBar progressBar;
		boolean refreshDownloadStatus = false;

		public ViewHolder(DownloadInfo info) {
			this.downloadInfo = info;
		}

		void init(View parent) {
			ivAppIcon = (ImageView) parent.findViewById(R.id.ivAppIcon);
			apkNameTxt = (TextView) parent.findViewById(R.id.nameTv);
			apkTotalTxt = (TextView) parent.findViewById(R.id.totalTv);
			btDown = (RippleView) parent.findViewById(R.id.downBtn);
			btStatus = (TextView) parent.findViewById(R.id.downStatus);
			progressBar = (NumberProgressBar) parent.findViewById(R.id.progressBar);
			btDown.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					pause_continue(downloadInfo);
				}
			});
		}

		public void update(DownloadInfo downloadInfo) {
			this.downloadInfo = downloadInfo;
			refresh(downloadInfo);
		}

		public void refresh(DownloadInfo downloadInfo) {
			apkNameTxt.setText(downloadInfo.getFileName());
			String tag = (String) progressBar.getTag();
			String tagIcon = (String) ivAppIcon.getTag();
			if (tagIcon.equals(downloadInfo.getIconPath())) {
				bitmapUtils.display(ivAppIcon, tagIcon, new CustomBitmapLoadCallBack());
			}
			HttpHandler<File> handler = downloadInfo.getHandler();
			if (handler != null && tag.equals(downloadInfo.getDownloadUrl())) {
				Log.e("YM", downloadInfo.getFileName() + "refresh progress");
				HttpHandler.State state = downloadInfo.getState();
				if (state == null) {

				} else {
					progressBar.setVisibility(View.VISIBLE);
					apkTotalTxt.setText("(" + StorageUtils.size(downloadInfo.getProgress()) + "/" + StorageUtils.size(downloadInfo.getFileLength()) + ")");
					if (downloadInfo.getFileLength() > 0) {
						progressBar.setProgress((int) (downloadInfo.getProgress() * 100 / downloadInfo.getFileLength()));
					}
					switch (state) {
					case LOADING:
						btStatus.setText("下载中..");
						progressBar.setVisibility(View.VISIBLE);
						break;
					case WAITING:
						btStatus.setText("等待中..");
						progressBar.setVisibility(View.VISIBLE);
						break;
					case STARTED:
						btStatus.setText("暂停");
						progressBar.setVisibility(View.VISIBLE);
						break;
					case CANCELLED:
						btStatus.setText("继续");
						progressBar.setVisibility(View.VISIBLE);
						break;
					case SUCCESS:
						btStatus.setText("打开");
						progressBar.setVisibility(View.GONE);
						break;
					case FAILURE:
						btStatus.setText("重试");
						progressBar.setVisibility(View.VISIBLE);
						break;
					default:
						break;
					}
				}
				refreshDownloadStatus = true;
			} else {
				progressBar.setVisibility(View.INVISIBLE);
				btStatus.setText("下载");
				apkTotalTxt.setText("");
				progressBar.setTag(null);
			}
		}

		private void pause_continue(DownloadInfo downloadInfo) {
			HttpHandler.State state = downloadInfo.getState();
			if (state == null && StorageUtils.isSDCardReady()) {
				File file = new File(fileUtils.getApkPath() + downloadInfo.getFileName());
				if (file.exists()) {
					ToastUtil.showShort("安装包已存在!");
					CommonUtils.install(downloadInfo.getFileSavePath(), SLAppication.getContext());
					return;
				}
				try {
					manager.addNewDownload(downloadInfo.getDownloadUrl(), downloadInfo.getIconPath(), downloadInfo.getFileName(), fileUtils.getApkPath()
							+ downloadInfo.getFileName(), true, // 如果目标文件存在，接着未完成的部分继续下载。服务器不支持RANGE时将从新下载。
							false, // 如果从请求返回信息中获取到文件名，下载完成后自动重命名。
							null);
				} catch (DbException e) {
				}
				notifyDataSetChanged();
			} else {
				switch (state) {
				case WAITING:
				case STARTED:
				case LOADING:
					try {
						manager.stopDownload(downloadInfo);
					} catch (DbException e) {

					}
					break;
				case CANCELLED:
				case FAILURE:
					try {
						manager.resumeDownload(downloadInfo, new DownloadRequestCallBack());
					} catch (DbException e) {

					}
					notifyDataSetChanged();
					break;
				case SUCCESS:
					CommonUtils.install(downloadInfo.getFileSavePath(), SLAppication.getContext());
					break;
				default:
					break;
				}
			}
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

	private class DownloadRequestCallBack extends RequestCallBack<File> {

		@SuppressWarnings("unchecked")
		private void refreshListItem() {
			if (userTag == null)
				return;
			WeakReference<ViewHolder> tag = (WeakReference<ViewHolder>) userTag;
			ViewHolder holder = tag.get();
			Log.e("YM", holder.downloadInfo.getFileName() + "-DownloadRequestCallBack" + holder.refreshDownloadStatus);
			if (holder != null && holder.refreshDownloadStatus) {
				holder.refresh(holder.downloadInfo);
			}
		}

		public void onStart() {
			refreshListItem();
		}

		public void onLoading(long total, long current, boolean isUploading) {
			refreshListItem();
		}

		public void onSuccess(ResponseInfo<File> responseInfo) {
			refreshListItem();
		}

		public void onFailure(HttpException error, String msg) {
			refreshListItem();
		}

		public void onCancelled() {
			refreshListItem();
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