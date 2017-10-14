/**
 * Copyright 2013 Mani Selvaraj
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tools.security.utils.volley.request;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.RetryPolicy;
import com.android.volley.toolbox.StringRequest;

/**
 * 请求工具类
 *
 * @author duyifei
 */
public class VolleyRequest extends StringRequest {

    private Priority priority = null;

    public VolleyRequest(String url, Listener<String> listener,
                         ErrorListener errorListener) {
        super(Method.POST, url, listener, errorListener);
    }

    public VolleyRequest(int method, String url, Listener<String> listener,
                         ErrorListener errorListener) {
        super(method, url, listener, errorListener);
    }


    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    /*
     * If prioirty set use it,else returned NORMAL
     *
     * @see com.android.volley.Request#getPriority()
     */
    public Priority getPriority() {
        if (this.priority != null) {
            return priority;
        } else {
            return Priority.NORMAL;
        }
    }

    @Override
    public Request<?> setRetryPolicy(RetryPolicy retryPolicy) {
        return super.setRetryPolicy(new DefaultRetryPolicy(30 * 1000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }
}
