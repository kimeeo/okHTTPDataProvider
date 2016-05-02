package com.kimeeo.kAndroid.okHTTPDataProvider;

import android.content.Context;

import com.kimeeo.kAndroid.listViews.dataProvider.BackgroundNetworkDataProvider;
import com.kimeeo.kAndroid.listViews.dataProvider.NetworkDataProvider;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;



/**
 * Created by BhavinPadhiyar on 02/05/16.
 */
abstract public class BaseOkHTTPDataProvider extends BackgroundNetworkDataProvider
{
    OkHttpClient client;
    public BaseOkHTTPDataProvider(OkHttpClient client)
    {
        this.client = client;
    }

    public void garbageCollectorCall()
    {
        super.garbageCollectorCall();
    }
    protected void dataIn(String url, Object data)
    {

    }

    public MediaType getMediaType()
    {
        return MediaType.parse("application/text; charset=utf-8");
    };
    public String getNextParamString()
    {
        return null;
    };
    public String getRefreshParamString()
    {
        return null;
    };

    private void invokePostService(String url, Object param)
    {
        RequestBody body = null;

        if(param instanceof Map) {
            Map<String, Object> params = (Map<String, Object>) param;
            if (params != null && params.entrySet().size() != 0) {
                
            }
        }
        else if(param instanceof String)
        {
            body = RequestBody.create(getMediaType(), (String)param);
        }

        Request request = new Request.Builder().post(body).url(url).build();
        try {
            if(client!=null) {
                Response response = client.newCall(request).execute();
                String value = response.body().string();
                dataHandler(url, value);
            }
            else
                dataLoadError("NULL CLIENT");
        } catch (IOException e) {
            dataLoadError(e);
        }
    }
    private void invokeGetService(String url)
    {
        Request request = new Request.Builder().url(url).build();
        try {
            if(client!=null) {
                Response response = client.newCall(request).execute();
                String value = response.body().string();
                dataHandler(url, value);
            }
            else
                dataLoadError("NULL CLIENT");
        } catch (IOException e) {
            dataLoadError(e);
        }
    }
    abstract protected void dataHandler(String url, String json);

    protected void invokeLoadNext()
    {
        String url = getNextURL();
        if(url!=null) {
            if (getMethod() == METHOD_GET) {
                invokeGetService(url);
            }
            else if (getMethod() == METHOD_POST) {
                if(getNextParam()!=null)
                    invokePostService(url,getNextParam());
                else if(getNextParamString()!=null)
                    invokePostService(url,getNextParamString());
            }
        }
        else {
            setCanLoadNext(false);
            dataLoadError(null);
        }
    }
    protected void invokeloadRefresh()
    {
        String url = getRefreshURL();
        if(url!=null) {
            if (getMethod() == METHOD_GET)
                invokeGetService(url);
            else if (getMethod() == METHOD_POST) {
                if(getRefreshParam()!=null)
                    invokePostService(url,getRefreshParam());
                else if(getRefreshParamString()!=null)
                    invokePostService(url,getRefreshParamString());
            }
        }
        else {
            setCanLoadRefresh(false);
            dataLoadError(null);
        }
    }
}
