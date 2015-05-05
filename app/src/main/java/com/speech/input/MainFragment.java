package com.speech.input;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.maps.SupportMapFragment;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainFragment extends Fragment{

    public static final int SECTION_NUMBER = 0;
    private TextView mTxtSpeechInput;
    private ImageButton mBtnSpeak;
    private SpeechRecognizer mSpeechRecognizer;
    private Intent mSpeechRecognizerIntent;
    private boolean is_listening = false;
    private View rootView;

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    public MainFragment() {
    }

    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_main, container, false);
        setUpSpeechRecognition();
        return rootView;
    }

    public void setUpSpeechRecognition() {
        mTxtSpeechInput = (TextView) rootView.findViewById(R.id.txtSpeechInput);
        mBtnSpeak = (ImageButton) rootView.findViewById(R.id.btnSpeak);

        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(getActivity().getApplicationContext());
        mSpeechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getActivity().getPackageName());

        SpeechRecognitionListener speechRecognitionListener = new SpeechRecognitionListener(mSpeechRecognizer, mSpeechRecognizerIntent, new FragmentCallback() {

            @Override
            public void onBeginningOfSpeech() {
                mTxtSpeechInput.setText("Listening...");
            }

            public void onEndOfSpeech() {
                is_listening = false;
                mSpeechRecognizer.stopListening();
                mTxtSpeechInput.setText("Processing...");
            }

            @Override
            public void onResults(String result) {
                is_listening = false;
                processResult(result);
            }

            public void onError(String error) {
                mTxtSpeechInput.setText("No recognition result matched.");
                is_listening = false;
            }
        });

        mSpeechRecognizer.setRecognitionListener(speechRecognitionListener);

        mBtnSpeak.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if(!is_listening) {
                            mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
                            is_listening = true;
                        }
                        mTxtSpeechInput.setText("Talk to me");
                        return true;
                    default:
                        break;
                }

                return false;
            }
        });
    }

    public void processResult(String result) {
        String result_l = result.toLowerCase();

        if(result_l.matches(".*?(open|launch).*?camera.*")) {
            ((MainActivity) getActivity()).getNavigationDrawerFragment().selectItem(CameraFragment.SECTION_NUMBER);
        } else if(result_l.matches(".*?(take|make).*?(picture|photo).*")) {
            Bundle bundle = new Bundle();
            bundle.putBoolean("takePicture", true);
            ((MainActivity) getActivity()).getNavigationDrawerFragment().selectItem(CameraFragment.SECTION_NUMBER, bundle);
        } else if(result_l.matches(".*?(open|launch).*?(map|maps).*")) {
            mTxtSpeechInput.setText("");
            ((MainActivity) getActivity()).getNavigationDrawerFragment().selectItem(MapsFragment.SECTION_NUMBER);
        } else {
            Matcher matcher = Pattern.compile(".*?search(.*)").matcher(result_l);
            if(matcher.find()) {
                Bundle bundle = new Bundle();
                bundle.putString("query", matcher.group(1));
                ((MainActivity) getActivity()).getNavigationDrawerFragment().selectItem(WebViewFragment.SECTION_NUMBER, bundle);
            } else {
                mTxtSpeechInput.setText(result);
            }
        }
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
