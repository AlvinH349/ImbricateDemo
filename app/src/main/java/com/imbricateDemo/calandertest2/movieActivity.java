package com.imbricateDemo.calandertest2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class movieActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RequestQueue requestQueue;
    private List<Movie> movieList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);

        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        requestQueue = VolleySingleton.getmInstance(this).getRequestQueue();

        movieList = new ArrayList<>();
        fetchMovies();
    }

    private void fetchMovies() {

        String url = "https://api.themoviedb.org/3/movie/popular?api_key=5e365a1e9b7c8e162e39adade55224a4&language=en-US";

        /*
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        for (int i = 0 ; i < response.length() ; i ++){
                            try {
                                JSONObject jsonObject = response.getJSONObject(i);


                                String title = jsonObject.getString("title");
                                String overview = jsonObject.getString("overview");
                                String poster = jsonObject.getString("poster");
                                Double rating = jsonObject.getDouble("rating");

                                Movie movie = new Movie(title , poster , overview , rating);
                                movieList.add(movie);


                             //   String page = jsonObject.getString("page");
                            //    Log.d("TAG",page);
                            //    String result = jsonObject.getString("results");
                           //     Log.d("TAG",result);
                           //     String totalP = jsonObject.getString("total_pages");
                            //    Log.d("TAG",totalP);
                          //      String totalR = jsonObject.getString("total_results");
                          //      Log.d("TAG",totalR);


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                          //  MovieAdapter adapter = new MovieAdapter(movieActivity.this , movieList);

                          //  recyclerView.setAdapter(adapter);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(movieActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        requestQueue.add(jsonArrayRequest);

         */

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                JSONArray movies = null;
                try {
                    movies = (JSONArray) response.get("results");

                //    Log.d("TAG", String.valueOf(movies.length()));

                    for (int i = 0 ; i < movies.length() ; i++){
                        JSONObject jsonObject = movies.getJSONObject(i);


                        String title = jsonObject.getString("title");
                     //   Log.d("TAG",title );
                        String overview = jsonObject.getString("overview");

                        String poster = "https://image.tmdb.org/t/p/w154" + jsonObject.getString("poster_path");
                        Double rating = jsonObject.getDouble("vote_average");
                     //   Log.d("TAG", String.valueOf(rating));


                        Movie movie = new Movie(title , poster , overview , rating);
                        movieList.add(movie);
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }


                MovieAdapter adapter = new MovieAdapter(movieActivity.this , movieList);

                recyclerView.setAdapter(adapter);


            }
        }, new Response.ErrorListener() {
            // this is the error listener method which
            // we will call if we get any error from API.
            @Override
            public void onErrorResponse(VolleyError error) {
                // below line is use to display a toast message along with our error.
                Toast.makeText(movieActivity.this, "Fail to get data..", Toast.LENGTH_SHORT).show();
            }
        });
        // at last we are adding our json
        // object request to our request
        // queue to fetch all the json data.
        //queue.add(jsonObjectRequest);
        requestQueue.add(jsonObjectRequest);


    }






        public void backCal(View view) {
        startActivity(new Intent(this, MainActivity.class) );
    }

}