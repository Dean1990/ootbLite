package com.deanlib.ootblite.utils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Parcelable;
import android.provider.Settings;

import com.deanlib.ootblite.OotbConfig;
import com.deanlib.ootblite.R;

/**
 * 应用操作相关
 * 打开，分享等
 *
 * @author dean
 * @time 2018/6/28 下午3:29
 */
public class AppUtils {

    public static final int REQ_CODE_UNKNOWN_APP_SOURCES = 5100;

    /**
     * 打电话
     * 权限 android.permission.CALL_PHONE
     * @param activity
     * @param phoneNum
     */
    public static void callPhone(Activity activity,String phoneNum){
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:"+phoneNum));
        activity.startActivity(intent);
    }

    /**
     * 打开第三方浏览器
     * @param activity
     * @param url
     */
    public static void openThirdBrowser(Activity activity, String url){
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        activity.startActivity(intent);
    }

    private static long exitTime;

    public static boolean exit(){
        return exit(OotbConfig.app().getString(R.string.ootb_again_exit));
    }

    public static boolean exit(String msg){
        return exit(msg,2000);
    }

    /**
     * 按两次退出
     * @param msg
     * @param interval
     * @return
     */
    public static boolean exit(String msg, long interval){
        if(System.currentTimeMillis()-exitTime>interval) {
            PopupUtils.sendToast(msg);
            exitTime= System.currentTimeMillis();
            return false;
        }

        return true;
    }

    /**
     * 创建快捷方式
     * 需要声明权限 com.android.launcher.permission.INSTALL_SHORTCUT
     * @param act   上下文
     * @param iconResId     图标
     * @param appnameResId      名称
     */
    public static void createShortCut(Activity act, int iconResId,
                                      int appnameResId) {
        createShortCut(act,iconResId,act.getString(appnameResId));

    }

    /**
     * 创建快捷方式
     * 需要声明权限 com.android.launcher.permission.INSTALL_SHORTCUT
     * @param act   上下文
     * @param iconResId     图标
     * @param appname       名称
     */
    public static void createShortCut(Activity act, int iconResId,
                                      String appname) {

        // com.android.launcher.permission.INSTALL_SHORTCUT

        Intent shortcutintent = new Intent(
                "com.android.launcher.action.INSTALL_SHORTCUT");
        // 不允许重复创建
        shortcutintent.putExtra("duplicate", false);
        // 需要现实的名称
        shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_NAME,
                appname);
        // 快捷图片
        Parcelable icon = Intent.ShortcutIconResource.fromContext(
                act.getApplicationContext(), iconResId);
        shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon);
        // 点击快捷图片，运行的程序主入口
        shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_INTENT,
                new Intent(act.getApplicationContext(), act.getClass()));
        // 发送广播
        act.sendBroadcast(shortcutintent);
    }

    /**
     * 安装APK
     * @param activity
     * @param uri
     * android 7.0 以下 可以使用 Uri.fromFile(file) 得到uri
     * android 7.0及以上，需要配置 provider (自行百度) ，使用 FileProvider.getUriForFile(context, "provider的authorities", file)
     */
    public static void installApk(Activity activity,Uri uri){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // 7.0+以上版本
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        intent.setDataAndType(uri, "application/vnd.android.package-archive");

        activity.startActivity(intent);
    }

    /**
     * 打开设置中的未知来源安装（android 8.0及以上）
     *
     * getPackageManager().canRequestPackageInstalls() 判断该设置是否打开
     * 如果设置被打开，返回时则会触发 onActivityResult ，否则不触发
     *
     */
    public static void openSettingsUnknownAppSources(Activity activity){
        Uri packageURI = Uri.parse("package:"+OotbConfig.app().getPackageName());//设置包名，可直接跳转当前软件的设置页面
        Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, packageURI);
        activity.startActivityForResult(intent, REQ_CODE_UNKNOWN_APP_SOURCES);
    }

    /**
     * 分享图片
     */
    public static void shareImage(Activity activity, Uri uri,String dialogTitle){
        if (uri!=null) {
            Intent share_intent = new Intent();
            share_intent.setAction(Intent.ACTION_SEND);//设置分享行为
            share_intent.setType("image/*");  //设置分享内容的类型
            share_intent.putExtra(Intent.EXTRA_STREAM,uri);
            //创建分享的Dialog
            share_intent = Intent.createChooser(share_intent, dialogTitle);
            activity.startActivity(share_intent);
        }
    }

}
