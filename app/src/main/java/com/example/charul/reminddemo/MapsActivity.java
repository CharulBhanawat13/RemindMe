package com.example.charul.reminddemo;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentSender;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.example.charul.reminddemo.R.layout.action_view;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener,GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener  {
    private GoogleMap mMap;
    boolean isGPSEnabled = false;
    boolean isNetworkEnabled = false;
    boolean canGetLocation = false;
    private static final String TAG = MapsActivity.class.getSimpleName();
    boolean flag = false;
    Location my = new Location(LocationManager.NETWORK_PROVIDER);
   // private static final int REQ_PERMISSION = 1;
    ArrayList<String> LocationList = new ArrayList<String>();

    ArrayList<Double> LatitudeList = new ArrayList<>();
    ArrayList<Double> LongitudeList = new ArrayList<>();
    ArrayList<String> TaskList = new ArrayList<String>();
    private static final int PERMISSION_ACCESS_COARSE_LOCATION = 1;
    private GoogleApiClient googleApiClient;
    private LocationManager locationManager;
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;
    int b;
    private Marker geoFenceMarker;
    String result;
    TextView text;
    boolean conn;
    EditText locationSearch;
    ArrayList<Marker> mark_list = new ArrayList<Marker>();
    ImageButton search_icon;
   // LinearLayout line1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        final SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
           // Toast.makeText(this,"Success",Toast.LENGTH_LONG).show();
        }



        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.ACCESS_COARSE_LOCATION },
                    PERMISSION_ACCESS_COARSE_LOCATION);
        }

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);


        text = (TextView) findViewById(R.id.text);


        //Setting up the action bar
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#0A053D")));
        actionBar.setDisplayShowCustomEnabled(true);
        setTitle("");
        LayoutInflater inflator = (LayoutInflater) this .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflator.inflate(action_view, null);
        actionBar.setCustomView(v);
        locationSearch = (EditText) actionBar.getCustomView().findViewById(
                R.id.locationSearch);
        search_icon=(ImageButton)actionBar.getCustomView().findViewById(R.id.search_icon);

        displayLocationSettingsRequest(this);

        //========Checking for internet connectivity==============================================
       conn=  checkConnection(this);
        if(conn)
        {
          //  Toast.makeText(this,"Connected ",Toast.LENGTH_LONG).show();
        }
        else
        {
            AlertDialog.Builder builder =new AlertDialog.Builder(this);
            builder.setTitle("No internet Connection");
            builder.setMessage("Please turn on internet connection to continue");
            builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
        //================finish checking internet connectivity==========================================


        LocationList.clear();
        LatitudeList.clear();
        LongitudeList.clear();
        TaskList.clear();
        final TinyDB tinydb = new TinyDB(this);

        LatitudeList = tinydb.getListDouble("LatitudesList");
        LongitudeList = tinydb.getListDouble("LongitudesList");
        LocationList = tinydb.getListString("LocationsList");
        TaskList = tinydb.getListString("TasksList");

        LocationListener listener = new LocationListener() {

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onProviderEnabled(String provider) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onProviderDisabled(String provider) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onLocationChanged(Location location) {
                // TODO Auto-generated method stub

                if (location != null) {
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                  //  Toast.makeText(getBaseContext(), "l=" + latitude + ",L=" + longitude, Toast.LENGTH_SHORT).show();
                }
            }
        };

        /////////////////////////////////////////////////Checking GPS and Network//////////////////////////////////////////////////////////////////
       /* if (!isGPSEnabled && !isNetworkEnabled) {
            Toast.makeText(this, "No network available", Toast.LENGTH_SHORT).show();

        } else {
            this.canGetLocation = true;
            if (isGPSEnabled) {
                if (location == null) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 60000, 3, listener);
                    // Toast.makeText(this, "GPS available", Toast.LENGTH_SHORT).show();
                    if (locationManager != null) {
                        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                            return;
                        }
                        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    }
                }
            }
            if (isNetworkEnabled) {
                //  Toast.makeText(this, " network available", Toast.LENGTH_SHORT).show();
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 60000, 3, listener);
                if (locationManager != null) {
                    location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                }
            }
        }*/
////////////////////////////////////////////////////////////finish Checking GPs nd Network//////////////////////////////////////////////////////////////////

search_icon.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
       search_location_func();
    }
});

        locationSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    Log.i(TAG,"Enter pressed");
                    search_location_func();

                }
                return false;
            }
        });

    }// oncreate finish


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Toast.makeText(this, "onRequestIermissione", Toast.LENGTH_SHORT).show();
        switch (requestCode) {
            case PERMISSION_ACCESS_COARSE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // All good!
                } else {
                    Toast.makeText(this, "Need your location!", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }


        protected void onStop() {
        googleApiClient.disconnect();
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (googleApiClient != null) {

            googleApiClient.connect();
        }
    }

    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        setUpMap();
        mMap.setOnMapClickListener(this);
        mMap.setOnMapLongClickListener(this);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(MapsActivity.class.getSimpleName(), "Connected to Google Play Services!");

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
        }
    }
    @Override
    public void onConnectionSuspended(int i) {}

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(MapsActivity.class.getSimpleName(), "Can't connect to Google Play Services!");
        Toast.makeText(this, " Can not Connected to Google play services", Toast.LENGTH_SHORT).show();

    }

    public void setUpMap() {
        // mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        // addMarkeratLocation();
    }


    //for implementing the back button in action bar
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
    @Override
    public void onMapLongClick(LatLng latLng) {
        setMarker(latLng);
        double lat=latLng.latitude;
        double lng=latLng.longitude;
        //Toast.makeText(getBaseContext(),"This is"+lat+", "+lng,Toast.LENGTH_SHORT).show();
        addressDragged(lat,lng);
    }



    public void setMarker(LatLng latLng)
    {
        MarkerOptions markerOptions = new MarkerOptions()
                .position(latLng)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)).draggable(true);
        // Remove last geoFenceMarker
        if ( geoFenceMarker != null)
            geoFenceMarker.remove();
        geoFenceMarker = mMap.addMarker(markerOptions);
        mark_list.add(geoFenceMarker);

        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {
            }
            @Override
            public void onMarkerDrag(Marker marker) {
            }
            @Override
            public void onMarkerDragEnd(Marker marker) {
                Log.d("System out", "onMarkerDragEnd...");
                mMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
                double lat=marker.getPosition().latitude;
                double lng=marker.getPosition().longitude;
                //Toast.makeText(getBaseContext(),""+lat+", "+lng,Toast.LENGTH_SHORT).show();
                addressDragged(lat,lng);
            }
        });
    }


    //-------------------------------------------For searching particular location from edittext method---------------------------------
    public void getLocationFromAddress(Context context,String strAddress) {

        Geocoder coder = new Geocoder(context);
        List<Address> address=null;
        LatLng p1 = null;
        try {
            // May throw an IOException
            address = coder.getFromLocationName(strAddress, 1);
            if (address.size()==1) {
                Address findlocation = address.get(0);
                findlocation.getLatitude();
                findlocation.getLongitude();
                p1 = new LatLng(findlocation.getLatitude(), findlocation.getLongitude() );
                float zoom = 14f;
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(p1, zoom);
                mMap.animateCamera(cameraUpdate);
                setMarker(p1);
               // addressDragged(p1.latitude,p1.longitude);
            }
            else {
                // Toast.makeText(this,"no location",Toast.LENGTH_LONG).show();
                AlertDialog alertDialog = new AlertDialog.Builder(this).create();
                alertDialog.setTitle("Alert Dialog");
                alertDialog.setMessage("No match found");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }catch (IllegalArgumentException illegalArgumentException) {
            // Catch invalid latitude or longitude values.
            Toast.makeText(this,"No match found",Toast.LENGTH_LONG).show();
        }
    }
//---------------------------------------------Finish searcging location in edittext method--------------------------------------------





  //****************************************Start Address Dragged*************************************************************************
    public void addressDragged(final double lat, final double lng){
        String addres="Check connection" ;
        Geocoder geocoder = new Geocoder(getBaseContext(), Locale.getDefault());

        try {
            List<Address> addressList = geocoder.getFromLocation(
                    lat, lng, 1);
            if (addressList != null && addressList.size() > 0) {
                addres = addressList.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                result = addres;
            }
            else  {
                result="Failed to retrieve address.";
            }
           // Toast.makeText(this,""+addressList.size(),Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Toast.makeText(this,"Unable to connect to geocoder",Toast.LENGTH_LONG).show();
            result="Unnamed Road";
        }
     /*   final Dialog dialogSave = new Dialog(this);
        dialogSave.setContentView(R.layout.saveloc);
    //    dialogSave.setTitle("Confirmation message");
        dialogSave.setTitle("Do you want to save location?");
       // TextView txt1=(TextView)dialogSave.findViewById(R.id.txt1);
        final TextView addrTxt=(TextView) dialogSave.findViewById(R.id.addrTxt);
        Button dialogButton = (Button) dialogSave.findViewById(R.id.Save);
        Button Cancel = (Button) dialogSave.findViewById(R.id.Cancel);
        addrTxt.setText(result);
        final TinyDB tinydb = new TinyDB(this);
        text.setText(result);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag=true;
                LatitudeList.add(lat);
                LongitudeList.add(lng);
                LocationList.add(result);
                Bundle bundle = getIntent().getExtras();
                String stred = bundle.getString("stred");
                TaskList.add(stred);
                tinydb.putListDouble("LatitudesList", LatitudeList);
                tinydb.putListDouble("LongitudesList", LongitudeList);
                LatitudeList = tinydb.getListDouble("LatitudesList");
                LongitudeList = tinydb.getListDouble("LongitudesList");
                tinydb.putListString("LocationsList", LocationList);
                tinydb.putListString("TasksList", TaskList);
                LocationList = tinydb.getListString("LocationsList");
                TaskList = tinydb.getListString("TasksList");
                dialogSave.dismiss();
                MapsActivity.super.onBackPressed();
            }
        });
        Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addrTxt.setText(" ");
                text.setText("");
                result="";
                dialogSave.dismiss();
            }
        });
        tinydb.putListDouble("LatitudesList", LatitudeList);
        tinydb.putListDouble("LongitudesList", LongitudeList);
        LatitudeList = tinydb.getListDouble("LatitudesList");
        LongitudeList = tinydb.getListDouble("LongitudesList");
        tinydb.putListString("LocationsList", LocationList);
        LocationList = tinydb.getListString("LocationsList");
        dialogSave.show();*/

        AlertDialog alertDialog = new AlertDialog.Builder(MapsActivity.this).create();
        alertDialog.setTitle("Do you want to save location?");
        alertDialog.setMessage(result);
        text.setText(result);
        final TinyDB tinydb = new TinyDB(this);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        flag=true;
                        LatitudeList.add(lat);
                        LongitudeList.add(lng);
                        LocationList.add(result);
                        Bundle bundle = getIntent().getExtras();
                        String stred = bundle.getString("stred");
                        TaskList.add(stred);
                        tinydb.putListDouble("LatitudesList", LatitudeList);
                        tinydb.putListDouble("LongitudesList", LongitudeList);
                        LatitudeList = tinydb.getListDouble("LatitudesList");
                        LongitudeList = tinydb.getListDouble("LongitudesList");
                        tinydb.putListString("LocationsList", LocationList);
                        tinydb.putListString("TasksList", TaskList);
                        LocationList = tinydb.getListString("LocationsList");
                        TaskList = tinydb.getListString("TasksList");
                        dialog.dismiss();
                        MapsActivity.super.onBackPressed();

                    }
                });
        /*alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        text.setText("");
                        dialog.dismiss();
                        result="";

                    }
                });*/
        alertDialog.setMessage(result);
        alertDialog.show();


    }
