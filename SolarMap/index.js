const SERVER_PORT = 4321;

// All URIs
var URIs = {
  URI_LOGIN: "/API/loginAccount/:email/:password",
  URI_SIGN_UP: "/API/registerAccount",
  URI_DELETE_ACCOUNT: "/API/deleteAccount",
  URI_GET_ACHIEVEMENTS: "/API/getAchievements",
  URI_SET_LOCATION_INTEREST: "/API/setInterestInLocation",
  URI_GET_LIST_OF_INTEREST_LOCATIONS: "/API/getListOfInterestLocations/:account_id/:email/:password/:json_available_locations",
  URI_GET_COUNT_INTEREST_IN_LOCATION: "/API/getCountInterestInLocation/:account_id/:email/:password/:location_id"
};
///////////////////
// Database
///////////////////
var databaseManager = new (require('./DatabaseManager.js'))();
databaseManager.connect();

/////////////////////////
// Node Server
/////////////////////////
var express = require('express');
var bodyParser = require('body-parser');

// The main instanced class, called app will be initialized by express
var app = express();

// Set the port in the app system; MAY WANT TO CHANGE LATER
app.set("port", SERVER_PORT);

// The next two sections tell bodyParser which content types to
// parse. We are mainly interested in JSON, but eventually, encoded,
// multipart data may be useful.
app.use(bodyParser.urlencoded({   // support encoded bodies
    extended: true
}));
app.use(bodyParser.json());  // support json encoded bodies

/**
 * Register an account
 *
 * @param {email_address: <String>} String email address
 * @param {password: <String>} String account password
 * @return {is_registered: <boolean>} Stringified json value
 */
app.post(URIs.URI_SIGN_UP, function (req, res) {

    // If for some reason, the JSON isn't parsed, return a HTTP ERROR
    // 400
    if (!req.body) return res.sendStatus(400);

    databaseManager.registerAccount(req.body.email, req.body.password, function(isSuccess) {
      if (isSuccess) {
        res.status(200);
        console.log(URIs.URI_SIGN_UP + ' POST accessed and approved');
      } else {
        res.status(409);
        console.log(URIs.URI_SIGN_UP + ' POST accessed but rejected');
      }

      res.send(JSON.stringify({is_registered: isSuccess}));
    });
});

app.get(URIs.URI_LOGIN, function(req, res) {

  // If for some reason, the JSON isn't parsed, return a HTTP ERROR
  // 400
  if (!req.params) return res.sendStatus(400);

  databaseManager.loginAccount(req.params.email, req.params.password, function(isSuccess, authorizationCode) {
    if (isSuccess) {
      res.status(200);
      console.log(URIs.URI_LOGIN + ' GET accessed and approved');
    } else {
      res.status(404);
      console.log(URIs.URI_LOGIN + ' GET accessed but rejected');
    }

    res.send(JSON.stringify({account_id: authorizationCode}));
  });
});

/**
 * Remove account from the database
 *
 * @param {email: <String>} - account email address
 * @param {password: <String>} - account password
 * @return {is_success: <boolean>} - stringified JSON data containing boolean value.
 *         Return true if successfully removed the account, else false
 */
app.delete(URIs.URI_DELETE_ACCOUNT, function(req, res) {
  // If for some reason, the JSON isn't parsed, return a HTTP ERROR
  // 400
  if (!req.params) return res.sendStatus(400);

  databaseManager.removeAccount(req.body.email, req.body.password, function(isSuccess) {
    if (isSuccess) {
      re.status(200);
      console.log(URIs.URI_DELETE_ACCOUNT + ' DELETE accessed and approved');
    } else {
      res.status(404);
      console.log(URIs.URI_DELETE_ACCOUNT + ' DELTE accessed but rejected');
    }
    res.send(JSON.stringify({is_success:isSuccess}));
  });
});

//databaseManager.setInterestInLocation("5831135cd39e652cd8241dab", "phungle.thanhnam@gmail.com", "password2", "516721", "University of Minnesota Duluth", function(isSuccess){});
//databaseManager.setInterestInLocation("5831135cd39e652cd8241dac", "phung025@d.umn.ed", "password1", "516721", "University of Minnesota Duluth", function(isSuccess){});

