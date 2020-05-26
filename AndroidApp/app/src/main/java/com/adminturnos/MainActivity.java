package com.adminturnos;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.adminturnos.Database.AccessToken;
import com.adminturnos.Database.ReadPostgreSQL;
import com.adminturnos.Exceptions.ExceptionEmailInUse;
import com.adminturnos.Listeners.ListenerUserManagement;
import com.adminturnos.ObjectInterfaces.ServiceProvider;
import com.adminturnos.UserManagment.SignUp;
import com.adminturnos.UserManagment.SignUpGoogle;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    SignUp signUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        signUp = new SignUpGoogle(this);
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    signUp.signUp(new ListenerUserManagement() {
                        @Override
                        public void onComplete(ServiceProvider e) {
                            Log.e("aSD", "asdsd");

                            AccessToken.getInstance().getAccessToken();
                            RequestBody requestBody = new FormEncodingBuilder()
                                    .build();

                            String url = "http://192.168.1.33:8000/test/";
                            ReadPostgreSQL.getInstance().GET(url, requestBody, new Callback() {
                                @Override
                                public void onFailure(final Request request, final IOException e) {
                                    Log.e("AAAAA", e.toString());
                                }

                                @Override
                                public void onResponse(Response response) throws IOException {
                                    try {
                                        if(response.code() == 200) {
                                            JSONObject jsonObject = new JSONObject(response.body().string());
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });

                        }

                        @Override
                        public void onFailure(String message) {
                            Log.e("AAAAA", message);
                        }
                    });
                } catch (ExceptionEmailInUse exceptionEmailInUse) {
                    exceptionEmailInUse.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        signUp.onActivityResult(requestCode, data);
    }
}
