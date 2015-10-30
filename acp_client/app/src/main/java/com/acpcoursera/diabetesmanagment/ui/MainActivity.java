package com.acpcoursera.diabetesmanagment.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.Button;

import com.acpcoursera.diabetesmanagment.R;
import com.acpcoursera.diabetesmanagment.service.NetOpsService;
import com.acpcoursera.diabetesmanagment.util.MiscUtils;

import static com.acpcoursera.diabetesmanagment.util.MiscUtils.showToast;

public class MainActivity extends FragmentActivity {

    private static String TAG = MainActivity.class.getSimpleName();

    Button mLogOutButton;

    private NetOpsReceiver mReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLogOutButton = (Button) findViewById(R.id.log_out_button);
        mLogOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProgressDialogFragment.show(MainActivity.this);
                Intent intent = new Intent(getApplicationContext(), NetOpsService.class);
                intent.setAction(NetOpsService.ACTION_LOG_OUT);
                getApplicationContext().startService(intent);
            }
        });

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

    private void logOut() {
        // clear the access token and go back to the login screen
        MiscUtils.saveAccessToken(getApplicationContext(), "");
        Intent authActivityIntent = new Intent(getApplicationContext(), AuthActivity.class);
        startActivity(authActivityIntent);
        finish();
    }

}
