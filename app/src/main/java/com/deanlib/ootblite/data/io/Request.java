package com.deanlib.ootblite.data.io;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.deanlib.ootblite.OotbConfig;
import com.deanlib.ootblite.data.io.thanksxutils.common.Callback;
import com.deanlib.ootblite.data.io.thanksxutils.common.util.KeyValue;
import com.deanlib.ootblite.data.io.thanksxutils.http.HttpMethod;
import com.deanlib.ootblite.data.io.thanksxutils.http.RequestParams;
import com.deanlib.ootblite.utils.DLog;
import com.deanlib.ootblite.utils.MD5;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;

/**
 * 网络请求的基类
 * <p>
 * Created by dean on 2017/4/19.
 */
public abstract class Request {

    public static final long EXPIRE_SECOND = 1000;
    public static final long EXPIRE_SECOND_10 = 1000 * 10;
    public static final long EXPIRE_MINUTE = 1000 * 60;
    public static final long EXPIRE_MINUTE_10 = 1000 * 60 * 10;
    public static final long EXPIRE_HOUR = 1000 * 60 * 60;
    public static final long EXPIRE_DAY = 1000 * 60 * 60 * 24;
    public static final long EXPIRE_WEEK = 1000 * 60 * 60 * 24 * 7;
    public static final long EXPIRE_MONTH = 1000 * 60 * 60 * 24 * 30;
    public static final long EXPIRE_YEAR = 1000 * 60 * 60 * 24 * 365;


    public static int requestCount;//当前请求次数
    /**
     * 服务器地址
     */
    public static String SERVER = "";
    //假数据
    public static boolean FALSEDATA = false;
    //假数据延时
    public static long DELAYED = 0;
    public static Dialog mDialog;//加载框
    public static IRequestParam iRequestParam;
    public static Result resultMode;
    public static ILoadingDialog iLoadingDialog;

    /**
     * 用于标识服务器内部规定的错误类型（区分）
     */
    public static final String THROWABLE_LABEL = "Request Inner Throwable";

    public Context context;

//	/** Https 证书验证对象 */
//	private static SSLContext s_sSLContext = null;

    RequestCallback mCallback;
    /**
     * 是否处理返回结果，处理指只对返回successcode的结果给于返回，其他都报错；不处理可以让不按定义好的返回结果格式也能返回。
     */
    boolean mResultDeal = true;
    // 是否使用缓存
    private boolean isCache;
    // 是否展示服务器msg
    private boolean isShowServerMsg = true;
    //自定义加载框信息  默认数据为类名称 做不到，系统自带弹出框不能修改
//    private String mLoadingMsg;

    public Request(Context context) {
        this(context,true);
    }

    public Request(Context context, boolean resultDeal) {
        this.context = context;
        this.mResultDeal = resultDeal;
//        setLoadingMsg(getName());
    }

    public abstract String getName();

    public abstract RequestParams params();

    public abstract <T> T parse(String json);

    /**
     * 设置是否使用缓存
     *
     * @param isCache
     * @return
     */
    public Request setCache(boolean isCache) {

        this.isCache = isCache;

        return this;

    }

    /**
     * 是否使用缓存
     *
     * @return
     */
    public boolean isCache() {

        return isCache;
    }

    /**
     * 设置是否显示服务器返回的信息
     *
     * @param isShowServerMsg
     * @return
     */
    public Request setShowServerMsg(boolean isShowServerMsg) {

        this.isShowServerMsg = isShowServerMsg;

        return this;
    }

    /**
     * 是否显示服务器返回的信息
     *
     * @return
     */
    public boolean isShowServerMsg() {

        return isShowServerMsg;
    }

    /**
     * 强制刷新
     */
    public void forceRefresh() {

        if (mCallback == null) {
            // throw new NullPointerException("Request mCallback is null");
            DLog.e("Request mCallback is null");

            return;
        }

        isCache = false;

        execute(mCallback);

    }

