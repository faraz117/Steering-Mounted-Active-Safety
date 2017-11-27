
package pk.ejunkies.android.activeDriverSafety;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
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
import android.os.Bundle;
import android.os.AsyncTask;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import pk.ejunkies.android.activeDriverSafety.connectivity.SendRequest;
import pk.ejunkies.android.smsparser.SmsConfig;
import pk.ejunkies.android.smsparser.SmsReceiver;
import pk.ejunkies.android.smsparser.SmsTool;


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
    TextView alcoholView;
    TextView obstacleView;
    TextView pulseView;
    TextView temperatureView;
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
        alcoholView = (TextView)findViewById(R.id.textViewAlcohol);
        obstacleView = (TextView)findViewById(R.id.textViewObstacle);
        pulseView = (TextView)findViewById(R.id.textViewPulse);
        temperatureView = (TextView)findViewById(R.id.textViewTemperature);
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

/*
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
*/
        public void onClick(View view) {
            //new SendRequest(MainActivity.this,"12","H").execute();

        }

        });
        callAsynchronousTask();
    }


    public void callAsynchronousTask() {
        final Handler handler = new Handler();
        Timer timer = new Timer();
        TimerTask doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        try {
                            SendRequest performBackgroundTask = new SendRequest(MainActivity.this);
                            // PerformBackgroundTask this class is the class that extends AsynchTask
                            String result = performBackgroundTask.execute().get();
                            Log.d("Result",result);
                            String temperature= result.substring(result.indexOf("Temp: ")+6,result.indexOf("Pulse: "));
                            String pulse= result.substring(result.indexOf("Pulse: ")+7,result.indexOf("Obstc: "));
                            String obstacle= result.substring(result.indexOf("Obstc: ")+7,result.indexOf("Alkoh: "));
                            String alcohol = result.substring(result.indexOf("Alkoh: ")+7,result.indexOf("End"));
                            alcoholView.setText(alcohol);
                            obstacleView.setText(obstacle);
                            pulseView.setText(pulse);
                            temperatureView.setText(temperature);
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            Toast.makeText(MainActivity.this,e.toString(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        };
        timer.schedule(doAsynchronousTask, 0, 500); //execute in every 50000 ms
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