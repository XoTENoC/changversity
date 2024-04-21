var express = require('express');
var router = express.Router();

const CLIENT_ID = 'MY-CLIENT-ID.apps.googleusercontent.com';
const { OAuth2Client } = require('google-auth-library');
const client = new OAuth2Client(CLIENT_ID);



/* GET home page. */
router.get('/', function (req, res, next) {
    res.render('index', { title: 'Express' });
});

let counter = 0;

// Send some text
router.get('/test', function (req, res, next) {
    res.send('This is some text ' + counter);
    counter++;
    console.log('This is a message: ' + counter);
});

let users = {
    bob: { password: 'password', email: 'example1@example.com' },
    alice: { password: 'foobar', email: 'example2@example.com' }
};

router.post('/login', async function (req, res, next) {

    // This code handles a Google login via an AJAX request to the regular login route
    if ('client_id' in req.body && 'credential' in req.body) {

        const ticket = await client.verifyIdToken({
            idToken: req.body.credential,
            audience: CLIENT_ID, // Specify the CLIENT_ID of the app that accesses the backend
            // Or, if multiple clients access the backend:
            //[CLIENT_ID_1, CLIENT_ID_2, CLIENT_ID_3]
        });
        const payload = ticket.getPayload();
        //console.log(payload['sub']);
        console.log(payload['email']);
        // If request specified a G Suite domain:
        // const domain = payload['hd'];

        // Search for user by email
        for (let id in users) {
            if (users[id].email === payload['email']) {
                req.session.user = users[id];
                res.json(req.session.user);
                return;
            }
        }

        // No user
        res.sendStatus(401);


    } else if ('username' in req.body && 'password' in req.body) {

        if (req.body.username in users && users[req.body.username].password === req.body.password) {
            // There is a user
            req.session.user = users[req.body.username];
            console.log(req.body.username);
            res.json(req.session.user);
        } else {
            // No user
            res.sendStatus(401);
        }

    } else {
        res.sendStatus(401);
    }

});

router.post('/signup', function (req, res, next) {

    if (req.body.username in users) {
        res.sendStatus(401);
    } else {
        req.session.username = req.body.username;
        users[req.body.username] = { password: req.body.password };
        console.log(req.body.username);
        res.end();
    }

});

router.post('/logout', function (req, res, next) {

    if ('username' in req.session) {
        delete req.session.username;
        res.end();
    } else {
        res.sendStatus(403);
    }

});

// This code handles a dedicated login route that would be made through a redirect
router.post('/google_login', async function (req, res, next) {

    const ticket = await client.verifyIdToken({
        idToken: req.body.credential,
        audience: CLIENT_ID
    });
    const payload = ticket.getPayload();
    //console.log(payload['sub']);
    console.log(payload['email']);
    // If request specified a G Suite domain:
    // const domain = payload['hd'];

    // Redirect the user to the desired page after login. e.g. user's profile page.
    res.redirect('/');

});


module.exports = router;
