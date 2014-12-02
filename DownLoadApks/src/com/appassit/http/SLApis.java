/*
 * Copyright (C) 2010 mAPPn.Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.appassit.http;

import android.os.Bundle;

/**
 * Market API utility class
 */
public class SLApis {
	public static final String API_BASE_URL = "http://zm.2345.com";

	public static final String GET_RECOMMEND_LIST = API_BASE_URL
			+ "/index.php?c=apiPaperCate&d=getRecomListByPage";
	
	public static void getRecommend(int page, ApiRequestListener listener) {
		Bundle values = new Bundle();
		values.putInt("page", page);
		ThreadUtils.execute(HttpRequestHandler.obtain(GET_RECOMMEND_LIST,
				RequestMethod.POST, listener, values));
	}

}