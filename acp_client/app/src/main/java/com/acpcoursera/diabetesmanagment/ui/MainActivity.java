package com.acpcoursera.diabetesmanagment.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.acpcoursera.diabetesmanagment.R;
import com.acpcoursera.diabetesmanagment.util.MiscUtils;

public class MainActivity extends Activity {

    private static String TAG = MainActivity.class.getSimpleName();

    Button mLogOutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLogOutButton = (Button) findViewById(R.id.log_out_button);
        mLogOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // clear the access token and go back to the login screen
                MiscUtils.saveAccessToken(getApplicationContext(), "");
                Intent authActivityIntent = new Intent(getApplicationContext(), AuthActivity.class);
                startActivity(authActivityIntent);
                finish();
            }
        });

    }

}
