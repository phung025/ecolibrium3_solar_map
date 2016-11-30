The most up to date node code is currently running as a server using a mongodb on lempo.d.umn.edu (port 2058)

Steps to run:
* This node server uses a mongo as a dependency, setup mongo.
* Install other dependencies by `npm install`
* Start server by `node index.js`

Currently supported restAPI calls:
* GET - `/API/loginAccount/:email/:password`
	* Logs in an already registered account
	* @param {email_address: <String>} String email address
 	* @param {password: <String>} String account password
* POST - `/API/registerAccount`
	* Registers an account
 	* @param {email_address: <String>} String email address
 	* @param {password: <String>} String account password
 	* @return {is_registered: <boolean>} Stringified json value
* DELETE - `/API/deleteAccount`
 	* Remove account from the database
 	* @param {email: <String>} - account email address
 	* @param {password: <String>} - account password
 	* @return {is_success: <boolean>} - stringified JSON data containing boolean value.
 	*         Return true if successfully removed the account, else false
* POST - `/API/changeAccountPassword`
 	* Change account's password
 	* @param {email: <String>} - Account's email address
 	* @param {current_password: <String>} - Account's current password
 	* @param {new_password: <String>} - Account's new password
 	* @return {is_success: <boolean>} - stringified JSON data containing boolean value.
 	*         Return true if successfully change the account's password, else false
* GET - `/API/getAchievements` (WiP)
* POST - `/API/setInterestInLocation` (WiP)
* GET - `/API/getListOfInterestLocations/:account_id/:email/:password/:available_locations`
 	* Get the list of all locations that have people showing interests. Return a list containing all locations that the client does not have instead of getting every locations.
 	* @param account_id: <String> - private ID of the account
 	* @param email: <String> - email address of the account
 	* @param password: <String> - password of the account
 	* @param available_locations: <location_id: <String>> - all available public locations id that the client has. Passed in as a string with elements separated by a comma ','
 	* @return [{location_id: <String>, interest_count: <int>}] - Stringified JSONArray containing all locations that the client does not have
* GET - `/API/getCountInterestInLocation/:account_id/:email/:password/:location_id`
 	* Get the total number of people interested in having solar panel installed in the location
 	* @param account_id: <String> - private ID of the account
 	* @param email: <String> - email address of the account
	* @param password: <String> - password of the account
 	* @param location_id: <String> - ID of the selected location
 	* @return {result: <int>} - total count of people showing interest in the location.
 	*         Return -1 if building not found or -2 if authorizing process failed.

Contributors:
* Nam Phung
* Cody Seavey
* Dale Dowling
* John Sudduth
