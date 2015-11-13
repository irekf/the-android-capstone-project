package com.acpcoursera.diabetesmanagment.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.acpcoursera.diabetesmanagment.R;
import com.acpcoursera.diabetesmanagment.service.NetOpsService;
import com.acpcoursera.diabetesmanagment.util.MiscUtils;

import static com.acpcoursera.diabetesmanagment.util.MiscUtils.showToast;

public class MainActivity extends AppCompatActivity {

    private static String TAG = MainActivity.class.getSimpleName();

    private static String ACTION_BAR_TITLE_KEY = "action_bar_title_key";

    TextView mCheckInButton;
    TextView mLogOutButton;

    private NetOpsReceiver mReceiver;

    private String[] mOptionTitles;
    private DrawerLayout mDrawerLayout;
    private ActionBar mActionBar;
    private ActionBarDrawerToggle mDrawerToggle;
    private ListView mDrawerList;

    CharSequence mActionBarTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mActionBar = getSupportActionBar();

        mOptionTitles = getResources().getStringArray(R.array.option_titles);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer_items);

        // Set the adapter for the list view
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, mOptionTitles));
        // Set the list's click listener
        mDrawerList.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView drawerItem = (TextView) view;
                String itemText = drawerItem.getText().toString();

                if (itemText.equals("Followers")) {
                    loadFollowersFragment();
                }

                mActionBarTitle = mOptionTitles[position];
                mDrawerLayout.closeDrawers();
            }
        });

        // drawer toggle
        mDrawerToggle = new ActionBarDrawerToggle(
                this, mDrawerLayout,
                R.string.drawer_open, R.string.drawer_close
        ) {

            @Override
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                mActionBar.setTitle(mActionBarTitle);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                mActionBar.setTitle(R.string.label_main_menu);
            }
        };

        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setHomeButtonEnabled(true);

        mCheckInButton = (TextView) findViewById(R.id.check_in_button);
        mCheckInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), CheckInActivity.class));
            }
        });

        // logout button (not really a button for now)
        mLogOutButton = (TextView) findViewById(R.id.log_out_button);
        mLogOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProgressDialogFragment.show(MainActivity.this);
                Intent intent = new Intent(getApplicationContext(), NetOpsService.class);
                intent.setAction(NetOpsService.ACTION_LOG_OUT);
                getApplicationContext().startService(intent);
            }
        });

        if (savedInstanceState == null) {
            loadFollowersFragment();
            mDrawerList.setItemChecked(1, true);
            mActionBarTitle = getString(R.string.title_followers);
        }
        else {
            mActionBarTitle = savedInstanceState.getCharSequence(ACTION_BAR_TITLE_KEY);
        }
        mActionBar.setTitle(mActionBarTitle);

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putCharSequence(ACTION_BAR_TITLE_KEY, mActionBarTitle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    /* This callback allows to open/close the drawer by clicking on the toggle */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        mReceiver = new NetOpsReceiver();
        IntentFilter filter = new IntentFilter(NetOpsService.ACTION_LOG_OUT);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, filter);
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawers();
        }
        else {
            this.finish();
        }
    }

    private class NetOpsReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            int resultCode = intent.getIntExtra(NetOpsService.RESULT_CODE, NetOpsService.RC_MISSING);
            if (action.equals(NetOpsService.ACTION_LOG_OUT)) {
                if (resultCode != NetOpsService.RC_OK) {
                    showToast(MainActivity.this, getString(R.string.logout_error) +
                            intent.getStringExtra(NetOpsService.EXTRA_ERROR_MESSAGE));
                }
                logOut();
                ProgressDialogFragment.dismiss(MainActivity.this);
            }

        }
    }

    private void selectOption(int position) {
        // TODO change fragments depending on the item selected
    }

    private void logOut() {
        // log out and go back to the login screen
        MiscUtils.setLoggedIn(getApplicationContext(), false);
        Intent authActivityIntent = new Intent(getApplicationContext(), AuthActivity.class);
        startActivity(authActivityIntent);
        finish();
    }

    private void loadFollowersFragment() {
        FollowersFragment followersFragment = new FollowersFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content_frame, followersFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

}
