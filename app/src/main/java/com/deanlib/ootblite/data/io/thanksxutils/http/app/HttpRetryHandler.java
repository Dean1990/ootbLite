package com.deanlib.ootblite.data.io.thanksxutils.http.app;


import org.json.JSONException;

import com.deanlib.ootblite.data.io.thanksxutils.common.Callback;
import com.deanlib.ootblite.data.io.thanksxutils.ex.HttpException;
import com.deanlib.ootblite.data.io.thanksxutils.http.HttpMethod;
import com.deanlib.ootblite.data.io.thanksxutils.http.request.UriRequest;
import com.deanlib.ootblite.utils.DLog;

import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.NoRouteToHostException;
import java.net.PortUnreachableException;
import java.net.ProtocolException;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.HashSet;

/**
 * Author: wyouflf
 * Time: 2014/05/30
 */
public class HttpRetryHandler {

    protected int maxRetryCount = 2;

    protected static HashSet<Class<?>> blackList = new HashSet<Class<?>>();

    static {
        blackList.add(HttpException.class);
        blackList.add(Callback.CancelledException.class);
        blackList.add(MalformedURLException.class);
        blackList.add(URISyntaxException.class);
        blackList.add(NoRouteToHostException.class);
        blackList.add(PortUnreachableException.class);
        blackList.add(ProtocolException.class);
        blackList.add(NullPointerException.class);
        blackList.add(FileNotFoundException.class);
        blackList.add(JSONException.class);
        blackList.add(UnknownHostException.class);
        blackList.add(IllegalArgumentException.class);
    }

    public HttpRetryHandler() {
    }

    public void setMaxRetryCount(int maxRetryCount) {
        this.maxRetryCount = maxRetryCount;
    }

    public boolean canRetry(UriRequest request, Throwable ex, int count) {

        DLog.w(ex.getMessage(), ex);

        if (count > maxRetryCount) {
            DLog.w(request.toString());
            DLog.w("The Max Retry times has been reached!");
            return false;
        }

        if (!HttpMethod.permitsRetry(request.getParams().getMethod())) {
            DLog.w(request.toString());
            DLog.w("The Request Method can not be retried.");
            return false;
        }

        if (blackList.contains(ex.getClass())) {
            DLog.w(request.toString());
            DLog.w("The Exception can not be retried.");
            return false;
        }

        return true;
    }
}
