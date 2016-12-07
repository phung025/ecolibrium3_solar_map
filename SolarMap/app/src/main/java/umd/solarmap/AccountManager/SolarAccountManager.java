package umd.solarmap.AccountManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import javax.security.auth.callback.Callback;

import umd.solarmap.RestAPI.HTTPAsyncTask;
import umd.solarmap.RestAPI.HTTPMethods;
import umd.solarmap.SolarData.SolarAchievement;
import umd.solarmap.SolarData.SolarLocation;
import umd.solarmap.UtilitiesClasses.CallbackFunction;

/**
 * Author: Nam Phung
 */

public class SolarAccountManager implements Serializable {

    /**
     * Singleton pattern, the instance of this class can only be returned when calling
     * static method appAccountManager
     */
    private static SolarAccountManager account;

    // URI Links
    private class URIs {
        public static final String URI_ACCOUNT_LOGIN = "/API/loginAccount";
        public static final String URI_ACCOUNT_SIGN_UP = "/API/registerAccount";
        public static final String URI_DELETE_ACCOUNT = "/API/deleteAccount";
        public static final String URI_CHANGE_ACCOUNT_PASSWORD = "/API/changeAccountPassword";
        public static final String URI_GET_ACHIEVEMENTS = "/API/getAchievements";
        public static final String URI_SET_LOCATION_INTEREST = "/API/setInterestInLocation";
        public static final String URI_GET_LIST_OF_INTEREST_LOCATIONS = "/API/getListOfInterestLocations";
        public static final String URI_GET_COUNT_INTEREST_IN_LOCATION = "/API/getCountInterestInLocation";
    }

    // Server connection info
    private static final int CONNECTION_PORT = 2058;
    private static final String URL = "https://lempo.d.umn.edu";
    //10.0.2.2

    // User's account info
    private String account_private_id = null; // This one should never be public
    private String account_email = null;
    private String account_password = null;

    // User's account data
    List<SolarAchievement> solarAchievementList; // User's achievements list
    Map<String, Integer> sharedLocationList; // Global locations list shared by all users

    /**
     * Defaul constructor. Declared as private in order to use singleton pattern
     */
    private SolarAccountManager() {

        // Set up all initial components of this class
        solarAchievementList = new LinkedList<SolarAchievement>();
        sharedLocationList = new HashMap<>();

        // Sign in the account if the information is already stored on the device
    }

    /**
     * Get instance of SolarAccountManager. This is the account manager associated with the app.
     * We can only get the account manager from this static method since the default constructor
     * is set to be private
     *
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

    private enum ACCOUNT_ACTION {
        REGISTER,
        LOG_IN
    }

    /**
     * Login to the app
     *
     * @param email_address
     * @param input_password
     * @return
     */
    public boolean login(String email_address, String input_password) {
        return loginSignUpHelper(ACCOUNT_ACTION.LOG_IN, email_address, input_password);
    }

    /**
     * Sign up account for the app
     *
     * @param email_address
     * @param input_password
     * @return
     */
    public boolean registerAccount(String email_address, String input_password) {
        return loginSignUpHelper(ACCOUNT_ACTION.REGISTER, email_address, input_password);
    }

