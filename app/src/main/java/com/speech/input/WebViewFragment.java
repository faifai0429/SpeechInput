package com.speech.input;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;


public class WebViewFragment extends Fragment {

    public final static int SECTION_NUMBER = 2;

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

    public void setUpWebview() {
        WebView webview = (WebView) getActivity().findViewById(R.id.webview);
        webview.getSettings().setJavaScriptEnabled(true);

        final Activity activity = getActivity();
        webview.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                // Activities and WebViews measure progress with different scales.
                // The progress meter will automatically disappear when we reach 100%
                activity.setProgress(progress * 1000);
            }
        });
        webview.setWebViewClient(new WebViewClient() {
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(activity, "Oh no! " + description, Toast.LENGTH_SHORT).show();
            }
        });

        String query = null;
        if(getArguments()!=null) {
            query = getArguments().getString("query");
        }

        if(query==null || query.equals("")) {
            webview.loadUrl("http://www.google.com");
        } else {
            webview.loadUrl("http://www.google.com/#q=" + query);
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
