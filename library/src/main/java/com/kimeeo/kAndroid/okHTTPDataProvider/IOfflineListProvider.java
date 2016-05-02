package com.kimeeo.kAndroid.okHTTPDataProvider;

import java.util.List;

/**
 * Created by BhavinPadhiyar on 02/05/16.
 */
public interface IOfflineListProvider {
    List getData(String nextURL, Object nextParam);
}
