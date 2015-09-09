package com.example.idltest.idltest;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedInputStream;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    String search_url = "http://104.236.125.9:8080/echo/sdf";
    String api_key = "&api_key=등록한 키를 입력하세요";
    String per_page = "&per_page=50";
    String sort = "&sort=interestingness-desc";
    String format = "&format=json";
    String safe_search = "&safe_search=1";
    String content_type = "&content_type=1";
    String search_text = "&text='cat'";


    String request = search_url;// + api_key + per_page + sort + format + content_type + search_text;
    private TextView textview = null;
    private String JSONdata1 = null;
    private final static int GET_JSON = 0;
    private final static int DO_STH = 1;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Button getJSON = (Button) findViewById(R.id.loginbtn);
        TextView ShowJSON = (TextView)findViewById(R.id.textViewtest);
        ShowJSON.setMovementMethod(new ScrollingMovementMethod());
        ShowJSON.setEllipsize(TextUtils.TruncateAt.END);

        getJSON.setOnClickListener(new Button.OnClickListener() {

            public void onClick(View v) {

                progressDialog = new ProgressDialog(MainActivity.this);
                progressDialog.setMessage("Please wait.....");
                progressDialog.show();

                new Thread() {
                    public void run() {
                        JSONdata1 = readJSON();
                        messageHandler.sendEmptyMessage(GET_JSON);
                    }
                }.start();

                //onstartbtnClicked(v);
            }

        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onstartbtnClicked(View view)
    {
        Toast.makeText(getApplicationContext(), "rrrrr", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, inner.class);
        startActivity(intent);
    }



    private Handler messageHandler= new Handler(){

        public void handleMessage(Message msg){
            super.handleMessage(msg);

            int what = msg.what;

            switch(what) {
                case GET_JSON:
                    if (textview == null)
                        textview = (TextView) findViewById(R.id.textViewtest);

                    textview.setText(JSONdata1);
                    progressDialog.dismiss();
                    break;

                case DO_STH:
                    break;
            }
        }
    };

    public String  readJSON(){
        StringBuilder JSONdata = new StringBuilder();
        HttpClient httpClient = new DefaultHttpClient();
        byte[] buffer = new byte[1024];


        try {
            HttpGet httpGetRequest = new HttpGet( request );
            HttpResponse httpResponse = httpClient.execute(httpGetRequest);

            StatusLine statusLine = httpResponse.getStatusLine(); //  문자열 HTTP⁄1.1 200 OK
            int statusCode = statusLine.getStatusCode();
            if ( statusCode == 200 ) //서버가 요청한 페이지를 제공했다면
            {
                HttpEntity entity = httpResponse.getEntity();
                if (entity != null) {
                    InputStream inputStream = entity.getContent();

                    try {

                        int bytesRead = 0;
                        BufferedInputStream bis = new BufferedInputStream(inputStream);

                        while ((bytesRead = bis.read(buffer) ) != -1) {
                            String line = new String(buffer, 0, bytesRead);

                            JSONdata.append(line);

                        }

                    } catch (Exception e) {
                        Log.e("logcat", Log.getStackTraceString(e));
                    } finally {
                        try {
                            inputStream.close();
                        } catch (Exception ignore) {
                        }
                    }
                }
            }

        }catch(Exception e){
            Log.e("logcat", Log.getStackTraceString(e));
        }finally{
            httpClient.getConnectionManager().shutdown();
            return JSONdata.toString();
        }
    }
}
