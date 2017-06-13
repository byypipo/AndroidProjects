package com.sebatmedikal.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Build;
import android.view.Display;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;

public class ImageUtil {
    private Bitmap rotateImageByte(Display display, byte[] data, int degree) {
        int x = display.getWidth();
        int y = display.getHeight();

        LogUtil.logMessage(getClass(), "Display X: " + x);
        LogUtil.logMessage(getClass(), "Display Y: " + y);

        Bitmap scaledBitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeByteArray(data, 0, data.length), y, x, true);
        return rotateImage(scaledBitmap, degree);
    }

    public Bitmap rotateImage(Bitmap bitmap, int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    public static byte[] converBitmapToByteArray(Bitmap bitmap, double resize) {
        if (NullUtil.isNull(bitmap)) {
            LogUtil.logMessage(ImageUtil.class, "Bitmap is null");
            return null;
        }

        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        if (CompareUtil.equal(resize, 0)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            return stream.toByteArray();
        }

        Bitmap resized = null;
        if (resize < 0) {
            resized = Bitmap.createScaledBitmap(bitmap, (int) (bitmap.getWidth() * resize), (int) (bitmap.getHeight() * resize), true);
        } else {
            resized = Bitmap.createScaledBitmap(bitmap, (int) resize, (int) resize, true);
        }

        resized.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        return stream.toByteArray();
    }
}
