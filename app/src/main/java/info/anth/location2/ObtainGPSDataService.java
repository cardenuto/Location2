package info.anth.location2;

import android.Manifest;
import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import info.anth.location2.Data.Stone;
import info.anth.location2.Data.StoneTBD;


public class ObtainGPSDataService extends IntentService
        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

    public static final String REQUEST_REF_STONE = "ref_stone";
    public static final String REQUEST_REF_STONETBD = "ref_stoneTBD";
    public static final String LOG_TAG = ObtainGPSDataService.class.getSimpleName();
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

    private static double bestAccuracy;
    private static double bestLongitude;
    private static double bestLatitude;
    private static double bestAltitude;

    private static Date startDate;

    private Firebase mFirebaseRef;
    private String fireRefStoneTBD;

    public ObtainGPSDataService() {
        super("ObtainGPSDataService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
/*            final String action = intent.getAction();
            if (ACTION_FOO.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionFoo(param1, param2);
            } else if (ACTION_BAZ.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionBaz(param1, param2);
            }
*/
            //try {

            final String refFirebase = intent.getStringExtra(REQUEST_REF_STONE);
            fireRefStoneTBD = intent.getStringExtra(REQUEST_REF_STONETBD);
            Log.i(LOG_TAG, refFirebase);

            /*} catch (InterruptedException e){

            }

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                // Restore interrupt status.
                Thread.currentThread().interrupt();
            }*/
            // Create an instance of GoogleAPIClient.

            if (mGoogleApiClient == null) {
                mGoogleApiClient = new GoogleApiClient.Builder(this)
                        .addConnectionCallbacks(this)
                        .addOnConnectionFailedListener(this)
                        .addApi(LocationServices.API)
                        .build();
            }
            mGoogleApiClient.connect();
            //mFirebaseRef = new Firebase(getResources().getString(R.string.FIREBASE_URL)).child("stone");
            mFirebaseRef = new Firebase(refFirebase);
        }

    }
