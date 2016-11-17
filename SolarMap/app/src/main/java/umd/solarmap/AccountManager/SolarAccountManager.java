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
        public static final String ACCOUNT_LOGIN = "/login";
        public static final String ACCOUNT_SIGN_UP = "/registerAccount";
        public static final String ACCOUNT_SAVE_LOCATION = "/addPrivateLocation";
        public static final String ACCOUNT_SHARE_LOCATION = "/addPublicLocation";
        public static final String ACCOUNT_LOAD_PRIVATE_LOCATION = "/getPrivateLocation";
        public static final String ACCOUNT_LOAD_PUBLIC_LOCATION = "/getPublicLocation";
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
    private String account_private_id = null; // This one should never be public
    private String account_email = null;
    private String account_password = null;

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
     * Login to the app
     * @param email_address
     * @param input_password
     * @return
     */
    public boolean login(String email_address, String input_password) {

        boolean[] success = {false};

        try {

            // Create JSON Object containing all information of the new user
            JSONObject loginInfo = new JSONObject();
            loginInfo.put("email", email_address);
            loginInfo.put("password", input_password);

            HTTPAsyncTask jsonResponse ;
            (jsonResponse = new HTTPAsyncTask() {
                @Override
                protected void onPostExecute(String result) {
                    System.out.println(result);

                    success[0] = true;

                    // Set email & password if successfully sign up
                    setEmail(email_address);
                    setPassword(input_password);
                    LOGIN_STATUS = true;
                }
            }).execute(URL(URIs.ACCOUNT_LOGIN), HTTPMethods.GET, loginInfo.toString());

        } catch (JSONException jsonException) {
            jsonException.printStackTrace();
        }

        return success[0];
    }

    /**
     * Sign up account for the app
     * @param email_address
     * @param input_password
     * @return
     */
    public boolean registerAccount(String email_address, String input_password) {

        boolean[] success = {false};

        try {

            // Create JSON Object containing all information of the new user
            JSONObject signUpInfo = new JSONObject();
            signUpInfo.put("email", email_address);
            signUpInfo.put("password", input_password);

            HTTPAsyncTask jsonResponse ;
            (jsonResponse = new HTTPAsyncTask() {
                @Override
                protected void onPostExecute(String result) {
                    System.out.println(result);

                    success[0] = true;

                    // Set email & password if successfully sign up
                    setEmail(email_address);
                    setPassword(input_password);
                }
            }).execute(URL(URIs.ACCOUNT_SIGN_UP), HTTPMethods.POST, signUpInfo.toString());

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
        account_email = email_address;
    }

    /**
     * Get email address of the user
     * @return email address string
     */
    public String getEmail() throws IllegalAccessException {

        if (account_email == null) {
            throw new IllegalAccessException("Error: User's not logged in yet");
        }
        return account_email;
    }

    /**
     * Set the password of the app's account. This function should only be called when the user successfully
     * sign up or login the app
     * @param password
     * @return
     */
    private void setPassword(String password) {
        account_password = password;
    }

    /**
     * Get account password
     * @return password string
     */
    private String getPassword() throws IllegalAccessException {
        if (account_password == null) {
            throw new IllegalAccessException("Error: User's not logged in yet");
        }
        return account_password;
    }

    // Actions for saving or sharing the locations. This enumeration is used only in save location and share location methods
    private enum INTERESTED_LOCATION_ACTION {
        SAVE,
        SHARE
    }

    public boolean saveInterestedLocation(String locationName, double longitude, double latitude) {
        return interestedLocationHelper(INTERESTED_LOCATION_ACTION.SAVE,
                locationName,
                longitude,
                latitude);
    }

    public boolean shareInterestedLocation(String locationName, double longitude, double latitude) {
        return interestedLocationHelper(INTERESTED_LOCATION_ACTION.SHARE,
                locationName,
                longitude,
                latitude);
    }

    private boolean interestedLocationHelper(INTERESTED_LOCATION_ACTION action, String locationName, double longitude, double latitude) {
        final boolean[] success = {false};

        JSONObject geoPoint = new JSONObject();

        try {
            geoPoint.put("account_id", account_private_id);
            geoPoint.put("location_id", String.valueOf(UUID.randomUUID()));
            geoPoint.put("location_name", locationName);
            geoPoint.put("location_longitude", longitude);
            geoPoint.put("location_latitude", latitude);

            (new HTTPAsyncTask() {
                @Override
                protected void onPostExecute(String result) {


                    success[0] = true;
                }
            }).execute(URL((action == INTERESTED_LOCATION_ACTION.SAVE) ? URIs.ACCOUNT_SAVE_LOCATION : URIs.ACCOUNT_SHARE_LOCATION), HTTPMethods.POST, geoPoint.toString());

        } catch (JSONException exception) {
            exception.printStackTrace();
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
        return "http://" + IP_ADDRESS + ":" + CONNECTION_PORT + URI;
    }
}
