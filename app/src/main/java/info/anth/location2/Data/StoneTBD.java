package info.anth.location2.Data;

import java.util.HashMap;
import java.util.Map;

import info.anth.location2.R;

/**
 * Created by Primary on 3/17/2016.
 *
 * Setup of StoneTBD (stone needing processed) class
 *
 */
public class StoneTBD {
    private String stoneID;
    private String pictureURI;
    private String pictureMsg;
    private String gpsMsg;
    private String peopleMsg;

    // Required default constructor for Firebase object mapping
    @SuppressWarnings("unused")
    private StoneTBD() {
    }

    public StoneTBD(String stoneID,
                    String pictureURI,
                    String pictureMsg,
                    String gpsMsg,
                    String peopleMsg
                    ) {
        this.stoneID = stoneID;
        this.pictureURI = pictureURI;
        this.pictureMsg = pictureMsg;
        this.gpsMsg = gpsMsg;
        this.peopleMsg = peopleMsg;
    }

    public String getStoneID() { return stoneID; }
    public String getPictureURI() { return pictureURI; }
    public String getPictureMsg() { return pictureMsg; }
    public String getGpsMsg() { return gpsMsg; }
    public String getPeopleMsg() { return peopleMsg; }

    public static class columns {

        //define columns
        public static String COLUMN_STONEID = "stoneID";
        public static String COLUMN_PICTUREURI = "pictureURI";
        public static String COLUMN_PICTUREMSG = "pictureMsg";
        public static String COLUMN_GPSMSG = "gpsMsg";
        public static String COLUMN_PEOPLEMSG = "peopleMsg";

        public static Map<String, Object> getFullMap(StoneTBD stoneTBD) {
            Map<String, Object> fullMap = new HashMap<String, Object>();

            fullMap.put(COLUMN_STONEID, stoneTBD.getStoneID());
            fullMap.put(COLUMN_PICTUREURI, stoneTBD.getPictureURI());
            fullMap.put(COLUMN_PICTUREMSG, stoneTBD.getPictureMsg());
            fullMap.put(COLUMN_GPSMSG, stoneTBD.getGpsMsg());
            fullMap.put(COLUMN_PEOPLEMSG, stoneTBD.getPeopleMsg());

            return fullMap;
        }

        public static String getCurrentStep(StoneTBD stoneTBD){
            String currentStep = "gps";
            String messageGPS = stoneTBD.getGpsMsg();
            String messagePicture = stoneTBD.getPictureMsg();
            String messagePeople = stoneTBD.getPeopleMsg();

            if(messageGPS != null && messageGPS.length() > 0 && !messageGPS.substring(0, 1).equals("M")){
                if(messagePicture != null && messagePicture.length() > 0 && messagePicture.substring(0, 1).equals("M")) {
                    currentStep = "camera";
                } else if(messagePeople != null && messagePeople.length() > 0 && messagePeople.substring(0, 1).equals("M")) {
                    currentStep = "people";
                } else {
                    currentStep = "refresh";
                }
            }

            return currentStep;
        }

    }
}
