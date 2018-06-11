package com.eagle.administrator.mywifinavigationapplicationfour;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.eagle.MyActivity.DestinationActivity;
import com.eagle.MyActivity.LoginActivity;

//用到的应用类
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
//

public class MainActivity extends AppCompatActivity {
    WifiManager wifiManager;
    EditText text;
    String resultStr;
    JSONArray jsonArray;
    LocatorView locator;
    DestinationView destinationView;
    int iLevel;
    ArrayList<Integer> levelsList;
    ArrayList<Double> levelsAvgList;
    String location;

    FrameLayout map_layout;
    //Handler handler=new Handler();

    Toast toast;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        map_layout=(FrameLayout) findViewById(R.id.map_layout);
        locator=new LocatorView(MainActivity.this);//创自定义控件LocatorView的实例；
        map_layout.addView(locator);


        levelsList=new ArrayList<>();
        levelsAvgList=new ArrayList<>();

        Button button1=(Button)findViewById(R.id.button);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginIntent=new Intent();
                loginIntent.setClass(MainActivity.this, LoginActivity.class);
                MainActivity.this.startActivity(loginIntent);
            }
        });

        Button button2=(Button)findViewById(R.id.button2) ;
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myintent = new Intent();


                //指定Intent对象的目标组件是DestinationActivity；
                myintent.setClass(MainActivity.this, DestinationActivity.class);
                //通过Bundle对象将所在位置即location传递到Activity2，即将信息保存到Bundle中通过Intent传递到另一个Activity中；
                Bundle bundle=new Bundle();
                bundle.putCharSequence("location",location);//通过键值对的方式 传递信息；
                myintent.putExtras(bundle);//将bunble对象添加到intent对象中；
                //startActivityResult()方法以指定的请求码启动另一个Activity，该方法关闭新启动的Activity后，可以将选择的结果返回到原Activity中；
                // startActivityResult()通过重写onActivityResult()方法来获取新启动的Activity返回的结果；
                MainActivity.this.startActivityForResult(myintent,0X11);
            }
        });

        Button button8=(Button)findViewById(R.id.button8) ;
        button8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Thread visitThread = new Thread(new VisitWebRunnable());
                visitThread.start();
                try {
                    visitThread.join();
                    if(!resultStr.equals("")){
                        text.setText(resultStr);
                    }
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });

        text=(EditText)findViewById(R.id.editText);

        wifiManager= (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();

        Thread t = new Thread(new Runnable()
        {
            public void run()
            {
                while(true)
                {
                    runOnUiThread(new Runnable()
                    {
                        public void run()
                        {
                            getTheScanResults();
                            levelsList.add(iLevel);
                            //得到1秒内信号强度的平均值，用于定位；
                            if(levelsList.size()==6){
                                fixPosition(getMeanLevel(levelsList));
                                levelsAvgList.add(getMeanLevel(levelsList));
                                //通过两秒内信号强度的平均值的改变确定用户在两秒内行走的方向；
                                if(levelsAvgList.size()==2){
                                    getDirection(levelsAvgList);
                                    levelsAvgList.clear();
                                }
                                levelsList.clear();
                            }
                        }
                    });
                    try{
                        Thread.sleep(500);
                    }catch(InterruptedException e){
                        e.printStackTrace();
                    }
                }
            }
        });
        t.start();
        /*new Thread(new Runnable() {
            @Override
            public void run() {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                    }
                },100);
            }
        });*/
    }


    @Override
    protected void onRestart(){
        super.onRestart();
    }

    @Override
    //startActivityResult()方法通过重写onActivityResult()方法来获取新启动的Activity返回的结果；
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //判断是否为待处理的结果；
        if(requestCode==0X11&&resultCode==0X11){
            Bundle bundle=data.getExtras();//获取传递的数据包；
            //text.setText(bundle.getString("destination"));
            //destinationView=new DestinationView(MainActivity.this);
            //map_layout.addView(destinationView);

            if(bundle.getString("destination").equals("325")){
                map_layout.removeView(destinationView);
                destinationView=new DestinationView(MainActivity.this);
                destinationView.bitmapDesY=0;
                map_layout.addView(destinationView);
            }else if(bundle.getString("destination").equals("327")){
                map_layout.removeView(destinationView);
                destinationView=new DestinationView(MainActivity.this);
                destinationView.bitmapDesY=450;
                map_layout.addView(destinationView);
            }else if(bundle.getString("destination").equals("310")){
                map_layout.removeView(destinationView);
                toast = Toast.makeText(MainActivity.this, "该位置无法导航", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }else {
                map_layout.removeView(destinationView);
                destinationView=new DestinationView(MainActivity.this);
                map_layout.addView(destinationView);
            }
        }

    }

    private void getTheScanResults(){
        wifiManager.startScan();
        List<ScanResult> lists=wifiManager.getScanResults();
        StringBuilder sb;

        for(int i=0;i<lists.size();i++){
            ScanResult sr=lists.get(i);
            if(sr.BSSID.equals("48:7d:2e:0a:b9:6d")){
                iLevel=sr.level;
                String sLevel=String.valueOf(iLevel);
                text.setText(sLevel);
            }
        }
    }
    private double getMeanLevel(ArrayList<Integer> list) {
        //将集合转化成int类型的数组；
        Integer[] tempInteger=new Integer[list.size()];
        //int[] tempInt=new int[list.size()];
        int sum = 0;
        int[] levels=new int[tempInteger.length-2];
        list.toArray(tempInteger);
        /*for(int i=1;i<tempInteger.length;i++) {
            tempInt[i]=tempInteger[i].intValue();//java5及新版本不需要拆箱操作；
        }*/
        //求信号强度 的平均值，去掉信号强度中的最大值和最小值，便于定位；
        Arrays.sort(tempInteger);
        for(int i=0;i<levels.length;i++){
            levels[i]=tempInteger[i+1];
            sum+=levels[i];
        }
        return sum/levels.length;
    }
    private void fixPosition(double avg){
        if(avg<=-20&&avg>=-45){
            locator.bitmapY=1300;
            locator.invalidate();
            location="329";
        }else if(avg<-45&&avg>=-55){
            locator.bitmapY=1100;
            locator.invalidate();
            location="329";
        }else if(avg<-55&&avg>=-60){
            locator.bitmapY=900;
            locator.invalidate();
            location="329";
        }else if(avg<-60&&avg>=-64){
            locator.bitmapY=700;
            locator.invalidate();
            location="327";
        }else if(avg<-64&&avg>=-68){
            locator.bitmapY=500;
            locator.invalidate();
            location="327";
        }else if(avg<-68&&avg>=-70){
            locator.bitmapY=200;
            locator.invalidate();
            location="325";
        }else
            Toast.makeText(MainActivity.this,"wifi信号较弱！",Toast.LENGTH_SHORT).show();

            location="您不在导航范围内";

    }
    private void getDirection(ArrayList<Double> list) {
        Double[] tempDouble = new Double[list.size()];
        list.toArray(tempDouble);
        if (tempDouble[0] != 0 || tempDouble[tempDouble.length - 1] != 0) {

            if (tempDouble[0] > tempDouble[tempDouble.length - 1]) {
                toast = Toast.makeText(MainActivity.this, "您正在朝远离路由器的方向走", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            } else {
                toast = Toast.makeText(MainActivity.this, "您正在朝靠近路由器的方向走", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        }
    }

    private String getURLResponse(String urlString){
        HttpURLConnection conn = null; //连接对象
        InputStream is = null;
        String resultData = "";
        try {
            URL url = new URL(urlString); //URL对象
            conn = (HttpURLConnection)url.openConnection(); //使用URL打开�?个链�?
            conn.setDoInput(true); //允许输入流，即允许下�?
            conn.setDoOutput(true); //允许输出流，即允许上�?
            conn.setUseCaches(false); //不使用缓�?
            conn.setRequestMethod("GET"); //使用get请求
            is = conn.getInputStream();   //获取输入流，此时才真正建立链�?
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader bufferReader = new BufferedReader(isr);
            String inputLine  = "";
            while((inputLine = bufferReader.readLine()) != null){
                resultData += inputLine + "\n";
            }

        }  catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }finally{
            if(is != null){
                try {
                    is.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            if(conn != null){
                conn.disconnect();
            }
        }
        return resultData;
    }

    class VisitWebRunnable implements Runnable{

        @Override
        public void run() {
            // TODO Auto-generated method stub
            String data = getURLResponse("http://192.168.1.101:10609/actapi/Myapi/GetProduct/1");
            resultStr = data;
        }

    }

}


