package info.anth.location2;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.Calendar;
import java.util.Date;

import info.anth.location2.Data.Stone;

public class AddStone extends AppCompatActivity {

    public static final String LOG_TAG = ObtainGPSDataService.class.getSimpleName();
    private Firebase mFirebaseRef;
    private Firebase stoneRef;
    private ValueEventListener valueEventListener;
    private String stoneID;
    private Boolean changed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_stone);

        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);

        CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        if (appBarLayout != null) {
            appBarLayout.setTitle(getString(R.string.title_add_stone));
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Updating Location", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                findLocation();
            }
        });

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mFirebaseRef = new Firebase(getResources().getString(R.string.FIREBASE_URL)).child("stone");
        Firebase pushRef = mFirebaseRef.push();
        Stone newStone = new Stone("", "", "intial insert", "", 0.0, 0.0, 0.0, 0.0, 0L, false, 0);
        pushRef.setValue(newStone);
        stoneID = pushRef.getKey();
        stoneRef = mFirebaseRef.child(stoneID);

        getDatabase();
    }

    private void getDatabase() {
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Stone thisStone = snapshot.getValue(Stone.class);
                refreshScreen(thisStone);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.e(LOG_TAG, "The read failed: " + firebaseError.getMessage());
            }
        };

        stoneRef.addValueEventListener(valueEventListener);
    }

    private void refreshScreen(Stone thisStone) {
        TextView longitudeTextView = (TextView) findViewById(R.id.longitude);
        longitudeTextView.setText(String.valueOf(thisStone.getLongitude()));
        TextView latitudeTextView = (TextView) findViewById(R.id.latitude);
        latitudeTextView.setText(String.valueOf(thisStone.getLatitude()));
        EditText name = (EditText) findViewById(R.id.location_name);
        name.setText(thisStone.getMethod());

        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setProgress(thisStone.getProgressGPS());
    }

    /*
    // On location change
    @Override
    public void onLocationChanged(Location location) {
        Log.i("MainActivity", "onLocationChanged count: " + currentCount + " Accuracy: " + String.valueOf(location.getAccuracy()));

        currentCheck++;
        if(bestAccuracy >= location.getAccuracy()) {
            bestAccuracy = location.getAccuracy();
            bestLatitude = location.getLatitude();
            bestLongitude = location.getLongitude();
            bestAltitude = location.getAltitude();
        }

        if (lastLatitude == location.getLatitude() && lastLongitude == location.getLongitude()) {
            currentCount++;
        } else {
            lastLatitude = location.getLatitude();
            lastLongitude = location.getLongitude();
            currentCount = 0;
        }

            if (countInRow == currentCount) {
                stopLocationUpdates();
                //set longitude and latitude
                TextView longitudeTextView = (TextView) findViewById(R.id.longitude);
                longitudeTextView.setText(String.valueOf(lastLongitude));
                TextView latitudeTextView = (TextView) findViewById(R.id.latitude);
                latitudeTextView.setText(String.valueOf(lastLatitude));
                EditText name = (EditText) findViewById(R.id.location_name);

                // Setup our Firebase mFirebaseRef
                String deviceModel = Build.MANUFACTURER + " : " + Build.MODEL;
                String deviceOS = "Android OS: " + Build.VERSION.RELEASE + " : sdk=" + String.valueOf(Build.VERSION.SDK_INT);
                String method = "Consistency";
                Long seconds = (new Date().getTime() - startDate.getTime())/1000;

                Stone newStone = new Stone(deviceModel, deviceOS, method, location.getProvider(), location.getLongitude(), location.getLatitude(), location.getAccuracy(),
                        location.getAltitude(), seconds, false);
                mFirebaseRef.push().setValue(newStone);
                //locationDb = new DBHelper(this);
                //locationDb.insertLocation(name.getText().toString(), lastLongitude, lastLatitude, "Accuracy: " + String.valueOf(location.getAccuracy()));

                Toast toast = Toast.makeText(this, "Saved", Toast.LENGTH_LONG);
                toast.show();
            }
         else if (currentCheck >= maxChecks && currentCount == 0) {
        stopLocationUpdates();
        //set longitude and latitude
        TextView longitudeTextView = (TextView) findViewById(R.id.longitude);
        longitudeTextView.setText(String.valueOf(bestLongitude));
        TextView latitudeTextView = (TextView) findViewById(R.id.latitude);
        latitudeTextView.setText(String.valueOf(bestLatitude));
        EditText name = (EditText) findViewById(R.id.location_name);

                String deviceModel = Build.MANUFACTURER + " : " + Build.MODEL;
                String deviceOS = "Android OS: " + Build.VERSION.RELEASE + " : sdk=" + String.valueOf(Build.VERSION.SDK_INT);
                String method = "Accuracy";
                Long seconds = (new Date().getTime() - startDate.getTime())/1000;

                Stone newStone = new Stone(deviceModel, deviceOS, method, location.getProvider(), bestLongitude, bestLatitude, bestAccuracy,
                        bestAltitude, seconds, false);
                mFirebaseRef.push().setValue(newStone);
        //locationDb = new DBHelper(this);
        //locationDb.insertLocation(name.getText().toString(), lastLongitude, lastLatitude, "Accuracy Saved \nAccuracy: " + String.valueOf(bestAccuracy));

        Toast toast = Toast.makeText(this, "Saved", Toast.LENGTH_LONG);
        toast.show();
    }
    }
    */


    protected void findLocation() {

        changed = true;
        Intent myIntent = new Intent(this, ObtainGPSDataService.class);
        myIntent.putExtra(ObtainGPSDataService.REQUEST_REF, mFirebaseRef.child(stoneID).getRef().toString());
        startService(myIntent);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stoneRef.removeEventListener(valueEventListener);
        if (!changed) {
            stoneRef.removeValue();
        }
    }
}
