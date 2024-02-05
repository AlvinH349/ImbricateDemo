package com.imbricateDemo.calandertest2;

//import androidx.annotation.NonNull;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
//import android.os.AsyncTask;
import android.os.Bundle;
//import android.util.Log;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
//import com.squareup.picasso.Picasso;

//import org.json.JSONArray;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

//import java.io.IOException;
//import java.io.InputStream;
//import java.net.MalformedURLException;

public class weatherActivity3 extends AppCompatActivity {

    private static final String API_KEY = "67db4b3ebdaa27988e02c4c9f9bd9d18";

    final String WEATHER_URL = "https://api.openweathermap.org/data/2.5/weather";

    final long MIN_TIME = 5000;
    final float MIN_DISTANCE = 1000;
    final int REQUEST_CODE = 101;

    String Location_Provider = LocationManager.GPS_PROVIDER;

    LocationManager mLocationManager;
    LocationListener mLocationListner;


    Button btnSearch;
    EditText etCityName;
    ImageView iconWeather;
    TextView tvTemp, tvCity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather3);

        //getSupportActionBar().hide();

        btnSearch = findViewById(R.id.btnSearch);
        etCityName = findViewById(R.id.etCityName);
        iconWeather = findViewById(R.id.iconWeather);
        tvTemp = findViewById(R.id.tvTemp);
        tvCity = findViewById(R.id.tvCity);


        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String city = etCityName.getText().toString();
                if(city.isEmpty()){
                    Toast.makeText(weatherActivity3.this, "Please enter a city name", Toast.LENGTH_SHORT).show();
                  //  getWeatherForCurrentLocation();
                }
                else{
                    //TODO : Load weather by city name !
                    loadWeatherByCityName(city);
                } //close else
            } //close onClick
        });


    }//close onCreate function



    @Override
    protected void onResume() {
        super.onResume();
        getWeatherForCurrentLocation();
    }




    private void getWeatherForCurrentLocation() {

        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mLocationListner = new LocationListener() {

            @Override
            public void onLocationChanged(Location location) {

                String Latitude = String.valueOf(location.getLatitude());
                String Longitude = String.valueOf(location.getLongitude());

                RequestParams params =new RequestParams();
                params.put("lat" ,Latitude);
                params.put("lon",Longitude);
                params.put("appid",API_KEY);
                letsdoSomeNetworking(params);

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {
                //not able to get location
            }


        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_CODE);
            return;
        }
        mLocationManager.requestLocationUpdates(Location_Provider, MIN_TIME, MIN_DISTANCE, mLocationListner);
    }





    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);


        if(requestCode==REQUEST_CODE)
        {
            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(weatherActivity3.this,"Location get Succesfully",Toast.LENGTH_SHORT).show();
                getWeatherForCurrentLocation();
            }
            else
            {
                //user denied the permission
            }
        }


    }


    private  void letsdoSomeNetworking(RequestParams params) {
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(WEATHER_URL, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                Toast.makeText(weatherActivity3.this, "Data Get Success", Toast.LENGTH_SHORT).show();

                weatherData weatherD = weatherData.fromJson(response);
                updateUI(weatherD);


                // super.onSuccess(statusCode, headers, response);
            }


            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                //super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });

    }


    private  void updateUI(weatherData weather){
        tvTemp.setText(weather.getmTemperature());
        tvCity.setText(weather.getMcity());
       // weatherState.setText(weather.getmWeatherType());
        int resourceID=getResources().getIdentifier(weather.getMicon(),"drawable",getPackageName());
        iconWeather.setImageResource(resourceID);

    }




    @Override
    protected void onPause() {
        super.onPause();
        if(mLocationManager!=null)
        {
            mLocationManager.removeUpdates(mLocationListner);
        }
    }






    private void loadWeatherByCityName(String city){




        String url = "https://api.openweathermap.org/data/2.5/weather?q="+city+"&&units=metric&appid="+API_KEY;
       // String url = "https://api.openweathermap.org/data/2.5/weather?q="+city+"&appid="+API_KEY;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try{
                            JSONObject jsonObject = new JSONObject(response);

                            //find country
                            JSONObject object1 = jsonObject.getJSONObject("sys");
                            String country_find = object1.getString("country");

                            //find city
                            String city_find = jsonObject.getString("name");

                            //set city and country
                            String cc = city_find + ", " + country_find;
                            tvCity.setText(cc);

                            //find temp
                            JSONObject object2 = jsonObject.getJSONObject("main");
                       //     double tempResult = object2.getDouble("temp")-273.15;
                       //     int roundedTemp=(int)Math.rint(tempResult);
                            int roundedTemp=(int)object2.getDouble("temp");
                            tvTemp.setText(roundedTemp + " C");

                            //find image icon
                       //     JSONArray jsonArray = jsonObject.getJSONArray("weather");
                       //     JSONObject jsonObject1 = jsonArray.getJSONObject(0);
                       //     String img = jsonObject1.getString("icon");



                          //  Picasso.get().load("http://openweathermap.org/img/wn/"+img+"@2x.png").into(iconWeather);
                        //    Picasso.get().load("http://openweathermap.org/img/w/"+img+".png").into(iconWeather);


                        //    String imageURL = "http://openweathermap.org/img/w/"+img+".png";
                         //   LoadImage loadImage = new LoadImage(iconWeather);
                         //   loadImage.execute(imageURL);

                            //find image icon 2
                            //int conditionID = jsonObject.getJSONArray("weather").getJSONObject(0).getInt("id");
                            //String conditionName = updateWeatherIcon(conditionID);
                            String conditionN = jsonObject.getJSONArray("weather").getJSONObject(0).getString("main");
                            String conditionName = updateWeatherIcon2(conditionN);
                            int resourceID=getResources().getIdentifier(conditionName ,"drawable",getPackageName());
                            iconWeather.setImageResource(resourceID);

                            //JSONObject  coord = jsonObject.getJSONArray("coord")
                            JSONObject coord = jsonObject.getJSONObject("coord");
                            double lon = coord.getDouble("lon");
                            double lat = coord.getDouble("lat");
                          //  loadDailyForecast(lon, lat);

                        } catch(JSONException e){
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(weatherActivity3.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(weatherActivity3.this);
        requestQueue.add(stringRequest);


    } //close loadWeatherByCityName function



    private void loadDailyForecast(double lon, double lat){

        String url = "https://api.openweathermap.org/data/3.0/onecall?lat="+lat+"&lon="+lat+"&exclude=hourly,minutely,current&&units=metric&appid="+API_KEY;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try{
                            JSONObject jsonObject = new JSONObject(response);
                            List<Weather> weatherList = new ArrayList<>();
                            String timeZone = jsonObject.getString("timezone");
                            JSONArray daily = jsonObject.getJSONArray("daily");

                            for(int i= 1; i <daily.length();i++){

                        //        Long date = daily.get(i).;
                                  String data = daily.get(i).toString();
                                  Log.d("TAG",data);

                                  Long date = Long.parseLong("1615572000");
                                  Double temp = 23.4;
                                  String icon = "01d";
                                  weatherList.add( new Weather(date, timeZone, temp, icon) );

                            }


                        } catch(JSONException e){
                            e.printStackTrace();
                            Toast.makeText(weatherActivity3.this,"Server error",Toast.LENGTH_SHORT).show();
                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(weatherActivity3.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(weatherActivity3.this);
        requestQueue.add(stringRequest);

    }



    /*
    private class LoadImage extends AsyncTask<String,Void, Bitmap> {

        private final ImageView iamgeView;


        public LoadImage(ImageView ivResult){
            this.iamgeView = ivResult;
        }

        @Override
        protected Bitmap doInBackground(String... strings) {
            String urlLink = strings[0];
            Bitmap bitmap = null;

            try{
                InputStream inputStream = new java.net.URL(urlLink).openStream();
                bitmap = BitmapFactory.decodeStream(inputStream);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            iconWeather.setImageBitmap(bitmap);
        }
    }

     */

    /*
    private static String updateWeatherIcon(int condition)
    {
        if(condition>=0 && condition<=300)
        {
            return "thunderstrom1";
        }
        else if(condition>=300 && condition<=500)
        {
            return "lightrain";
        }
        else if(condition>=500 && condition<=600)
        {
            return "shower";
        }
        else  if(condition>=600 && condition<=700)
        {
            return "snow2";
        }
        else if(condition>=701 && condition<=771)
        {
            return "fog";
        }

        else if(condition>=772 && condition<=800)
        {
            return "overcast";
        }
        else if(condition==800)
        {
            return "sunny";
        }
        else if(condition>=801 && condition<=804)
        {
            return "cloudy";
        }
        else  if(condition>=900 && condition<=902)
        {
            return "thunderstrom1";
        }
        if(condition==903)
        {
            return "snow1";
        }
        if(condition==904)
        {
            return "sunny";
        }
        if(condition>=905 && condition<=1000)
        {
            return "thunderstrom2";
        }

        return "dunno";


    }
    */


    private static String updateWeatherIcon2(String condition)
    {
        if(condition.equals("Thunderstorm"))
        {
            return "thunderstrom1";
        }
        else if( condition.equals("Drizzle") ||  condition.equals("Rain") ){
            return "lightrain";
        }
        else if( condition.equals("Snow") ){
            return "snow1";
        }
        else if( condition.equals("Fog") ){
            return "fog";
        }
        else if( condition.equals("Clear") ){
            return "sunny";
        }
        else if( condition.equals("Clouds") ){
            return "cloudy";
        }

        return "finding";
    }





    public void backCal(View view) {
        startActivity(new Intent(this, MainActivity.class));
    }


}