package com.speech.input;

/**
 * Created by FaiFai on 4/5/2015.
 */
public interface FragmentCallback {
    public void onReadyForSpeech();
    public void onBeginningOfSpeech();
    public void onEndOfSpeech();
    public void onError(String error);
    public void onResults(String result);
}