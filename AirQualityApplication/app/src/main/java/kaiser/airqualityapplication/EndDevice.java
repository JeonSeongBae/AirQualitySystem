package kaiser.airqualityapplication;

import java.sql.Time;

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

    public double getLatitude() {
        return latitude;
    }

    public double getHardness() {
        return hardness;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getID() {
        return ID;
    }

    public String getColor() {
        return color;
    }
}
