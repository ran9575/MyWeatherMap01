package com.ajw1079.myweathermap01;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    TextView cityField;
    TextView updatedField;
    TextView selectCity;
    TextView weatherIcon;
    TextView currentTempField;
    TextView detailField;
    TextView humidityField;
    ProgressBar loader;

    Typeface weatherFont;

    String city = "Seoul";
    String OPEN_WEATHER_MAP_API = "1451c23e29ef1202473d5f41f30131c2";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();  //상단 액션바 제거시
        setContentView(R.layout.activity_main);

        cityField = (TextView) findViewById(R.id.city_field);
        updatedField = (TextView) findViewById(R.id.updated_field);
        selectCity = (TextView) findViewById(R.id.selectCity);
        weatherIcon = (TextView) findViewById(R.id.weather_icon);
        currentTempField = (TextView) findViewById(R.id.current_temp_field);
        detailField = (TextView) findViewById(R.id.details_field);
        humidityField = (TextView) findViewById(R.id.humidity_field);
        loader = (ProgressBar) findViewById(R.id.loader);

        /*폰트 적용시 Typeface를 사용한다.*/
        weatherFont = Typeface.createFromAsset(getAssets(), "fonts/weathericons-regular-webfont.ttf");
        weatherIcon.setTypeface(weatherFont);

        taskLoadUp(city);

        selectCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*"도시명 변경" 텍스트를 클릭했을 때 팝업창(AlertDialog)을 띄우고 내부를 구성 - 입력창, 변경, 취소 버튼*/

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
                alertDialog.setTitle("도시명 변경");
                    final EditText input = new EditText(MainActivity.this);
                input.setText(city);
                    LinearLayout.LayoutParams Ip = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.MATCH_PARENT);
                input.setLayoutParams(Ip);
                alertDialog.setView(input);

                alertDialog.setPositiveButton("변경", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            city = input.getText().toString();
                            taskLoadUp(city);
                        }
                });
                alertDialog.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                alertDialog.show();
            }
        });

    }

    public void taskLoadUp(String city) {
        if(Function.isNetworkAvailable(getApplicationContext())){
            DownloadWeather task = new DownloadWeather();
            task.execute(city);
        }else{
            Toast.makeText(getApplicationContext(),
                    "Internet Disconnected", Toast.LENGTH_LONG).show();
        }

    }

    /*화면 구성을 확인하고 보여주는 역할을 담당*/
    public class DownloadWeather extends AsyncTask<String, Void, String>{

        /*어플리케이션을 로딩을 함과 동시에 화면을 띄워주는 역할을 함*/
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            loader.setVisibility(View.VISIBLE);
        }
        /*주소창으로부터 본인의 API를 확인*/
        @Override
        protected String doInBackground(String... args) {
            String xml = Function.executeGet("http://api.openweathermap.org/data/2.5/weather?q="
                    + args[0] + "&units=metric&appid=" + OPEN_WEATHER_MAP_API);

            return xml;
        }
        @Override
        protected void onPostExecute(String xml){
            try{
                JSONObject json = new JSONObject(xml);
                if(json != null){
                    JSONObject details = json.getJSONArray("weather").getJSONObject(0);
                    JSONObject main = json.getJSONObject("main");
                    DateFormat df = DateFormat.getDateTimeInstance();

                    cityField.setText(json.getString("name").toUpperCase(Locale.US)
                            + ", " + json.getJSONObject("sys").getString("country"));
                    detailField.setText(details.getString("description").toUpperCase(Locale.US));
                    currentTempField.setText(String.format("%.2f", main.getDouble("temp")) + "℃");
                    humidityField.setText("습도 : " + main.getString("humidity") + "%");
                    updatedField.setText(df.format(new Date(json.getLong("dt") * 1000)));
                    weatherIcon.setText(Html.fromHtml(Function.setWeatherIcon(details.getInt("id"),
                            json.getJSONObject("sys").getLong("sunrise") * 1000,
                            json.getJSONObject("sys").getLong("sunset") * 1000)));
                    loader.setVisibility(View.GONE);

                }
            }catch (JSONException e){
                Toast.makeText(getApplicationContext(),
                        "다시 입력 바랍니다.", Toast.LENGTH_LONG).show();
            }
        }

    }


}
