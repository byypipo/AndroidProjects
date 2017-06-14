package com.sebatmedikal.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.view.Display;

import java.io.ByteArrayOutputStream;

public class ImageUtil {
    private Bitmap rotateImageByte(Display display, byte[] data, int degree) {
        int x = display.getWidth();
        int y = display.getHeight();

        LogUtil.logMessage(getClass(), "Display X: " + x);
        LogUtil.logMessage(getClass(), "Display Y: " + y);

        Bitmap scaledBitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeByteArray(data, 0, data.length), y, x, true);
        return rotateImage(scaledBitmap, degree);
    }

    private static Bitmap rotateImage(Bitmap bitmap, int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    public static byte[] converBitmapToByteArray(Bitmap bitmap, float resize) {
        if (NullUtil.isNull(bitmap)) {
            LogUtil.logMessage(ImageUtil.class, "Bitmap is null");
            return null;
        }

        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        if (CompareUtil.equal(resize, 0)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            return stream.toByteArray();
        }

        Bitmap resized;
        if (resize < 1) {
            resized = Bitmap.createScaledBitmap(bitmap, (int) (bitmap.getWidth() * resize), (int) (bitmap.getHeight() * resize), true);
        } else {
            resized = Bitmap.createScaledBitmap(bitmap, (int) resize, (int) resize, true);
        }

        resized.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        return stream.toByteArray();
    }

    public static Bitmap prepareBitmapOrientation(String picturePath) {
        Bitmap bitmap = BitmapFactory.decodeFile(picturePath);

        try {
            ExifInterface ei = new ExifInterface(picturePath);
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED);

            switch (orientation) {

                case ExifInterface.ORIENTATION_ROTATE_90:
                    bitmap = ImageUtil.rotateImage(bitmap, 90);
                    LogUtil.logMessage(ImageUtil.class, "ORIENTATION_ROTATE_90");
                    break;

                case ExifInterface.ORIENTATION_ROTATE_180:
                    bitmap = ImageUtil.rotateImage(bitmap, 180);
                    LogUtil.logMessage(ImageUtil.class, "ORIENTATION_ROTATE_180");
                    break;

                case ExifInterface.ORIENTATION_ROTATE_270:
                    bitmap = ImageUtil.rotateImage(bitmap, 270);
                    LogUtil.logMessage(ImageUtil.class, "ORIENTATION_ROTATE_270");
                    break;

                case ExifInterface.ORIENTATION_NORMAL:
                    LogUtil.logMessage(ImageUtil.class, "ORIENTATION_NORMAL");

                default:
                    break;
            }
        } catch (Exception ignored) {

        }

        return bitmap;
    }
}
