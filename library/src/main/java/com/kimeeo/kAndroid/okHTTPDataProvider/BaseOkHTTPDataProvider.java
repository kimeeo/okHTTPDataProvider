package com.kimeeo.kAndroid.okHTTPDataProvider;

import android.os.Handler;
import android.os.Looper;
import com.kimeeo.kAndroid.dataProvider.NetworkDataProvider;
import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;



/**
 * Created by BhavinPadhiyar on 02/05/16.
 */
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
        if(body!=null) {
            Request request = new Request.Builder().post(body).url(url).build();
            callServcice(request,url);
        }
        else
            dataLoadError("NO PARAM");
    }
    private void invokeGetService(final String url)
    {
        Request request = new Request.Builder().url(url).build();
        callServcice(request,url);
    }

    private void callServcice(Request request,final String url) {
        if(client!=null) {
            Callback callback =new Callback(){
                @Override
                public void onFailure(Call call, final IOException e) {
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
                    try {
                        final String value = response.body().string();
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {dataHandler(url, value);
                            }
                        });
                    } catch (final IOException e) {
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {dataLoadError(e);
                            }
                        });
                    }
                }
            };
            Call call = client.newCall(request);
            call.enqueue(callback);
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

