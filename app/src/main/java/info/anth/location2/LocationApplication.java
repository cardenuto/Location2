package info.anth.location2;

import android.content.Context;

import com.cloudinary.Cloudinary;
import com.cloudinary.android.Utils;
import com.firebase.client.Config;
import com.firebase.client.Firebase;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.utils.L;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Primary on 3/15/2016.
 *
 * Needed to initialize Firebase into the correct context and set local persistence of data
 * added data for cloudinary (image content management):
 * https://cloudinary.com/
 * and
 * Android Universal Image Loader:
 * https://github.com/nostra13/Android-Universal-Image-Loader/wiki/Quick-Setup
 */
public class LocationApplication extends android.app.Application {
    private Cloudinary cloudinary;

    /**
     * @return An initialized Cloudinary instance
     */
    public Cloudinary getCloudinary() {
        return cloudinary;
    }

    /**
     * Provides access to the singleton and the getCloudinary method
     * @param context Android Application context
     * @return instance of the Application singleton.
     */
    public static LocationApplication getInstance(Context context) {
        return (LocationApplication)context.getApplicationContext();
    }

    // needed for the universal image loader
    private void initUIL() {
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.ic_camera_alt_24dp)
                .showImageOnFail(R.drawable.ic_cancel_24dp)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .displayer(new FadeInBitmapDisplayer(300))
                .build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .defaultDisplayImageOptions(defaultOptions)
                .build();
        ImageLoader.getInstance().init(config);
        L.i("Universal Image Loader initialized");
    }

    private void initCloudinary() {
        // Cloudinary: creating a cloudinary instance using meta-data from manifest
        Map configCloudinary = new HashMap();
        configCloudinary.put("cloud_name", "dqeqimfy5");
        cloudinary = new Cloudinary(configCloudinary);
        L.i("Cloudinary initialized");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Firebase.setAndroidContext(this);

        // update the default config to save all data locally.
        Config config = Firebase.getDefaultConfig();
        config.setPersistenceEnabled(true);
        Firebase.setDefaultConfig(config);

        initUIL();
        initCloudinary();
    }
}
