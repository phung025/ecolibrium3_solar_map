/////////////////////////////////
// ALL KEYS FOR MONGODB DOCUMENTS
/////////////////////////////////
//
// ACCOUNT_PRIVATE_ID: "account_private_ID",
// ACCOUNT_EMAIL_ADDRESS: "account_email_address",
// ACCOUNT_PASSWORD: "account_password"
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
   * @param email_address String email address
   * @param password String account password
   * @param isSuccessFn callback function to notify once the registering process is finished
   */
  this.registerAccount = function(email_address, password, isSuccessFn) {
    registerAndLoginHelper(ACCOUNT_ACTIONS.REGISTER, email_address, password, isSuccessFn);
  };

  /**
   * Login account
   * @param email_address String email address
   * @param password String account password
   * @param isSuccessFn callback function to notify once the registering process is finished
   */
  this.loginAccount = function(email_address, password, isSuccessFn) {
    registerAndLoginHelper(ACCOUNT_ACTIONS.LOGIN, email_address, password, isSuccessFn);
  }

  function registerAndLoginHelper(action, email_address, password, isSuccessFn) {
    MongoClient.connect(DATABASE_URL, function(err, db) {
      // Get the collection we're going to search
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

};
