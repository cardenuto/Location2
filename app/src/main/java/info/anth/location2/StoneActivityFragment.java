package info.anth.location2;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

/**
 * Created by Primary on 3/17/2016.
 *
 * Attempt to expand code into Fragment instead of activity alone
 */
public class StoneActivityFragment extends Fragment {
    public static final String LOG_TAG = ObtainGPSDataService.class.getSimpleName();
    //private Firebase mFirebaseRef;
    private static Firebase stoneRef;
    private static Firebase stoneTBDRef;
    private ValueEventListener valueEventListener;
    //private String stoneID;
    //private String stoneTBDID;
    private static Boolean changed;
    private static View rootView;

    public StoneActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
        Map<String, Object> updateStoneTBD = new HashMap<>();
        updateStoneTBD.put(Stone.columns.COLUMN_STONETBD, stoneTBDID);
        stoneRef.updateChildren(updateStoneTBD);

        changed = false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_stone, container, false);

        getDatabase();

        return rootView;
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
        TextView longitudeTextView = (TextView) rootView.findViewById(R.id.longitude);
        longitudeTextView.setText(String.valueOf(thisStone.getLongitude()));
        TextView latitudeTextView = (TextView) rootView.findViewById(R.id.latitude);
        latitudeTextView.setText(String.valueOf(thisStone.getLatitude()));
        EditText name = (EditText) rootView.findViewById(R.id.location_name);
        name.setText(thisStone.getMethod());

        ProgressBar progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        progressBar.setProgress(thisStone.getProgressGPS());
    }

    protected static void findLocation() {

        changed = true;
        Intent myIntent = new Intent(rootView.getContext(), ObtainGPSDataService.class);
        myIntent.putExtra(ObtainGPSDataService.REQUEST_REF_STONE, stoneRef.getRef().toString());
        myIntent.putExtra(ObtainGPSDataService.REQUEST_REF_STONETBD, stoneTBDRef.getRef().toString());
        rootView.getContext().startService(myIntent);

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
