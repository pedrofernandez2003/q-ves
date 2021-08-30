//import * as admin from 'firebase-admin';

const admin = require("firebase-admin");

var serviceAccount = require("/home/conectividad/Escritorio/q-ves/api/clave.json");

admin.initializeApp({
  credential: admin.credential.cert(serviceAccount),
  databaseURL: "https://qves-ddf27-default-rtdb.firebaseio.com"
});

let db=admin.firestore();
let a=db.collection('plantilas')

const express = require('express');
const bp = require('body-parser');

const app = express();
const port = 3000;

app.use(bp.json())
app.use(bp.urlencoded({ extended: true }))

app.listen(port, () => {
    console.log(`App listening at http://localhost:${port}`)
});


app.get('/topMods', async (req, res) => {
    console.log("entre");
    await a.get()
    .then((querySnapshot) => {
        querySnapshot.forEach((doc) => {
            console.log(doc.id, " => ", doc.data());
        });
    })
    .catch((error) => {
        console.log("Error getting documents: ", error);
    });
    res.send("hola");
});