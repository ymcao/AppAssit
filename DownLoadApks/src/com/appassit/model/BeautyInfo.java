package com.appassit.model;

import android.os.Parcel;
import android.os.Parcelable;

public class BeautyInfo implements Parcelable, IBaseModel {

	public int girlId;
	public String girlName;
	public String recomTime;
	public String url1, url2, url3;
	public int flag = 0;

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(girlId);
		dest.writeString(girlName);
		dest.writeString(recomTime);
		dest.writeString(url1);
		dest.writeString(url2);
		dest.writeString(url3);
		dest.writeInt(flag);
	}

	public static final Parcelable.Creator<BeautyInfo> CREATOR = new Creator<BeautyInfo>() {
		@Override
		public BeautyInfo createFromParcel(Parcel source) {
			BeautyInfo ri = new BeautyInfo();
			ri.girlId = source.readInt();
			ri.girlName = source.readString();
			ri.recomTime = source.readString();
			ri.url1 = source.readString();
			ri.url2 = source.readString();
			ri.url3 = source.readString();
			ri.flag = source.readInt();
			return ri;
		}

		@Override
		public BeautyInfo[] newArray(int size) {
			return new BeautyInfo[size];
		}
	};

	/*public static final IConvertJson.IConvertModel<BeautyInfo> CONVERTOR = new AbsConvertModel<BeautyInfo>() {

		@Override
		public BeautyInfo createFromJson(JSONObject json) {
			BeautyInfo ri = new BeautyInfo();
			ri.girlId = json.optInt("girlId");
			ri.girlName = json.optString("girlName");
			ri.recomTime = json.optString("recomTime");
			ri.url1 = json.optString("url1");
			ri.url2 = json.optString("url2");
			ri.url3 = json.optString("url3");
			ri.flag = json.optInt("flag");
			return ri;
		}
	};*/

}
