package com.example.phonecallnotifier;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {
                Manifest.permission.SEND_SMS,
                Manifest.permission.READ_PHONE_STATE
        };

        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==1){
            if (grantResults.length > 0) {
                boolean phoneStatePermission = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                boolean smsPermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;

                if(phoneStatePermission && smsPermission) {
                    // call the Broadcasr Reciver
                    Toast.makeText(this, "u are good to go", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(this, phoneBR.class);
                    sendBroadcast(i);
                }else{
                    Toast.makeText(this, "all permissions not granted ", Toast.LENGTH_SHORT).show();
                }
            }  else {
                Toast.makeText(this, "none of the permissions granted ", Toast.LENGTH_SHORT).show();
            }
            return;
        }
    }

    // click listener from xml btn
    public void sendSmsFunc(View view) {
        // nothing
    }

}
