package com.android.pay.net;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static android.os.Environment.DIRECTORY_DOCUMENTS;

/**
 * Created by Relin
 * on 2017/4/7.
 * 文件管理
 */

public class IOUtils {

    //日志标记
    private static final String TAG = IOUtils.class.getSimpleName();
    //Bitmap转成的文件的文件夹
    private static String BITMAP_TO_FILE_FOLDER = "BitmapCache";
    //压缩Bitmap路径
    public static String COMPRESS_BITMAP_FOLDER = "Compressor";
    //压缩Bitmap路径
    public static String WRITE_FOLDER = "Downloader";
    //文件工具
    public static final String COMPRESS_BITMAP_PATH = getSDCardPath() + File.separator + COMPRESS_BITMAP_FOLDER;
    //要压缩的宽度
    public final static int COMPRESS_WIDTH = 480;
    //要压缩的高度
    public final static int COMPRESS_HEIGHT = 800;
    //压缩质量
    public static int COMPRESS_QUALITY = 80;

    /**
     * 判断内存卡是否存在
     *
     * @return
     */
    public static boolean isExistSDCard() {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return false;
        }
        return true;
    }

    /**
     * 获取内存卡的路径
     *
     * @return
     */
    public static String getSDCardPath() {
        File file = Environment.getExternalStoragePublicDirectory(DIRECTORY_DOCUMENTS);
        if (!file.exists()) {
            return Environment.getExternalStorageDirectory().getAbsolutePath();
        }
        return file.getParent();
    }

    /**
     * 创建新文件夹
     *
     * @return
     */
    public static String createFolder(String folderName) {
        File folder = new File(getSDCardPath() + File.separator + folderName);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        return folder.getAbsolutePath();
    }



    /**
     * 压缩图片成文件
     *
     * @param file           压缩文件
     * @param format         压缩格式
     * @param compressedSize 压缩大小
     * @return
     */
    public static File compress(File file, Bitmap.CompressFormat format, int compressedSize) {
        long time = System.currentTimeMillis();
        createFolder(COMPRESS_BITMAP_FOLDER);
        File targetFile = new File(COMPRESS_BITMAP_PATH + File.separator + file.getName());
        if (targetFile.exists()) {
            Log.i(TAG, "compress bitmap is compressed!");
            return targetFile;
        }
        Log.i(TAG, "compress bitmap before file length :" + (file.length() / 1024) + "kb");
        File resultFile = compress(file.getAbsolutePath(), targetFile.getAbsolutePath(), COMPRESS_QUALITY, format, COMPRESS_WIDTH, COMPRESS_HEIGHT);
        while ((resultFile.length() / 1024) > compressedSize) {//压缩到100kb以下
            COMPRESS_QUALITY -= 5;
            resultFile = compress(resultFile.getAbsolutePath(), targetFile.getAbsolutePath(), COMPRESS_QUALITY, format, COMPRESS_WIDTH, COMPRESS_HEIGHT);
        }
        Log.i(TAG, "compress bitmap use time: " + (System.currentTimeMillis() - time) + "ms (" + ((System.currentTimeMillis() - time) / 1000) + "s) , compress after file length " + (resultFile.length() / 1024) + "kb");
        return resultFile;
    }

    /**
     * 压缩图片
     *
     * @param sourcePath     源文件路径
     * @param targetPath     目标路径
     * @param quality        压缩质量
     * @param compressWidth  压缩宽度
     * @param compressHeight 压缩高度
     * @return
     */
    public static File compress(String sourcePath, String targetPath, int quality, Bitmap.CompressFormat format, int compressWidth, int compressHeight) {
        Bitmap bm = inSampleSize(sourcePath, compressWidth, compressHeight);//获取一定尺寸的图片
        int degree = calculateExifRotateAngle(sourcePath);//获取相片拍摄角度
        if (degree != 0) {//旋转照片角度，防止头像横着显示
            bm = rotateBitmap(bm, degree);
        }
        File outputFile = new File(targetPath);
        try {
            if (!outputFile.exists()) {
                outputFile.getParentFile().mkdirs();
            } else {
                outputFile.delete();
            }
            FileOutputStream out = new FileOutputStream(outputFile);
            bm.compress(format, quality, out);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return outputFile;
    }

    /**
     * 根据路径获得图片信息并按比例压缩，返回bitmap
     *
     * @param filePath
     * @return
     */
    public static Bitmap inSampleSize(String filePath, int compressWidth, int compressHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;//只解析图片边沿，获取宽高 960
        BitmapFactory.decodeFile(filePath, options);
        // 计算缩放比
        options.inSampleSize = calculateInSampleSize(options, compressWidth, compressHeight);
        // 完整解析图片返回bitmap
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filePath, options);
    }

    public static Bitmap inSampleSize(String filePath) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;//只解析图片边沿，获取宽高 960
        BitmapFactory.decodeFile(filePath, options);
        // 计算缩放比
        options.inSampleSize = calculateInSampleSize(options, COMPRESS_WIDTH, COMPRESS_HEIGHT);
        // 完整解析图片返回bitmap
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filePath, options);
    }

    /**
     * 计算图片旋转角度
     *
     * @param path 图片路径
     * @return
     */
    public static int calculateExifRotateAngle(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    /**
     * 旋转照片
     *
     * @param bitmap
     * @param degree
     * @return
     */
    public static Bitmap rotateBitmap(Bitmap bitmap, int degree) {
        if (bitmap != null) {
            Matrix m = new Matrix();
            m.postRotate(degree);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
            return bitmap;
        }
        return bitmap;
    }

    /**
     * 计算图片的缩放值
     *
     * @param options
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }

}
