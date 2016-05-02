package com.kimeeo.kAndroid.okHTTPDataProvider;

import android.content.Context;

import com.google.gson.Gson;
import com.kimeeo.kAndroid.listViews.dataProvider.BackgroundNetworkDataProvider;
import com.kimeeo.kAndroid.listViews.dataProvider.DataModel;
import com.kimeeo.kAndroid.listViews.dataProvider.IParseableObject;


import java.util.List;

import okhttp3.OkHttpClient;

/**
 * Created by BhavinPadhiyar on 02/05/16.
 */
abstract public class JSONDataProvider extends BaseOkHTTPDataProvider
{
    protected Gson gson;
    public JSONDataProvider(OkHttpClient client)
    {
        super(client);
        gson= new Gson();
    }
    public void garbageCollectorCall()
    {
        super.garbageCollectorCall();
        gson=null;
    }

    @Override
    protected void dataHandler(String url, String json)
    {
        try
        {
            Class<DataModel> clazz = getDataModel();
            DataModel dataModel = gson.fromJson(json, clazz);
            List<?> list=dataModel.getDataProvider();
            if(list!=null) {
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i) instanceof IParseableObject)
                        ((IParseableObject) list.get(i)).dataLoaded(dataModel);
                }
                dataIn(url,dataModel);
                addDataThreadSafe(list);
                //addData(list);
            }
            else
            {
                dataIn(url,json);
                dataLoadError(json);
            }
        }
        catch (Throwable e)
        {
            dataIn(url,json);
            dataLoadError(e);
        }
    }


}
