package com.speech.input;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Handler;
import java.util.Timer;
import java.util.TimerTask;


public class WebViewFragment extends Fragment {

    public final static int SECTION_NUMBER = 2;
    private WebView mWebview;
    private EditText mAddressBar;
    private ProgressBar mProgressBar;

    public static WebViewFragment newInstance() {
        return new WebViewFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_web_view, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setUpWebview();
    }

    public WebView getWebview() {
        return mWebview;
    }

    public void setUpWebview() {
        final Activity activity = getActivity();
        mWebview = (WebView) activity.findViewById(R.id.webview);
        mWebview.getSettings().setJavaScriptEnabled(true);
        mAddressBar = (EditText) activity.findViewById(R.id.addressBar);
        mProgressBar = (ProgressBar) activity.findViewById(R.id.progressBar);

        mWebview.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                // Activities and WebViews measure progress with different scales.
                // The progress meter will automatically disappear when we reach 100%
                mProgressBar.setProgress(progress * 1000);
            }
        });

        mWebview.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(activity, "Oh no! " + description, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                mAddressBar.setText(url);
                mProgressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                mProgressBar.setVisibility(View.GONE);
            }
        });

        mAddressBar.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if(!hasFocus) {
                    mAddressBar.setSelection(0);
                } else {
                    (new Handler()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mAddressBar.setSelection(mAddressBar.length());
                        }
                    }, 0);
                }
            }
        });

        mAddressBar.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if (event != null && event.getAction() != KeyEvent.ACTION_DOWN) {
                    return false;
                } else if (actionId == EditorInfo.IME_ACTION_SEARCH || event == null || event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    String input = mAddressBar.getText().toString();
                    if(android.util.Patterns.WEB_URL.matcher(input).matches()) {
                        loadUrl(input);
                    } else {
                        loadUrl("http://www.google.com/#q=" + input);
                    }
                    return true; // consume.
                }

                return false; // pass on to other listeners.
            }
        });

        String query = null;
        if(getArguments()!=null) {
            query = getArguments().getString("query");
        }

        if(query==null || query.equals("")) {
           loadUrl("http://www.google.com");
        } else {
           loadUrl("http://www.google.com/#q=" + query);
        }
    }

    public void loadUrl(String url) {
        mWebview.loadUrl(url);
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