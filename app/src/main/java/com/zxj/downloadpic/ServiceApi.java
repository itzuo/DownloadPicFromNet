package com.zxj.downloadpic;

import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Url;
import rx.Observable;

public interface ServiceApi {
    /**
     * 下载图片
     * @param fileUrl
     * @return
     */
    @GET
    Observable<ResponseBody> downloadPicFromNet(@Url String fileUrl);
}
