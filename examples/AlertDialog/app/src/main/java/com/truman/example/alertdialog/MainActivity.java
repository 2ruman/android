package com.truman.example.alertdialog;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Author  : Truman
 * Contact : truman.t.kim@gmail.com
 * Version : 1.0.0
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG_SUFFIX = ".2ruman"; // For grep
    private static final String TAG = "MainActivity" + TAG_SUFFIX;

//    private TextView mTvStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "onCreate()");

        findViewById(R.id.btn_long_msg).setOnClickListener(this);
        findViewById(R.id.btn_cpy_msg).setOnClickListener(this);
        findViewById(R.id.btn_pwd).setOnClickListener(this);
    }

    private void showLongMessageDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Notice")
                .setMessage(
                        "This is a dialog containing a long message.\n" +
                        "The long message will make a scroll right side without any efforts from" +
                        "developers.\n\nLeaving a long message...\n" +
                        "H\ni\n,\n \nt\nh\ne\nr\ne\n!\n\n" +
                        "H\no\nw\n \na\nr\ne\n \ny\no\nu\n \nd\no\ni\nn\ng\n?\n\n" +
                        "ABC\nDEF\nGHI\nJKL\nMNO\nPQR\nSTU\nVWX\nYZ\n")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    private void showCopyMessageDialog() {
        final EditText etURL = new EditText(this);
        etURL.setText(R.string.url);
        etURL.setTextIsSelectable(true);
        etURL.setInputType(InputType.TYPE_NULL);
        etURL.setSelectAllOnFocus(true);
        etURL.requestFocus();

        new AlertDialog.Builder(this)
                .setView(etURL)
                .setTitle("URL")
//                .setMessage("Enter your password")
                .setPositiveButton("Copy", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        copyToClipboard("My URL", etURL.getText().toString());
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .show();
    }

    private void copyToClipboard(String label, String data) {
        if (label == null || data == null) {
            Toast.makeText(getApplicationContext(), "Copy failed...", Toast.LENGTH_LONG).show();
        }
        ClipboardManager clipboard = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(label, data);
        clipboard.setPrimaryClip(clip);

        Toast.makeText(getApplicationContext(), label + " copy success!", Toast.LENGTH_LONG).show();
    }

    private void showPasswordDialog() {
        final EditText etPassword = new EditText(this);
        etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        etPassword.requestFocus();

        new AlertDialog.Builder(this)
                .setView(etPassword)
                .setIcon(R.mipmap.ic_launcher)
                .setTitle("Authentication")
                .setMessage("Enter your password")
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        confirmPassword(etPassword.getText().toString());
                        dialog.dismiss();
                    }
                })
                .setNeutralButton("Forgot", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .show();
    }

    private void confirmPassword(String password) {
        Toast.makeText(getApplicationContext(), "Password confirmed! : " + password, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_long_msg:
                showLongMessageDialog();
                break;
            case R.id.btn_cpy_msg:
                showCopyMessageDialog();
                break;
            case R.id.btn_pwd:
                showPasswordDialog();
                break;
        }
    }
}