package com.speech.input;

import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;

/**
 * Created by FaiFai on 4/5/2015.
 */
public class CameraManager implements SurfaceHolder.Callback, Camera.ShutterCallback, Camera.PictureCallback{

    private boolean m_is_previewing = false;
    private Context mContext;
    private SurfaceView mSurfaceView;
    private Camera mCamera;
    private SurfaceHolder mSurfaceHolder;
    private boolean takePicture;

    public CameraManager(SurfaceView surfaceView, Context context, boolean takePicture) {
        mContext = context;
        mSurfaceView = surfaceView;
        mSurfaceHolder = surfaceView.getHolder();
        mSurfaceHolder.addCallback(this);
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        this.takePicture = takePicture;
    }

    public boolean isPreviewing() {
        return m_is_previewing;
    }

    public void setPreviewing(boolean isEnabled) {
        m_is_previewing = isEnabled;
    }

    public void init() {
        if (!isPreviewing() && mCamera==null) {
            mCamera = Camera.open();
            if (mCamera != null) {
                try {
                    mCamera.setPreviewDisplay(mSurfaceHolder);
                    mCamera.setDisplayOrientation(90);
                    Camera.Parameters params = mCamera.getParameters();
                    params.setRotation(90);
                    params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
                    params.setPictureSize(params.getSupportedPictureSizes().get(0).width, params.getSupportedPictureSizes().get(0).height);
                    params.setPictureFormat(ImageFormat.JPEG);
                    params.setJpegQuality(100);
                    params.setVideoStabilization(true);
                    mCamera.setParameters(params);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    public void startPreview() {
        if(mCamera != null && !isPreviewing()) {
            setPreviewing(true);
            mCamera.startPreview();
        }
    }

    public void stopPreview() {
        if (mCamera != null && isPreviewing()) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
            setPreviewing(false);
        }
    }

    @Override
    public void onShutter() {
        // Toast.makeText(mContext, "onShutter!", Toast.LENGTH_SHORT).show();
    }

    public void takePicture(Camera.PictureCallback raw, Camera.PictureCallback postview) {
        mCamera.takePicture(this, raw, postview, this);
    }

    @Override
    public void onPictureTaken(byte[] data, Camera camera) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HHmmss");
            String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/DCIM/Camera";
            String fileName = "picture_" + sdf.format(new Date()) + ".jpg";
            FileOutputStream out = new FileOutputStream(new File(path, fileName));
            out.write(data);
            out.flush();
            out.close();

            Toast.makeText(mContext, "File saved on " + path + " named " + fileName, Toast.LENGTH_SHORT).show();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mCamera.startPreview();
    }

    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        Log.e("CameraRecord", "surfaceCreated");
        init();
        startPreview();
        if(takePicture) {
            takePicture(null, null);
        }
    }

    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        Log.e("CameraRecord", "surfaceDestroyed");
        stopPreview();
    }

    public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int width, int height) {
        Log.e("CameraRecord", "surfaceChanged");
        startPreview();
    }

}
