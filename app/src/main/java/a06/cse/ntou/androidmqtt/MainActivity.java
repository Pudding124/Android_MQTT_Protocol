package a06.cse.ntou.androidmqtt;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    static String MQTTHOST = "你的網址";
    static String USERNAME = "帳號";
    static String PASSWORD = "密碼";
    String topicStr = "test1";
    MqttAndroidClient client;
    TextView subText;
    TextView food_hum;
    TextView food_tos;
    TextView drink_soy;

    private static List<meal> order= new ArrayList<>();
    static String id = "pudding"; //假設已抓取目前顧客之id 將其裝進JSON
    static String time ="09/25"; //抓取目前時間
    static int sum = 300;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        subText = (TextView)findViewById(R.id.subText);
        food_hum = (TextView)findViewById(R.id.food_hum);
        food_tos = (TextView)findViewById(R.id.food_tos);
        drink_soy = (TextView)findViewById(R.id.drink_soy);

        String clientId = MqttClient.generateClientId();
        client = new MqttAndroidClient(this.getApplicationContext(), MQTTHOST, clientId);
        MqttConnectOptions options = new MqttConnectOptions();
        options.setUserName(USERNAME);
        options.setPassword(PASSWORD.toCharArray());

        try {
            IMqttToken token = client.connect(options);
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) { // 連線成功 欲執行的程式
                    Toast.makeText(MainActivity.this,"connected!",Toast.LENGTH_LONG).show();
                    setSubscription();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Something went wrong e.g. connection timeout or firewall problems
                    Toast.makeText(MainActivity.this,"connected failed!",Toast.LENGTH_LONG).show();
                }
            });
        } catch (MqttException e) {e.printStackTrace();}

        client.setCallback(new MqttCallback() { // 收到訂閱之看板的回傳訊息
            @Override
            public void connectionLost(Throwable throwable) { Toast.makeText(MainActivity.this," connection Lost!",Toast.LENGTH_LONG).show();}

            @Override
            public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {subText.setText(new String(mqttMessage.getPayload()));}

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {}
        });
    }

    public void pub(View v){  // 放置想要推播的訊息
        String topic = topicStr;
        String message = Send_Order_to_Host(id,time,sum,order);;
        try {
            client.publish(topic, message.getBytes(),0,false);
        } catch (MqttException e) {e.printStackTrace();}
    }

    private void setSubscription(){ // 想訂閱的看板
        try{
            client.subscribe(topicStr,0);
        }catch(MqttException e){e.printStackTrace();}
    }

    public static String Send_Order_to_Host(String id,String time,int Sumprice,List list) { // 將選取的餐點資訊丟進來會用Gson包好
        OrderBean bean = new OrderBean();
        bean.setId(id);
        bean.setTime(time);
        bean.setSumprice(Sumprice);
        bean.setList(list);
        Gson gson = new Gson();
        String printer = gson.toJson(bean);
        System.out.println(printer);
        return printer;
    }

   /* public void add_soy(View v){
        order.add(new meal("飲料","豆漿",1));
    }

    public void del(View v){
        order.clear();
    }*/
}