    /**
     * @param action
     * @param email_address
     * @param input_password
     * @return
     */
    private boolean loginSignUpHelper(ACCOUNT_ACTION action, String email_address, String input_password) {
        boolean[] success = {false};

        JSONObject jsonData = new JSONObject();
        try {
            // Create JSON Object containing all information of the new user
            jsonData.put("email", email_address);
            jsonData.put("password", input_password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            String result = (new HTTPAsyncTask() {
                @Override
                protected void onPostExecute(String result) {}
            }).execute(URL((action == ACCOUNT_ACTION.LOG_IN) ? (URIs.URI_ACCOUNT_LOGIN + "/" + email_address + "/" + input_password) : URIs.URI_ACCOUNT_SIGN_UP),
                    (action == ACCOUNT_ACTION.LOG_IN) ? HTTPMethods.GET : HTTPMethods.POST,
                    (action == ACCOUNT_ACTION.LOG_IN) ? "" : jsonData.toString()).get();

            JSONObject jsonResponse = new JSONObject(result);
            if (action == ACCOUNT_ACTION.LOG_IN) {
                String accountID = String.valueOf(jsonResponse.get("account_id"));

                // if successfully sign up, the client will receive the private user ID
                if (!accountID.equals("")) {

                    // Set email & password if successfully sign in
                    account_private_id = String.valueOf(jsonResponse.get("account_id"));
                    setEmail(email_address);
                    setPassword(input_password);

                    success[0] = true;
                }
            } else if (action == ACCOUNT_ACTION.REGISTER) {
                String isSuccess = String.valueOf(jsonResponse.get("is_registered"));
                // if successfully sign up, the client will receive the private user ID
                if (isSuccess.equals("true")) success[0] = true;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return success[0];
    }


    public void signOut() {

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

    /**
     * Set the account's private ID
     * @param id account's private ID
     */
    private void setAccountPrivateID(String id) {
        account_private_id = id;
    }

    /**
     * Get the account's private ID
     * @return String account's private ID
     */
    private String getAccountPrivateID() throws IllegalAccessException {
        if (account_private_id == null) {
            throw new IllegalAccessException("Error: User's not logged in yet");
        }
        return account_private_id;
    }

    public void shareInterestInLocation(String locationID, Callback callbackFunction) {

        JSONObject jsonRequest = new JSONObject();

        try {
            jsonRequest.put("account_id", account_private_id);
            jsonRequest.put("email", account_email);
            jsonRequest.put("password", account_password);
            jsonRequest.put("location_id", locationID);

            (new HTTPAsyncTask() {
                @Override
                protected void onPostExecute(String result) {

                    try {
                        JSONObject jsonResult = new JSONObject(result);

                        // Update the list if there's anything changed
                        updateListOfInterestedLocation(callbackFunction);

                        // Display dialog showing whether the operation is successful
                        System.out.println(jsonResult.get("is_success").toString().equals("true") ? "Successfully set interest" : "Failed to set interest");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }).execute(URL(URIs.URI_SET_LOCATION_INTEREST), HTTPMethods.POST, jsonRequest.toString());

        } catch (JSONException exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Get the list of public locations. This function will update the list before returning it. Because of that,
     * user need to pass in a callback function. Once it finish updating the list, it will be return through the callback function
     * @param callbackFunction
     */
    public void getListOfInterestedLocation(Callback callbackFunction) {
        updateListOfInterestedLocation(callbackFunction);
    }

    private void updateListOfInterestedLocation(Callback callbackFunction) {

        try {
            JSONArray availableLocations = new JSONArray();
            for (Map.Entry<String, Integer> entry : sharedLocationList.entrySet()) {
                availableLocations.put(new JSONObject().put("location_ID", entry.getKey()).put("interest_count", entry.getValue()));
            }

            JSONObject request = new JSONObject();
            request.put("account_id", account_private_id);
            request.put("email", account_email);
            request.put("password", account_password);
            request.put("available_locations", availableLocations);

            (new HTTPAsyncTask() {
                @Override
                protected void onPostExecute(String result) {
                    try {
                        JSONArray returnedList = new JSONArray(String.valueOf((new JSONObject(result).get("location_list"))));
                        for (int i = 0; i < returnedList.length(); ++i) {
                            JSONObject arrayElement = (JSONObject) returnedList.get(i);
                            String location_id = String.valueOf(arrayElement.get("location_id"));
                            Integer interest_count = Integer.parseInt(String.valueOf(arrayElement.get("interest_count")));

                            // If interest count is 0, remove the location id from the map
                            if (interest_count.intValue() == 0) {
                                sharedLocationList.remove(location_id);
                            } else {
                                sharedLocationList.put(location_id, interest_count);
                            }
                        }

                        // Execute callback once the update process is finish
                        ((CallbackFunction)callbackFunction).onPostExecute(sharedLocationList);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }).execute(URL(URIs.URI_GET_LIST_OF_INTEREST_LOCATIONS), HTTPMethods.POST, request.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /****************************
     * SERVER UTILITIES METHODS *
     ****************************/

    /**
     * Get the URL to connect to server
     *
     * @param URI
     * @return URL string
     */
    private String URL(String URI) {
        return URL + ":" + CONNECTION_PORT + URI;
    }
}
