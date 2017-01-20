package com.zxj.downloadpic;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import java.io.File;

import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Url;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    private Subscription mSubscribe;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final ImageView imageIv = (ImageView) findViewById(R.id.iv);
        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl("http://test.ugou88.com/ugou-wx/")
                .baseUrl("http://www.baidu.com")
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create()) //添加Rxjava
                .addConverterFactory(GsonConverterFactory.create()) //定义转化器 可以将结果返回一个json格式
                .build();

        final ServiceApi serviceApi = retrofit.create(ServiceApi.class);
        mSubscribe =  serviceApi.downloadPicFromNet("http://7xs71d.com2.z0.glb.qiniucdn.com/5f72cedd-47f6-489b-990d-e2c795aef5d6.png")
                .subscribeOn(Schedulers.io())//在新线程中实现该方法
                .map(new Func1<ResponseBody, Bitmap>() {
                    @Override
                    public Bitmap call(ResponseBody responseBody) {
                        String name = "baidu.png";
                        if (FileUtil.writeResponseBodyToDisk(responseBody, name)) {//保存图片成功
                            Bitmap bitmap = BitmapFactory.decodeFile(FileUtil.pathPath+name);
                            return bitmap;//返回一个bitmap对象
                        }
                        return null;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())//在Android主线程中展示
                .subscribe(new Subscriber<Bitmap>() {
                    ProgressDialog dialog = new ProgressDialog(MainActivity.this);

                    @Override
                    public void onStart() {
                        dialog.show();
                        super.onStart();
                    }

                    @Override
                    public void onCompleted() {
                        dialog.dismiss();
                    }

                    @Override
                    public void onError(Throwable arg0) {
                        Log.d("zXj", "onError ===== " + arg0.toString());
                    }

                    @Override
                    public void onNext(Bitmap arg0) {
                        imageIv.setImageBitmap(arg0);

                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mSubscribe != null) mSubscribe.unsubscribe();
    }
}
