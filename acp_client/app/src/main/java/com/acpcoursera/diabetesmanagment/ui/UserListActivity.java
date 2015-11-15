package com.acpcoursera.diabetesmanagment.ui;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.acpcoursera.diabetesmanagment.R;
import com.acpcoursera.diabetesmanagment.model.UserInfo;
import com.acpcoursera.diabetesmanagment.model.UserSettings;
import com.acpcoursera.diabetesmanagment.service.NetOpsService;
import com.acpcoursera.diabetesmanagment.util.MiscUtils;

import static com.acpcoursera.diabetesmanagment.util.MiscUtils.showToast;

public class UserListActivity extends AppCompatActivity implements
        UserSettingsDialogFragment.UserSettingsDialogListener {

    private static String TAG = UserListActivity.class.getSimpleName();

    public  static final String EXTRA_USERNAME = "extra_username";
    public  static final String EXTRA_USER_SETTINGS = "extra_user_settings";

    public static final String ARG_TEEN_ONLY = "arg_teen_only";

    private static final String USER_LIST_KEY = "user_list_key";
    private static final String SELECTED_USER_KEY = "selected_user_key";

    private NetOpsReceiver mReceiver;

    ListView mUserListView;
    private UserInfo[] mUserList;
    private UserInfo mSelectedUser;
    private boolean mTeenOnly;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        Intent args = getIntent();
        mTeenOnly = args.getBooleanExtra(ARG_TEEN_ONLY, false);

        if (savedInstanceState != null) {
            mUserList = MiscUtils.convertParcelableArray(
                    savedInstanceState.getParcelableArray(USER_LIST_KEY), UserInfo.class);
            mSelectedUser = savedInstanceState.getParcelable(SELECTED_USER_KEY);
        }
        else {
            requestUserList();
        }

        mUserListView = (ListView) findViewById(R.id.users_list_view);
        mUserListView.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mSelectedUser = mUserList[position];
                // pick settings for the selected user
                UserSettingsDialogFragment dialogFragment = new UserSettingsDialogFragment();
                dialogFragment.show(getSupportFragmentManager(), UserSettingsDialogFragment.TAG);
            }
        });

        if (mUserList != null) {
            setUserListAdapter();
        }

    }

    private void returnResults(String username, UserSettings settings) {
        Intent data = new Intent();
        data.putExtra(EXTRA_USERNAME, username);
        data.putExtra(EXTRA_USER_SETTINGS, settings);
        if (getParent() == null) {
            setResult(Activity.RESULT_OK, data);
        } else {
            getParent().setResult(Activity.RESULT_OK, data);
        }
        finish();
    }

    @Override
    public void onResume() {
        super.onResume();
        mReceiver = new NetOpsReceiver();
        IntentFilter filter = new IntentFilter(TAG);
        filter.addAction(NetOpsService.ACTION_GET_USER_LIST);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, filter);
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArray(USER_LIST_KEY, mUserList);
        outState.putParcelable(SELECTED_USER_KEY, mSelectedUser);
    }

    @Override
    public void onFinishUserSettingsDialog(UserSettings settings) {
        returnResults(mSelectedUser.getUsername(), settings);
    }

    private class NetOpsReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            int resultCode = intent.getIntExtra(NetOpsService.RESULT_CODE, NetOpsService.RC_MISSING);
            if (action.equals(NetOpsService.ACTION_GET_USER_LIST)) {
                if (resultCode == NetOpsService.RC_OK) {
                    Parcelable[] parcelables = intent
                            .getParcelableArrayExtra(NetOpsService.EXTRA_USER_LIST);
                    if (parcelables != null) {
                        mUserList = MiscUtils.convertParcelableArray(parcelables, UserInfo.class);
                        setUserListAdapter();
                    }
                }
                else {
                    showToast(UserListActivity.this, getString(R.string.error_user_list) +
                            intent.getStringExtra(NetOpsService.EXTRA_ERROR_MESSAGE));
                }
                ProgressDialogFragment.dismiss(UserListActivity.this);
            }

        }
    }

    private class UserListAdapter extends ArrayAdapter<UserInfo> {

        UserInfo[] mUserList;

        public UserListAdapter(Context context, int resource, UserInfo[] objects) {
            super(context, resource, objects);
            this.mUserList = objects;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View view = convertView;
            if (view == null) {
                LayoutInflater inflater = (LayoutInflater) getContext()
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.user_list_item, null);
            }

            UserInfo user = mUserList[position];
            if (user != null) {

                ImageView icon = (ImageView) view.findViewById(R.id.user_list_icon);
                TextView fullName = (TextView) view.findViewById(R.id.user_list_full_name);
                TextView username = (TextView) view.findViewById(R.id.user_list_username);
                TextView email = (TextView) view.findViewById(R.id.user_list_email);

                if (user.getUserType().equals(UserInfo.TYPE_TEEN)) {
                    icon.setImageResource(R.drawable.ic_teen);
                }
                else {
                    icon.setImageResource(R.drawable.ic_follower);
                }

                fullName.setText(user.getFirstName() + " " + user.getSecondName());
                username.setText(user.getUsername());
                email.setText(user.getEmail());

            }

            return view;
        }

    }

    private void requestUserList() {
        ProgressDialogFragment.show(this);
        Intent intent = new Intent(this, NetOpsService.class);
        intent.setAction(NetOpsService.ACTION_GET_USER_LIST);
        intent.putExtra(NetOpsService.ARG_TEEN_ONLY, mTeenOnly);
        startService(intent);
    }

    private void setUserListAdapter() {
        mUserListView.setAdapter(new UserListAdapter(this, R.layout.user_list_item, mUserList));
    }

}
