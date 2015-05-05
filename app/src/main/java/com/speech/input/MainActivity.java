package com.speech.input;

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

public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

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

    public NavigationDrawerFragment getNavigationDrawerFragment() {
        return mNavigationDrawerFragment;
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = null;
        switch(position) {
            default:
            case MainFragment.SECTION_NUMBER:
                fragment = MainFragment.newInstance();
                break;
            case MapsFragment.SECTION_NUMBER:
                fragment = MapsFragment.newInstance();
                break;
            case WebViewFragment.SECTION_NUMBER:
                fragment = WebViewFragment.newInstance();
                break;
            case CameraFragment.SECTION_NUMBER:
                fragment = CameraFragment.newInstance();
                break;
        }

        fragmentManager.beginTransaction().replace(R.id.container, fragment).commit();

        onSectionAttached(position);
        restoreActionBar();
    }

    @Override
    public void onNavigationDrawerItemSelected(int position, Bundle bundle) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = null;
        switch(position) {
            default:
            case MainFragment.SECTION_NUMBER:
                fragment = MainFragment.newInstance();
                break;
            case MapsFragment.SECTION_NUMBER:
                fragment = MapsFragment.newInstance();
                break;
            case WebViewFragment.SECTION_NUMBER:
                fragment = WebViewFragment.newInstance();
                break;
            case CameraFragment.SECTION_NUMBER:
                fragment = CameraFragment.newInstance();
                break;
        }

        fragment.setArguments(bundle);
        fragmentManager.beginTransaction().replace(R.id.container, fragment).commit();

        onSectionAttached(position);
        restoreActionBar();
    }

    public void onSectionAttached(int position) {
        switch (position) {
            case MainFragment.SECTION_NUMBER:
                mTitle = getString(R.string.title_section1);
                break;
            case MapsFragment.SECTION_NUMBER:
                mTitle = getString(R.string.title_section2);
                break;
            case WebViewFragment.SECTION_NUMBER:
                mTitle = getString(R.string.title_section3);
                break;
            case CameraFragment.SECTION_NUMBER:
                mTitle = getString(R.string.title_section4);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