    /**
     * 设置加载框
     *
     * @param iLoadingDialog
     * @return
     */
    public Request setLoadingDialog(ILoadingDialog iLoadingDialog) {

        this.iLoadingDialog = iLoadingDialog;

        return this;

    }

//    public Request setLoadingMsg(String msg){
//        this.mLoadingMsg = msg;
//        return this;
//    }

    //默认加签名
//    static boolean needSign = true;

    /**
     * 是否加了签名
     * @return
     */
//    public boolean isNeedSign(){
//
//        return needSign;
//    }

    /**
     * 设置是否加签名
     *
     * @param needSign
     * @return
     */
//    public Request setNeedSign(boolean needSign){
//
//        this.needSign = needSign;
//
//        return this;
//
//    }


    /**
     * 执行网络请求方法
     * 加载框默认打开
     *
     * @param callback 回调函数
     * @return 返回该请求的句柄，可以用来控制其取消执行操作
     */
    public <T> Callback.Cancelable execute(RequestCallback callback) {

        return execute(true, callback);
    }

    /**
     * 执行网络请求方法
     *
     * @param showDialog 是否显示加载框
     * @param callback   回调函数
     * @return 返回该请求的句柄，可以用来控制其取消执行操作
     */
    public <T> Callback.Cancelable execute(boolean showDialog, final RequestCallback callback) {

        if (callback == null) {
            // throw new NullPointerException("Request mCallback is null");
            DLog.e("Request callback is null");
            throw new NullPointerException("Request callback is null");
        }

        if (showDialog)
            showLoadingDialog();

        requestCount++;
        mCallback = callback;

        if (OotbConfig.isDEBUG()) {
            for (int i = 0; params().getHeaders() != null && i < params().getHeaders().size(); i++) {
                DLog.d("Header >>> " + params().getHeaders().get(i).key + " : " + params().getHeaders().get(i).getValueStr());
            }
            for (KeyValue kv : params().getQueryStringParams()) {
                DLog.d("QueryStringParam >>> " + kv.key + " : " + kv.getValueStr());
            }
            for (KeyValue kv : params().getBodyParams()) {
                DLog.d("BodyParam >>> " + kv.key + " : " + kv.getValueStr());
            }

            DLog.d("BodyContent >>> " + params().getBodyContent());
        }

        RequestParams params = iRequestParam != null ? iRequestParam.disposeParam(params()) : params();


        Callback.Cancelable cancelable = null;

        if (!FALSEDATA) {
            //默认POST请求 可以通过自定义IRequestParam改写默认设置
            if (params.getMethod() == HttpMethod.GET){
                cancelable = OotbConfig.http().get(params,xUtilsCalback);
            }else if (params.getMethod() == HttpMethod.POST){
                cancelable = OotbConfig.http().post(params, xUtilsCalback);
            }else {
                cancelable = OotbConfig.http().reqeust(params.getMethod(),params,xUtilsCalback);
            }

        } else {
            DLog.d(getName() + ": 假数据测试,延时：" + DELAYED + "毫秒");

            OotbConfig.task().postDelayed(new Runnable() {
                @Override
                public void run() {

                    callback.onSuccess(parse("{}"));

                    onRequestFinished(callback);

                }
            }, DELAYED);


        }

        return cancelable;
    }

