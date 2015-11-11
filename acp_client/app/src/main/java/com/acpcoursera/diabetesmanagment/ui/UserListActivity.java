package com.acpcoursera.diabetesmanagment.ui;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.acpcoursera.diabetesmanagment.R;
import com.acpcoursera.diabetesmanagment.model.UserInfo;
import com.acpcoursera.diabetesmanagment.service.NetOpsService;

import static com.acpcoursera.diabetesmanagment.util.MiscUtils.showToast;

public class UserListActivity extends AppCompatActivity {

    private static String TAG = UserListActivity.class.getSimpleName();

    public  static final String EXTRA_USERNAME = "extra_username";
    private static String USER_LIST_KEY = "user_list_key";

    private NetOpsReceiver mReceiver;

    ListView mUserListView;
    private UserInfo[] mUserList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        if (savedInstanceState != null) {
            mUserList = (UserInfo[]) savedInstanceState.getParcelableArray(USER_LIST_KEY);
        }
        else {
            requestUserList();
        }

        mUserListView = (ListView) findViewById(R.id.users_list_view);
        mUserListView.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                UserInfo selectedUser = mUserList[position];
                returnUsername(selectedUser.getUsername());
            }
        });

        if (mUserList != null) {
            setUserListAdapter();
        }

    }

    private void returnUsername(String username) {
        Intent data = new Intent();
        data.putExtra(EXTRA_USERNAME, username);
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
    }

    private class NetOpsReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            int resultCode = intent.getIntExtra(NetOpsService.RESULT_CODE, NetOpsService.RC_MISSING);
            if (action.equals(NetOpsService.ACTION_GET_USER_LIST)) {
                if (resultCode == NetOpsService.RC_OK) {
                    mUserList = (UserInfo[]) intent
                            .getParcelableArrayExtra(NetOpsService.EXTRA_USER_LIST);
                    if (mUserList != null) {
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
                ((TextView) view.findViewById(R.id.user_list_username)).setText(user.getUsername());
                ((TextView) view.findViewById(R.id.user_list_first_name)).setText(user.getFirstName());
                ((TextView) view.findViewById(R.id.user_list_second_name)).setText(user.getSecondName());
                ((TextView) view.findViewById(R.id.user_list_email)).setText(user.getEmail());
            }

            return view;
        }

    }

    private void requestUserList() {
        ProgressDialogFragment.show(this);
        Intent intent = new Intent(this, NetOpsService.class);
        intent.setAction(NetOpsService.ACTION_GET_USER_LIST);
        startService(intent);
    }

    private void setUserListAdapter() {
        mUserListView.setAdapter(new UserListAdapter(this, R.layout.user_list_item, mUserList));
    }

}