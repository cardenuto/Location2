package info.anth.location2;

//import android.app.Fragment;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Telephony;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.utils.ObjectUtils;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
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

    public static final String LOG_TAG = StoneActivityFragment.class.getSimpleName();
    private Firebase mFirebaseRef;
    private static Firebase stoneRef;
    private static Firebase stoneTBDRef;
    private ValueEventListener valueEventListener;
    private static String stoneID;
    //private String stoneTBDID;
    private static Boolean changed;
    private static View rootView;
    private int priorProgress = 0;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private static  Uri stoneGEOURI;
    private static Boolean keepStoneInMemeory;
    private static Boolean firstTime;
    private static Fragment thisFragment;


    public StoneActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        thisFragment = this;

        stoneRef = null;
        //stoneTBDRef = null;

        mFirebaseRef = new Firebase(getResources().getString(R.string.FIREBASE_URL));

        Log.i(LOG_TAG, "before Arguments Stone: " + stoneID);
        //Log.i(LOG_TAG, "Arguments: " + getArguments().toString());
        if (getArguments() != null && getArguments().containsKey(ARGUMENT_STONEID)) {
            if (keepStoneInMemeory == null || !keepStoneInMemeory) {
                stoneID = getArguments().getString(ARGUMENT_STONEID);
                keepStoneInMemeory = false;
                // It is expected to be loaded, therefor changed before, do not delete
                if (stoneID != null) { changed = true; }
            }
        }

        Log.i(LOG_TAG, "after Arguments Stone: " + stoneID);

        if (stoneID != null) {
            stoneRef = mFirebaseRef.child("stone").child(stoneID);
        } else {
            Firebase pushRefStone = mFirebaseRef.child("stone").push();
            Firebase pushRefStoneTBD = mFirebaseRef.child("stoneTBD").push();

            // process the stone
            Stone newStone = new Stone("", "", "intial insert", "", 0.0, 0.0, 0.0, 0.0, 0L, false, 0, "", false);
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
            keepStoneInMemeory = true;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_stone, container, false);

        // code for button on fragment xml
        // show map
        Button buttonMap = (Button) rootView.findViewById(R.id.show_map_button);
        buttonMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW, stoneGEOURI);
                if (intent.resolveActivity(getContext().getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });
        // take picture
        Button buttonPicture = (Button) rootView.findViewById(R.id.picture_button);
        buttonPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent(getActivity());
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

                if (thisStone != null) {
                    if (thisStone.getProcessed() && thisStone.getStoneTBD() != null) {
                        stoneTBDRef = null;
                    } else {
                        stoneTBDRef = mFirebaseRef.child("stoneTBD").child(thisStone.getStoneTBD());
                    }
                    refreshScreen(thisStone);
                }
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

        stoneGEOURI = Stone.columns.geoUri(thisStone, "Grave Site");

        if (firstTime) {
            firstTime = false;
            displayImageIfExists(thisStone.getImageUploaded());
        }
    }

    protected static void findLocation() {

        changed = true;
        Intent myIntent = new Intent(rootView.getContext(), ObtainGPSDataService.class);
        myIntent.putExtra(ObtainGPSDataService.REQUEST_REF_STONE, stoneRef.getRef().toString());
        myIntent.putExtra(ObtainGPSDataService.REQUEST_REF_STONETBD, stoneTBDRef.getRef().toString());
        rootView.getContext().startService(myIntent);

    }
