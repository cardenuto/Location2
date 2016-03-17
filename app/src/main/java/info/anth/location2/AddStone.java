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
import java.util.HashMap;
import java.util.Map;

import info.anth.location2.Data.Stone;
import info.anth.location2.Data.StoneTBD;

public class AddStone extends AppCompatActivity {

    public static final String LOG_TAG = ObtainGPSDataService.class.getSimpleName();
    //private Firebase mFirebaseRef;
    private Firebase stoneRef;
    private Firebase stoneTBDRef;
    private ValueEventListener valueEventListener;
    //private String stoneID;
    //private String stoneTBDID;
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

        Firebase mFirebaseRef = new Firebase(getResources().getString(R.string.FIREBASE_URL));
        Firebase pushRefStone = mFirebaseRef.child("stone").push();
        Firebase pushRefStoneTBD = mFirebaseRef.child("stoneTBD").push();

        // process the stone
        Stone newStone = new Stone("", "", "intial insert", "", 0.0, 0.0, 0.0, 0.0, 0L, false, 0, "");
        pushRefStone.setValue(newStone);
        String stoneID = pushRefStone.getKey();
        stoneRef = mFirebaseRef.child("stone").child(stoneID);

        // add stoneTBD
        StoneTBD newStoneTBD = new StoneTBD(stoneID, "", "Missing Picture", "Missing GPS", "Missing People");
        pushRefStoneTBD.setValue(newStoneTBD);
        String stoneTBDID = pushRefStoneTBD.getKey();
        stoneTBDRef = mFirebaseRef.child("stoneTBD").child(stoneTBDID);

        // update stone for stoneTBD
        Map<String, Object> updateStoneTBD = new HashMap<String, Object>();
        updateStoneTBD.put(Stone.columns.COLUMN_STONETBD, stoneTBDID);
        stoneRef.updateChildren(updateStoneTBD);

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

    protected void findLocation() {

        changed = true;
        Intent myIntent = new Intent(this, ObtainGPSDataService.class);
        myIntent.putExtra(ObtainGPSDataService.REQUEST_REF_STONE, stoneRef.getRef().toString());
        myIntent.putExtra(ObtainGPSDataService.REQUEST_REF_STONETBD, stoneTBDRef.getRef().toString());
        startService(myIntent);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stoneRef.removeEventListener(valueEventListener);
        if (!changed) {
            stoneRef.removeValue();
            stoneTBDRef.removeValue();
        }
    }
}
