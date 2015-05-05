package com.speech.input;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by FaiFai on 4/5/2015.
 */
public class SpeechRecognitionListener implements RecognitionListener
{
    private SpeechRecognizer mSpeechRecognizer;
    private Intent mSpeechRecognizerIntent;
    private FragmentCallback mFragmentCallback;

    public SpeechRecognitionListener(SpeechRecognizer speechRecognizer, Intent speechRecognizerIntent, FragmentCallback fragmentCallback) {
        mSpeechRecognizer = speechRecognizer;
        mSpeechRecognizerIntent = speechRecognizerIntent;
        mFragmentCallback = fragmentCallback;
    }

    @Override
    public void onBeginningOfSpeech() {
        mFragmentCallback.onBeginningOfSpeech();
        Log.d("SpeechRecognizerListener", "onBeginingOfSpeech");
    }

    @Override
    public void onBufferReceived(byte[] buffer) {

    }

    @Override
    public void onEndOfSpeech() {
        mFragmentCallback.onEndOfSpeech();
        Log.d("SpeechRecognizerListener", "onEndOfSpeech");
    }

    @Override
    public void onError(int error) {
        String errorMessage = "";
        switch(error) {
            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                errorMessage = "Network operation timed out.";
                break;
            case SpeechRecognizer.ERROR_NETWORK:
                errorMessage = "Network error.";
                break;
            case SpeechRecognizer.ERROR_AUDIO:
                errorMessage = "Audio recording error.";
                break;
            case SpeechRecognizer.ERROR_SERVER:
                errorMessage = "Server error.";
                break;
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                errorMessage = "No speech detected.";
                break;
            case SpeechRecognizer.ERROR_NO_MATCH:
                errorMessage = "No recognition result matched.";
                break;
            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                errorMessage = "RecognitionService busy.";
                break;
            default:
                errorMessage = "Something went wrong.";
                break;
        }

        errorMessage = errorMessage + " Please try again.";
        mFragmentCallback.onError(errorMessage);
        Log.d("SpeechRecognizerListener - onError", "error = " + error);
    }

    @Override
    public void onEvent(int eventType, Bundle params) {
        Log.d("SpeechRecognizerListener", "onEvent");
    }

    @Override
    public void onPartialResults(Bundle partialResults) {
        Log.d("SpeechRecognizerListener", "onPartialResults");
    }

    @Override
    public void onReadyForSpeech(Bundle params) {
        Log.d("SpeechRecognizerListener", "onReadyForSpeech");
    }

    @Override
    public void onResults(Bundle results) {
       Log.d("onResults", "onResults");
       mFragmentCallback.onResults(results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION).get(0));
        // matches are the return values of speech recognition engine
        // Use these values for whatever you wish to do
    }

    @Override
    public void onRmsChanged(float rmsdB) {

    }
}
