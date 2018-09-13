package com.deanlib.ootblite;

import android.app.Application;
import android.content.Context;

import com.deanlib.ootblite.data.io.Request;
import com.deanlib.ootblite.data.io.thanksxutils.DbManager;
import com.deanlib.ootblite.data.io.thanksxutils.HttpManager;
import com.deanlib.ootblite.data.io.thanksxutils.common.TaskController;
import com.deanlib.ootblite.data.io.thanksxutils.common.task.TaskControllerImpl;
import com.deanlib.ootblite.data.io.thanksxutils.db.DbManagerImpl;
import com.deanlib.ootblite.data.io.thanksxutils.http.HttpManagerImpl;
import com.deanlib.ootblite.utils.DLog;

import java.lang.reflect.Method;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;


/**
 * 该工程配置类
 * <p>
 * 配置DEBUG,服务器地址等
 * <p>
 * Created by dean on 2017/4/24.
 */

public class OotbConfig {


    private static boolean DEBUG = false;
//    private static RefWatcher mRefWatcher;

//    public static Context mContext;
    private static Application mAppContext;

    private static TaskController mTaskController;
    private static HttpManager mHttpManager;

    public static void init(Application context, boolean debug) {

        DEBUG = debug;
        mAppContext = context;
//        mContext = context.getApplicationContext();

        DLog.getInstance();

        //内存检测
//        if (!LeakCanary.isInAnalyzerProcess(context)) {
//            mRefWatcher = LeakCanary.install(context);
//        }

    }

    public static boolean isDEBUG() {

        return DEBUG;
    }

//    public static RefWatcher getRefWatcher() {
//        if (mRefWatcher==null)
//            mRefWatcher = LeakCanary.install(mAppContext);
//        return mRefWatcher;
//    }

    public static String getRequestServer() {
        return Request.SERVER;
    }

//    public static void setRequestServer(String requestServer) {
//        RequestServer = requestServer;
//    }

//    public static RequestParams getExtenisonParams() {
//        return extenisonParams;
//    }
//
//    public static void setExtenisonParams(RequestParams extenisonParams) {
//        UtilsConfig.extenisonParams = extenisonParams;
//    }

    /**
     * 使用Requst类时，必须先对其设置
     *
     * @param requestServer
     * @param param
     * @param result
     */
    public static void setRequestServer(String requestServer, Request.IRequestParam param, Request.Result result, Request.ILoadingDialog dialog) {

        Request.SERVER = requestServer;

        Request.iRequestParam = param;

        Request.resultMode = result;

        Request.iLoadingDialog = dialog;

    }


    /**
     * @param falseData
     * @see OotbConfig#setRequestFalseData(boolean, long)
     */
    public static void setRequestFalseData(boolean falseData) {

        setRequestFalseData(falseData, 0);

    }

    /**
     * 设置网络请求的假数据开关
     * 在实现request的parse方法中设置假数据，默认json = {}
     * 做为测试时使用，开启后，不会请求网络而直接调用parse方法，并回调RequestCallback的onSuccess和onFinish方法。
     *
     * @param falseData
     * @param delayed   延迟时间毫秒
     */
    public static void setRequestFalseData(boolean falseData, long delayed) {

        Request.FALSEDATA = falseData;
        Request.DELAYED = delayed < 0 ? 0 : delayed;

    }

    //xutils3

    public static Application app() {
        if (mAppContext == null) {
            try {
                // 在IDE进行布局预览时使用
                Class<?> renderActionClass = Class.forName("com.android.layoutlib.bridge.impl.RenderAction");
                Method method = renderActionClass.getDeclaredMethod("getCurrentContext");
                Context context = (Context) method.invoke(null);
                mAppContext = new MockApplication(context);
            } catch (Throwable ignored) {
                throw new RuntimeException("please invoke OotbConfig.init(app) on Application#onCreate()"
                        + " and register your Application in manifest.");
            }
        }
        return mAppContext;
    }

    public static HttpManager http() {
        if (mHttpManager == null) {
            HttpManagerImpl.registerInstance();
        }
        return mHttpManager;
    }

    public static TaskController task() {
        TaskControllerImpl.registerInstance();
        return mTaskController;
    }

    public static DbManager getDb(DbManager.DaoConfig daoConfig) {
        return DbManagerImpl.getInstance(daoConfig);
    }

    public static void setHttpManager(HttpManager httpManager) {
        mHttpManager = httpManager;
    }

    public static void setTaskController(TaskController taskController) {
        if (mTaskController == null) {
            mTaskController = taskController;
        }
    }

    private static class MockApplication extends Application {
        public MockApplication(Context baseContext) {
            this.attachBaseContext(baseContext);
        }
    }
}
