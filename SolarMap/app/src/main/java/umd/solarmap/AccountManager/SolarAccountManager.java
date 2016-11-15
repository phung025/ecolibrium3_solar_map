package umd.solarmap.AccountManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import umd.solarmap.HTTPAsyncTask;
import umd.solarmap.SolarData.SolarAchievement;

/**
 * Created by user on 11/15/16.
 */

public class SolarAccountManager implements Serializable {

    /**
     * Singleton pattern, the instance of this class can only be returned when calling
     * static method appAccountManager
     */
    private static SolarAccountManager account;

    // Server connection info
    private static int CONNECTION_PORT = 4321;
    private static String IP_ADDRESS = "10.0.2.2";

    // Account login status
    public static boolean LOGIN_STATUS = false;

    // User's account info
    private String email = null;
    private String password = null;

    // User's account data
    List<SolarAchievement> solarAchievementList;

    /**
     * Defaul constructor. Declared as private in order to use singleton pattern
     */
    private SolarAccountManager() {
        solarAchievementList = new LinkedList<SolarAchievement>();
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

    /**
     *
     * @param email_address
     * @return
     */
    public boolean setEmail(String email_address) {

        boolean success = false;
        if (email == null) {
            email = email_address;
            success = true;
        }

        return success;
    }

    /**
     *
     * @return
     */
    public String getEmail() {
        return email;
    }

    /**
     *
     * @param password
     * @return
     */
    public boolean setPassword(String password) {

        boolean success = false;
        if (this.password == null) {
            this.password = password;
            success = true;
        }

        return success;
    }

    public void login() {


        LOGIN_STATUS = true;
    }

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
            }).execute("http://10.0.2.2:4321/registerAccount", "POST", signUpInfo.toString());

        } catch (JSONException jsonException) {
            jsonException.printStackTrace();
        }

        return success[0];
    }

    public void signout() {
        LOGIN_STATUS = false;
    }

    /**
     * Account Utilities
     */
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

        String requestURI = "saveLocation";
        if (action == INTERESTED_LOCATION_ACTION.SHARE) {
            requestURI = "shareLocation";
        }

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
            }).execute("http://" + IP_ADDRESS + ":" + CONNECTION_PORT + "/" + requestURI, "POST", geoPoint.toString());

        } catch (JSONException exception) {
            exception.printStackTrace();
        }

        return success[0];
    }

}
