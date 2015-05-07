package com.speech.input;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;


public class CameraFragment extends Fragment {

    public final static int SECTION_NUMBER = 3;
    private CameraManager mCameraManager;
    private SurfaceView mSurfaceView;
    private View rootView;

    public static CameraFragment newInstance() {
        return new CameraFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_camera, container, false);
        setUpCamera();
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public void setUpCamera() {
        if(mCameraManager==null) {
            mSurfaceView = (SurfaceView) rootView.findViewById(R.id.camera_preview);
        }

        if(getArguments()!=null && getArguments().getBoolean("takePicture")) {
            mCameraManager = new CameraManager(mSurfaceView, ((MainActivity) getActivity()).getApplicationContext(), true);
        } else {
            mCameraManager = new CameraManager(mSurfaceView, ((MainActivity) getActivity()).getApplicationContext(), false);
        }
    }

    public CameraManager getCameraManager() {
        return mCameraManager;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(SECTION_NUMBER);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

}
