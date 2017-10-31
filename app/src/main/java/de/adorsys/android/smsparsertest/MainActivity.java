
package de.adorsys.android.smsparsertest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Vector;

import de.adorsys.android.smsparser.SmsConfig;
import de.adorsys.android.smsparser.SmsReceiver;
import de.adorsys.android.smsparser.SmsTool;

import static java.lang.Boolean.TRUE;

public class MainActivity extends AppCompatActivity {
    @NonNull
    private TextView smsSenderTextView;
    @NonNull
    private TextView smsMessageTextView;
    @NonNull
    private ImageView[] parking=  new ImageView[4];
    private Button addNode;
    private Button sendSMS;
    private boolean[] parkingSpots = new boolean[4];
    DatabaseHandler db;
    //private String[] messageArray = {}
    private LocalBroadcastManager localBroadcastManager;

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(SmsReceiver.INTENT_ACTION_SMS)) {
                Log.d("SMSParser","SMS Received");
                String receivedSender = intent.getStringExtra(SmsReceiver.KEY_SMS_SENDER);
                String receivedMessage = intent.getStringExtra(SmsReceiver.KEY_SMS_MESSAGE);
                smsSenderTextView.setText(getString(R.string.text_sms_sender_number,
                        receivedSender != null ? receivedSender : "NO NUMBER"));
                smsMessageTextView.setText(getString(R.string.text_sms_message,
                        receivedMessage != null ? receivedMessage : "NO MESSAGE"));
                if(receivedMessage != null && receivedSender != null){
                    switch(receivedMessage){
                        case "08ff08d3b2981eb6c611a385ffa4f865":
                            parkingSpots[0]=!parkingSpots[0];
                            break;
                        case "08ff08d3b2981eb6c611a385ffa4f866":
                            parkingSpots[1]=!parkingSpots[1];
                            break;
                        case "08ff08d3b2981eb6c611a385ffa4f867":
                            parkingSpots[2]=!parkingSpots[2];
                            break;
                        case "08ff08d3b2981eb6c611a385ffa4f868":
                            parkingSpots[3]=!parkingSpots[3];
                            break;
                    }
                    int count= 0;
                    for(boolean spot:parkingSpots){
                        if(spot == TRUE) {
                            parking[count].setImageResource(R.mipmap.noparking);
                        }else {
                            parking[count].setImageResource(R.mipmap.parking);
                        }
                        count ++;
                    }
                }
            }

        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        // If request is cancelled, the result arrays are empty.
        if (requestCode == SmsTool.REQUEST_CODE_ASK_PERMISSIONS
                && (grantResults.length <= 0
                || grantResults[0] != PackageManager.PERMISSION_GRANTED)) {
            Toast.makeText(this, getString(R.string.warning_permission_not_granted),
                    Toast.LENGTH_LONG).show();
            startActivity(new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.parse("package:" + getApplicationContext().getPackageName())));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        addNode = (Button) findViewById(R.id.add_sender_button);
        sendSMS = (Button) findViewById(R.id.send_sms);
        db = new DatabaseHandler(this);
        ArrayList<Contact> contact_array_from_db = db.Get_Contacts();
        Vector<String> phoneNumbers = new Vector<String>();
        for(int i = 0 ; i < contact_array_from_db.size() ; i++){
                phoneNumbers.add(contact_array_from_db.get(i).getPhoneNumber());
        }
        String[] arrayPhoneNumbers = phoneNumbers.toArray(new String[phoneNumbers.size()]);
        SmsConfig.INSTANCE.initializeSmsConfig(
                "BEGIN-MESSAGE",
                "END-MESSAGE",
                arrayPhoneNumbers);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            SmsTool.requestSMSPermission(this);
        }

        initViews();
        addNode.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent data_activity = new Intent(MainActivity.this,
                        DataActivity.class);
                data_activity.putExtra("called", "Data Activity");
                data_activity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                        | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(data_activity);
                finish();
            }
        });
        sendSMS.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent sendsms_activity = new Intent(MainActivity.this,
                        SendSMSActivity.class);
                sendsms_activity.putExtra("called", "Data Activity");
                sendsms_activity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                        | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(sendsms_activity);
                finish();
            }
        });
    }

    @Override
    protected void onPause() {
        unRegisterReceiver();
        super.onPause();
    }

    @Override
    protected void onResume() {
        registerReceiver();
        super.onResume();
    }

    private void initViews() {
        smsSenderTextView = findViewById(R.id.sms_sender_text_view);
        smsMessageTextView = findViewById(R.id.sms_message_text_view);
        parking[0] = findViewById(R.id.imageView1);
        parking[1] = findViewById(R.id.imageView2);
        parking[2] = findViewById(R.id.imageView3);
        parking[3] = findViewById(R.id.imageView4);
        for(ImageView slot:parking){
            slot.setImageResource(R.mipmap.parking);
        }
        smsSenderTextView.setText(getString(R.string.text_sms_sender_number, ""));
        smsMessageTextView.setText(getString(R.string.text_sms_message, ""));
    }

    private void registerReceiver() {
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(SmsReceiver.INTENT_ACTION_SMS);
        localBroadcastManager.registerReceiver(broadcastReceiver, intentFilter);
    }

    private void unRegisterReceiver() {
        localBroadcastManager.unregisterReceiver(broadcastReceiver);
    }
}