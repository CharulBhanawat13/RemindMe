package com.example.charul.reminddemo;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.ActionProvider;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class Main2Activity extends AppCompatActivity {
    int t, l;
    View v;

    ArrayList<String> TaskList = new ArrayList<String>();
    ArrayList<String> LocationList = new ArrayList<String>();
    ArrayList<Double> LatitudeList = new ArrayList<>();
    ArrayList<Double> LongitudeList = new ArrayList<>();
    ArrayList<LatLng> locationParce = new ArrayList();
    CheckBox[] cb = new CheckBox[100];
    TextView[] txtLat = new TextView[100];
    TextView[] txtLon = new TextView[100];
    int count = 0;
    LinearLayout ll;
    ShareActionProvider mShareActionProvider;


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.share:
                share();
                return true;
            case R.id.delete:
                delete();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sharee, menu);


        MenuItem item = menu.findItem(R.id.share);
        if (1 == 1) {
            item.setVisible(true);
        }
        return true;
    }

    //For implementing the back button in action bar
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);//1793695812
        final ScrollView sv = new ScrollView(this);

        v = findViewById(android.R.id.content);
        ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.VERTICAL);
        sv.addView(ll);
        setTitle("Your To-DO List");
        sv.setBackgroundResource(R.drawable.crush);
        ActionBar bar = getSupportActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#0A053D")));
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        getSupportActionBar().setDisplayUseLogoEnabled(true);

        bar.setDisplayHomeAsUpEnabled(true);

        TinyDB tinydb = new TinyDB(getApplicationContext());
        TaskList.clear();
        LocationList.clear();


        TaskList = tinydb.getListString("TasksList");
        LocationList = tinydb.getListString("LocationsList");
        LatitudeList = tinydb.getListDouble("LatitudesList");
        LongitudeList = tinydb.getListDouble("LongitudesList");

        Log.d("Val=", +t + "," + l);
        t = TaskList.size();
        l = LocationList.size();
        int lat = LatitudeList.size();
        int lon = LongitudeList.size();

        Intent intent = new Intent();
        intent.putExtra("locationParce", locationParce);
        setResult(RESULT_OK, intent);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(175, 40, 0, 0);

        for (int i = 0; i < t && i < l && i < lat && i < lon; i++) {
            cb[i] = new CheckBox(this);
            cb[i].setText(TaskList.get(i) + " From " + LocationList.get(i));

            ll.addView(cb[i], params);
            cb[i].setId(i);
            txtLat[i] = new TextView(this);
            txtLat[i].setText("Lat= " + (LatitudeList.get(i)).toString());
            txtLon[i] = new TextView(this);
            txtLon[i].setText("Long= " + (LongitudeList.get(i)).toString());
            ll.addView(txtLat[i], params);
            ll.addView(txtLon[i], params);


            locationParce.add(new LatLng(LatitudeList.get(i), LongitudeList.get(i)));
            count++;
        }

        this.setContentView(sv);
    }

    public void delete() {
        int flag = 0;
        if (count == 0) {
            AlertDialog alertDialog = new AlertDialog.Builder(Main2Activity.this).create();
            alertDialog.setTitle("Alert Dialog");
            alertDialog.setMessage("No items to delete.");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
            flag = 0;
            return;
        }
        for (int i = 0; i < count; i++)
            if (cb[i].isChecked()) {
                flag++;
            }
        if (flag == 0 && count != 0) {
            AlertDialog alertDialog = new AlertDialog.Builder(Main2Activity.this).create();
            alertDialog.setTitle("Alert Dialog");
            alertDialog.setMessage("No items selected");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();

        } else {
            //Standard Dialog
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(Main2Activity.this);
            alertBuilder.setTitle("Confirmation Message");
            alertBuilder.setMessage("Are you Sure to delete?");
            alertBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    for (int i = count - 1; i >= 0; i--) {
                        if (cb[i].isChecked()) {
                            ll.removeView(cb[i]);
                            ll.removeView(txtLat[i]);
                            ll.removeView(txtLon[i]);
                            TaskList.remove(i);
                            LocationList.remove(i);
                            LatitudeList.remove(i);
                            LongitudeList.remove(i);

                            TinyDB tinydb = new TinyDB(getApplicationContext());
                            tinydb.putListString("TasksList", TaskList);
                            tinydb.putListString("LocationsList", LocationList);
                            tinydb.putListDouble("LatitudesList", LatitudeList);
                            tinydb.putListDouble("LongitudesList", LongitudeList);
                        }
                    }

                    Intent mIntent = getIntent();
                    finish();
                    startActivity(mIntent);
                }

            });

            alertBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            alertBuilder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            AlertDialog ad = alertBuilder.create();
            ad.show();
        }

    }

    void share() {
        if (count == 0) {
            AlertDialog alertDialog = new AlertDialog.Builder(Main2Activity.this).create();
            alertDialog.setTitle("Alert Dialog");
            alertDialog.setMessage("No items to share");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
        }

        int flag = 0;
        StringBuilder message = new StringBuilder();
        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        message.append("This message is from application under testing(AUT).Application is in the stage of maturity.PLEASE IGNORE \n");
        for (int i = 0; i < count; i++) {
            if (cb[i].isChecked()) {
                flag++;
                message.append("TASK "+(i+1)+": "+TaskList.get(i));
                message.append(" FROM ");
                message.append(LocationList.get(i)+"\n\n");
            }
        }

        //Now check whether any task is selected or not
        if (flag == 0 && count != 0) {
            AlertDialog alertDialog = new AlertDialog.Builder(Main2Activity.this).create();
            alertDialog.setTitle("Alert Dialog");
            alertDialog.setMessage("No items selected");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
        }

        //Well there are tasks in user's task list and he intends to seriously share few...
        if (flag > 0) {
            try {
                sendIntent.putExtra(Intent.EXTRA_TEXT, message.toString());
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "SMS failed, please try again later!", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }
    }
}
