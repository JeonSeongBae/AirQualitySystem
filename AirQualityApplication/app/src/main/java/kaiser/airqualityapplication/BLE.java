package kaiser.airqualityapplication;

/**
 * Created by tjdqo_000 on 2018-06-02.
 */

public class BLE {
    private boolean isConnect;
    private String ID;

    public BLE(){
        this.isConnect = false;
    }
    public void scan_BLE() {

    }

    public boolean isConnect() {
        return isConnect;
    }

    public void stop_BLE() {
    }

    public String connectedID() {
        return ID;
    }
}
