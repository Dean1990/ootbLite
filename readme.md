### OotbLite  

[![](https://jitpack.io/v/Dean1990/ootbLite.svg)](https://jitpack.io/#Dean1990/ootbLite)

#### 安装

Project build.gradle

```groovy
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}
```

Module build.gradle

```groovy
dependencies {
	implementation 'com.github.Dean1990:OotbLite:-SNAPSHOT'
}
```



#### 使用

[https://github.com/Dean1990/ootb](https://github.com/Dean1990/ootb)

#### 拓展

```groovy
dependencies {
    //控件注解
    //butterknife https://github.com/JakeWharton/butterknife
    implementation 'com.jakewharton:butterknife:8.8.1'
  	annotationProcessor 'com.jakewharton:butterknife-compiler:8.8.1'
    
    //json
    //fastjson https://github.com/alibaba/fastjson
	implementation 'com.alibaba:fastjson:1.1.70.android'
    
    //图片加载
    //Glide https://github.com/bumptech/glide
    implementation 'com.github.bumptech.glide:glide:4.8.0'
  	annotationProcessor 'com.github.bumptech.glide:compiler:4.8.0'
    //glide-transformations https://github.com/wasabeef/glide-transformations
    implementation 'jp.wasabeef:glide-transformations:3.3.0'
    implementation 'jp.co.cyberagent.android.gpuimage:gpuimage-library:1.4.1'// If you want to use the GPU Filters
    
    //权限管理
    //RxPermissions https://github.com/tbruyelle/RxPermissions
    implementation 'com.github.tbruyelle:rxpermissions:0.10.2'
    //HiPermission https://github.com/yewei02538/HiPermission
    implementation 'me.weyye.hipermission:library:1.0.7'
    
    //图表
    //MPAndroidChart https://github.com/PhilJay/MPAndroidChart
    implementation 'com.github.PhilJay:MPAndroidChart:v3.0.3'
    
    //扫码
    //BGAQRCode-Android https://github.com/bingoogolapple/BGAQRCode-Android
    implementation 'cn.bingoogolapple:bga-qrcode-zbar:1.2.5'//zbar扫码
    implementation 'cn.bingoogolapple:bga-qrcode-zxing:1.2.5'//zxing扫码
    
    //消息传递
    //EventBus https://github.com/greenrobot/EventBus
    implementation 'org.greenrobot:eventbus:3.1.1'
    
    //Rx
    //RxJava https://github.com/ReactiveX/RxJava
    implementation "io.reactivex.rxjava2:rxjava:2.2.2"
    //RxAndroid https://github.com/ReactiveX/RxAndroid
    implementation 'io.reactivex.rxjava2:rxandroid:2.1.0'
    //RxBinding https://github.com/JakeWharton/RxBinding
    implementation 'com.jakewharton.rxbinding2:rxbinding:2.1.1'
    
    //下载器
    //FileDownloader https://github.com/lingochamp/FileDownloader
    implementation 'com.liulishuo.filedownloader:library:1.7.5'
    
    //内存检测
    //leakcanary https://github.com/square/leakcanary
    debugImplementation 'com.squareup.leakcanary:leakcanary-android:1.6.1'
  	releaseImplementation 'com.squareup.leakcanary:leakcanary-android-no-op:1.6.1'
  	debugImplementation 'com.squareup.leakcanary:leakcanary-support-fragment:1.6.1'// Optional, if you use support library fragments
    
    //图片查看
    //PhotoView https://github.com/chrisbanes/PhotoView
    implementation 'com.github.chrisbanes:PhotoView:2.1.4'
    
    //音视频播放
    //VideoDemoQcl https://github.com/qiushi123/VideoDemoQcl
    implementation 'fm.jiecao:jiecaovideoplayer:1.8'//引入类库时有个bug所以建议直接引入源码lib
    
    //汉语拼音
    //pinyin4j https://github.com/belerweb/pinyin4j
    implementation 'com.belerweb:pinyin4j:2.5.0'
    
    //上下拉刷新加载
    //SmartRefreshLayout https://github.com/scwang90/SmartRefreshLayout
    implementation 'com.scwang.smartrefresh:SmartRefreshLayout:1.1.0-alpha-14'
	implementation 'com.scwang.smartrefresh:SmartRefreshHeader:1.1.0-alpha-14'//没有使用特殊Header，可以不加这行
	implementation 'com.android.support:appcompat-v7:25.3.1'//版本 23以上（必须）
    
    //流式布局
    //FlowLayout https://github.com/hongyangAndroid/FlowLayout
    implementation 'com.hyman:flowlayout-lib:1.1.2'
    
    //小红点
    //BadgeView https://github.com/qstumn/BadgeView
    implementation 'q.rorbin:badgeview:1.1.3'
    
    //循环banner
    //Android-LoopView https://github.com/xuehuayous/Android-LoopView
    implementation 'com.kevin:loopview:1.4.1'
    
    //竖向TabLayout
    //VerticalTabLayout https://github.com/qstumn/VerticalTabLayout
    implementation 'q.rorbin:VerticalTabLayout:1.2.5'
    
    //带标题悬停的ListView
    //StickyListHeaders https://github.com/emilsjolander/StickyListHeaders
    implementation 'se.emilsjolander:stickylistheaders:2.7.0'
    
    //双向滑动的ListView
    //BothwayListview https://github.com/Dean1990/BothwayListview
    implementation 'com.github.Dean1990:BothwayListview:1.0.3'
    
    //HTML解析
	//jsoup https://jsoup.org
    implementation 'org.jsoup:jsoup:1.11.3'
    
    //ListView滑出菜单
    //SwipeMenuListView https://github.com/baoyongzhang/SwipeMenuListView
    implementation 'com.baoyz.swipemenulistview:library:1.3.0'
    
    //动画文本
    //TextPathView https://github.com/totond/TextPathView
    implementation 'com.yanzhikai:TextPathView:0.1.3'
    
    //文件选择器
    //AndroidPicker https://github.com/gzu-liyujiang/AndroidPicker
    implementation('com.github.gzu-liyujiang.AndroidPicker:FilePicker:1.5.6') {
        exclude group: 'com.android.support'
    }
    
    //图片，视频选择器
    //RxGalleryFinal https://github.com/FinalTeam/RxGalleryFinal
    implementation 'cn.finalteam.rxgalleryfinal:library:1.1.3'
    
    //日历选择器
    //calendar_select 可单选日期，可选时间段 https://github.com/richzjc/calendar_select
    implementation 'com.richzjc:calendar_select:1.0.0'
    implementation 'com.android.support:recyclerview-v7:28.0.0-rc02'//需要recyclerview
    
    //RatingBar 评分
    //MaterialRatingBar https://github.com/DreaminginCodeZH/MaterialRatingBar
    implementation 'me.zhanghai.android.materialratingbar:library:1.3.1'
    
}
```

