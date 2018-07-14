package kaiser.airqualityapplication;

public class Useful {

    static public String DEBUG_LOG = "LOG";
    static public String DEBUG_READ = "READ";
    static public String DEBUG_COMM = "COMM";
    static public String DEBUG_SCAN = "SCAN";

    static public Integer MAP_SERVER = 1;
    static public Integer MAP_CONN_Y = 2;
    static public Integer MAP_CONN_N = 3;

    static public int LOG_NO_1 = 1; // BLE detection Time
    static public int LOG_NO_2 = 2; // Request authentication
    static public int LOG_NO_3 = 3; // Reply authentication
    static public int LOG_NO_4 = 4; // Request connection with sensor node
    static public int LOG_NO_4_1 = 41; // Can't connection below CONSTANT_LIMIT_RSSI
    static public int LOG_NO_5 = 5; // Connected with sensor node
    static public int LOG_NO_6 = 6; // Request data
    static public int LOG_NO_7 = 7; // End data
    static public int LOG_NO_8 = 8; // Disconnected with sensor node

    static public int CONSTANT_LIMIT_RSSI = -80; // unit dBm

    static public String pruningString(String result) {
        if (result == null || result.contains("Error") || result.contains(404 + "") || result.contains(500 + ""))
            return null;

        result = result.substring(result.indexOf("<body>"), result.indexOf("</body>"));
        result = result.replaceAll("<br>", "\n");
        result = result.replaceAll("<body>", "");
        result = result.trim();
        return result;
    }

}