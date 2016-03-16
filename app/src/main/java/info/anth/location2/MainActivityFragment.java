package info.anth.location2;

import android.os.Build;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.client.Firebase;
import com.firebase.ui.FirebaseRecyclerAdapter;

import java.lang.reflect.Field;

import info.anth.location2.Data.Chat;

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
        mFirebaseRef = new Firebase(FIREBASE_URL).child("chat").child("lower");
        Log.i("ajc", Build.MANUFACTURER + " : " + Build.MODEL);
        Log.i("ajc", "Android OS: " + Build.VERSION.RELEASE + " : sdk=" + String.valueOf(Build.VERSION.SDK_INT));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        RecyclerView recycler = (RecyclerView) rootView.findViewById(R.id.myList);
        recycler.setHasFixedSize(true);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        //recycler.setItemAnimator(new SlideInOutLeftItemAnimator(mRecyclerView));

        mAdapter = new FirebaseRecyclerAdapter<Chat, ChatMessageViewHolder>(Chat.class, R.layout.chat_message, ChatMessageViewHolder.class, mFirebaseRef) {
            @Override
            public void populateViewHolder(ChatMessageViewHolder chatMessageViewHolder, Chat chatMessage, int position) {
                chatMessageViewHolder.nameText.setText(chatMessage.getAuthor());
                chatMessageViewHolder.messageText.setText(chatMessage.getMessage());
                chatMessageViewHolder.keyText.setText(String.valueOf(mAdapter.getRef(position).getKey()));
                //chatMessageViewHolder.lowerText.setText(chatMessage.getLower().toString());
                chatMessageViewHolder.lowerText.setText(chatMessage.getLevel1());
            }
        };
        recycler.setAdapter(mAdapter);

        return rootView;

    }

    public static class ChatMessageViewHolder extends RecyclerView.ViewHolder {
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
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mAdapter.cleanup();
    }
}