/*
    protected static void takePicture(Activity activity) {
        changed = true;
        Map<String, Object> messageGPS = new HashMap<String, Object>();
        messageGPS.put(StoneTBD.columns.COLUMN_PICTUREMSG, "");
        stoneTBDRef.updateChildren(messageGPS);
        dispatchTakePictureIntent2(activity);
    }

    protected static void dispatchTakePictureIntent2(Activity activity) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(activity.getPackageManager()) != null) {
            activity.startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);

        }
    }
*/

    // this is called from the main activity
    protected static void onBackPressed() {
        keepStoneInMemeory = false;
        if (!changed) {
            stoneRef.removeValue();
            stoneTBDRef.removeValue();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stoneRef.removeEventListener(valueEventListener);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Make firstTime true to load image
        firstTime = true;
        Log.w("ajc", "in onResume");
    }

    /**
     * ********************************************************************************************
     * All the picture code
     * ********************************************************************************************
     */
    static Uri takenPhotoURI;
    static final int REQUEST_TAKE_PHOTO = 0;

    private File createImageFile() throws IOException {
        createImageFileNew();
        // Create an image file name
        String imageFileName = stoneID;
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File imageFile = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        takenPhotoURI = Uri.parse("file://" + imageFile.getAbsolutePath());
        Log.i(LOG_TAG,"image: " + imageFile.getAbsolutePath() + " uri: " + takenPhotoURI.toString() + " Stone ID: " + stoneID);
        return imageFile;
    }

    public void displayImageIfExists(Boolean imageUploaded) {
        // Create file path
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        String filePath = storageDir + "/" + stoneID + ".jpg";
        takenPhotoURI = Uri.parse("file://" + filePath);

        if (new File(filePath).exists()) {
            displayImage(takenPhotoURI, "displayImageIfExists - file exists");
        } else if (imageUploaded) {
            // determine the Cloudinary URI
            Cloudinary cloudinary = LocationApplication.getInstance(getContext()).getCloudinary();
            String url_string = cloudinary.url().transformation(new Transformation().width(500).height(500).crop("fill")).generate("stones/" + stoneID + ".png");
            //String url_string = cloudinary.url().imageTag("stones/" + stoneID + ".png");
            Log.i("ajc", url_string);
            //ImageLoader.getInstance().displayImage(url_string, testImage);

            displayImage(Uri.parse(url_string), "displayImageIfExists - imageUploaded");
        }
    }

    private static File createImageFileNew() throws IOException {
        // Create file path
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        String filePath = storageDir + "/" + stoneID + ".jpg";
        takenPhotoURI = Uri.parse("file://" + filePath);

        File myfile = new File(filePath);

        Boolean checkExists = myfile.exists();


        Boolean checkDelete = true;
        Boolean checkCreate = false;

                    if(checkExists) {
                        checkDelete = myfile.delete();
                    }
                    if (checkDelete) {
                        checkCreate = myfile.createNewFile();
                    }

        String message;
        if (checkCreate) {
            message = "File created. existed: " + checkExists.toString() + " file: " + filePath + " stone: " + stoneID;
            Log.i(LOG_TAG, message);
            return myfile;
        } else {
            message = "File creation FAILED. existed: " + checkExists.toString() + " file: " + filePath + " stone: " + stoneID;
            Log.e(LOG_TAG, message);
            return null;
        }
    }

    protected static void dispatchTakePictureIntent(Activity activity) {

        //getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
        // Setup the intent to take picture
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(activity.getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFileNew();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.e(LOG_TAG, "Error creating file: " + takenPhotoURI.toString() + " with error: " + ex.toString());
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                // start intent
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                thisFragment.startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    /**
     * ********************************************************************************************
     * Photo intent response
     * ********************************************************************************************
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.w("ajc", "onActivityResult requestCode: " + String.valueOf(requestCode) + " resultCode: " + String.valueOf(resultCode)
        + " requestCode == REQUEST_TAKE_PHOTO:" + String.valueOf(requestCode == REQUEST_TAKE_PHOTO));
        if(requestCode == REQUEST_TAKE_PHOTO) {
            if (resultCode == Activity.RESULT_OK) {
                // set notification of update so the system does not delete on "back"
                Log.w("ajc", "onActivityResult made it to the inner code");
                changed = true;

                // return the image to the user's screen
                // do not call - until the app resumes all data not available
                //displayImage(takenPhotoURI, "onActivityResult");

                if (stoneTBDRef != null) {
                    // update stone To Be Done database saying picture was taken
                    Map<String, Object> messageGPS = new HashMap<String, Object>();
                    messageGPS.put(StoneTBD.columns.COLUMN_PICTUREMSG, "Waiting for Upload");
                    stoneTBDRef.updateChildren(messageGPS);
                } else {
                    Log.w("ajc", "onActivityResults stoneTBDRef null");
                }

                // upload the image to the internet
                startUpload(takenPhotoURI.getPath());
                //getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
            }
        }
    }

    private void displayImage(Uri imageUri, String callingProgram) {
        final ProgressBar spinner = (ProgressBar) rootView.findViewById(R.id.loading);
        final ImageView pictureView = (ImageView) rootView.findViewById(R.id.picture_view);
        //final Button pictureButton = (Button) rootView.findViewById(R.id.picture_button);

        Log.w("ajc" , "in displayImage from: " + callingProgram);
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.ic_camera_alt_24dp)
                .showImageOnFail(R.drawable.ic_cancel_24dp)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .displayer(new FadeInBitmapDisplayer(300))
                .build();

        ImageLoader.getInstance().displayImage(imageUri.toString(), pictureView, options,
                // code to add new functions
                new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String imageUri, View view) {
                        Log.w("ajc", "in onloadingStarted uri: " + imageUri);
                        spinner.setVisibility(View.VISIBLE);
                        //pictureButton.setVisibility(View.GONE);
                    }

                    @Override
                    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                        String message = null;
                        switch (failReason.getType()) {
                            case IO_ERROR:
                                message = "Input/Output error";
                                break;
                            case DECODING_ERROR:
                                message = "Image can't be decoded";
                                break;
                            case NETWORK_DENIED:
                                message = "Downloads are denied";
                                break;
                            case OUT_OF_MEMORY:
                                message = "Out Of Memory error";
                                break;
                            case UNKNOWN:
                                message = "Unknown error";
                                break;
                        }
                        Toast.makeText(view.getContext(), message, Toast.LENGTH_SHORT).show();
                        spinner.setVisibility(View.GONE);
                        //pictureButton.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        spinner.setVisibility(View.GONE);
                    }
                });
    }
    private void startUpload(String filePath) {
        Log.i("ajc", "in StartUpload");
        final String testPath =filePath;
        AsyncTask<String, Void, String> task = new AsyncTask<String, Void, String>() {
            protected String doInBackground(String... paths) {
                Log.i("ajc", "Running upload task");

                Cloudinary cloudinary = new Cloudinary();
                //String testPath = gimageUri.getPath();
                //testPath = "/storage/emulated/0/Pictures/JPEG_20160319_140404_773191082.jpg";

                File myfile = new File(testPath);
                if (myfile.exists()) {
                    Log.i("ajc", "file exist");
                } else {
                    Log.i("ajc", "file does NOT exist");
                }

                Log.i("ajc", "onLoadComplete uri path: " + testPath);
                Map cloudinaryResult;
                try {
                    cloudinaryResult = cloudinary.uploader().unsignedUpload(myfile, "ithmyy6i", ObjectUtils.asMap("cloud_name", "dqeqimfy5"));
                    Log.i("ajc", "Uploaded file: " + cloudinaryResult.toString());
                } catch (RuntimeException e) {
                    Log.e("ajc", "Error uploading file: " + e.toString());
                    return "Error uploading file: " + e.toString();
                } catch (IOException e) {
                    Log.e("ajc", "Error uploading file: " + e.toString());
                    return "Error uploading file: " + e.toString();
                }

                return null;
            }

            protected void onPostExecute(String error) {

                Log.i("ajc", "Is Error?: " + error);

                if (error == null) {
                    // update stone To Be Done database saying picture was taken
                    Map<String, Object> messageGPS = new HashMap<String, Object>();
                    messageGPS.put(StoneTBD.columns.COLUMN_PICTUREMSG, "");
                    stoneTBDRef.updateChildren(messageGPS);

                    // update stone for imageUploaded
                    Map<String, Object> updateStone = new HashMap<>();
                    updateStone.put(Stone.columns.COLUMN_IMAGEUPLOADED, true);
                    stoneRef.updateChildren(updateStone);
                }
                Log.i("ajc", "onPostExecution!");
            }
        };
        task.execute(filePath);
    }

}
