package com.kimeeo.kAndroid.okHTTPDataProvider;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.kimeeo.kAndroid.listViews.dataProvider.BackgroundNetworkDataProvider;
import com.kimeeo.kAndroid.listViews.dataProvider.NetworkDataProvider;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;



/**
 * Created by BhavinPadhiyar on 02/05/16.
 */
abstract public class BaseOkHTTPDataProvider extends NetworkDataProvider
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
    final protected Object getNextParam(){return null;}
    final protected Object getRefreshParam() {return null;}

    protected abstract RequestBody getNextRequestBody();
    protected abstract RequestBody getRefreshRequestBody();

    private void invokePostService(final String url, RequestBody body)
    {
        /*
        RequestBody body = null;

        if(param instanceof Map) {
            Map<String, Object> params = (Map<String, Object>) param;
            if (params != null && params.entrySet().size() != 0) {
                FormBody.Builder builder= new FormBody.Builder();
                Map<String, Object> paramsMap = (Map<String, Object>) param;
                for (Map.Entry<String, Object> stringObjectEntry : paramsMap.entrySet()) {
                    builder.add(stringObjectEntry.getKey(), (String) stringObjectEntry.getValue());
                }
                body = builder.build();
            }
        }
        else if(param instanceof File) {
            body = RequestBody.create(getMediaType(), (File)param);
        }
        else if(param instanceof String)
        {
            body = RequestBody.create(getMediaType(), (String)param);
        }
        */
        if(body!=null) {
            Request request = new Request.Builder().post(body).url(url).build();
            if (client != null) {
                Callback callback =new Callback(){
                    @Override
                    public void onFailure(Call call,final IOException e) {
                        Handler mainHandler = new Handler(Looper.getMainLooper());
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                dataLoadError(e);
                            }
                        });
                    }
                    @Override
                    public void onResponse(Call call, final Response response) throws IOException {

                        Handler mainHandler = new Handler(Looper.getMainLooper());
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                String value = null;
                                try {
                                    value = response.body().string();
                                    dataHandler(url, value);
                                } catch (IOException e) {
                                    dataLoadError(e);
                                }
                            }
                        });
                    }
                };
                client.newCall(request).enqueue(callback);

            } else
                dataLoadError("NULL CLIENT");
        }
        else
            dataLoadError("NO PARAM");
    }
    private void invokeGetService(final String url)
    {
        Request request = new Request.Builder().url(url).build();
        if(client!=null) {
            Callback callback =new Callback(){
                @Override
                public void onFailure(Call call,final IOException e) {
                    Handler mainHandler = new Handler(Looper.getMainLooper());
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            dataLoadError(e);
                        }
                    });
                }
                @Override
                public void onResponse(Call call, final Response response) throws IOException {
                    Handler mainHandler = new Handler(Looper.getMainLooper());
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            String value = null;
                            try {
                                value = response.body().string();
                                dataHandler(url, value);
                            } catch (IOException e) {
                                dataLoadError(e);
                            }
                        }
                    });


                }
            };
            client.newCall(request).enqueue(callback);
        }
        else
            dataLoadError("NULL CLIENT");
    }
    abstract protected void dataHandler(String url, String json);
    @Override
    protected void invokeLoadNext()
    {
        String url = getNextURL();
        if(url!=null) {
            if (getMethod() == METHOD_GET) {
                invokeGetService(url);
            }
            else if (getMethod() == METHOD_POST) {
                if(getNextParam()!=null)
                    invokePostService(url,getNextRequestBody());
            }
        }
        else {
            setCanLoadNext(false);
            dataLoadError(null);
        }
    }


    @Override
    protected void invokeLoadRefresh()
    {
        String url = getRefreshURL();
        if(url!=null) {
            if (getMethod() == METHOD_GET)
                invokeGetService(url);
            else if (getMethod() == METHOD_POST) {
                if(getRefreshParam()!=null)
                    invokePostService(url,getRefreshRequestBody());
            }
        }
        else {
            setCanLoadRefresh(false);
            dataLoadError(null);
        }
    }

}
