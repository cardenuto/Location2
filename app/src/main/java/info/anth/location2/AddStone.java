package info.anth.location2;

import android.Manifest;
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
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.Calendar;
import java.util.Date;

import info.anth.location2.Data.Stone;

public class AddStone extends AppCompatActivity
        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

    /**
     * Provides the entry point to Google Play services.
     */
    protected GoogleApiClient mGoogleApiClient;
    /**
     * Represents a geographical location.
     */
    protected Location mLastLocation;
    /**
     * Stores parameters for requests to the FusedLocationProviderApi.
     */
    protected LocationRequest mLocationRequest;
    /**
     * The desired interval for location updates. Inexact. Updates may be more or less frequent.
     */
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    /**
     * The fastest rate for active location updates. Exact. Updates will never be more frequent
     * than this value.
     */
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    private static double lastLongitude;
    private static double lastLatitude;
    private static int countInRow = 3;
    private static int currentCount;

    private static int maxChecks = 5;
    private static int currentCheck;

    private static float bestAccuracy;
    private static double bestLongitude;
    private static double bestLatitude;
    private static double bestAltitude;

    private static Date startDate;

    private Firebase mFirebaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_stone);

        //Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        //setSupportActionBar(toolbar);

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

        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        mFirebaseRef = new Firebase(getResources().getString(R.string.FIREBASE_URL)).child("stone");

    }
    /**
     * GoogleApiClient.ConnectionCallbacks abstact method
     * Runs when a GoogleApiClient object successfully connects.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        Log.i("MainActivity", "Connection established");
        //localGetLocationOld();
    }

    /**
     * GoogleApiClient.ConnectionCallbacks abstact method
     * Runs when a GoogleApiClient object is temporarily in a disconnected state.
     */
    @Override
    public void onConnectionSuspended(int cause) {
        Log.i("MainActivity", "Connection suspended");
    }

    /**
     * GoogleApiClient.OnConnectionFailedListener abstact method
     * Runs when there is an error connection the client to the service.
     */
    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i("MainActivity", "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }

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
/*
    Stone(String deviceModel, String deviceOS, String provider,
          Double longitude, Double latitude, Float accuracy,
          Double altitude, Long secondsToGPS, Boolean processed)
 */
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

    @Override
    protected void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected()) {
            stopLocationUpdates();
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    protected void findLocation() {
        // initalize the variables
        lastLongitude = 0;
        lastLatitude = 0;
        currentCount = 0;
        currentCheck = 0;
        bestAccuracy = 1000;
        bestLongitude = 0;
        bestLatitude = 0;
        bestAltitude = 0;
        startDate = new Date();

        // create a location request and start updating location data
        createLocationRequest();
        startLocationUpdates();
    }
    // setup location request
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();

        // Sets the desired interval for active location updates. This interval is
        // inexact. You may not receive updates at all if no location sources are available, or
        // you may receive them slower than requested. You may also receive updates faster than
        // requested if other applications are requesting location at a faster interval.
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);

        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    // Location updates started
    protected void startLocationUpdates() {
        Log.i("MainActivity", "startLocationUpdates");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Log.i("MainActivity", "Access denied to call");
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }

    // location updates stopped
    protected void stopLocationUpdates() {
        Log.i("MainActivity", "stopLocationUpdates");
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
    }
}
