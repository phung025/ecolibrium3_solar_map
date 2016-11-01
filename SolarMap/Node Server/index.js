
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
app.get('/achievements', function(req, res) {

    // Prepare output in JSON format
    var dataToReturn = {
        //User achievement data
        //Example:
        //first_name:'Pete'
    };

    console.log('/achievements GET URI accessed');
    res.send(JSON.stringify(dataToReturn));
});

app.get('/savedLocations', function(req, res) {

    // Prepare output in JSON format
    var dataToReturn = {
        //User achievement data
        //Example:
        //first_name:'Pete'
    };

    console.log('/saveLocations GET URI accessed');
    res.send(JSON.stringify(dataToReturn));
});

// ----------------------------------------
// POST
// ----------------------------------------

///initial post of username and other info
app.post('/userData', function (req, res) {

    // If for some reason, the JSON isn't parsed, return a HTTP ERROR
    // 400
    if (!req.body) return res.sendStatus(400)

    //gather data from req
    //Example:
    //var name = req.body.name;

    console.log('/userData POST accessed, jsonData=', req.body);

    res.json(req.body);
})

app.post('/achievements', function (req, res) {

    // If for some reason, the JSON isn't parsed, return a HTTP ERROR
    // 400
    if (!req.body) return res.sendStatus(400)

    //gather data from req
    //Example:
    //var name = req.body.name;

    console.log('/achievements POST accessed, jsonData=', req.body);

    res.json(req.body);
})

/* Not Sure if using put yet; will comment out code until we know
// ----------------------------------------
// PUT
// ----------------------------------------
var countUserDataPUT = 0
app.put('/userData', function (req, res) {

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

    console.log('/userData PUT, count=', countUserDataPUT, ', jsonData=', req.body);
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

app.delete('/savedLocations', function (req, res) {

    // If for some reason, the JSON isn't parsed, return a HTTP ERROR
    // 400
    if (!req.body) return res.sendStatus(400)

    //var savedLocation = req.body.savedLocation;

    console.log('/savedLocations DELETE accessed, jsonData=', req.body);
    //console.log('   deleting saved location=', savedLocation, ' from the server.');

    var jsonResponse = {
	id: '321',
	status: 'deleted'
    };
    res.json(jsonResponse);
})

app.delete('/userData', function (req, res) {

    // If for some reason, the JSON isn't parsed, return a HTTP ERROR
    // 400
    if (!req.body) return res.sendStatus(400)

    //var savedLocation = req.body.savedLocation;

    console.log('/userData DELETE accessed, jsonData=', req.body);
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
  res.status(404).send('Sorry; Location Not Found');
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
