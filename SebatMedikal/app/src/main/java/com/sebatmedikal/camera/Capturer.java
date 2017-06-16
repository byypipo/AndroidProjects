package com.sebatmedikal.camera;

import android.content.Context;
import android.hardware.Camera;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import com.sebatmedikal.R;
import com.sebatmedikal.util.LogUtil;
import com.sebatmedikal.util.NullUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by orhan on 16.06.2017.
 */
public class Capturer {
    Camera mCamera;
    private CameraPreview mPreview;
    public static final int MEDIA_TYPE_IMAGE = 1;

    private File pictureFile;
    private Button captureButton;
    private Object waitCaptureMutex = new Object();

    public Capturer(Context context, View inftaledView) {
        mCamera = getCameraInstance();

        // Create our Preview view and set it as the content of our activity.
        mPreview = new CameraPreview(context, mCamera);
        FrameLayout preview = (FrameLayout) inftaledView.findViewById(R.id.layout_camera_preview);
        preview.addView(mPreview);

        captureButton = (Button) inftaledView.findViewById(R.id.layout_camera_capture);
    }

    public String waitCaptureAndGetPath() {
        captureButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // get an image from the camera
                        mCamera.takePicture(null, null, new Camera.PictureCallback() {
                            @Override
                            public void onPictureTaken(byte[] bytes, Camera camera) {
                                pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
                                try {
                                    FileOutputStream fos = new FileOutputStream(pictureFile);
                                    fos.write(bytes);
                                    fos.close();
                                } catch (FileNotFoundException e) {
                                    LogUtil.logMessage(getClass(), "File not found: " + e.getMessage());
                                } catch (IOException e) {
                                    LogUtil.logMessage(getClass(), "Error accessing file: " + e.getMessage());
                                }

                                synchronized (waitCaptureMutex) {
                                    waitCaptureMutex.notifyAll();
                                }
                            }
                        });
                    }
                }
        );

        try {
            synchronized (waitCaptureMutex) {
                waitCaptureMutex.wait();
            }
        } catch (Exception ex) {
        }

        if (NullUtil.isNull(pictureFile)) {
            return null;
        }

        return pictureFile.getAbsolutePath();
    }

    private static File getOutputMediaFile(int type) {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "SebatMedikal");

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_" + timeStamp + ".jpg");
        } else {
            return null;
        }

        return mediaFile;
    }

    public static Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        } catch (Exception e) {
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }
}
