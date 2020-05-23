package com.adminturnos;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.adminturnos.Builder.BuilderServiceProvider;
import com.adminturnos.Database.PostgreSQL;
import com.adminturnos.Listeners.ListenerDatabase;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        PostgreSQL.getInstance().execute(
                new ListenerDatabaseImp(),
                String.format("select * from \"newServiceProvider\"(%1$s, %2$s)", "Ezequiel Giménez", "eze.gimenez.98@gmail.com"));
    }

    public class ListenerDatabaseImp implements ListenerDatabase {

        @Override
        public void onSuccess(String resultSet) {

            // Building Service Provider from database
            Log.e("AAA",
                    new BuilderServiceProvider()
                            .build(resultSet)
                            .toString());
        }

        @Override
        public void onFailure(String error) {
            Log.e("AAA",
                    error);
        }
    }
}
