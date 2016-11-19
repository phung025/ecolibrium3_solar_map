module.exports = function DatabaseManager() {

  // MongoDB
  const MongoClient = require('mongodb').MongoClient;
  const DATABASE_URL = "mongodb://localhost:27017/ecolibrium_solarDB";
  const COLLECTIONS = {
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
  }
}