    Callback.CacheCallback xUtilsCalback = new Callback.CacheCallback<String>() {

        @Override
        public void onSuccess(String result) {

            if (result != null) {

                DLog.d(getName() + ": 网络请求任务成功");

                DLog.d(getName() + ": " + result);

                new ParseTask(result, mCallback,mResultDeal).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        }

        @Override
        public void onError(Throwable ex, boolean isOnCallback) {

            DLog.d(getName() + ": 网络请求任务失败");

            ex.printStackTrace();

            mCallback.onError(ex, isOnCallback);

            onRequestFinished(mCallback);

        }

        @Override
        public void onCancelled(Callback.CancelledException cex) {

            DLog.d(getName() + ": 网络请求任务被取消");

            mCallback.onCancelled(cex);

        }

        @Override
        public void onFinished() {
            //xUtils的这个方法在onSuccess之前调用，有些不妥
        }

        @Override
        public boolean onCache(String result) {

            if (isCache) {

                DLog.d(getName() + ": 使用缓存数据");
                new ParseTask(result, mCallback,mResultDeal).execute();
            }

            return isCache;
        }
    };

    private void onRequestFinished(RequestCallback callback){
        DLog.d(getName() + ": 网络请求任务完成");
        callback.onFinished();
        requestCount--;
        dismissLoadingDialog();
    }


    /**
     * 签名方法
     *
     * @param params
     * @return 得到一个MD5
     */
    private static String getSign(List<KeyValue> params) {
        TreeMap<String, String> map = new TreeMap<String, String>();
        StringBuffer str = new StringBuffer();
        String key;

        for (KeyValue kv : params) {
            map.put(kv.key, (String) kv.value);
        }
        Iterator it = map.keySet().iterator();
        while (it.hasNext()) {
            key = (String) it.next();
            str.append(key + "=" + map.get(key));
            str.append("&");
        }
        //str.append("secretKey=" + "");
        return MD5.md5(str.toString());
    }

//    public static class Result {
//
//        public String code;
//
//        public String msg;
//
//    }
//
//    Handler mHandler = new Handler(){
//
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//
//            Result result = (Result) msg.obj;
//
//            Toast.makeText(getContext(),result.msg,Toast.LENGTH_SHORT).show();
//
//        }
//    };

    /**
     * 解析JSON任务
     */
    class ParseTask<T> extends AsyncTask<Object, Void, Result> {

        String json;

        RequestCallback<T> callback;

        T t;

        boolean resultDeal;

        public ParseTask(String json, RequestCallback<T> callback, boolean resultDeal) {

            this.json = json;

            this.callback = callback;

            this.resultDeal = resultDeal;
        }

        @Override
        protected Result doInBackground(Object... params) {
            if (resultMode != null && resultDeal) {

                try {

                    Result mResult;

                    if (callback == null) {

                        throw new NullPointerException("RequestCallback is null");

                    } else {

                        mResult = JSON.parseObject(json, resultMode.getClass());

                        if (mResult != null)
                            if (!resultMode.onResultParse(mResult.getResultCode())) {

                                if (resultMode.successCode.equals(mResult.getResultCode())) {

                                    t = parse(json);

                                }
                            }
                    }

                    return mResult;

                } catch (Exception e) {

                    e.printStackTrace();

                    return null;
                }

            } else {

                t = parse(json);

                return null;
            }
        }

        @Override
        protected void onPostExecute(Result result) {

            super.onPostExecute(result);

            if (resultMode == null || !resultDeal) {

                callback.onSuccess(t);

            } else if (result == null) {

                callback.onError(new Throwable(THROWABLE_LABEL + ":解析结果为空"), false);

                if (isShowServerMsg)
                    Toast.makeText(getContext(), "解析结果为空", Toast.LENGTH_SHORT).show();


            } else {

                DLog.d(getName() + "  code:" + result.getResultCode() + "  msg:" + result.getResultMsg());

                if (resultMode.successCode.equals(result.getResultCode())) {

                    callback.onSuccess(t);

                    String msg = resultMode.resultCodeMap.get(result.getResultCode());

                    if (!TextUtils.isEmpty(msg)) {

                        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();

                    }

                } else {

                    callback.onError(new Throwable(THROWABLE_LABEL + ":" + result.getResultCode() + "-" + result.getResultMsg()), false);

                    String msg = resultMode.resultCodeMap.get(result.getResultCode());

                    if (!TextUtils.isEmpty(msg)) {

                        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();

                    } else if (isShowServerMsg)
                        Toast.makeText(getContext(), result.getResultMsg(), Toast.LENGTH_SHORT).show();

                }
            }

            onRequestFinished(callback);

        }

    }

    private Context getContext() {

        if (context == null)
            context = OotbConfig.app();

        return context;
    }

    private void showLoadingDialog() {

        if (iLoadingDialog != null && requestCount == 0 && getContext()!=null && getContext() instanceof Activity && !((Activity) getContext()).isFinishing()) {
            //mDialog = ProgressDialog.show(getContext(), "", "加载中...");
            mDialog = iLoadingDialog.showLoadingDialog((Activity) getContext());
        }

    }

    private void dismissLoadingDialog() {

        if (iLoadingDialog != null && requestCount == 0 && mDialog != null && mDialog.isShowing()) {
//            if(getContext() != null && getContext() instanceof Activity && !((Activity) getContext()).isFinishing()){
                mDialog.dismiss();
//            }else {
//                mDialog = null;
//            }
            iLoadingDialog.dismissLoadingDialog();
        }
    }

    /**
     * 取消加载框
     * 有时需要手动取消加载框  比如当用户在 RequestCallback 的各回调方法中，调用finish，就需要在finish之前手动取消加载框
     * 为了以免忘记以上类似情况的关闭activity，建议在 base activity 中统一处理
     */
    public static void dismissDialog(){
        requestCount = 0;
        if (mDialog!=null && mDialog.isShowing()) {
            mDialog.dismiss();
            mDialog = null;
            if (iLoadingDialog!=null)
                iLoadingDialog.dismissLoadingDialog();
        }
    }

    /**
     * 网络请求的回调函数
     * <p>
     * Created by dean on 2017/4/19.
     */
    public interface RequestCallback<T> {

        /**
         * 请求成功
         *
         * @param t 指定对象类型
         */
        void onSuccess(T t);

        /**
         * 请求失败
         *
         * @param ex           失败的异常信息
         * @param isOnCallback 是否调用了事前设置的失败时的回调，请忽略这个参数
         */
        void onError(Throwable ex, boolean isOnCallback);

        /**
         * 请求取消
         *
         * @param cex 取消的异常信息
         */
        void onCancelled(Callback.CancelledException cex);

        /**
         * 请求完成
         * 不管请求结果如何，onFinished 总是会被调用
         */
        void onFinished();

    }

    /**
     * 定义全局请求参数
     * <p>
     * Created by dean on 2017/5/22.
     */
    public interface IRequestParam {

        RequestParams disposeParam(RequestParams params);

    }

    /**
     * 自定义加载框
     * <p>
     * Created by dean on 2017/7/8.
     */
    public interface ILoadingDialog {

        /**
         * 需要显示加载框时被调用
         * 需要生成一个加载框，并返回给这个方法
         *
         * @param activity
         * @return
         */
        Dialog showLoadingDialog(Activity activity);

        /**
         * 加载框关闭时被调用
         */
        void dismissLoadingDialog();
    }

    /**
     * 返回结果参数的自定义类接口
     * <p>
     * <p>
     * Created by dean on 2017/6/15.
     */

    public static abstract class Result {

        protected String successCode;

        /**
         * key:code,value:msg
         * <p>
         * msg的显示级别高于服务器信息，当msg不为空时，优先展示msg信息并屏蔽服务器的msg信息
         */
        protected HashMap<String, String> resultCodeMap = new HashMap<>();

        public Result(String successCode) {

            this(successCode, "");
        }

        public Result(String successCode, String successMsg) {

            this.successCode = successCode;

            resultCodeMap.put(successCode, successMsg);
        }

        public Result(String successCode, HashMap<String, String> resultCodeMap) {

            this.successCode = successCode;

            resultCodeMap.put(successCode, resultCodeMap.get(successCode));

            this.resultCodeMap.putAll(resultCodeMap);
        }

        public Type getEntityType() {
            return this.getClass().getGenericSuperclass();
        }

        /**
         * 请求结果 自行分析处理
         *
         * @param code
         * @return 返回true，表示分析由用户自己完成，返回false，表示分析由用户和request一起完成
         */
        public boolean onResultParse(String code) {
            return false;
        }

        /**
         * 指定ResultCode的值
         * 数据请求的状态码
         *
         * @return
         */
        public abstract String getResultCode();

        /**
         * 指定ResultMsg的值
         * 数据请求的服务器信息
         *
         * @return
         */
        public abstract String getResultMsg();
//    public abstract T getResultData();


    }
}
