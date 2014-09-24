package com.myasishchev.wheelytest.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.myasishchev.wheelytest.R;
import com.myasishchev.wheelytest.model.WSocketManager;

public class LoginActivity extends ActionBarActivity {

    private static final String KEY_USERNAME = "username";
    private static final String KEY_PASSWORD = "password";

    private EditText textUsername;
    private EditText textPassword;

    private View.OnClickListener connectClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            final ProgressDialog dialog = ProgressDialog.show(LoginActivity.this, "", "Connecting...");
            final WSocketManager socketManager = WSocketManager.get(LoginActivity.this);
            socketManager.connect(textUsername.getText().toString(), textPassword.getText().toString(), new WSocketManager.IConnectionListener() {
                @Override
                public void onConnectionOpen() {
                    dialog.dismiss();
                    startMapActivity();
                }

                @Override
                public void onConnectionClose(int code, Bundle data) {
                    dialog.dismiss();
                    socketManager.handleConnectionClose(code, data, LoginActivity.this);
                }
            });
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (WSocketManager.get(this).isConnected()) startMapActivity();

        textUsername = (EditText) findViewById(R.id.username);
        textPassword = (EditText) findViewById(R.id.password);

        findViewById(R.id.connect).setOnClickListener(connectClick);
    }

    private void startMapActivity() {
        Intent intent = new Intent(LoginActivity.this, MapActivity.class);
        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //WSocketManager.get(this).onResume(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        //WSocketManager.get(this).onStop(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_USERNAME, textUsername.getText().toString());
        outState.putString(KEY_PASSWORD, textPassword.getText().toString());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        textUsername.setText(savedInstanceState.getString(KEY_USERNAME));
        textPassword.setText(savedInstanceState.getString(KEY_PASSWORD));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
