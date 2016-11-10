
var express = require('express');


var bodyParser = require('body-parser');

// The main instanced class, called app will be initialized by express
var app = express()

// Set the port in the app system; MAY WANT TO CHANGE LATER
app.set("port", 4321);

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

    // Prepare output in JSON format
    var dataToReturn = {
        //User achievement data
        //Example:
        //first_name:'Pete'
    };

    console.log('/getAchievements GET URI accessed');
    res.send(JSON.stringify(dataToReturn));
});

app.get('/getSavedLocations', function(req, res) {

    // Prepare output in JSON format
    var dataToReturn = {
        //User achievement data
        //Example:
        //first_name:'Pete'
    };

    console.log('/getSavedLocations GET URI accessed');
    res.send(JSON.stringify(dataToReturn));
});

// ----------------------------------------
// POST
// ----------------------------------------

///initial post of username and other info
accounts = {
  account_list:[]
};
app.post('/registerAccount', function (req, res) {

    // If for some reason, the JSON isn't parsed, return a HTTP ERROR
    // 400
    if (!req.body) return res.sendStatus(400)

    //gather data from req
    //Example:
    //var name = req.body.name;
    var email_address = req.body.email;
    var password = req.body.password;

    /*Need to check whether email is valid, password validation if desired*/

    isAccountExisted = false;
    for (var account in accounts.account_list) {
        if (account.email === email_address) {
          isAccountExisted = true;
          break;
        }
    }

    // Check whether email address is already in the database or not
    if (!isAccountExisted) {

      var achievements_list = {"all_achivements": [
        'Have 20 friends': false,
        'Saved 10 locations': false
      ]};

      var saved_locations_list = [];

      var jsonAccount = {
        'email': email_address,
        'password': password,
        'achievements': achievements_list,
        'savedLocation': saved_locations_list
      };

      accounts.account_list.push(jsonAccount);
      res.status(200).send('Account successfully registered!');
    } else {

      res.status(409).send('Account information already exists.')
    }

    //console.log('/registerAccount POST accessed, jsonData=', req.body);

    res.json(req.body);
});

app.post('/setAchievements', function (req, res) {

    // If for some reason, the JSON isn't parsed, return a HTTP ERROR
    // 400
    if (!req.body) return res.sendStatus(400)

    //gather data from req
    //Example:
    //var name = req.body.name;

    console.log('/setAchievements POST accessed, jsonData=', req.body);

    res.json(req.body);
})

app.post('/setSavedLocations', function (req, res) {

    // If for some reason, the JSON isn't parsed, return a HTTP ERROR
    // 400
    if (!req.body) return res.sendStatus(400)

    //gather data from req
    //Example:
    //var name = req.body.name;

    console.log('/setAchievements POST accessed, jsonData=', req.body);

    res.json(req.body);
})

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
app.listen(app.get("port"), function () {
    console.log('CS4531 Node Example: Node app listening on port: ', app.get("port"));
});
