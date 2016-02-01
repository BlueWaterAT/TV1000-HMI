package com.bwat.hmi;

/**
 * Constants for use with the HMI and individual pages
 *
 * @author Kareem El-Faramawi
 */
public class Constants {
    public static int HV_GAP = 15;
    public static int DEFAULT_PAGE_SPINNERS = 5;

    public static final String KEY_DATA_TYPE = "type";
    public static final String DATA_TYPE_NUM = "number";
    public static final String DATA_TYPE_STR = "string";

    public static final class SETTINGS {
        public static final String PATH = "settings.json";

        public static final String KEY_FILE_POLL_DELAY = "file-poll-delay";
        public static final String KEY_FONT_SIZE = "font-size";
        public static final String KEY_STATUS_BAR_PATH = "status-bar-path";
        public static final String KEY_VEHICLE_NAME = "vehicle-name";
        public static final String KEY_PAGE_SPINNERS = "spinners-per-page";
        public static final String KEY_CSV_LIST_LIMIT = "csv-list-limit";

        // Color settings
        public static final String KEY_BG_COLOR = "bg-color";
        public static final String KEY_FG_COLOR = "fg-color";
        public static final String KEY_BUTTON_COLOR = "button-color";
    }

    public static final class MAIN {
        public static final String PATH = "main.json";

        public static final String KEY_STATUS = "status";
        public static final String KEY_PAGES = "pages";
        public static final String KEY_TIMERS = "timers";
        public static final String KEY_NAME = "name";
        public static final String KEY_DATAPATH = "datapath";
    }

    public static final class LOG {
        public static final String PATH = "log.json";

        public static final String KEY_TABS = "tabs";
        public static final String KEY_NAME = "name";
        public static final String KEY_DATAPATH = "datapath";
    }

    public static final class STATUS {
        public static final String PATH = "status.json";

        public static final String KEY_NAME = "name";
        public static final String KEY_DATAPATH = "datapath";
    }

    public static final class IO {
        public static final String PATH = "io.json";

        public static final String KEY_COLS = "cols";
        public static final String KEY_INPUTS = "inputs";
        public static final String KEY_OUTPUTS = "outputs";
        public static final String KEY_NAME = "name";
        public static final String KEY_DATAPATH = "datapath";
    }

    public static final class PROGRAM {
        public static final String PATH = "program.json";

        public static final String KEY_JTB_PATH = "jtb-path";
        public static final String KEY_ROW_H = "row-height";
        public static final String KEY_PRIMARY_SORT = "primary-sort";

        //File format variables
        public final static String EXTENSION = ".jtb";
        public final static String PROGRAM_EXTENSION = ".prg";
        public final static String COMMA = ",";
        public final static String COMMENT = ";";
        public final static int PROGRAM_DEFAULT = 1;

        //FTP Related
        public final static String SFTP_ALERT_FILE = "SFTPUPDATE";

        //GUI
        public final static float FONT_SIZE = 28.0f; //TODO: Needed?
    }

    public static final class RFID {
        public static final String PATH = "rfid.json";

        public static final String KEY_VALUES = "values";
        public static final String KEY_NAME = "name";
        public static final String KEY_DATAPATH = "datapath";
        // public static final String KEY_STEP = "step";
        public static final String KEY_MIN = "min";
        public static final String KEY_MAX = "max";
    }

    public static final class CONFIG {
        public static final String PATH = "config.json";

        public static final String KEY_VALUES = "values";
        public static final String KEY_NAME = "name";
        public static final String KEY_DATAPATH = "datapath";
        // public static final String KEY_STEP = "step";
        public static final String KEY_MIN = "min";
        public static final String KEY_MAX = "max";
    }

    public static final class NET {
        public static final String PATH = "net.json";

        public static final String KEY_VALUES = "values";
        public static final String KEY_NAME = "name";
        public static final String KEY_DATAPATH = "datapath";
    }
}
