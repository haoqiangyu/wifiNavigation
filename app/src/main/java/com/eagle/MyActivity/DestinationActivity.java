package com.eagle.MyActivity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.eagle.administrator.mywifinavigationapplicationfour.MainActivity;
import com.eagle.administrator.mywifinavigationapplicationfour.R;

public class DestinationActivity extends AppCompatActivity {


    String destination;//目的地信息；

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_destination);


        final Intent intent=getIntent();//获取Intent对象；
        final Bundle bundle=intent.getExtras();//获取传递的Bundle信息；
        EditText editText4=(EditText)findViewById(R.id.editText4) ;
        editText4.setText(bundle.getString("location"));//在编辑框显示所在位置；
        //创建下拉列表；
        String[] mItems = {"325", "327", "329", "310"};
        Spinner spinner=(Spinner)findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, mItems);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        //创建监听器，获取下拉列表中的值；
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                destination=parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        Button button7=(Button)findViewById(R.id.button7);
        button7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               finish();
            }
        });
        Button button6=(Button)findViewById(R.id.button6);
        button6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1=getIntent();//获取intent对象；
                //intent1.setClass(DestinationActivity.this,MainActivity.class);
                Bundle bundle1=new Bundle();//实例化要传递的数据包；
                bundle1.putCharSequence("destination",destination);//目的地数据；
                intent1.putExtras(bundle1);//将数据包保存到intent中；
                setResult(0X11,intent1);//设置返回的结果码，并返回调用该Activity的Activity；
                //DestinationActivity.this.startActivity(intent1);
                finish();//关闭当前Activity；

            }
        });
    }
}
