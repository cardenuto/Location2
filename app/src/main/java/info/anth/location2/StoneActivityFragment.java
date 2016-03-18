package info.anth.location2;

//import android.app.Fragment;
import android.animation.ObjectAnimator;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
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

/**
 * Created by Primary on 3/17/2016.
 * <p/>
 * Attempt to expand code into Fragment instead of activity alone
 */
public class StoneActivityFragment extends Fragment {

    public static final String ARGUMENT_STONEID = "stoneID";

    public static final String LOG_TAG = ObtainGPSDataService.class.getSimpleName();
    private Firebase mFirebaseRef;
    private static Firebase stoneRef;
    private static Firebase stoneTBDRef;
    private ValueEventListener valueEventListener;
    //private String stoneID;
    //private String stoneTBDID;
    private static Boolean changed;
    private static View rootView;
    private int priorProgress = 0;

    public StoneActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String stoneID = null;
        stoneRef = null;
        stoneTBDRef = null;

        mFirebaseRef = new Firebase(getResources().getString(R.string.FIREBASE_URL));

        Log.i(LOG_TAG, "before Arguments");
        //Log.i(LOG_TAG, "Arguments: " + getArguments().toString());
        if (getArguments() != null && getArguments().containsKey(ARGUMENT_STONEID)) {
            stoneID = getArguments().getString(ARGUMENT_STONEID);
        }

        if (stoneID != null) {
            stoneRef = mFirebaseRef.child("stone").child(stoneID);

            // It was loaded, therefor changed before, do not delete
            changed = true;

        } else {
            Firebase pushRefStone = mFirebaseRef.child("stone").push();
            Firebase pushRefStoneTBD = mFirebaseRef.child("stoneTBD").push();

            // process the stone
            Stone newStone = new Stone("", "", "intial insert", "", 0.0, 0.0, 0.0, 0.0, 0L, false, 0, "");
            pushRefStone.setValue(newStone);
            stoneID = pushRefStone.getKey();
            stoneRef = mFirebaseRef.child("stone").child(stoneID);

            // add stoneTBD
            StoneTBD newStoneTBD = new StoneTBD(stoneID, "", "Missing Picture", "Missing GPS", "Missing People");
            pushRefStoneTBD.setValue(newStoneTBD);
            String stoneTBDID = pushRefStoneTBD.getKey();
            stoneTBDRef = mFirebaseRef.child("stoneTBD").child(stoneTBDID);

            // update stone for stoneTBD
            Map<String, Object> updateStoneTBD = new HashMap<>();
            updateStoneTBD.put(Stone.columns.COLUMN_STONETBD, stoneTBDID);
            stoneRef.updateChildren(updateStoneTBD);

            changed = false;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_stone, container, false);

        // code for button on fragment xml
        // show map
        Button buttonMap = (Button) rootView.findViewById(R.id.show_map_button);
        buttonMap.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Log.i(LOG_TAG, "button Clicked");
            }
        });
        // take picture
        Button buttonPicture = (Button) rootView.findViewById(R.id.picture_button);
        buttonPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changed = true;
            }
        });


        getDatabase();

        return rootView;
    }

    private void getDatabase() {
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Stone thisStone = snapshot.getValue(Stone.class);

                if (thisStone.getProcessed() && thisStone.getStoneTBD() != null) {
                    stoneTBDRef = null;
                } else {
                    stoneTBDRef = mFirebaseRef.child("stoneTBD").child(thisStone.getStoneTBD());
                }
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
        TextView longitudeTextView = (TextView) rootView.findViewById(R.id.longitude);
        longitudeTextView.setText(String.valueOf(thisStone.getLongitude()));
        TextView latitudeTextView = (TextView) rootView.findViewById(R.id.latitude);
        latitudeTextView.setText(String.valueOf(thisStone.getLatitude()));
        TextView accuracyTextView = (TextView) rootView.findViewById(R.id.accuracy);
        accuracyTextView.setText(String.valueOf((int) Math.round(thisStone.getAccuracy())));

        TextView otherTextView = (TextView) rootView.findViewById(R.id.other_info);
        String message = "Method: " + thisStone.getMethod();
        message += " Provider: " + thisStone.getProvider();
        message += "\n";
        message += "Device: " + thisStone.getDeviceModel();
        message += "\n";
        message += "OS: " + thisStone.getDeviceOS();
        message += "\n";
        message += "Time: " + String.valueOf(thisStone.getSecondsToGPS()) + " seconds";

        otherTextView.setText(message);

        //EditText name = (EditText) rootView.findViewById(R.id.location_name);
        //name.setText(thisStone.getMethod());

        //ProgressBar progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        //progressBar.setProgress(thisStone.getProgressGPS());

        ProgressBar progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        ObjectAnimator animation = ObjectAnimator.ofInt (progressBar, "progress", priorProgress, thisStone.getProgressGPS());
        // see this max value coming back here, we animale towards that value
        animation.setDuration (1000); //in milliseconds
        animation.setInterpolator(new DecelerateInterpolator());
        animation.start();
        priorProgress = thisStone.getProgressGPS();
    }

    protected static void findLocation() {

        changed = true;
        Intent myIntent = new Intent(rootView.getContext(), ObtainGPSDataService.class);
        myIntent.putExtra(ObtainGPSDataService.REQUEST_REF_STONE, stoneRef.getRef().toString());
        myIntent.putExtra(ObtainGPSDataService.REQUEST_REF_STONETBD, stoneTBDRef.getRef().toString());
        rootView.getContext().startService(myIntent);

    }

    protected static void takePicture() {
        changed = true;
        Map<String, Object> messageGPS = new HashMap<String, Object>();
        messageGPS.put(StoneTBD.columns.COLUMN_PICTUREMSG, "");
        stoneTBDRef.updateChildren(messageGPS);
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
