package vn.iot.dashboard;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.nio.charset.Charset;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    MQTTHelper mqttHelper;
    TextView txtTemp, txtHumid, nameTemp, nameHumid;
    ToggleButton BtnLED;
    int mode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtTemp = findViewById(R.id.txtTemperature);
        txtHumid = findViewById(R.id.txtHumidity);
        nameTemp = findViewById(R.id.nameTemperature);
        nameHumid = findViewById(R.id.nameHumidity);
        BtnLED = findViewById(R.id.BtnLED);

        BtnLED.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    Log.d("mqtt", "Button is ON");
                    //sendDataMQTT("Namnguyen22/feeds/bbc-temp", "32");
//                    ---------------------------------------------------
                    sendDataMQTT("Namnguyen22/feeds/bbc-led", "1");
//                    sendDataMQTT("Namnguyen22/feeds/bbc-temp", "980000");
//                    sendDataMQTT("Namnguyen22/feeds/bbc-temp-1", "22000");
//                    ---------------------------------------------------
                } else {
                    Log.d("mqtt", "Button is OFF");
                    //sendDataMQTT("Namnguyen22/feeds/bbc-temp", "0");
//                    ---------------------------------------------------
                    sendDataMQTT("Namnguyen22/feeds/bbc-led", "0");
//                    sendDataMQTT("Namnguyen22/feeds/bbc-humid", "28000000");
//                    sendDataMQTT("Namnguyen22/feeds/bbc-humid-1", "60000000");
//                    ---------------------------------------------------
                }
            }
        });

        BtnLED.setVisibility(View.VISIBLE);
        startMQTT();
    }

    int waiting_period = 0;
    boolean sending_message_again;
    String mess = "blablabla";
    private void setupScheduler(){
        Timer aTimer = new Timer();
        TimerTask scheduler = new TimerTask() {
            @Override
            public void run() {
                Log.d("mqtt", "Timer is ticking...");
                if(waiting_period > 0){
                    waiting_period--;
                    if(waiting_period == 0) sending_message_again = true;
                }
                if(sending_message_again == true){
                    sendDataMQTT("abcde", mess);
                }
            }
        };
        aTimer.schedule(scheduler, 5000, 1000);
    }

    private void sendDataMQTT(String topic, String value){
        waiting_period = 3;
        sending_message_again = false;
        MqttMessage msg = new MqttMessage();
        msg.setId(1234);
        msg.setQos(0);
        msg.setRetained(true);


        byte[] b = value.getBytes(Charset.forName("UTF-8"));
        msg.setPayload(b);

        try {
            mqttHelper.mqttAndroidClient.publish(topic, msg);
        }catch (Exception e){}

    }

    private void startMQTT(){
        mqttHelper = new MQTTHelper(getApplicationContext(), "123456789");
        mqttHelper.setCallback(new MqttCallbackExtended(){
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {
                Log.d("mqtt", "connection is successful");
            }

            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                Log.d("mqtt", "Deliver Successfully");
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                Log.d("mqtt", "Received: " + message.toString());
//                if(topic.contains("bbc-temp")){
//                    Log.d("mqtt", "Received Temp: " + message.toString());
//                    txtTemp.setText(message.toString());
//                }
//                if(topic.contains("bbc-humid")){
//                    Log.d("mqtt", "Received Humid: " + message.toString());
//                    txtHumid.setText(message.toString());
//                }
                if(topic.contains("bbc-led")){
                    Log.d("mqtt", "Received LED: " + message.toString());
                    if(message.toString().equals("0")){
                        BtnLED.setChecked(false);
                        mode = 0;
                    }else{
                        BtnLED.setChecked(true);
                        mode = 1;
                    }
                }
                int tmp;
                if(mode == 0) {
                    tmp = Integer.parseInt(message.toString());
                    nameTemp.setText("Confirmed Cases");
                    nameHumid.setText("Deaths");
                    if (topic.contains("bbc-temp-1")) {
                        Log.d("mqtt", "Received Temp: " + message.toString());
                        if(tmp <= 900000)
                            txtHumid.setText(message.toString());
                    }
                    else if (topic.contains("bbc-temp")) {
                        Log.d("mqtt", "Received Temp: " + message.toString());
                        if(tmp > 900000)
                            txtTemp.setText(message.toString());
                    }
                }
                else if(mode == 1) {
                    tmp = Integer.parseInt(message.toString());
                    nameTemp.setText("Fully Vaccinated");
                    nameHumid.setText("Partially Vaccinated");
                    if (topic.contains("bbc-humid-1")) {
                        Log.d("mqtt", "Received Humid: " + message.toString());
                        if(tmp >= 60000000)
                            txtHumid.setText(message.toString());
                    }
                    else if (topic.contains("bbc-humid")) {
                        Log.d("mqtt", "Received Humid: " + message.toString());
                        if(tmp < 60000000)
                            txtTemp.setText(message.toString());
                    }
                }
            }
        });
    }
}