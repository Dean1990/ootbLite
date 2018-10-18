package com.deanlib.ootblite.data.io.thanksxutils.http.loader;

import com.deanlib.ootblite.data.io.thanksxutils.cache.DiskCacheEntity;
import com.deanlib.ootblite.data.io.thanksxutils.http.request.UriRequest;
import com.deanlib.ootblite.data.IOUtils;

import java.io.InputStream;

/**
 * Author: wyouflf
 * Time: 2014/05/30
 */
/*package*/ class ByteArrayLoader extends Loader<byte[]> {

    @Override
    public Loader<byte[]> newInstance() {
        return new ByteArrayLoader();
    }

    @Override
    public byte[] load(final InputStream in) throws Throwable {
        return IOUtils.readBytes(in);
    }

    @Override
    public byte[] load(final UriRequest request) throws Throwable {
        request.sendRequest();
        return this.load(request.getInputStream());
    }

    @Override
    public byte[] loadFromCache(final DiskCacheEntity cacheEntity) throws Throwable {
        return null;
    }

    @Override
    public void save2Cache(final UriRequest request) {
    }
}
