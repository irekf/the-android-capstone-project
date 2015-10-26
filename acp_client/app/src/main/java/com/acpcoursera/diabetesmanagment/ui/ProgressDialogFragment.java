package com.acpcoursera.diabetesmanagment.ui;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import com.acpcoursera.diabetesmanagment.R;

public class ProgressDialogFragment extends DialogFragment {

    private static String TAG = ProgressDialogFragment.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(false);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        ProgressDialog dialog = new ProgressDialog(getActivity(), getTheme());
        dialog.setTitle(getString(R.string.prompt_loading));
        dialog.setMessage(getString(R.string.prompt_please_wait));
        dialog.setIndeterminate(true);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        return dialog;
    }

    public static void show(FragmentActivity context) {
        FragmentManager fragmentManager = context.getSupportFragmentManager();
        ProgressDialogFragment progressDialog = new ProgressDialogFragment();
        progressDialog.show(fragmentManager, TAG);
    }

    public static void dismiss(FragmentActivity context) {
        FragmentManager fragmentManager = context.getSupportFragmentManager();
        ProgressDialogFragment progressDialog
                = (ProgressDialogFragment) fragmentManager.findFragmentByTag(TAG);
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

}