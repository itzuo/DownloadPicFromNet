# DownloadPicFromNet
Retrofit Rxjava 实现图片下载、保存并展示

####首先我们看一下Retrofit常规的用法，在不使用Rxjava的情况下，我们默认返回的是Call。
```java
public interface ServiceApi {  
      
    //下载文件  
    @GET  
    Call<ResponseBody> downloadPicFromNet(@Url String fileUrl);  
}  
```
####但是如果我们要配合Rxjava使用，那么就要按照如下方式来重新定义我们的方法：
```java
@GET  
Observable<ResponseBody> downloadPicFromNet(@Url String fileUrl);  
```
返回一个Observable，方法名很直观就是从网络下载图片 参数是图片的URL路径

####完成请求接口的定义，我们接下来创建Retrofit 对象
```java
Retrofit retrofit = new Retrofit.Builder()  
.baseUrl(BASE_URL)  
.addCallAdapterFactory(RxJavaCallAdapterFactory.create()) //添加Rxjava  
.addConverterFactory(GsonConverterFactory.create()) //定义转化器 可以将结果返回一个json格式 
.build(); 
```

####接下来我们给刚才定义的ServiceApi创建实例，通过上面创建的retrofit来创建
```java
ServiceApi serviceApi = retrofit.create(ServiceApi.class);  
```
####ok，现在我们可以通过serviceApi来调用我们刚才定义的 downloadPicFromNet方法来下载一张图片，可以随意百度一张图片，复制图片地址来做测试。
```java
serviceApi.downloadPicFromNet("http://7xs71d.com2.z0.glb.qiniucdn.com/5f72cedd-47f6-489b-990d-e2c795aef5d6.png")
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
                    Log.d("zxj", "onError ===== " + arg0.toString());
                }

                @Override
                public void onNext(Bitmap arg0) {
                    imageIv.setImageBitmap(arg0);

                }
            });
```
上面的示例就实现了一个下载、保存、并展示的过程，跟传统的AsyncTask相比，代码很简洁，没有很多回调。当然在实际使用中还可以封装一层，把Retrofit 和 ServiceApi 的创建放在一个ServiceApiImpl的实现类中。
