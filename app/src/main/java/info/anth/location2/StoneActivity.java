package info.anth.location2;

import android.app.Activity;
import android.content.Intent;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import info.anth.location2.Data.Stone;
import info.anth.location2.Data.StoneTBD;

public class StoneActivity extends AppCompatActivity {

    public static final String LOG_TAG = ObtainGPSDataService.class.getSimpleName();
    public static final String REQUEST_CURRENT_STEP = "current_step";

    //private Firebase mFirebaseRef;
    private Firebase stoneRef;
    private Firebase stoneTBDRef;
    private ValueEventListener valueEventListener;
    //private String stoneID;
    //private String stoneTBDID;
    private Boolean changed = false;
    private static String currentStep;
    private Activity thisActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stone);

        thisActivity = this;

        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);

        CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        if (appBarLayout != null) {
            appBarLayout.setTitle(getString(R.string.title_add_stone));
        }

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        // update the icon for the current step
        currentStep = getIntent().getStringExtra(REQUEST_CURRENT_STEP);
        switch(currentStep) {
            case "gps":
                fab.setImageResource(R.drawable.ic_place_24dp);
                break;
            case "camera":
                fab.setImageResource(R.drawable.ic_camera_alt_24dp);
                break;
            default:
                // do not show the fab
                fab.hide();
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message;
                switch(currentStep) {
                    case "gps":
                        message = "Updating Location";
                        currentStep = "camera";
                        fab.setImageResource(R.drawable.ic_camera_alt_24dp);
                        StoneActivityFragment.findLocation();
                        break;
                    case "camera":
                        message = "Starting Camera";
                        currentStep = "people";
                        fab.hide();
                        StoneActivityFragment.dispatchTakePictureIntent(thisActivity);
                        //StoneActivityFragment.takePicture(thisActivity);
                        break;
                    default:
                        // do not show the fab
                        message = "Error - hidden button pressed!";
                        Log.e(LOG_TAG, message);
                }
                Snackbar.make(view, message, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //
        // Needed to replace this import
        // --> import android.app.Fragment;
        // With this one for this function to work, and pass arguments.
        // <-- import android.support.v4.app.Fragment;
        //
        // also needed to change the xml frame type for content_stone
        // from fragment to FrameLayout
        //
        if (savedInstanceState == null) {

            StoneActivityFragment fragment = new StoneActivityFragment();

            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putString(StoneActivityFragment.ARGUMENT_STONEID,
                    getIntent().getStringExtra(StoneActivityFragment.ARGUMENT_STONEID));
            //fragment = new StoneActivityFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_stone, fragment)
                    .commit();
        }
/*
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
*/
    }
/*
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
*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home) {
            StoneActivityFragment.onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        StoneActivityFragment.onBackPressed();
    }

}
