package com.speech.input;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

public class MainFragment extends Fragment{

    public static final int SECTION_NUMBER = 0;
    private TextView mTxtSpeechInput;
    private ImageButton mBtnSpeak;
    private Activity mActivity;
    private MainActivity mMainActivity;
    private View mRootView;

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_main, container, false);

        mActivity = getActivity();
        mMainActivity = (MainActivity) mActivity;
        mTxtSpeechInput = (TextView) mRootView.findViewById(R.id.txtSpeechInput);
        mBtnSpeak = (ImageButton) mRootView.findViewById(R.id.btnSpeak);

        mBtnSpeak.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                mMainActivity.setUpSpeechRecognition();
                return true;
            }

            return false;
            }
        });

        return mRootView;
    }

    public void setOutputText(String text) {
        mTxtSpeechInput.setText(text);
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
