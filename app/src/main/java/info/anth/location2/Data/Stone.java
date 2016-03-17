package info.anth.location2.Data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.firebase.client.Firebase;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Primary on 3/16/2016.
 *
 * Setup Stone data class
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Stone {
    private String deviceModel;
    private String deviceOS;
    private String method;
    private String provider;
    private Double longitude;
    private Double latitude;
    private Double accuracy;
    private Double altitude;
    private Long secondsToGPS;
    private Boolean processed;
    private int progressGPS;

    // Required default constructor for Firebase object mapping
    @SuppressWarnings("unused")
    private Stone() {
    }

    //    Chat(String message, String author, String lower[]) {
    public Stone(String deviceModel, String deviceOS, String method, String provider,
          Double longitude, Double latitude, Double accuracy,
          Double altitude, Long secondsToGPS, Boolean processed, int progressGPS) {
        this.deviceModel = deviceModel;
        this.deviceOS = deviceOS;
        this.method = method;
        this.provider = provider;
        this.longitude = longitude;
        this.latitude = latitude;
        this.accuracy = accuracy;
        this.altitude = altitude;
        this.secondsToGPS = secondsToGPS;
        this.processed = processed;
        this.progressGPS = progressGPS;

    }

    public String getDeviceModel() { return deviceModel; }
    public String getDeviceOS() { return deviceOS; }
    public String getMethod() { return method; }
    public String getProvider() { return provider; }
    public Double getLongitude() { return longitude; }
    public Double getLatitude() { return latitude; }
    public Double getAccuracy() { return accuracy; }
    public Double getAltitude() { return altitude; }
    public Long getSecondsToGPS() { return secondsToGPS; }
    public Boolean getProcessed() { return processed; }
    public int getProgressGPS() { return progressGPS; }

    public static class columns {

        //define columns
        public static String COLUMN_DEVICEMODEL = "deviceModel";
        public static String COLUMN_DEVICEOS = "deviceOS";
        public static String COLUMN_METHOD = "method";
        public static String COLUMN_PROVIDER = "provider";
        public static String COLUMN_LONGITUDE = "longitude";
        public static String COLUMN_LATITUDE = "latitude";
        public static String COLUMN_ACCURACY = "accuracy";
        public static String COLUMN_ALTITUDE = "altitude";
        public static String COLUMN_SECONDSTOGPS = "secondsToGPS";
        public static String COLUMN_PROCESSED = "processed";
        public static String COLUMN_PROGRESSGPS = "progressGPS";

        public static Map<String, Object> getFullMap(Stone stone) {
            Map<String, Object> fullMap = new HashMap<String, Object>();

            fullMap.put(COLUMN_DEVICEMODEL, stone.getDeviceModel());
            fullMap.put(COLUMN_DEVICEOS, stone.getDeviceOS());
            fullMap.put(COLUMN_METHOD, stone.getMethod());
            fullMap.put(COLUMN_PROVIDER, stone.getProvider());
            fullMap.put(COLUMN_LONGITUDE, stone.getLongitude());
            fullMap.put(COLUMN_LATITUDE, stone.getLatitude());
            fullMap.put(COLUMN_ACCURACY, stone.getAccuracy());
            fullMap.put(COLUMN_ALTITUDE, stone.getAltitude());
            fullMap.put(COLUMN_SECONDSTOGPS, stone.getSecondsToGPS());
            fullMap.put(COLUMN_PROCESSED, stone.getProcessed());
            fullMap.put(COLUMN_PROGRESSGPS, stone.getProgressGPS());

            return fullMap;
        }

    }
}
