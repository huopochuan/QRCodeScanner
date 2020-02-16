package com.blikoon.qrcodescannerlibrary;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.blikoon.qrcodescanner.QrCodeActivity;
import com.blikoon.qrcodescanner.grant.PermissionsManager;
import com.blikoon.qrcodescanner.grant.PermissionsResultAction;

public class MainActivity extends AppCompatActivity {
    private EditText mProtocalEditText;
    private Button mJumpBtn;
    private Button mScanBtn;
    private static final int REQUEST_CODE_QR_SCAN = 101;
    private final String LOGTAG = "QRCScanner-MainActivity";
    private String mProtol = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mProtocalEditText = (EditText) findViewById(R.id.protocal_textview);
        mProtocalEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mProtol = s.toString();
            }
        });

        mScanBtn = (Button) findViewById(R.id.button_start_scan);
        mScanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Start the qr scan activity

                PermissionsManager.getInstance().requestPermissionsIfNecessaryForResult(MainActivity.this , new String[]{
                        Manifest.permission.CAMERA}, new PermissionsResultAction() {
                    @Override
                    public void onGranted() {
                        Intent i = new Intent(MainActivity.this,QrCodeActivity.class);
                        startActivityForResult( i,REQUEST_CODE_QR_SCAN);
                    }

                    @Override
                    public void onDenied(String permission) {
                        Toast.makeText(MainActivity.this,"需要相机权限",Toast.LENGTH_LONG).show();
                    }
                });

            }
        });

        mJumpBtn = (Button) findViewById(R.id.jump_btn);
        mJumpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(mProtol)) {
                    Toast.makeText(MainActivity.this, "跳转协议不能为空",Toast.LENGTH_SHORT).show();
                    return ;
                }
                Uri uri = Uri.parse(mProtol);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                try {
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, "没有匹配的APP，请下载安装",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(resultCode != Activity.RESULT_OK) {
            Log.d(LOGTAG,"COULD NOT GET A GOOD RESULT.");
            if(data==null) {
                return;
            }
            //Getting the passed result
            String result = data.getStringExtra("com.blikoon.qrcodescanner.error_decoding_image");
            if(result == null) {
                return ;
            }

            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
            alertDialog.setTitle("Scan Error");
            alertDialog.setMessage("QR Code could not be scanned");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
            return;
        }
        if(requestCode == REQUEST_CODE_QR_SCAN) {
            if(data==null)
                return;
            //Getting the passed result
            String result = data.getStringExtra("com.blikoon.qrcodescanner.got_qr_scan_relult");
            Log.d(LOGTAG,"Have scan result in your app activity :"+ result);

            Toast.makeText(MainActivity.this, "扫描成功", Toast.LENGTH_LONG).show();

            mProtocalEditText.setText(result);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        PermissionsManager.getInstance().notifyPermissionsChange(this, permissions, grantResults);
    }

}
