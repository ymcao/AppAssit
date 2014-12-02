package com.appassit.widget;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import android.content.Context;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.appassit.R;
import com.appassit.model.city.BaseCityModel;


@EViewGroup(R.layout.layout_city_item)
public class CityItem extends LinearLayout {

    @ViewById(R.id.tv_city_name_layout)
    TextView tvCityName;

    public CityItem(Context context) {
        super(context);
    }

    public void bind(BaseCityModel model) {
        tvCityName.setText(model.getCityName());
    }
}
