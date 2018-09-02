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
 //   protected static final int REQUEST_CHECK_SETTINGS = 0x1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);


        //========  ==========Layout Settings============= ======== ======== ========
        setTheme(android.R.style.Theme_Holo);
        android.support.v7.app.ActionBar bar = getSupportActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#0A053D")));
        getSupportActionBar().setDisplayShowHomeEnabled(true);
      //  getSupportActionBar().setLogo(R.mipmap.remindme_logo);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
    //================ =========== Layout Settings finish=========== ============== ==========


        v=findViewById(android.R.id.content);
        Task = (Button) findViewById(R.id.Task);

        final TinyDB tinydb = new TinyDB(this);
        LatitudeList = tinydb.getListDouble("LatitudesList");
        LongitudeList = tinydb.getListDouble("LongitudesList");

        /*final LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE)
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
         //   Toast.makeText(context, "GPS is Enabled in your devide", Toast.LENGTH_SHORT).show();
        }
*/

        int f=LatitudeList.size();
        for(int i=0;i<f;i++) {
            locationParce.add(new LatLng(LatitudeList.get(i), LongitudeList.get(i)));
            //   markerForGeofence(locationParce.get(i),i);
        }
        Intent intent = new Intent();
        intent.putExtra("locationParce", locationParce);
        setResult(RESULT_OK, intent);



        //2222222222222222222 222222222222222222 First button TASK 222222222222222 2222222222222222 22222222
        Task.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                final Dialog dialog = new Dialog(context);
                    dialog.setContentView(R.layout.custom);
                    dialog.setTitle("ADD TASK");
                    final EditText ed = (EditText) dialog.findViewById(R.id.edt1);
                    // Button dialogButton = (Button) dialog.findViewById(R.id.dialogButton);
                    final Button Location = (Button) dialog.findViewById(R.id.Location);
                    Button Cancel = (Button) dialog.findViewById(R.id.Cancel);
                    v = findViewById(android.R.id.content);
                    final TinyDB tinydb = new TinyDB(context);
                    TaskList = tinydb.getListString("TasksList");
                    LocationList = tinydb.getListString("LocationsList");
                    //dialog.dismiss();

          Location.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                          //  askPermission();
                           // displayLocationSettingsRequest(context);

                          ///  if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                              //  Toast.makeText(context, "GPS is Enabled in your devide", Toast.LENGTH_SHORT).show();
                                stred = ed.getText().toString();
                                if ((stred.equals(""))) {
                                    stred = "Unknown task";
                                    // Toast.makeText(context, stred, Toast.LENGTH_SHORT).show();
                                }
                            tinydb.putListString("TasksList", TaskList);
                                Bundle bundle = new Bundle();
                                bundle.putString("stred", stred);
                                Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                                intent.putExtras(bundle);
                                startActivity(intent);
                                dialog.dismiss();
                        }
                          //  else
                           // {*/

                               // Intent i=new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                               // startActivity(i);

                           // }
                        //  }
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
        //222222222222222222222 First button TASK finish 2222222222222222222222 2222222222222222222222222222222 2222222222

    }//---------onCreate finsh




    //ääaaaaaaaaaaa aaaaaaaaaaaaa Menus(3 dots) aaaaaaaaaaaaaaaaaaaaaaaa aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa aaaaaaaaaaaaaaaaaaaaaaaaa
 /*   @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.options,menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.Start:
             //   LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
             //   if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                   // Toast.makeText(context, "GPS is Enabled in your devide", Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(this,Main3Activity.class);
                    intent.putExtra("locationParce", locationParce);//sap
                    setResult(RESULT_OK, intent);
                    startActivityForResult(intent,2);
               // }
               // else
               // {
                 //  displayLocationSettingsRequest(this);
               // }
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    //ääaaaaaaaaaaaaaaaaaaaaaaaaMenus finish aaaaaaaaaaaaaaaaaaaa aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa aaaaaaaaaaaaaaaaaaaaaaaa
*/
    public void startJourney(View v)
    {
        Intent intent=new Intent(this,Main3Activity.class);
        intent.putExtra("locationParce", locationParce);//sap
        setResult(RESULT_OK, intent);
        startActivityForResult(intent,2);
    }


    //[[[[[[[[[[[[[[[[[[[[[ [[[[[[[[[[[[[[[[[[[[Show Reminders]]]]]]]]]]]]]]]]]]]]]]]]]]]]]] ]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]
    public void storage(View v)
    {
        Intent intent=new Intent(this,Main2Activity.class);
        startActivityForResult(intent,1);
    }
    //[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[Show Reminders finish]]]]]]]]]]]]]]]]]]]] ]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]



    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 || requestCode==2) {
            if(resultCode == RESULT_OK) {
                locationParce= data.getParcelableArrayListExtra("locationParce");//sa
           //     Toast.makeText(getApplicationContext(),"Parce="+locationParce,Toast.LENGTH_SHORT).show();//s

                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("locationParce", locationParce);//spa
            }
        }
    }



//////////////////////////////////on Back Pressed////////////////////////////////// ///////////////////////////////
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
//////////////////////////////////on Back Pressed finish/////////////////////////// //////////////////////////////////////



    @Override
    public void onLocationChanged(Location location) {
    }
   /* private void displayLocationSettingsRequest(Context context) {
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API).build();
        googleApiClient.connect();

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(10000 / 2);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        Log.i(TAG, "All location settings are satisfied.");
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        Log.i(TAG, "Location settings are not satisfied. Show the user a dialog to upgrade location settings ");

                        try {
                            // Show the dialog by calling startResolutionForResult(), and check the result
                            // in onActivityResult().
                            status.startResolutionForResult(MainActivity.this, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            Log.i(TAG, "PendingIntent unable to execute request.");
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Log.i(TAG, "Location settings are inadequate, and cannot be fixed here. Dialog not created.");
                        break;
                }
            }



    });
}*/
}