//****************************************Stop Address Dragged*****************************************************************************


    @Override
    public void onMapClick(LatLng latLng) {
        //line1.removeView(LinearLayout line1);
    }


    //&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&  Internet Connection Checking   &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&
    public static boolean checkConnection(Context context) {
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connMgr.getActiveNetworkInfo();
        if (activeNetworkInfo != null) { // connected to the internet
         //   Toast.makeText(context, activeNetworkInfo.getTypeName(), Toast.LENGTH_SHORT).show();
            if (activeNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                // connected to wifi
                return true;
            } else if (activeNetworkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                // connected to the mobile provider's data plan
                return true;
            }
        }
        return false;
    }
    // &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&Internet Connection Checking Over&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&


    private void displayLocationSettingsRequest(Context context) {
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
                            status.startResolutionForResult(MapsActivity.this, REQUEST_CHECK_SETTINGS);
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



    }
    //==============================================EditText Location Search function()=======================================
    public void search_location_func()
    {
        String search_location=locationSearch.getText().toString();

        if(search_location.equals("") || search_location==null ||search_location.isEmpty() || search_location.length()==0) {

            AlertDialog alertDialog = new AlertDialog.Builder(MapsActivity.this).create();
            alertDialog.setTitle("Alert Dialog");
            alertDialog.setMessage("Enter some location");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
        }
        else {
            conn=checkConnection(getBaseContext());
            if (conn) {
                getLocationFromAddress(getBaseContext(), search_location);
            }
            else
            {
                Toast.makeText(getBaseContext(),"Check Connection",Toast.LENGTH_LONG).show();

            }
        }
    }

}