/**
 * Get the total number of people interested in having solar panel installed in
 * the location
 *
 * @param account_id: <String> - private ID of the account
 * @param email: <String> - email address of the account
 * @param password: <String> - password of the account
 * @param location_id: <String> - ID of the selected location
 * @return {result: <int>} - total count of people showing interest in the location.
 *         Return -1 if building not found or -2 if authorizing process failed.
 */
app.get(URIs.URI_GET_COUNT_INTEREST_IN_LOCATION, function(req, res) {

  // If for some reason, the JSON isn't parsed, return a HTTP ERROR
  // 400
  if (!req.params) return res.sendStatus(400);

  //gather data from req
  var account_id = req.params.account_id;
  var email = req.params.email;
  var password = req.params.password;
  var location_id = req.params.location_id;

  databaseManager.getCountInterestInLocation(account_id, email, password, location_id, function(returned_size) {
    if (returned_size < 0) {
      res.status((returned_size === -1) ? 404 : 409);
      console.log(URIs.URI_GET_COUNT_INTEREST_IN_LOCATION + ' GET accessed and rejected');
    } else {
      res.status(200);
      console.log(URIs.URI_GET_COUNT_INTEREST_IN_LOCATION + ' GET accessed and accepted');
    }
    res.send(JSON.stringify({result:returned_size}));
  });
});

/**
 *
 *
 *
 *
 *
 *
 */
app.post(URIs.URI_SET_LOCATION_INTEREST, function(req, res) {

  // If for some reason, the JSON isn't parsed, return a HTTP ERROR
  // 400
  if (!req.body) return res.sendStatus(400);

  //gather data from req
  var account_id = req.body.account_id;
  var email = req.body.email;
  var password = req.body.password;
  var location_id = req.body.location_id;
  var location_name = req.body.location_name;

  databaseManager.setInterestInLocation(account_id, email, password, location_id, location_name, function(isSuccess) {

    if (isSuccess) {
      res.status(200);
      console.log(URIs.URI_SET_LOCATION_INTEREST + ' POST accessed and accepted');
    } else {
      res.status(409);
      console.log(URIs.URI_SET_LOCATION_INTEREST + ' POST accessed but rejected');
    }
  });
});

/**
 * Get the list of all locations that have people showing interests. Return
 * a list containing all locations that the client does not have instead
 * of getting every locations.
 *
 * @param account_id: <String> - private ID of the account
 * @param email: <String> - email address of the account
 * @param password: <String> - password of the account
 * @param available_locations: <location_id: <String>> - all available public
 *        locations id that the client has. Passed in as a string with elements
 *        separated by a comma ','
 * @return [{location_id: <String>, interest_count: <int>}] - Stringified JSONArray
 *         containing all locations that the client does not have
 */
app.get(URIs.URI_GET_LIST_OF_INTEREST_LOCATIONS, function(req, res) {

  // If for some reason, the JSON isn't parsed, return a HTTP ERROR
  // 400
  if (!req.params) return res.sendStatus(400);

  //gather data from req
  var account_id = req.params.account_id;
  var email = req.params.email;
  var password = req.params.password;
  var available_locations = (req.params.available_locations).split(',');

  databaseManager.getListOfInterestLocations(account_id, email, password, available_locations, function(isSuccess, returned_list) {
    if (isSuccess) {
      res.status(200);
      console.log(URIs.URI_GET_LIST_OF_INTEREST_LOCATIONS + ' GET accessed and accepted');
    } else {
      res.status(409);
      console.log(URIs.URI_GET_LIST_OF_INTEREST_LOCATIONS + ' GET accessed and rejected');
    }

    res.send(JSON.stringify(returned_list));
  });
});

//
// respond with basic webpage HTML when a GET request is made to the homepage path /
//
app.get('/', function(req, res) {
    res.send('<HTML><HEAD></HEAD><BODY><H1>Solar Map Node Server</H1></BODY></HTML>');
    console.log(req);
});

// ERROR Conditions
// ----------------
// page not found - 404
app.use(function(req, res, next) {
  res.status(404).send('Sorry; Your request is not found');
});

// page not found - 404
app.use(function(err, req, res, next) {
  console.error(err.stack);
  res.status(500).send('Internal Server Error message - very strange request came in and we do not know how to handle it!!!');
});

app.listen(app.get("port"), function () {
    console.log('Ecolibrium3 Solar Map Android App, listening on port:', app.get("port"));
});
