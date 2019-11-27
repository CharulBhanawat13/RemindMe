package com.example.charul.reminddemo;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.model.LatLng;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements LocationListener {
    final Context context=this;
    Button Task;
    String stred;
    View v;
    ArrayList<String> TaskList =new ArrayList<String>();
    ArrayList<String> LocationList =new ArrayList<String>();
    ArrayList<Double> LatitudeList = new ArrayList<>();
    ArrayList<Double> LongitudeList = new ArrayList<>();
    ArrayList<LatLng> locationParce= new ArrayList<LatLng>();//s

    protected static final String TAG = "MainActivity";
    private static final int REQ_PERMISSION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);

        setTheme(android.R.style.Theme_Holo);
        android.support.v7.app.ActionBar bar = getSupportActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#0A053D")));
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        v=findViewById(android.R.id.content);
        Task = (Button) findViewById(R.id.Task);

        final TinyDB tinydb = new TinyDB(this);
        LatitudeList = tinydb.getListDouble("LatitudesList");
        LongitudeList = tinydb.getListDouble("LongitudesList");

        int f=LatitudeList.size();
        for(int i=0;i<f;i++) {
            locationParce.add(new LatLng(LatitudeList.get(i), LongitudeList.get(i)));
        }
        Intent intent = new Intent();
        intent.putExtra("locationParce", locationParce);
        setResult(RESULT_OK, intent);

        Task.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                final Dialog dialog = new Dialog(context);
                    dialog.setContentView(R.layout.custom);
                    dialog.setTitle("ADD TASK");
                    final EditText ed = (EditText) dialog.findViewById(R.id.edt1);
                    final Button Location = (Button) dialog.findViewById(R.id.Location);
                    Button Cancel = (Button) dialog.findViewById(R.id.Cancel);
                    v = findViewById(android.R.id.content);
                    final TinyDB tinydb = new TinyDB(context);
                    TaskList = tinydb.getListString("TasksList");
                    LocationList = tinydb.getListString("LocationsList");

          Location.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                                stred = ed.getText().toString();
                                if ((stred.equals(""))) {
                                    stred = "Unknown task";
                                }
                            tinydb.putListString("TasksList", TaskList);
                                Bundle bundle = new Bundle();
                                bundle.putString("stred", stred);
                                Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                                intent.putExtras(bundle);
                                startActivity(intent);
                                dialog.dismiss();
                        }

                    });
                    Cancel.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                    dialog.show();
                }
             });

    }

    public void startJourney(View v)
    {
        Intent intent=new Intent(this,Main3Activity.class);
        intent.putExtra("locationParce", locationParce);//sap
        setResult(RESULT_OK, intent);
        startActivityForResult(intent,2);
    }


    public void storage(View v)
    {
        Intent intent=new Intent(this,Main2Activity.class);
        startActivityForResult(intent,1);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 || requestCode==2) {
            if(resultCode == RESULT_OK) {
                locationParce= data.getParcelableArrayListExtra("locationParce");//sa
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("locationParce", locationParce);//spa
            }
        }
    }
    int backpress=0;
    @Override
    public void onBackPressed() {
        if (backpress<1) {
            backpress = (backpress + 1);
           Toast.makeText(getApplicationContext(), " Press Back again to Exit ", Toast.LENGTH_SHORT).show();
        }
        else
            MainActivity.super.onBackPressed();
    }



    @Override
    public void onLocationChanged(Location location) {
    }

}