/*
    @Override
    public void onCreate(){
        super.onCreate();
        Log.i(LOG_TAG, "onCreate ");
        if (mGoogleApiClient == null) {
            Log.i(LOG_TAG, "onCreate here ");
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
            if (mGoogleApiClient == null) {
                Log.i(LOG_TAG, "onCreate here2 ");
            }
        }
        mFirebaseRef = new Firebase(getResources().getString(R.string.FIREBASE_URL)).child("stone");
    }*/
    /**
     * GoogleApiClient.ConnectionCallbacks abstact method
     * Runs when a GoogleApiClient object successfully connects.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        Log.i(LOG_TAG, "Connection established");
        findLocation();
        //localGetLocationOld();
    }

    /**
     * GoogleApiClient.ConnectionCallbacks abstact method
     * Runs when a GoogleApiClient object is temporarily in a disconnected state.
     */
    @Override
    public void onConnectionSuspended(int cause) {
        Log.i(LOG_TAG, "Connection suspended");
    }

    /**
     * GoogleApiClient.OnConnectionFailedListener abstact method
     * Runs when there is an error connection the client to the service.
     */
    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i(LOG_TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }

    // On location change
    @Override
    public void onLocationChanged(Location location) {
        Log.i(LOG_TAG, "onLocationChanged count: " + currentCount + " Accuracy: " + String.valueOf(location.getAccuracy()));

        currentCheck++;
        int progress;

        if(currentCheck >= maxChecks) {
            progress = 90;
        } else {
            progress = (int) Math.round(((double) currentCheck / (double) maxChecks) * 100.0);
        }

        Log.i(LOG_TAG, "progress: " + String.valueOf(progress) + " currentCheck: " + String.valueOf(currentCheck) + " maxChecks: " + String.valueOf(maxChecks));
        //Log.i(LOG_TAG, " math: " + String.valueOf(Math.round((currentCheck / maxChecks) * 100)));
        //Log.i(LOG_TAG, " math: " + String.valueOf((currentCheck / maxChecks) * 100));
        //Log.i(LOG_TAG, " math: " + String.valueOf((int) Math.round(((double) currentCheck / (double) maxChecks) * 100.0)));

        Map<String, Object> progressGPS = new HashMap<String, Object>();
        progressGPS.put(Stone.columns.COLUMN_PROGRESSGPS, progress);
        mFirebaseRef.updateChildren(progressGPS);

        //mFirebaseRef.child(Stone.columns.COLUMN_PROGRESSGPS).setValue(progress);

        if(bestAccuracy >= location.getAccuracy()) {
            bestAccuracy = (double) location.getAccuracy();
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
            //TextView longitudeTextView = (TextView) findViewById(R.id.longitude);
            //longitudeTextView.setText(String.valueOf(lastLongitude));
            //TextView latitudeTextView = (TextView) findViewById(R.id.latitude);
            //latitudeTextView.setText(String.valueOf(lastLatitude));
            //EditText name = (EditText) findViewById(R.id.location_name);

            // Setup our Firebase mFirebaseRef
            String deviceModel = Build.MANUFACTURER + " : " + Build.MODEL;
            String deviceOS = "Android OS: " + Build.VERSION.RELEASE + " : sdk=" + String.valueOf(Build.VERSION.SDK_INT);
            String method = "Consistency";
            Long seconds = (new Date().getTime() - startDate.getTime())/1000;

            Stone newStone = new Stone(deviceModel, deviceOS, method, location.getProvider(), location.getLongitude(), location.getLatitude(), (double) location.getAccuracy(),
                    location.getAltitude(), seconds, false, 100, "", false);
            //mFirebaseRef.push().setValue(newStone);

            mFirebaseRef.updateChildren(Stone.columns.getGPSMap(newStone));
            mFirebaseRef.onDisconnect();

            Firebase refStoneTBD = new Firebase(fireRefStoneTBD);
            Map<String, Object> messageGPS = new HashMap<String, Object>();
            messageGPS.put(StoneTBD.columns.COLUMN_GPSMSG, "GPS Accuracy " + String.valueOf(Math.round(location.getAccuracy())) + " meters");
            refStoneTBD.updateChildren(messageGPS);
            refStoneTBD.onDisconnect();

        }
        else if (currentCheck >= maxChecks && currentCount == 0) {
            stopLocationUpdates();
            //set longitude and latitude
            //TextView longitudeTextView = (TextView) findViewById(R.id.longitude);
            //longitudeTextView.setText(String.valueOf(bestLongitude));
            //TextView latitudeTextView = (TextView) findViewById(R.id.latitude);
            //latitudeTextView.setText(String.valueOf(bestLatitude));
            //EditText name = (EditText) findViewById(R.id.location_name);

            String deviceModel = Build.MANUFACTURER + " : " + Build.MODEL;
            String deviceOS = "Android OS: " + Build.VERSION.RELEASE + " : sdk=" + String.valueOf(Build.VERSION.SDK_INT);
            String method = "Accuracy";
            Long seconds = (new Date().getTime() - startDate.getTime())/1000;

            Stone newStone = new Stone(deviceModel, deviceOS, method, location.getProvider(), bestLongitude, bestLatitude, bestAccuracy,
                    bestAltitude, seconds, false, 100, "", false);
            //mFirebaseRef.push().setValue(newStone);

            mFirebaseRef.updateChildren(Stone.columns.getGPSMap(newStone));
            mFirebaseRef.onDisconnect();

            Firebase refStoneTBD = new Firebase(fireRefStoneTBD);
            Map<String, Object> messageGPS = new HashMap<String, Object>();
            messageGPS.put(StoneTBD.columns.COLUMN_GPSMSG, "GPS Accuracy " + String.valueOf(Math.round(bestAccuracy)) + " meters");
            refStoneTBD.updateChildren(messageGPS);
            refStoneTBD.onDisconnect();

        }
    }
/*
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
*/
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
        Log.i(LOG_TAG, "startLocationUpdates");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Log.i(LOG_TAG, "Access denied to call");
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }

    // location updates stopped
    protected void stopLocationUpdates() {
        Log.i(LOG_TAG, "stopLocationUpdates");
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
        mGoogleApiClient.disconnect();
    }

}
