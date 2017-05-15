package com.kimeeo.kAndroid.okHTTPDataProvider;

import android.content.Context;
import com.kimeeo.kAndroid.core.utils.NetworkUtilities;
import java.util.List;

import okhttp3.OkHttpClient;

/**
 * Created by BhavinPadhiyar on 02/05/16.
 */
abstract public class JSONFallbackOfflineDataProvider extends JSONDataProvider {
    public boolean isConnected() {
        return isConnected;
    }
    private boolean isConnected=false;
    private IOfflineListProvider offlineListProvider;

    public JSONFallbackOfflineDataProvider(OkHttpClient client,Context context, IOfflineListProvider offlineListProvider)
    {
        super(client);
        this.isConnected = NetworkUtilities.isConnected(context);
        this.offlineListProvider = offlineListProvider;
    }
    protected void invokeLoadNext()
    {
        if(isConnected)
            super.invokeLoadNext();
        else
            listDataIn(offlineListProvider.getData(getNextURL(),getNextParam()));
    }
    @Override
    protected void invokeLoadRefresh()
    {
        if(isConnected)
            super.invokeLoadRefresh();
        else
            listDataIn(offlineListProvider.getData(getNextURL(), getRefreshParam()));
    }

    protected void listDataIn(List list) {
        if(list!=null)
            addData(list);
        else
            dataLoadError(null);
    }
}