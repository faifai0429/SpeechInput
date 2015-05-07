package com.speech.input;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Binder;
import android.os.IBinder;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;

public class SpeechRecognitionService extends Service {

    public interface SpeechRecognitionServiceCallback{
        public void onReadyForSpeech();
        public void onBeginningOfSpeech();
        public void onEndOfSpeech();
        public void onResults(String result);
        public void onError(String error);
    }

    private Binder binder;
    private static SpeechRecognitionServiceCallback mSpeechRecognitionServiceCallback;
    private SpeechRecognizer mSpeechRecognizer;
    private Intent mSpeechRecognizerIntent;

    public static SpeechRecognitionServiceCallback getSpeechRecognitionServiceCallback() {
        return SpeechRecognitionService.mSpeechRecognitionServiceCallback;
    }

    public static void setServiceListener(SpeechRecognitionServiceCallback serviceListener) {
        SpeechRecognitionService.mSpeechRecognitionServiceCallback = serviceListener;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        binder = new Binder();
        setUpSpeechRecognition();
    }

    public void setUpSpeechRecognition() {
        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(getApplicationContext());
        mSpeechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getPackageName());

        SpeechRecognitionListener speechRecognitionListener = new SpeechRecognitionListener(mSpeechRecognizer, mSpeechRecognizerIntent, new FragmentCallback() {
            @Override
            public void onReadyForSpeech() {
                if(getSpeechRecognitionServiceCallback()!=null) {
                    ((AudioManager) getSystemService(Context.AUDIO_SERVICE)).setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
                    getSpeechRecognitionServiceCallback().onReadyForSpeech();
                }
            }

            @Override
            public void onBeginningOfSpeech() {
                if(getSpeechRecognitionServiceCallback()!=null) {
                    getSpeechRecognitionServiceCallback().onBeginningOfSpeech();
                }
            }

            public void onEndOfSpeech() {
                if(getSpeechRecognitionServiceCallback()!=null) {
                    mSpeechRecognizer.stopListening();
                    getSpeechRecognitionServiceCallback().onEndOfSpeech();
                }
            }

            @Override
            public void onResults(String result) {
                if(getSpeechRecognitionServiceCallback()!=null) {
                    getSpeechRecognitionServiceCallback().onResults(result);
                    mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
                }
            }

            public void onError(String error) {
                if(getSpeechRecognitionServiceCallback()!=null) {
                    getSpeechRecognitionServiceCallback().onError(error);
                    mSpeechRecognizer.stopListening();
                    mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
                }
            }
        });

        mSpeechRecognizer.setRecognitionListener(speechRecognitionListener);
        mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }
}
