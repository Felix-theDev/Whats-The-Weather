package com.company.whatstheweather;
/** A program to get the weather of a particular location anywhere in the world
 * @author Felix Ogbonnaya
 * @since 2020-03-02
 */
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.MalformedInputException;

public class MainActivity extends AppCompatActivity {
    TextView resultField;
    EditText cityName;
    Button button;
    public class DownloadTask extends AsyncTask<String, Void, String>{
        @Override
        protected String doInBackground(String... strings) {
            HttpURLConnection httpURLConnection;
            URL url;
            String result = "";
            try{
                url = new URL(strings[0]);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(inputStream);
                int data = reader.read();
                while(data != -1){
                    result += (char) data;
                    data = reader.read();
                }
                return result;
            }catch (MalformedURLException e){
                e.printStackTrace();

                Toast.makeText(getApplicationContext(), "Couldn't find weather", Toast.LENGTH_SHORT).show();

            }catch (IOException e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);


            try{
                JSONObject jsonObject = new JSONObject(s);
                String weatherInfo = jsonObject.getString("weather");
                JSONArray arr = new JSONArray(weatherInfo);

                for(int i =0; i< arr.length(); i++){
                    JSONObject jsonPart = arr.getJSONObject(i);
                    String main = jsonPart.getString("main");
                    String description = jsonPart.getString("description");
                    Log.i("results: ", main);
                    String message = "";
                    if(!main.equals("") && !description.equals("")){
                        message += main + ": " + description + "\n";
                    }
                    if(!message.equals("")){
                        resultField.setText(message);
                    }else {

                        Toast.makeText(getApplicationContext(), "Couldn't find weather", Toast.LENGTH_SHORT).show();
                    }


                }

            }catch (Exception e){
                e.printStackTrace();
            }


        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cityName = findViewById(R.id.city);
        button = findViewById(R.id.button);
        resultField = findViewById(R.id.result);


        cityName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(cityName.length() > 0){
                    button.setEnabled(true);
                }else{
                    button.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


    }

    public void clickFunction(View view){
        try{
        String encodedCityName = URLEncoder.encode(cityName.getText().toString(), "UTF-8");
        InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(cityName.getWindowToken(), 0);

        DownloadTask task = new DownloadTask();
        String result;
        StringBuilder cityUrl = new StringBuilder("https://api.openweathermap.org/data/2.5/weather?q=");
        cityUrl.append(encodedCityName);
        cityUrl.append("&appid=b3e5c4db67ab3a41d2093c8a56be9295");


        result = task.execute(cityUrl.toString()).get();
        Log.i("Info", result);
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Couldn't find weather", Toast.LENGTH_SHORT).show();
        }


    }
}
