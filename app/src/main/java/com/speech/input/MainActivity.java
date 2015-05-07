package com.speech.input;

import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.util.ArrayMap;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.widget.DrawerLayout;
import android.view.Window;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private Menu mMenu;
    private CharSequence mTitle;
    private int mIcon;

    private Fragment mFragment;
    private Fragment mMainFragment;
    private boolean is_listening = false;
    private Intent mSpeechRecognitionIntent;
    private int mFragmentDisplaying = MainFragment.SECTION_NUMBER;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mSpeechRecognitionIntent!=null) {
            stopService(mSpeechRecognitionIntent);
        }
    }

    public NavigationDrawerFragment getNavigationDrawerFragment() {
        return mNavigationDrawerFragment;
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();

        switch(position) {
            default:
            case MainFragment.SECTION_NUMBER:
                mFragment = MainFragment.newInstance();
                mMainFragment = mFragment;
                break;
            case MapsFragment.SECTION_NUMBER:
                mFragment = MapsFragment.newInstance();
                break;
            case WebViewFragment.SECTION_NUMBER:
                mFragment = WebViewFragment.newInstance();
                break;
            case CameraFragment.SECTION_NUMBER:
                mFragment = CameraFragment.newInstance();
                break;
        }

        fragmentManager.beginTransaction().replace(R.id.container, mFragment).commit();

        onSectionAttached(position);
    }

    @Override
    public void onNavigationDrawerItemSelected(int position, Bundle bundle) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();

        switch(position) {
            default:
            case MainFragment.SECTION_NUMBER:
                mFragment = MainFragment.newInstance();
                mMainFragment = mFragment;
                break;
            case MapsFragment.SECTION_NUMBER:
                mFragment = MapsFragment.newInstance();
                break;
            case WebViewFragment.SECTION_NUMBER:
                mFragment = WebViewFragment.newInstance();
                break;
            case CameraFragment.SECTION_NUMBER:
                mFragment = CameraFragment.newInstance();
                break;
        }

        mFragment.setArguments(bundle);
        fragmentManager.beginTransaction().replace(R.id.container, mFragment).commit();

        onSectionAttached(position);
    }

    public void onSectionAttached(int position) {
        ActionBar actionBar = getSupportActionBar();

        switch (position) {
            case MainFragment.SECTION_NUMBER:
                mTitle = getString(R.string.title_section1);
                mIcon = android.R.drawable.ic_btn_speak_now;
                break;
            case MapsFragment.SECTION_NUMBER:
                mTitle = getString(R.string.title_section2);
                mIcon = android.R.drawable.ic_menu_mapmode;
                break;
            case WebViewFragment.SECTION_NUMBER:
                mTitle = getString(R.string.title_section3);
                mIcon = android.R.drawable.ic_menu_search;
                break;
            case CameraFragment.SECTION_NUMBER:
                mTitle = getString(R.string.title_section4);
                mIcon = android.R.drawable.ic_menu_camera;
                break;
        }

        actionBar.setTitle(mTitle);
        actionBar.setHomeAsUpIndicator(mIcon);
        mFragmentDisplaying = position;
    }

    public Fragment getCurrentFragmentInstance() {
        return mFragment;
    }

    public int getFragmentDisplaying() {
        return mFragmentDisplaying;
    }

    public Menu getMenu() {
        return mMenu;
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
        actionBar.setHomeAsUpIndicator(mIcon);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            mMenu = menu;
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch(item.getItemId()) {
            case R.id.app_settings:
                Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(intent);
                return true;
            case R.id.speak_status:
                setUpSpeechRecognition();
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void setUpSpeechRecognition() {
        if (!is_listening) {
            mSpeechRecognitionIntent = new Intent(this, SpeechRecognitionService.class);
            startService(mSpeechRecognitionIntent);

            SpeechRecognitionService.setServiceListener(new SpeechRecognitionService.SpeechRecognitionServiceCallback() {
                @Override
                public void onReadyForSpeech() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            is_listening = true;
                            getMenu().findItem(R.id.speak_status).setIcon(android.R.drawable.presence_online);
                            ((MainFragment) mMainFragment).setOutputText("Talk to me");
                        }
                    });
                }

                @Override
                public void onBeginningOfSpeech() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            is_listening = true;
                            getMenu().findItem(R.id.speak_status).setIcon(android.R.drawable.presence_busy);
                            ((MainFragment) mMainFragment).setOutputText("Listening...");
                        }
                    });
                }

                @Override
                public void onEndOfSpeech() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getMenu().findItem(R.id.speak_status).setIcon(android.R.drawable.presence_away);
                            ((MainFragment) mMainFragment).setOutputText("Processing...");
                        }
                    });
                }

                public void onResults(final String result) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getMenu().findItem(R.id.speak_status).setIcon(android.R.drawable.presence_invisible);
                            processResult(result);
                        }
                    });
                }

                public void onError(final String error) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getMenu().findItem(R.id.speak_status).setIcon(android.R.drawable.presence_offline);
                            ((MainFragment) mMainFragment).setOutputText(error);
                        }
                    });
                }
            });
        }
    }

    public void processResult(String response) {
        String result = response.toLowerCase();

        if(result.matches(".*?(open|launch).*?camera.*")) {
            getNavigationDrawerFragment().selectItem(CameraFragment.SECTION_NUMBER);
        } else if(result.matches(".*?(take|make).*?(picture|photo).*")) {
            if(getFragmentDisplaying()==CameraFragment.SECTION_NUMBER) {
                ((CameraFragment) getCurrentFragmentInstance()).getCameraManager().takePicture(null, null);
            } else {
                Bundle bundle = new Bundle();
                bundle.putBoolean("takePicture", true);
                getNavigationDrawerFragment().selectItem(CameraFragment.SECTION_NUMBER, bundle);
            }
        } else if(result.matches(".*?(open|launch).*?(map|maps).*")) {
            getNavigationDrawerFragment().selectItem(MapsFragment.SECTION_NUMBER);
        } else if(result.matches(".*?setting.*")) {
            Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
            startActivity(intent);
        } else {
            Matcher matcher = Pattern.compile(".*?search(.*)").matcher(result);
            if(matcher.find()) {
                Bundle bundle = new Bundle();
                bundle.putString("query", matcher.group(1));
                getNavigationDrawerFragment().selectItem(WebViewFragment.SECTION_NUMBER, bundle);
            }
        }

        ((MainFragment) mMainFragment).setOutputText(response);
    }

}
