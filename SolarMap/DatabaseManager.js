/////////////////////////////////
// ALL KEYS FOR MONGODB DOCUMENTS
/////////////////////////////////
//
// ACCOUNT_PRIVATE_ID: {_id: <ObjectId>}
// ACCOUNT_EMAIL_ADDRESS: {account_email_address: <String>}
// ACCOUNT_PASSWORD: {account_password: <String>}
//
// Public locations: {{location_ID:<String>, location_name:<String>, users_interested:[], total_users_interested:<Int>}, ...}
//
/////////////////////////////////

module.exports = function DatabaseManager() {

  // MongoDB
  var MongoClient = require('mongodb').MongoClient;
  var DATABASE_URL = "mongodb://localhost:27017/ecolibrium_solarDB";
  var COLLECTIONS = {
    COLLECTION_USER_ACCOUNT_DATABASE: "USER_ACCOUNT_DATABASE",
    COLLECTION_PUBLIC_LOCATIONS: "PUBLIC_LOCATIONS"
  };

  /**
   * Connect to the MongoDB database
   */
  this.connect = function() {
    // Connect to the db
    MongoClient.connect(DATABASE_URL, function(err, db) {
      if(!err) {

        console.log("\n============================================================\n");

        console.log("We're connected to MongoDB\n");

        // Set up collection containing user account
        console.log("Creating collection " + COLLECTIONS.COLLECTION_USER_ACCOUNT_DATABASE + "...");
        db.createCollection(COLLECTIONS.COLLECTION_USER_ACCOUNT_DATABASE, function(err, collection) {});

        // Set up collection containing all public solar locations in Duluth
        console.log("Creating collection " + COLLECTIONS.COLLECTION_PUBLIC_LOCATIONS + "...");
        db.createCollection(COLLECTIONS.COLLECTION_PUBLIC_LOCATIONS, function(err, collection) {});

        console.log("\nFinished setting up database");

        console.log("\n============================================================\n");
      } else {
      	console.log("Error occured while trying to connect to MongoDB")
      }
    });
  };

  var ACCOUNT_ACTIONS = {
    REGISTER : 0,
    LOGIN : 1
  };

  /**
   * Register an account
   *
   * @param email_address String email address
   * @param password String account password
   * @param isSuccessFn callback function to notify once the registering process is finished
   */
  this.registerAccount = function(email_address, password, isSuccessFn) {
    registerAndLoginHelper(ACCOUNT_ACTIONS.REGISTER, email_address, password, isSuccessFn);
  };

  /**
   * Login account
   *
   * @param email_address String email address
   * @param password String account password
   * @param isSuccessFn callback function to notify once the registering process is finished
   */
  this.loginAccount = function(email_address, password, isSuccessFn) {
    registerAndLoginHelper(ACCOUNT_ACTIONS.LOGIN, email_address, password, isSuccessFn);
  };

  function registerAndLoginHelper(action, email_address, password, isSuccessFn) {
    MongoClient.connect(DATABASE_URL, function(err, db) {

      var search_collection = db.collection(COLLECTIONS.COLLECTION_USER_ACCOUNT_DATABASE);
      search_collection.findOne({account_email_address: email_address}).then(function(doc) {

        // Check if account already existed
        var isAccountExisted = (doc != null) ? true : false;
        if (!isAccountExisted) { // Account is not exist

          if (action === ACCOUNT_ACTIONS.REGISTER) {
            var new_account = {
              account_email_address: email_address,
              account_password: password
            };

            // Insert new account to the collection
            search_collection.insertOne(new_account);

            // Respond with the private ID
            isSuccessFn(true);
          } else if (action === ACCOUNT_ACTIONS.LOGIN) {
            // Respond with empty string
            isSuccessFn(false, "");
          }

        } else { // Account is already existed
          if (action === ACCOUNT_ACTIONS.REGISTER) {
            // Failed to register an account because
            // the it's already exist
            isSuccessFn(false);
          } else if (action === ACCOUNT_ACTIONS.LOGIN) {

            if (doc.account_password === password) {
              isSuccessFn(true, doc._id.toString());
            } else {
              isSuccessFn(false, "");
            }
          }
        }
      });
    });
  }

  /**
   *
   *
   *
   */
  this.setInterestInLocation = function(authorization_ID, email_address, password, locationID, locationName, completionFN) {
    MongoClient.connect(DATABASE_URL, function(err, db) {

      isAuthorized(authorization_ID, email_address, password, function(isAllowed) {

        if (isAllowed) {

          var public_locations_collection = db.collection(COLLECTIONS.COLLECTION_PUBLIC_LOCATIONS);

          // FIND IF THE LOCATION IS IN THE LIST
          public_locations_collection.findOne({location_ID: locationID}).then(function(location_document) {

            var isLocationFound = (location_document != null) ? true : false;
            if (isLocationFound) {

              // FIND IF USER ALREADY SHOW INTEREST IN THE LOCATION
              public_locations_collection.findOne({$and:[{location_ID: locationID},{users_interested:authorization_ID}]}).then(function(location_document1) {

                var filter = {location_ID: locationID};
                var update = (location_document1 != null) ? {$pop:{users_interested:authorization_ID},$inc:{total_users_interested:-1}} : {$push:{users_interested:authorization_ID},$inc:{total_users_interested:1}};

                // Update interest list [Either remove or add interest]
                public_locations_collection.updateOne(filter, update);
              });
            } else { // LOCATION IS NOT IN THE DATABASE, CREATE NEW LOCATION AND ADD IT TO DATABASE

              var new_location = {
                location_ID: locationID,
                location_name: locationName,
                users_interested: [authorization_ID],
                total_users_interested: 1
              };
              public_locations_collection.insertOne(new_location);
            }
          });

          // Successfully set interest in location
          completionFN(true);
        } else {

          // Failed to get authorized for setting interest in a location
          completionFN(false);
        }
      });
    });
  };

  /**
   * Get the list of all locations that have people showing interests. Return
   * a list containing all locations that the client does not have instead
   * of getting every locations.
   *
   * @param authorization_ID: <String> - private ID of the account
   * @param email_address: <String> - email address of the account
   * @param password: <String> - password of the account
   * @param available_locations: <[location_id: <String>]> - array of type string
   *        with all available locations id that the client has
   * @param completionFN - callback function when the query is finish
   * @return [{location_id: <String>, interest_count: <int>}] - Stringified JSONArray
   *         containing all locations that the client does not have
   */
  this.getListOfInterestLocations = function(authorization_ID, email_address, password, available_locations, completionFN) {
    MongoClient.connect(DATABASE_URL, function(err, db) {
      isAuthorized(authorization_ID, email_address, password, function(isAllowed){

        if (isAllowed) {

          // Find all locations that are not in the list
          db.collection(COLLECTIONS.COLLECTION_PUBLIC_LOCATIONS).find({location_ID:{$nin:available_locations}}).toArray().then(function(docs) {

            // Map the array to new array containing only needed data. i.e. total people showing interest & location id
            var returned_list = docs.map(function(element) {
              var mappedJSONData = {
                location_id: element.location_ID,
                interest_count: element.total_users_interested
              };
              return mappedJSONData;
            });

            completionFN(true, returned_list);
          });
        } else {
          // Failed to get authorization, return false and empty list
          completionFN(false, []);
        }
      });
    });
  };

  /**
   * Get the total number of people interested in having solar panel installed in
   * the location
   *
   * @param account_id: <String> - private ID of the account
   * @param email: <String> - email address of the account
   * @param password: <String> - password of the account
   * @param location_id: <String> - ID of the selected location
   * @return result: <int> - total count of people showing interest in the location
   *         Return -1 if building not found or authorization process failed.
   */
  this.getCountInterestInLocation = function(authorization_ID, email_address, password, locationID, completionFN) {
    MongoClient.connect(DATABASE_URL, function(err, db) {
      isAuthorized(authorization_ID, email_address, password, function(isAllowed) {
        if (isAllowed) {
          var public_locations_collection = db.collection(COLLECTIONS.COLLECTION_PUBLIC_LOCATIONS);
          public_locations_collection.findOne({location_ID:locationID}).then(function(location_document) {
            if (location_document != null) {
              // Return the total count of people having interest in the location
              completionFN(location_document.total_users_interested);
            } else {
              // Location is not found
              completionFN(-1);
            }
          });
        } else {
          // Can't get authorization for retrieving the count
          completionFN(-1);
        }
      });
    });
  };

  /**
   * Get authorization using account's private ID, account email address, and account password
   *
   * @param authorization_id: <String> - account's private ID
   * @param email_address: <String> - account's email address
   * @param password: <String> - account's password
   * @param completionFN - callback function once the authorizing process is finished
   */
  function isAuthorized(authorization_id, email_address, password, completionFN) {
    MongoClient.connect(DATABASE_URL, function(err, db) {

      var {ObjectId} = require('mongodb'); // or ObjectID
      var authorizeQuery = {
        $and: [
          {_id: ObjectId(authorization_id)},
          {account_email_address: email_address},
          {account_password: password}
        ]
      };

      db.collection(COLLECTIONS.COLLECTION_USER_ACCOUNT_DATABASE).findOne(authorizeQuery).then(function(user_document) {

        var isAuthorized = (user_document != null) ? true : false;
        if (isAuthorized) { // User is authorized
          completionFN(true);
        } else {
          completionFN(false);
        }
      });
    });
  }

};
