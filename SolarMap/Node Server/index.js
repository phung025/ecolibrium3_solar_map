var express = require('express');


var bodyParser = require('body-parser');

// The main instanced class, called app will be initialized by express
var app = express()

// Set the port in the app system; MAY WANT TO CHANGE LATER
app.set("port", 4321);

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
app.get('/getAchievements', function(req, res) {
	
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

app.get('/getPrivateLocation', function(req, res) {
	
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

app.get('/getPublicLocation', function(req, res) {
	
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
app.post('/registerAccount', function (req, res) {

    // If for some reason, the JSON isn't parsed, return a HTTP ERROR
    // 400
    if (!req.body) return res.sendStatus(400)

    //gather data from req
    var email_address = req.body.email;
    var myPassword = req.body.password;

    /*Need to check whether email is valid, password validation if desired*/
    
    //find out if the account already exists
    var accountExists = false;
    for (var account in accounts.account_list) {
        if (account.email === email_address) {
	    accountExists = true;
	    break;
        }
    }

    var jsonResponse = {
    };

    // Check whether email address is already in the database or not
    if (!accountExists) {
	
	var achievements_list = { 
	    //some Example Achievements
            Have20Friends : false,
            Saved10Locations : false,
	};
	
	var saved_locations_list = [];
	
	var jsonAccount = {
            'email': email_address,
            'password': myPassword,
            'achievements': achievements_list,
            'savedLocation' : saved_locations_list,
	    'userID' : guid()
	};

	// Respond with the account private ID
	jsonResponse = {
	    'account_id': jsonAccount.userID;
	};
	
	accounts.account_list.push(jsonAccount);
    } else {
	
	res.status(409).send('Account information already exists.');

	// Respond with the empty string in account private ID
	jsonResponse = {
	    'account_id': ""
	};
    }
    
    console.log('/registerAccount POST accessed');

    // Respond with the stringified JSON data
    res.send(JSON.stringify(jsonResponse));
});

app.get('/login', function(req, res) {

    //gather data from req
    var email_address = req.body.email;
    var password = req.body.password;
    var accountID = ""; // If the account is not found, this id should always be empty string
    
    accountExists = false;

    for (var account in accounts.account_list) {
        if ((account.email === email_address) &&
	    (account.password === password)) {
	    
	    accountExists = true;
	    accountID = account.userID;
	    break;
        }
    }

    var jsonResponse = {
	'account_id': accountID
    };
    
    // Respond to client with approriate json data
    if (accountExists) {
	res.status(200).send(JSON.stringify(jsonResponse));
    } else {
	res.status(404).send(JSON.stringfiy(jsonResponse);
    }
    
    console.log('/login GET accessed');
});

app.post('/setAchievements', function (req, res) {

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

app.post('/addPrivateLocation', function (req, res) {

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

app.post('/addPublicLocation', function (req, res) {

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
	
	if(getAccountData(accountID,0) != null)
	{
		publicLocations.push(myNewLocation);
	}

    console.log('/addPublicLocation POST accessed, jsonData=', req.body);

    res.json(req.body);
});

/* Not Sure if using put yet; will comment out code until we know
// ----------------------------------------
// PUT
// ----------------------------------------
var countUserDataPUT = 0
app.put('/registerAccount', function (req, res) {

    // If for some reason, the JSON isn't parsed, return a HTTP ERROR
    // 400
    if (!req.body) return res.sendStatus(400)

    countUserDataPUT++;

    var name = req.body.name;
    var descr = req.body.description;
    var enable = req.body.enable;
    var val1 = req.body.val1;

    console.log(req.accepts('json'));
    console.log(req.get('Content-Type'));

    console.log('/registerAccount PUT, count=', countUserDataPUT, ', jsonData=', req.body);
    console.log('   name=', name, ', description=', descr, ', enable=', enable, ', val1=', val1);

    var jsonResponse = {
	id: '123',
	status: 'updated'
    };
    res.json(jsonResponse);
})
*/

// ----------------------------------------
// DELETE
// ----------------------------------------

app.delete('/removeSavedLocations', function (req, res) {

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

// ================================================
// ================================================
// ================================================
//
// FINALLY, start the app and let it listen for connections on the
// network
//
// This really needs to be last.
//
// app.listen opens up a network socket on port "port" and waits for
// HTTP connections
//
// ================================================
// ================================================
// ================================================


function guid() {
  function s4() {
    return Math.floor((1 + Math.random()) * 0x10000)
      .toString(16)
      .substring(1);
  }
  return s4() + s4() + '-' + s4() + '-' + s4() + '-' +
    s4() + '-' + s4() + s4() + s4();
}

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

app.listen(app.get("port"), function () {
    console.log('CS4531 Node Example: Node app listening on port: ', app.get("port"));
});
