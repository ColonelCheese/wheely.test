package com.myasishchev.wheelytest.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.ActionBarActivity;

import com.myasishchev.wheelytest.R;
import com.myasishchev.wheelytest.model.WSocketManager;

/**
 * Created by MyasishchevA on 26.09.2014.
 */
public class ExitActivity extends ActionBarActivity {

    private AlertDialog exitDialog;

    @Override
    public void onBackPressed() {
        if (exitDialog == null) {
            exitDialog = new AlertDialog.Builder(this)
                .setMessage(getString(R.string.exit_app))
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        ExitActivity.super.onBackPressed();
                        onApplicationExit();
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create();
        }
        exitDialog.show();
    }

    protected void onApplicationExit() {
        WSocketManager.get(this).disconnect();
    }
}
