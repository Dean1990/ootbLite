package com.deanlib.ootblite.data;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;

import com.deanlib.ootblite.OotbConfig;
import com.deanlib.ootblite.utils.DLog;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by dean on 2017/4/24.
 */

public class ImageUtils {

    /**
     * 保存Bitmap
     *
     * @param bitmap
     * @param dir
     * @param name
     * @param cb
     */
    public static void saveImageFile(final Bitmap bitmap, final File dir, final String name, final FileUtils.FileCallback cb) {
        new AsyncTask<File, Void, File>() {

            @Override
            protected File doInBackground(File... params) {
                FileOutputStream fos = null;
                try {

                    File file = new File(dir, name);
                    fos = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 90, fos);


                    return file;
                } catch (Exception e) {
                    if (cb!=null)
                        cb.onFail(e);
                    e.printStackTrace();

                } finally {
                    try {
                        if (fos != null) {
                            fos.flush();
                            fos.close();
                            fos = null;
                        }
                    } catch (IOException e) {
                        if (cb!=null)
                            cb.onFail(e);
                        e.printStackTrace();
                    }
                }
                return null;
            }

            protected void onPostExecute(File file) {
                if (file == null) {
                    DLog.e("save image failed");
                } else {
                    DLog.i("image file：" + file.getAbsolutePath());
                    if (cb!=null)
                        cb.onSuccess(file);
                }
            }

            ;

        }.execute();
    }

    /**
     * 得到以uri存储在设备上的图片的真实文件路径
     *
     * @param activity
     * @param uri
     * @return
     */
    public static String getImageUriToFile(Activity activity, Uri uri) {
        String[] proj = {MediaStore.Images.Media.DATA};

        Cursor c = activity.getContentResolver().query(uri, proj, null, null, null);

        int index = c.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

        c.moveToFirst();

        String img_path = c.getString(index);

        return img_path;

    }


    /**
     * 压缩处理图片
     *
     * @param fileUri
     * @return
     */
    public static File scal(Activity activity, Uri fileUri) {
        String path = getImageUriToFile(activity,fileUri);
        File outputFile = new File(path);
        return scal(outputFile);
    }

    /**
     * 压缩处理图片
     *
     * @param outputFile
     * @return
     */
    public static File scal(File outputFile) {


        long fileSize = outputFile.length();
        final long fileMaxSize = 200 * 1024;
        if (fileSize >= fileMaxSize) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(outputFile.getPath(), options);
            int height = options.outHeight;
            int width = options.outWidth;

            double scale = Math.sqrt((float) fileSize / fileMaxSize);
            options.outHeight = (int) (height / scale);
            options.outWidth = (int) (width / scale);
            options.inSampleSize = (int) (scale + 0.5);
            options.inJustDecodeBounds = false;

            Bitmap bitmap = BitmapFactory.decodeFile(outputFile.getPath(), options);

            Uri uri = Uri.fromFile(FileUtils.createTempFile("", ".jpg"));
            if (uri == null) {
                return null;
            }
            outputFile = new File(uri.getPath());
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(outputFile);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, fos);
                fos.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            //Log.d(, sss ok + outputFile.length());
            if (!bitmap.isRecycled()) {
                bitmap.recycle();
            } else {
                File tempFile = outputFile;
                outputFile = new File(uri.getPath());
                FileUtils.copyFileUsingFileChannels(tempFile, outputFile);
            }

        }
        return outputFile;

    }

    /**
     * 以最省内存的方式读取本地资源的图片
     *
     * @param resId
     * @return
     */
    public static Bitmap readBitMap(int resId) {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.RGB_565;
        opt.inPurgeable = true;
        opt.inInputShareable = true;
        // 获取资源图片
        InputStream is = OotbConfig.app().getResources().openRawResource(resId);
        return BitmapFactory.decodeStream(is, null, opt);
    }

    /**
     * 获得图片的URL地址
     * Uri.fromFile 的替换方法
     * SDK >= 24 使用Uri.fromFile 会报错
     * @param imageFile
     * @return
     */
    public static Uri getImageContentUri(File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = OotbConfig.app().getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[] { MediaStore.Images.Media._ID },
                MediaStore.Images.Media.DATA + "=? ",
                new String[] { filePath }, null);

        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor
                    .getColumnIndex(MediaStore.MediaColumns._ID));
            Uri baseUri = Uri.parse("content://media/external/images/media");
            return Uri.withAppendedPath(baseUri, "" + id);
        } else {
            if (imageFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                return OotbConfig.app().getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }

    /*
 * 剪切图片
 */
    private void crop(Activity activity, Uri uri, int requestCode) {
        // 裁剪图片意图
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        // 裁剪框的比例，1：1
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // 裁剪后输出图片的尺寸大小
        intent.putExtra("outputX", 144);
        intent.putExtra("outputY", 144);

        intent.putExtra("outputFormat", "JPEG");// 图片格式
        intent.putExtra("noFaceDetection", true);// 取消人脸识别
        intent.putExtra("return-data", true);

        // 开启一个带有返回值的Activity，请求码为REQUEST_CODE_CROP
        activity.startActivityForResult(intent, requestCode);
    }
}
