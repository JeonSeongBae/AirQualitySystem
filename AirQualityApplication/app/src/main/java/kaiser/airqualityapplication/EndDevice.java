package kaiser.airqualityapplication;

<<<<<<< HEAD
import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;
=======
import java.sql.Time;
>>>>>>> 45cc0a9a03cf49f66092d1daf0c57d0e437b34b3

/**
 * Created by tjdqo_000 on 2018-06-02.
 */

public class EndDevice {
    private String ID;
    private double[] density;
    private String color;
    private double latitude;
    private double hardness;
    Time time;

    public EndDevice(String ID, int density, double latitude, double hardness){
        this.ID = ID;
        this.density = new double[10];
        this.color = "";
        this.latitude = latitude;
        this.hardness = hardness;
        this.time = new Time(1);
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setHardness(double hardness) {
        this.hardness = hardness;
    }

<<<<<<< HEAD
    private String ID;
    private double density;
    private double latitude;
    private double longitude;

    public EndDevice(){
        // Defalut constructor required for calls to DataSnapshot.getValue(EndDevice.class)
    }

    public EndDevice(String color, String ID, double density, double latitude, double longitude) {
        this.color = color;
        this.ID = ID;
        this.density = density;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Exclude
    public Map<String, Object> toMap(){
        HashMap<String, Object> result = new HashMap<>();
        result.put("ID", ID);
        result.put("density", density);
        result.put("latitude", latitude);
        result.put("longitude", longitude);

        return result;
=======
    public double getLatitude() {
        return latitude;
    }

    public double getHardness() {
        return hardness;
    }

    public void setID(String ID) {
        this.ID = ID;
>>>>>>> 45cc0a9a03cf49f66092d1daf0c57d0e437b34b3
    }

    public void setColor(String color) {
        this.color = color;
    }

<<<<<<< HEAD
    public void setID(String ID) {
        this.ID = ID;
    }

    public void setDensity(double density) {
        this.density = density;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getColor() {
        return color;
    }

=======
>>>>>>> 45cc0a9a03cf49f66092d1daf0c57d0e437b34b3
    public String getID() {
        return ID;
    }

<<<<<<< HEAD
    public double getDensity() {
        return density;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
=======
    public String getColor() {
        return color;
>>>>>>> 45cc0a9a03cf49f66092d1daf0c57d0e437b34b3
    }
}
