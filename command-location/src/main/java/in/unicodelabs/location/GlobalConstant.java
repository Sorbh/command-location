package in.unicodelabs.location;

/**
 * Created by saurabh on 24/5/17.
 */

public class GlobalConstant {
    public static final long DEFAULT_FALLBACK_TIME = 3000;

    public static class Action {
        public static final String LOCATION_COMMAND_START = "start_location";
        public static final String LOCATION_COMMAND_STOP = "stop_location";

        public static final String LOCATION_RECEIVED = "location_received";
        public static final String NO_LOCATION_RECEIVED = "no_location_received";

        public static final String CHECK_SETTINGS = "check_setting";
        public static final String OPEN_SETTINGS = "open_setting";
    }

    public static class Bundle {
        public static final String ACTION = "action";
        public static final String COMMAND_MODE = "command_mode";
        public static final String LOCATION_REQUEST = "location_request";
        public static final String FALLBACK_TIME = "fallback_time";
        public static final String RESULT_RECEIVER = "result_receiver";

        public static final String RESULT_CODE = "result_code";
        public static final String REQUEST_CODE = "request_code";

        public static final String LOCATION = "location";


        public static final String EXCEPTION = "exception";
    }




}
