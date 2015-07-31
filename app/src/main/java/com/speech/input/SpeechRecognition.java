package com.speech.input;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.preference.PreferenceManager;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;

import java.io.IOException;

public class SpeechRecognition{

    public interface SpeechRecognitionServiceCallback{
        public void onReadyForSpeech();
        public void onBeginningOfSpeech();
        public void onEndOfSpeech();
        public void onResults(String result);
        public void onError(String error);
    }

    private static SpeechRecognitionServiceCallback mSpeechRecognitionServiceCallback;
    private Activity mActivity;
    private SpeechRecognitionListener mSpeechRecognitionListener;
    private SpeechRecognizer mSpeechRecognizer;
    private Intent mSpeechRecognizerIntent;
    private SharedPreferences preferences;
    private boolean is_listening = false;
    private boolean auto_restart_listening = true;

    public static SpeechRecognitionServiceCallback getSpeechRecognitionServiceCallback() {
        return SpeechRecognition.mSpeechRecognitionServiceCallback;
    }

    public static void setServiceListener(SpeechRecognitionServiceCallback serviceListener) {
        SpeechRecognition.mSpeechRecognitionServiceCallback = serviceListener;
    }

    public SpeechRecognition(Activity activity) {
        mActivity = activity;
        setUpSpeechRecognition();
    }

    public SpeechRecognizer getSpeechRecognizer() {
        return mSpeechRecognizer;
    }

    public void resetSpeechRecognition() {
        mSpeechRecognizer.destroy();
        setUpSpeechRecognition();
    }

    public void setRestartListening() {
        if(!is_listening) {
            mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
            is_listening = true;
        }
    }

    public void setUpSpeechRecognition() {
        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(mActivity.getApplicationContext());
        mSpeechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, mActivity.getPackageName());

        preferences = PreferenceManager.getDefaultSharedPreferences(mActivity.getApplicationContext());

        mSpeechRecognitionListener = new SpeechRecognitionListener(mSpeechRecognizer, mSpeechRecognizerIntent, new FragmentCallback() {
            @Override
            public void onReadyForSpeech() {
                if(getSpeechRecognitionServiceCallback()!=null) {
                    ((AudioManager) mActivity.getSystemService(Context.AUDIO_SERVICE)).setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
                    is_listening = true;
                    getSpeechRecognitionServiceCallback().onReadyForSpeech();
                }
            }

            @Override
            public void onBeginningOfSpeech() {
                if(getSpeechRecognitionServiceCallback()!=null) {
                    is_listening = true;
                    getSpeechRecognitionServiceCallback().onBeginningOfSpeech();
                }
            }

            public void onEndOfSpeech() {
                if(getSpeechRecognitionServiceCallback()!=null) {
                    mSpeechRecognizer.stopListening();
                    is_listening = false;
                    getSpeechRecognitionServiceCallback().onEndOfSpeech();
                }
            }

            @Override
            public void onResults(String result) {
                if(getSpeechRecognitionServiceCallback()!=null) {
                    getSpeechRecognitionServiceCallback().onResults(result);
                    if(preferences.getBoolean("auto_relistening", true) && ((MainActivity) mActivity).getFragmentDisplaying() != MainFragment.SECTION_NUMBER && !is_listening) {
                        mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
                        is_listening = true;
                    }
                }
            }

            public void onError(String error) {
                if(getSpeechRecognitionServiceCallback()!=null) {
                    getSpeechRecognitionServiceCallback().onError(error);
                    is_listening = false;
                    if(preferences.getBoolean("auto_relistening", true) && ((MainActivity) mActivity).getFragmentDisplaying() != MainFragment.SECTION_NUMBER && !is_listening) {
                        resetSpeechRecognition();
                    }
                }
            }
        });

        mSpeechRecognizer.setRecognitionListener(mSpeechRecognitionListener);
        mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
    }

}
