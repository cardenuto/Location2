package info.anth.location2;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.client.Firebase;
import com.firebase.ui.FirebaseRecyclerAdapter;

import info.anth.location2.Data.StoneTBD;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    // TODO: change this to your own Firebase URL
    private static final String FIREBASE_URL = "https://shining-inferno-6812.firebaseio.com";

    private Firebase mFirebaseRef;
    FirebaseRecyclerAdapter mAdapter;

    public MainActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Setup our Firebase mFirebaseRef
        mFirebaseRef = new Firebase(FIREBASE_URL).child("stoneTBD");
        //Log.i("ajc", Build.MANUFACTURER + " : " + Build.MODEL);
       // Log.i("ajc", "Android OS: " + Build.VERSION.RELEASE + " : sdk=" + String.valueOf(Build.VERSION.SDK_INT));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        RecyclerView recycler = (RecyclerView) rootView.findViewById(R.id.myList);
        recycler.setHasFixedSize(true);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        //recycler.setItemAnimator(new SlideInOutLeftItemAnimator(mRecyclerView));

        mAdapter = new FirebaseRecyclerAdapter<StoneTBD, StoneTBDMessageViewHolder>(StoneTBD.class, R.layout.x_stone_tbd, StoneTBDMessageViewHolder.class, mFirebaseRef) {
            @Override
            public void populateViewHolder(StoneTBDMessageViewHolder stoneTBDMessageViewHolder, StoneTBD stoneTBD, int position) {
                String messageGPS = stoneTBD.getGpsMsg();
                String messagePicture = stoneTBD.getPictureMsg();
                String messagePeople = stoneTBD.getPeopleMsg();

                String message = messageGPS;
                message += (messagePicture == null || messagePicture.equals("")) ? "" : System.getProperty("line.separator") + messagePicture;
                message += (messagePeople == null || messagePeople.equals("")) ? "" : System.getProperty("line.separator") + messagePeople;
                //message += (stoneTBD.getPictureMsg() == null || stoneTBD.getPictureMsg() == "") ? "" : System.getProperty("line.separator") + stoneTBD.getPictureMsg();
                //message += (stoneTBD.getPeopleMsg() == null || stoneTBD.getPeopleMsg() == "") ? "" : System.getProperty("line.separator") + stoneTBD.getPeopleMsg();
                stoneTBDMessageViewHolder.stoneTBD_text.setText(message);

                if(messageGPS != null && !messageGPS.substring(0, 1).equals("M")){
                    if(messagePicture != null && messagePicture.substring(0, 1).equals("M")) {
                        stoneTBDMessageViewHolder.stoneTBD_image.setImageResource(R.drawable.ic_camera_alt_24dp);
                    } else if(messagePeople != null && messagePeople.substring(0, 1).equals("M")) {
                        stoneTBDMessageViewHolder.stoneTBD_image.setImageResource(R.drawable.ic_person_24dp);
                    } else {
                        stoneTBDMessageViewHolder.stoneTBD_image.setImageResource(R.drawable.ic_refresh_24dp);
                    }
                }
            }
        };
        recycler.setAdapter(mAdapter);

        return rootView;

    }

    public static class StoneTBDMessageViewHolder extends RecyclerView.ViewHolder {
        TextView stoneTBD_text;
        ImageView stoneTBD_image;

        public StoneTBDMessageViewHolder(View itemView) {
            super(itemView);
            stoneTBD_text = (TextView)itemView.findViewById(R.id.stoneTBD_text);
            stoneTBD_image = (ImageView)itemView.findViewById(R.id.stoneTBD_image);
        }
    }

    /*public static class ChatMessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageText;
        TextView nameText;
        TextView keyText;
        TextView lowerText;

        public ChatMessageViewHolder(View itemView) {
            super(itemView);
            nameText = (TextView)itemView.findViewById(R.id.author);
            messageText = (TextView) itemView.findViewById(R.id.message);
            keyText = (TextView) itemView.findViewById(R.id.key);
            lowerText = (TextView) itemView.findViewById(R.id.lower);
        }
    }*/

    @Override
    public void onDestroy() {
        super.onDestroy();
        mAdapter.cleanup();
    }
}
