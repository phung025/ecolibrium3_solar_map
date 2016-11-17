package umd.solarmap.SolarData;

/**
 * Created by phung025 on 11/15/16.
 * Author: Nam Phung
 * This class represents the solar location that users saved/shared on the map. It contains
 * some attributes such as location ID, location name, longitude, latitude
 */
public class SolarLocation {

    /**
     * Instance fields
     */
    private String location_id; // Unique location ID. This variable is immutable
    private String location_name; // Name of the location
    private double location_longitude; // The longitude of the location. This variable is immutable
    private double location_latitude; // The latitude of the location. This variable is immutable

    // We want the default constructor to be private and this class should only
    // be instantiated using the parameterized constructor
    private SolarLocation(){};

    public SolarLocation(String locationID, String locationName, double longitude, double latitude) {
        location_id = locationID;
        location_name = locationName;
        location_longitude = longitude;
        location_latitude = latitude;
    }

    //////////////////
    // ACCESSORS
    /////////////////

    public String getLocationName() {
        return location_name;
    }

    public String getLocationID() {
        return location_id;
    }

    public double getLocationLongitude() {
        return location_longitude;
    }

    public double getLocationLatitude() {
        return location_latitude;
    }

    ///////////////////
    // MUTATORS
    ///////////////////

    public void setLocationName(String locationName) {
        location_name = locationName;
    }
}
