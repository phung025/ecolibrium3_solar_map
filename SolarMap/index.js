const SERVER_PORT = 4321;

// All URIs
var URIs = {
  URI_LOGIN: "/API/loginAccount",
  URI_SIGN_UP: "/API/registerAccount",
  URI_GET_ACHIEVEMENTS: "/API/getAchievements",
  URI_SET_LOCATION_INTEREST: "/API/setInterestInLocation"
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

accounts = {
  account_list:[]
};

publicLocations = [];

// The next two sections tell bodyParser which content types to
// parse. We are mainly interested in JSON, but eventually, encoded,
// multipart data may be useful.
app.use(bodyParser.urlencoded({   // support encoded bodies
    extended: true
}));
app.use(bodyParser.json());  // support json encoded bodies

// @*@*@*@*@*@*@*@*@*@*@*@*@*@*@*@*@*@*@
//
// ROUTE Section
//
// This is where you process the GET, POST, PUT, DELETE and other
// potential routes.
//

// ----------------------------------------
// GET
// ----------------------------------------
app.get('/API/getAchievements', function(req, res) {

	//get account from req
	var email_address = req.body.email;
	// Find the achievements for this particular account
	var dataToReturn;
	for (var account in accounts.account_list) {
	    if (account.email === email_address) {
		dataToReturn = account.achievements_list;
		break;
	    }
	}

	console.log('/getAchievements GET URI accessed');
	res.send(JSON.stringify(dataToReturn));
    });

app.get('/API/getPrivateLocation', function(req, res) {

	 //get data from req
    var uuid = req.body.account_id;
	var localLocationList = req.body.location_id_list;

	var myLocationList = getAccountData(accountID,4);
	var retrieved_location_list = [];

	if(myLocationList != null)
	{
	    for (var myLocation in myLocationList) {
		isInMyList = false;
		for(var location in localLocationList) {
		    if (myLocation.LocationID === localLocationList.location_id) {
			isInMyList = true;
			break;
		    }
		}

		if(isInMyList == false) {
		    retrieved_location_list.push(myLocation);
		}
	    }
	}
    console.log('/getPrivateLocation GET URI accessed');
    res.send(JSON.stringify(retrieved_location_list));
});

app.get('/API/getPublicLocation', function(req, res) {

	//get data from req
	var uuid = req.body.account_id;
	var localLocationList = req.body.location_id_list;

	var retrieved_location_list = [];

	if(getAccountData(accountID,0) != null)
	{
		for (var myLocation in PublicLocations) {
			isInMyList = false;
			for(var location in localLocationList) {
				if (myLocation.LocationID === localLocationList.location_id) {
					isInMyList = true;
					break;
				}
			}

			if(isInMyList == false) {
			    retrieved_location_list.push(myLocation);
			}
		}
	}
    console.log('/getPublicLocation GET URI accessed');
    res.send(JSON.stringify(retrieved_location_list));
});

// ----------------------------------------
// POST
// ----------------------------------------

//initial post of username and other info
app.post(URIs.URI_SIGN_UP, function (req, res) {

    // If for some reason, the JSON isn't parsed, return a HTTP ERROR
    // 400
    if (!req.body) return res.sendStatus(400);

    databaseManager.registerAccount(req.body.email, req.body.password, function(isSuccess) {
      if (isSuccess) {
        res.status(200);
        console.log('/API/registerAccount POST accessed and approved');
      } else {
        res.status(409);
        console.log('/API/registerAccount POST accessed but rejected');
      }

      res.send(JSON.stringify({is_registered: isSuccess}));
    });
});

app.post(URIs.URI_LOGIN, function(req, res) {

    // If for some reason, the JSON isn't parsed, return a HTTP ERROR
    // 400
    if (!req.body) return res.sendStatus(400);

    databaseManager.loginAccount(req.body.email, req.body.password, function(isSuccess, authorizationCode) {
      if (isSuccess) {
        res.status(200);
        console.log('/API/loginAccount POST accessed and approved');
      } else {
        res.status(404);
        console.log('/API/loginAccount POST accessed but rejected');
      }

      res.send(JSON.stringify({account_id: authorizationCode}));
    });
});

app.post('/API/setAchievements', function (req, res) {

    // If for some reason, the JSON isn't parsed, return a HTTP ERROR
    // 400
    if (!req.body) return res.sendStatus(400)

    //gather data from req
    //Example:
    //var name = req.body.name;

	 // get this account and its acheivement list
	dataToReturn = getAccountData(2);


    console.log('/setAchievements POST accessed, jsonData=', req.body);

    res.json(req.body);
});

app.post('/API/addPrivateLocation', function (req, res) {

    // If for some reason, the JSON isn't parsed, return a HTTP ERROR
    // 400
    if (!req.body) return res.sendStatus(400)

    //gather data from req
	var accountID = req.body.account_id;
	var myNewLocation = {
		locationID: req.body.location_id,
		locationLongitude: req.body.location_longitude,
		locationLatitude: req.body.location_latitude,
		name: req.body.location_name
	}

	myLocationList = getAccountData(accountID,4);

	if(myLocationList != null)
	{
		myLocationList.push(myNewLocation);
	}

    console.log('/addPrivateLocation POST accessed, jsonData=', req.body);

    res.json(req.body);
});

app.post(URIs.URI_SET_LOCATION_INTEREST, function (req, res) {

  // If for some reason, the JSON isn't parsed, return a HTTP ERROR
  // 400
  if (!req.body) return res.sendStatus(400)

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

// ----------------------------------------
// DELETE
// ----------------------------------------

app.delete('/API/removeSavedLocations', function (req, res) {

    // If for some reason, the JSON isn't parsed, return a HTTP ERROR
    // 400
    if (!req.body) return res.sendStatus(400)

    //var savedLocation = req.body.savedLocation;

    console.log('/removeSavedLocations DELETE accessed, jsonData=', req.body);
    //console.log('   deleting saved location=', savedLocation, ' from the server.');

    var jsonResponse = {
	id: '321',
	status: 'deleted'
    };
    res.json(jsonResponse);
})

app.delete('/removeAccount', function (req, res) {

    // If for some reason, the JSON isn't parsed, return a HTTP ERROR
    // 400
    if (!req.body) return res.sendStatus(400)

    //var savedLocation = req.body.savedLocation;

    console.log('/removeAccount DELETE accessed, jsonData=', req.body);
    //console.log('   deleting saved location=', savedLocation, ' from the server.');

    var jsonResponse = {
	id: '321',
	status: 'deleted'
    };
    res.json(jsonResponse);
})

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

////////////////////////////
// UTILITIES
////////////////////////////

function getAccountData(UID, dataType){
	returnData = null;
	for (var account in accounts.account_list) {
        if (account.userID === UID) {
		/*
		Case 0 : email
		Case 1 : password,
        Case 2 : achievements_list,
        Case 3 : saved_locations_list
		Case 4 : uuid  */
			switch(dataType){
				default:
				case 0:
					dataToReturn = account.email;
					break;
				case 1:
					dataToReturn = account.password;
					break;
				case 2:
					dataToReturn = account.achievements_list;
					break;
				case 3:
					dataToReturn = account.saved_locations_list;
					break;
				case 4:
					dataToReturn = account.userID;
					break;
			}
        }
    }
	return returnData


}
