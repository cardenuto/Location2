package info.anth.location2.Data;

import java.util.HashMap;
import java.util.Map;

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

            fullMap.put(COLUMN_STONEID, stoneTBD. getStoneID());
            fullMap.put(COLUMN_PICTUREURI, stoneTBD. getPictureURI());
            fullMap.put(COLUMN_PICTUREMSG, stoneTBD. getPictureMsg());
            fullMap.put(COLUMN_GPSMSG, stoneTBD. getGpsMsg());
            fullMap.put(COLUMN_PEOPLEMSG, stoneTBD. getPeopleMsg());

            return fullMap;
        }

    }
}
