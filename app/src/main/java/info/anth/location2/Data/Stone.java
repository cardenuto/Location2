package info.anth.location2.Data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

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
    private Float accuracy;
    private Double altitude;
    private Long secondsToGPS;
    private Boolean processed;

    // Required default constructor for Firebase object mapping
    @SuppressWarnings("unused")
    private Stone() {
    }

    //    Chat(String message, String author, String lower[]) {
    public Stone(String deviceModel, String deviceOS, String method, String provider,
          Double longitude, Double latitude, Float accuracy,
          Double altitude, Long secondsToGPS, Boolean processed) {
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
    }

    public String getDeviceModel() { return deviceModel; }
    public String getDeviceOS() { return deviceOS; }
    public String getMethod() { return method; }
    public String getProvider() { return provider; }
    public Double getLongitude() { return longitude; }
    public Double getLatitude() { return latitude; }
    public Float getAccuracy() { return accuracy; }
    public Double getAltitude() { return altitude; }
    public Long getSecondsToGPS() { return secondsToGPS; }
    public Boolean getProcessed() { return processed; }
}
