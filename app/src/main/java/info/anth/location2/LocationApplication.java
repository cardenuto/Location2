package info.anth.location2;

import com.firebase.client.Config;
import com.firebase.client.Firebase;

/**
 * Created by Primary on 3/15/2016.
 *
 * Needed to initialize Firebase into the correct context and set local persistence of data
 */
public class LocationApplication extends android.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Firebase.setAndroidContext(this);

        // update the default config to save all data locally.
        Config config = Firebase.getDefaultConfig();
        config.setPersistenceEnabled(true);
        Firebase.setDefaultConfig(config);
    }
}
