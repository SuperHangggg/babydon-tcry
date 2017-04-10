package com.example.a279095640.babycry;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListActivity extends AppCompatActivity {
    private ListView lv;
    private String result;
    private PersonAdapter adapter;
    private List<Map<String, String>> list = new ArrayList<Map<String, String>>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        lv = (ListView) findViewById(R.id.listView);
        Intent intent = getIntent();
        result = intent.getStringExtra("result");
        List data = parseJson2List(result);
        adapter = new PersonAdapter(data,ListActivity.this,R.layout.item);
        list = Js2List(result);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new OnItemClickListenerImp());
    }

    private static List<Person> parseJson2List(String data){
        List<Person> result = new ArrayList<Person>();
        try{

            JSONArray item = new JSONArray(data);
            for(int i=0;i<item.length();i++){
                JSONObject tmpObj = item.getJSONObject(i);
                String name = tmpObj.getString("name");
                String no = tmpObj.getString("babyid");
                String time = tmpObj.getString("creat_time");
                Person person = new Person(name,no,time);
                result.add(person);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }
    private static List<Map<String, String>> Js2List(String data){
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        try{

            JSONArray item = new JSONArray(data);
            for(int i=0;i<item.length();i++){
                JSONObject tmpObj = item.getJSONObject(i);
                Map<String, String> map = new HashMap<String, String>();
                map.put("babyname",tmpObj.getString("name"));
                map.put("babyid",tmpObj.getString("babyid"));
                map.put("creat_time",tmpObj.getString("creat_time"));
                list.add(map);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    private class OnItemClickListenerImp implements AdapterView.OnItemClickListener {


        @SuppressWarnings("unchecked")
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            Map<String,String> map=(Map<String, String>) list.get(position);
           String name = map.get("babyname");
            String no = map.get("babyid");
            String time = map.get("creat_time");

            Intent intent = new Intent();
            intent.putExtra("babyname", name);
            intent.putExtra("babyid",no);
            intent.putExtra("creat_time",time);
            System.out.println(time);
            setResult(12,intent);
             finish();
        }
    }
}
