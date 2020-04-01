package com.anthonyramnarain.loginregisterapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;

import cz.msebera.android.httpclient.Header;

public class LoginActivity extends AppCompatActivity {
    EditText username, password;
    Button loginbtn;
    String u, p;
    AsyncHttpClient client;
    RequestParams params;
    String MYURL = "http://173.52.92.21:8080/LoginRegisterApp/Login";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        username = (EditText) findViewById(R.id.username_loginpage);
        password = (EditText) findViewById(R.id.password_loginpage);
        loginbtn = (Button) findViewById(R.id.Loginbtn_loginpage);

        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                u = username.getText().toString();
                p = password.getText().toString();

                params = new RequestParams();
                params.put("username", u);
                params.put("password", p);
                client = new AsyncHttpClient();
                client.post(MYURL, params, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                        super.onSuccess(statusCode, headers, response);
                        Toast.makeText(LoginActivity.this, "Login Success" + response, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        super.onFailure(statusCode, headers, responseString, throwable);
                        Toast.makeText(LoginActivity.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
    }
}
