package a06.cse.ntou.androidmqtt;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
    EditText ed_userid,ed_mealtype,ed_mealname, ed_mealnumber,ed_sumprice,ed_currenttime;
    TextView tv_orderstatus;
    Button btn_sendorder;
    String Hostboard = "CustomerOrder";
    MqttAndroidClient client;
    MqttConnectOptions options;
    OrderRespondBean orderRespondBean;
    static Gson gson = new Gson();

    private static List<meal> order= new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ed_userid = (EditText)findViewById(R.id.ed_userid);
        ed_mealtype = (EditText)findViewById(R.id.ed_mealtype);
        ed_mealname = (EditText)findViewById(R.id.ed_mealname);
        ed_mealnumber = (EditText)findViewById(R.id.ed_mealnumber);
        ed_sumprice = (EditText)findViewById(R.id.ed_sumprice);
        ed_currenttime = (EditText)findViewById(R.id.ed_currenttime);
        tv_orderstatus = (TextView)findViewById(R.id.tv_orderstatus);
        btn_sendorder = (Button)findViewById(R.id.btn_sendorder);

        String clientId = MqttClient.generateClientId();
        client = new MqttAndroidClient(this.getApplicationContext(), MQTTHOST, clientId);
        options = new MqttConnectOptions();
        options.setUserName(USERNAME);
        options.setPassword(PASSWORD.toCharArray());

        btn_sendorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String Send_to_Boss = Hostboard;
                final String getTopic = ed_userid.getText().toString();
                final String getTime = ed_currenttime.getText().toString();
                final int getSum = Integer.parseInt(ed_sumprice.getText().toString());
                final String message = Send_Order_to_Host(getTopic,getTime,getSum,order);
                try{
                    IMqttToken token = client.connect(options);
                    token.setActionCallback(new IMqttActionListener() {
                        @Override
                        public void onSuccess(IMqttToken asyncActionToken) { // 連線成功 欲執行的程式
                            Toast.makeText(MainActivity.this,"connected!",Toast.LENGTH_LONG).show();
                            try {
                                client.subscribe(getTopic,0);  // 訂閱顧客本身的看板
                                client.publish(Send_to_Boss, message.getBytes(),0,false); //推播訂單給老闆的看板
                            } catch (MqttException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                            // Something went wrong e.g. connection timeout or firewall problems
                            Toast.makeText(MainActivity.this,"connected failed!",Toast.LENGTH_LONG).show();
                        }
                    });
                }catch(MqttException e){e.printStackTrace();}
            }
        });

        client.setCallback(new MqttCallback() { // 收到訂閱之看板的回傳訊息
            @Override
            public void connectionLost(Throwable throwable) { Toast.makeText(MainActivity.this," connection Lost!",Toast.LENGTH_LONG).show();}

            @Override
            public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
               //tv_orderstatus.setText(new String(mqttMessage.getPayload()));
                String re = new String(mqttMessage.getPayload());
                orderRespondBean = gson.fromJson(re,OrderRespondBean.class);
                tv_orderstatus.setText("帳戶:"+orderRespondBean.getId()+"訂單狀態:"+orderRespondBean.getOrderStatus()+"訂單序號"+orderRespondBean.getOrderSerialNumber());
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {}
        });
    }

    public static String Send_Order_to_Host(String id,String time,int Sumprice,List list) { // 將選取的餐點資訊丟進來會用Gson包好
        OrderBean bean = new OrderBean();
        bean.setId(id);
        bean.setTime(time);
        bean.setSumprice(Sumprice);
        bean.setList(list);
        String printer = gson.toJson(bean);
        System.out.println(printer);
        return printer;
    }

   public void addmeal(View v){
       String getMealtype = ed_mealtype.getText().toString();
       String getMealname = ed_mealname.getText().toString();
       int getMealnumber = Integer.parseInt(ed_mealnumber.getText().toString());
       order.add(new meal(getMealtype, getMealname, getMealnumber));
       ed_mealtype.setText("");
       ed_mealname.setText("");
       ed_mealnumber.setText("");
    }

}


