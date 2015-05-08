package com.speech.input;

import android.app.FragmentTransaction;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceFragment;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.widget.DrawerLayout;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends ActionBarActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks {

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
    private MainFragment mMainFragment;
    private SpeechRecognition mSpeechRecognition;
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
        if(mSpeechRecognition!=null) {
            mSpeechRecognition.getSpeechRecognizer().destroy();
        }
    }

    public NavigationDrawerFragment getNavigationDrawerFragment() {
        return mNavigationDrawerFragment;
    }

    public boolean setReplaceFragment(int position) {
        switch(position) {
            default:
            case MainFragment.SECTION_NUMBER:
                mFragment = MainFragment.newInstance();
                mMainFragment = (MainFragment) mFragment;
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
            case 4:
                Intent dialIntent = new Intent(Intent.ACTION_DIAL);
                //callIntent.setData(Uri.parse("tel:" + c.getString(0)));
                startActivity(dialIntent);
                return false;
            case 5:
                Intent smsIntent = new Intent(Intent.ACTION_VIEW);
                smsIntent.setType("vnd.android-dir/mms-sms");
                smsIntent.putExtra("address", "Edit the number");
                smsIntent.putExtra("sms_body","");
                startActivity(smsIntent);
                return false;
            case SettingsFragment.SECTION_NUMBER:
                mFragment = SettingsFragment.newInstance();
                break;
        }

        return true;
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        if(setReplaceFragment(position)) {
            fragmentManager.beginTransaction().replace(R.id.container, mFragment).commit();
            onSectionAttached(position);
        }
    }

    @Override
    public void onNavigationDrawerItemSelected(int position, Bundle bundle) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        if(!setReplaceFragment(position)) {
            mFragment.setArguments(bundle);
            fragmentManager.beginTransaction().replace(R.id.container, mFragment).commit();
            onSectionAttached(position);
        }
    }

    public void onSectionAttached(int position) {
        ActionBar actionBar = getSupportActionBar();
        mTitle = getResources().getStringArray(R.array.nav_drawer_items)[position];
        mIcon = getResources().obtainTypedArray(R.array.nav_drawer_icons).getResourceId(position, -1);
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
        if (mSpeechRecognition==null) {
            mSpeechRecognition = new SpeechRecognition(this);

            mSpeechRecognition.setServiceListener(new SpeechRecognition.SpeechRecognitionServiceCallback() {
                @Override
                public void onReadyForSpeech() {
                    mMenu.findItem(R.id.speak_status).setIcon(android.R.drawable.presence_online);
                    mMainFragment.setOutputText("Talk to me");
                }

                @Override
                public void onBeginningOfSpeech() {
                    mMenu.findItem(R.id.speak_status).setIcon(android.R.drawable.presence_away);
                    mMainFragment.setOutputText("Listening...");
                }

                @Override
                public void onEndOfSpeech() {
                    mMenu.findItem(R.id.speak_status).setIcon(android.R.drawable.presence_busy);
                    mMainFragment.setOutputText("Processing...");
                }

                public void onResults(final String result) {
                    mMenu.findItem(R.id.speak_status).setIcon(android.R.drawable.presence_invisible);
                    processResult(result);
                }

                public void onError(final String error) {
                    mMenu.findItem(R.id.speak_status).setIcon(android.R.drawable.presence_offline);
                    mMainFragment.setOutputText(error);
                    if(getFragmentDisplaying() == MainFragment.SECTION_NUMBER) {
                        mSpeechRecognition.setAutoRestartListening(false);
                    }
                }
            });
        } else if(!mSpeechRecognition.autoRestartListening()) {
            mSpeechRecognition.setAutoRestartListening(true);
        }
    }

    public void phoneCall(String result) {
        Matcher matcher = Pattern.compile(".*?call(.*)").matcher(result);
        if(matcher.find()) {
            StringBuilder str_builder =  new StringBuilder(matcher.group(1).trim().toLowerCase());
            str_builder.setCharAt(0, Character.toUpperCase(str_builder.charAt(0)));
            String name = str_builder.toString();
            String selection = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME+" = '" + name + "'";
            String[] projection = new String[] { ContactsContract.CommonDataKinds.Phone.NUMBER};
            Cursor c = getApplicationContext().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, projection, selection, null, null);
            if (c.moveToFirst()) {
                try {
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:" + c.getString(0)));
                    startActivity(callIntent);
                    mMainFragment.setOutputText(getString(R.string.defaultSpeechText));
                } catch (ActivityNotFoundException e) {
                    mMainFragment.setOutputText("Error in your phone call" + e.getMessage());
                }
            } else {
                mMainFragment.setOutputText("Cannot find the contact.\nPlease try again.");
            }
            c.close();
        }
    }

    public void sms(String result) {
        Matcher matcher = Pattern.compile(".*?(sms|tell) (.*?) that (.*)").matcher(result);
        if(matcher.find()) {
            String phoneNumber = "0";
            String sms_body =  matcher.group(3).trim();
            sms_body = sms_body.substring(0,1).toUpperCase() + sms_body.substring(1);

            StringBuilder str_builder =  new StringBuilder(matcher.group(2).trim().toLowerCase());
            str_builder.setCharAt(0, Character.toUpperCase(str_builder.charAt(0)));
            String name = str_builder.toString();

            String selection = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME+" = '" + name + "'";
            String[] projection = new String[] { ContactsContract.CommonDataKinds.Phone.NUMBER};
            Cursor c = getApplicationContext().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, projection, selection, null, null);

            if (c.moveToFirst()) {
                phoneNumber =  c.getString(0);
            }

            Intent smsIntent = new Intent(Intent.ACTION_VIEW);
            smsIntent.setType("vnd.android-dir/mms-sms");
            smsIntent.putExtra("address", phoneNumber);
            smsIntent.putExtra("sms_body", sms_body);
            startActivity(smsIntent);

            mMainFragment.setOutputText(getString(R.string.defaultSpeechText));
        }
    }

    public void search(String result) {
        Matcher matcher = Pattern.compile(".*?search(.*)").matcher(result);
        if(matcher.find()) {
            if(getFragmentDisplaying()==WebViewFragment.SECTION_NUMBER) {
                String url = "http://www.google.com/#q=" + matcher.group(1).trim();
                ((WebViewFragment) getCurrentFragmentInstance()).loadUrl(url);
            } else {
                Bundle bundle = new Bundle();
                bundle.putString("query", matcher.group(1).trim());
                getNavigationDrawerFragment().selectItem(WebViewFragment.SECTION_NUMBER, bundle);
            }
        }
    }

    public void processResult(String response) {
        String result = response.toLowerCase();
        boolean auto = true;

        if(result.matches(".*?(open|launch).*?camera.*")) {
            mNavigationDrawerFragment.selectItem(CameraFragment.SECTION_NUMBER);
        } else if(result.matches(".*?(take|make).*?(picture|photo).*")) {
            if(getFragmentDisplaying()==CameraFragment.SECTION_NUMBER) {
                CameraManager cameraManager = ((CameraFragment) getCurrentFragmentInstance()).getCameraManager();
                cameraManager.takePicture(null, null);
            } else {
                Bundle bundle = new Bundle();
                bundle.putBoolean("takePicture", true);
                mNavigationDrawerFragment.selectItem(CameraFragment.SECTION_NUMBER, bundle);
            }
        } else if(result.matches(".*?(open|launch).*?(map|maps).*")) {
            mNavigationDrawerFragment.selectItem(MapsFragment.SECTION_NUMBER);
        } else if(result.matches(".*?setting.*")) {
            mNavigationDrawerFragment.selectItem(SettingsFragment.SECTION_NUMBER);
        } else if(result.matches(".*?call(.*)")) {
            phoneCall(result);
            auto = false;
        } else if(result.matches(".*?(sms|tell) (.*?) that (.*)")) {
            sms(result);
            auto = false;
        } else if(result.matches(".*?search(.*)")){
            search(result);
        } else {
            if(getFragmentDisplaying() == MainFragment.SECTION_NUMBER) {
                auto = false;
            }
            mMainFragment.setOutputText(response);
         }

        mSpeechRecognition.setAutoRestartListening(auto);
    }

}
