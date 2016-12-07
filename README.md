This repo is divided into two parts: a node server and a android client. View each of the perspective README's for more information on how to run this project.

This project is aimed at presenting the solar information provided to us by the "Duluth Shines!" and to add distributed aspects to the map in order to gain interest in solar.
* javascript map : `http://umd-cla-gis04.d.umn.edu/duluthshines/`
* Map on esri umn servers : `http://umn.maps.arcgis.com/home/item.html?id=53151b88aa124cf09d5a58c02bfe5a33`

Purpose of app:
* The aim of our app was to recreate the “Duluth Shines!” solar map on the android platform in order to add features that are specific to a phone.  
* We also wanted to allow the sharing of data in order to show how popular solar is, and get rid of the misconception that no one has it.

User stories conquered:
* User wants to be able to:
	* see all of the important elements of the “Duluth Shines! Solar Map” on android.
	* let others know that they are interested in solar at a particular location. 
	* educate themselves on the importance of solar energy.
	* see how common solar projects and installations are.

Technical Description of Distributed app
* Map Basics
	* Our app uses Esri maps rather than Google Maps.
	* We used Esri since the previous map used the Esri runtime and because it is more GiS focused than Google Maps.
	* The solar related data we are working with and displaying on the map comes from a project that the GiS department at the school is working on. (We get this information via the REST API we were provided)
* Client 
	* The information is stored & retrieved using a centralized class AccountManager.
	* The client retrieves the locations that people are interested in, and places a marker on them for easier identification.
	* The client also pulls list of past solar projects from a website and marks them with a clickable marker.

Features we would like to be in app:
* A better system for showing people the most popular solar places in Duluth, possibly by adding a gradient layer or changing the color of each pin depending on how many “likes” it has.
* Achievements, or another method to encourage use of the app. (Gamify!)
* “GeoCaching” popular solar spots
* Comment threads on each of the buildings, organized by building IDs
* Utilize phone hardware to calculate solar in an area

Last built sucessfully using (dependencies):
* Android Studio -- v2.2.2
* Node.js -- v6.9.2
* mongodb  -- community server v3.4.0

--- 

## Node Server

The most up to date node code is currently running as a server using a mongodb on lempo.d.umn.edu (port 2058)

Steps to run:
* Start mongodb - `mongod`
* Install other dependencies -- `npm install`
* Start the server -- `npm start`

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

---

## Android Client

Notes:
* Node server is a dependency
* The node server that it is currently using is lempo.d.umn.edu:2058 (If you would like to run the server on your look at the servers README.)
* Password are in plain text so do not reuse password from an account that has ANY personal information on it..

Development notes:
* To change the server ip from `lempo.d.umn.edu` update the CONNECTION_PORT and URL variables in the SolarAccountManager.java file.

Contributors:
* Nam Phung
* Cody Seavey
* Dale Dowling
* John Sudduth




