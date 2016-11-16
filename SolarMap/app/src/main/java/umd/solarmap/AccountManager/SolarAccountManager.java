package umd.solarmap.AccountManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import umd.solarmap.HTTPAsyncTask;
import umd.solarmap.SolarData.SolarAchievement;
import umd.solarmap.SolarData.SolarLocation;

/**
 * Created by user on 11/15/16.
 */

public class SolarAccountManager implements Serializable {

    /**
     * Singleton pattern, the instance of this class can only be returned when calling
     * static method appAccountManager
     */
    private static SolarAccountManager account;

    // URI Links
    private class URIs {
        public static final String ACCOUNT_LOGIN = "loginAccount";
        public static final String ACCOUNT_SIGN_UP = "registerAccount";
        public static final String ACCOUNT_SAVE_LOCATION = "saveLocation";
        public static final String ACCOUNT_SHARE_LOCATION = "shareLocation";
    }

    // HTTP Methods
    private class HTTPMethods {
        public static final String GET = "GET";
        public static final String POST = "POST";
        public static final String PUT = "PUT";
        public static final String DELETE = "DELETE";
    }

    // Server connection info
    private static int CONNECTION_PORT = 4321;
    private static String IP_ADDRESS = "10.0.2.2";

    // Account login status
    public static boolean LOGIN_STATUS = false;

    // User's account info
    private String email = null;
    private String password = null;

    // User's account data
    List<SolarAchievement> solarAchievementList; // User's achievements list
    List<SolarLocation> userPrivateLocationList; // User's private interested location
    List<SolarLocation> sharedLocationList; // Global locations list shared by all users

    /**
     * Defaul constructor. Declared as private in order to use singleton pattern
     */
    private SolarAccountManager() {

        // Set up all initial components of this class
        solarAchievementList = new LinkedList<SolarAchievement>();
        userPrivateLocationList = new LinkedList<SolarLocation>();
        sharedLocationList = new LinkedList<SolarLocation>();

        // Sign in the account if the information is already stored on the device
    }

    /**
     * Get instance of SolarAccountManager. This is the account manager associated with the app.
     * We can only get the account manager from this static method since the default constructor
     * is set to be private
     * @return the account manager of the app
     */
    public static SolarAccountManager appAccountManager() {
        if (account == null) {
            account = new SolarAccountManager();
        }
        return account;
    }

    /******************************
     * ACCOUNT CONNECTION METHODS *
     ******************************/

    /**
     *
     * @param email_address
     * @param password
     * @return
     */
    public boolean login(String email_address, String password) {

        boolean sucess = false;

        LOGIN_STATUS = true;

        return sucess;
    }

    /**
     *
     * @param email_address
     * @param password
     * @return
     */
    public boolean signUp(String email_address, String password) {

        boolean[] success = {false};

        try {

            // Create JSON Object containing all information of the new user
            JSONObject signUpInfo = new JSONObject();
            signUpInfo.put("email", email_address);
            signUpInfo.put("password", password);

            HTTPAsyncTask jsonResponse ;
            (jsonResponse = new HTTPAsyncTask() {
                @Override
                protected void onPostExecute(String result) {
                    System.out.println(result);

                    success[0] = true;
                }
            }).execute(this.URL(URIs.ACCOUNT_SIGN_UP), HTTPMethods.POST, signUpInfo.toString());

        } catch (JSONException jsonException) {
            jsonException.printStackTrace();
        }

        return success[0];
    }

    public void signout() {
        LOGIN_STATUS = false;
    }

    /*********************
     * Account Utilities *
     *********************/

    /**
     * Set the email of the app's account. This function should only be called when the user successfully
     * sign up or login the app
     * @param email_address
     * @return
     */
    private void setEmail(String email_address) {
        email = email_address;
    }

    /**
     * Get email address of the user
     * @return email address string
     */
    public String getEmail() throws IllegalAccessException {

        if (email == null) {
            throw new IllegalAccessException("Error: Email address not defined");
        }
        return email;
    }

    /**
     * Set the password of the app's account. This function should only be called when the user successfully
     * sign up or login the app
     * @param password
     * @return
     */
    private void setPassword(String password) {
        this.password = password;
    }

    // Actions for saving or sharing the locations. This enumeration is used only in save location and share location methods
    private enum INTERESTED_LOCATION_ACTION {
        SAVE,
        SHARE
    }

    public boolean saveInterestedLocation(String locationName, double longitude, double latitude) {
        return interestedLocationHelper(INTERESTED_LOCATION_ACTION.SAVE, locationName, longitude, latitude);
    }

    public boolean shareInterestedLocation(String locationName, double longitude, double latitude) {
        return interestedLocationHelper(INTERESTED_LOCATION_ACTION.SHARE, locationName, longitude, latitude);
    }

    private boolean interestedLocationHelper(INTERESTED_LOCATION_ACTION action, String locationName, double longitude, double latitude) {
        final boolean[] success = {false};

        JSONObject geoPoint = new JSONObject();

        try {
            geoPoint.put("UUID", String.valueOf(UUID.randomUUID()));
            geoPoint.put("userEmail", appAccountManager().getEmail());
            geoPoint.put("name", locationName);
            geoPoint.put("longitude", longitude);
            geoPoint.put("latitude", latitude);

            (new HTTPAsyncTask() {
                @Override
                protected void onPostExecute(String result) {


                    success[0] = true;
                }
            }).execute(this.URL((action == INTERESTED_LOCATION_ACTION.SAVE) ? URIs.ACCOUNT_SAVE_LOCATION : URIs.ACCOUNT_SHARE_LOCATION), HTTPMethods.POST, geoPoint.toString());

        } catch (JSONException exception) {
            exception.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        // If successfully save/share location, add it to the local list
        if (success[0] && (action == INTERESTED_LOCATION_ACTION.SAVE)) {

        } else if (success[0] && (action == INTERESTED_LOCATION_ACTION.SHARE)) {

        }

        return success[0];
    }

    /****************************
     * SERVER UTILITIES METHODS *
     ****************************/

    /**
     * Get the URL to connect to server
     * @param URI
     * @return URL string
     */
    private String URL(String URI) {
        return "http://" + IP_ADDRESS + ":" + CONNECTION_PORT + "/" + URI;
    }
}
