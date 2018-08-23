package com.example.tryloginws;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    EditText etEmail,etPassword;
    RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);

        queue = Volley.newRequestQueue(this);
    }

    public void login(View view) {
        final String email = etEmail.getText().toString();
        final String password = etPassword.getText().toString();

        //definisikan http req anda
        String url = "http://192.168.43.153/cobaphp/doWSLogin.php";

        StringRequest loginRequest = new StringRequest(
                Request.Method.POST,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject objResponse = null;

                        try {
                            objResponse = new JSONObject(response);
                            Toast.makeText(MainActivity.this, objResponse.getString("hasil"), Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> loginParam = new HashMap<>();
                loginParam.put("iEmail",email);
                loginParam.put("iPassword",password);
                return loginParam;
            }

            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }
        };

        queue.add(loginRequest);
    }

    public void getDirection(View view) {
        String address = "jl. pelepah indah 2 blok LA 24 no. 24";

        try {
            String addressParam = URLEncoder.encode(address, "UTF-8");
            Uri gmmIntentUri = Uri.parse("google.navigation:q=" + addressParam);
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            startActivity(mapIntent);
        }catch(Exception e){}
    }

    public void showMapAPI(View view) {
        Intent goMap = new Intent(this,MapsActivity.class);
        startActivity(goMap);
    }

    public void getUsers(View view) {
        //definisikan http req anda
        String url = "http://192.168.43.153/cobaphp/doWSGetUsers.php";

        JsonArrayRequest getUsersReq = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        for(int i=0;i<response.length();i++){
                            try {
                                Log.v("hasiljsonarray", response.get(i).toString());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        );

        queue.add(getUsersReq);
    }
}
