
package com.wuzhanglong.library.application;

import android.app.Application;

import com.umeng.socialize.UMShareAPI;
import com.vondear.rxtools.RxUtils;

public class BaseAppApplication extends Application {
    private static BaseAppApplication mAppApplication;

    @Override
    public void onCreate() {
        super.onCreate();
        mAppApplication = this;
//        registerActivityLifecycleCallbacks(ActivityLifecycleHelper.build());//仿微信滑动返回
        RxUtils.init(this);//rx工具
        UMShareAPI.get(this);//友盟
    }

    /** 获取Application */
    public static BaseAppApplication getInstance() {
        return mAppApplication;
    }
    /** 初始化ImageLoader */
//    public  void initImageLoader(Context context) {
//        File cacheDir = StorageUtils.getOwnCacheDirectory(context, FileUtil.getSaveFilePath(context));// 获取到缓存的目录地址
//        Log.d("cacheDir", cacheDir.getPath());
//        // 创建配置ImageLoader(所有的选项都是可选的,只使用那些你真的想定制)，这个可以设定在APPLACATION里面，设置为全局的配置参数
//        ImageLoaderConfiguration config = new ImageLoaderConfiguration
//                .Builder(context)
//                // .memoryCacheExtraOptions(480, 800) // max width, max
//                // height，即保存的每个缓存文件的最大长宽
//                // .discCacheExtraOptions(480, 800, CompressFormat.JPEG, 75, null) // Can
//                // slow ImageLoader, use it carefully (Better don't use
//                // it)设置缓存的详细信息，最好不要设置这个
//                .threadPoolSize(3)// 线程池内加载的数量
//                .threadPriority(Thread.NORM_PRIORITY - 2)
//                .denyCacheImageMultipleSizesInMemory()
//                // .memoryCache(new UsingFreqLimitedMemoryCache(2 * 1024 * 1024)) // You can
//                // pass your own memory cache implementation你可以通过自己的内存缓存实现
//                // .memoryCacheSize(2 * 1024 * 1024)
//                // /.discCacheSize(50 * 1024 * 1024)
//                .discCacheFileNameGenerator(new Md5FileNameGenerator())// 将保存的时候的URI名称用MD5
//                // 加密
//                // .discCacheFileNameGenerator(new
//                // HashCodeFileNameGenerator())//将保存的时候的URI名称用HASHCODE加密
//                .tasksProcessingOrder(QueueProcessingType.LIFO)
//                // .discCacheFileCount(100) //缓存的File数量
//                .discCache(new UnlimitedDiskCache(cacheDir))
//                // .defaultDisplayImageOptions(DisplayImageOptions.createSimple())
//                // .imageDownloader(new BaseImageDownloader(context, 5 * 1000, 30 * 1000))
//                // // connectTimeout (5 s), readTimeout (30 s)超时时间
//                .writeDebugLogs() // Remove for release app
//                .build();
//        // Initialize ImageLoader with configuration.
//        ImageLoader.getInstance().init(config);// 全局初始化此配置
//    }


}
