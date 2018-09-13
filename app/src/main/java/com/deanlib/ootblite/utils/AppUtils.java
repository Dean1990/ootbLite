package com.deanlib.ootblite.utils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Parcelable;

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

    private long exitTime;

    private boolean exit(){
        return exit(OotbConfig.app().getString(R.string.ootb_again_exit));
    }

    private boolean exit(String msg){
        return exit(msg,2000);
    }

    /**
     * 按两次退出
     * @param msg
     * @param interval
     * @return
     */
    private boolean exit(String msg, long interval){
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

}
