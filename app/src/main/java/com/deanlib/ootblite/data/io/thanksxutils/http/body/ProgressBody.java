package com.deanlib.ootblite.data.io.thanksxutils.http.body;


import com.deanlib.ootblite.data.io.thanksxutils.http.ProgressHandler;

/**
 * Created by wyouflf on 15/8/13.
 */
public interface ProgressBody extends RequestBody {
    void setProgressHandler(ProgressHandler progressHandler);
}
