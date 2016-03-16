package info.anth.location2.Data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @author greg
 * @since 6/21/13
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Chat {

    private String message;
    private String author;
    private lowerClass lower;
    private String level1;
    //private String[] lower;

    // Required default constructor for Firebase object mapping
    @SuppressWarnings("unused")
    private Chat() {
    }

//    Chat(String message, String author, String lower[]) {
     Chat(String message, String author, lowerClass lower, String level1) {
        this.message = message;
        this.author = author;
         this.lower = lower;
         this.level1 = level1;
        //this.lower = lower;
    }

    public String getMessage() {
        return message;
    }

    public String getAuthor() {
        return author;
    }

    public String getLevel1() {
        if(lower != null) {
            return "not null: " + lower.getLowerLevel1();
        }
        return "null: " + level1;
    }

    public class lowerClass {
        private String level1;
        private String level2;
        private String Level3;

        @SuppressWarnings("unused")
        private lowerClass() {
        }

        //    Chat(String message, String author, String lower[]) {
        lowerClass(String level1, String level2, String Level3) {
            this.level1 = level1;
            this.level2 = level2;
            this.Level3 = Level3;
        }

        public String getLowerLevel1() { return level1; }
        public String getLowerLevel2() { return level2; }
        public String getLowerLevel3() { return Level3; }
    }
}

