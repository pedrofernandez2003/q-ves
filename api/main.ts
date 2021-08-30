//import * as admin from 'firebase-admin';

const admin = require("firebase-admin");

var serviceAccount = require("/Users/MartinBarbieri/Desktop/q-ves/api/clave.json");
var moderadores = new Map();

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
    await db.collection('plantilas').get()
    .then((querySnapshot) => {
        querySnapshot.forEach((doc) => {
            cantidadDeApariencias(doc.data().moderador)
            console.log(doc.id, " => ", doc.data().categorias);
        });
    })
    .catch((error) => {
        console.log("Error getting documents: ", error);
    });
    res.send("hola");
});

function cantidadDeApariencias(nombreModerador){
    let cantidadModerador = 0;
    if(moderadores.has(nombreModerador)){
        cantidadModerador = moderadores.get(nombreModerador);
    }

    moderadores.set(nombreModerador, cantidadModerador++ );
}

function definirTop3(moderadoresConCantidad){
    let valorMayor = 0
    let moderador;
    moderadoresConCantidad.forEach((values,keys)=>{
        if(values>)
      })
}