//import * as admin from 'firebase-admin';

const admin = require("firebase-admin");

var serviceAccount = require("./clave.json");
var moderadoresApariciones = new Map();
var personajesApariciones = new Map();
var moderadoresUnicos = new Array();

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


app.get('/promEquipos', async (req,res) =>{
    await db.collection('plantillas').get()
    .then((querySnapshot) => {
        
        let sumaEquipos:number=0
        let cantidadPlantillas:number=0

        querySnapshot.forEach((doc) => {
            let prueba:string=doc.data().cantEquipos
            let cantEquipos:number=parseInt(prueba)

            sumaEquipos=sumaEquipos+cantEquipos;
            cantidadPlantillas++;

        });
        let promedio=sumaEquipos/cantidadPlantillas;
        res.send(promedio.toString())
    })
    .catch((error) => {
        res.status(503)
        res.send(error)
    });
    
})

app.get('/promPartidas', async (req,res) =>{
    await db.collection('plantillas').get()
    .then((querySnapshot) => {
        
        let sumaPartidas:number=0
        let cantidadPlantillas:number=0

        querySnapshot.forEach((doc) => {
            let cantPartidasString:string=doc.data().cantPartidas
            let cantPartidas:number=parseInt(cantPartidasString)

            sumaPartidas=sumaPartidas+cantPartidas;
            cantidadPlantillas++;

        });
        let promedio=sumaPartidas/cantidadPlantillas;
        res.send(promedio.toString())
    })
    .catch((error) => {
        res.status(503)
        res.send(error)
    });
    
})

app.get('/mods', async (req,res) =>{


    await db.collection('plantillas').get()
    .then((querySnapshot) => {
        querySnapshot.forEach((doc) => {
            listarModeradores(doc.data().usuario)
        });
    })
    .catch((error) => {
        res.status(503)
        res.send(error)
    });
    let listaModeradores=moderadoresUnicos
    moderadoresUnicos=[]
    res.send(listaModeradores)

})

app.get('/topPersonajes', async (req,res) =>{

    await db.collection('plantillas').get()
    .then((querySnapshot) => {
        querySnapshot.forEach((doc) =>{
            let personajes:Array<String>=doc.data().personajes;

            for (let i = 0; i < personajes.length; i++) {
                const personaje = personajes[i];
                cantidadDeApariciones(personaje);
            }
   
        })
    })
    .catch((error) => {
        res.status(503)
        res.send(error)
    });

    let top3:Array<String>=definirTop3(personajesApariciones)
    personajesApariciones.clear()
    res.send(top3)
})

app.get('/topMods', async (req, res) => {

    await db.collection('plantillas').get()
    .then((querySnapshot) => {
        querySnapshot.forEach((doc) => {
            cantidadDeApariencias(doc.data().usuario)
            console.log(doc.id, " => ", doc.data().usuario);
        });
    })
    .catch((error) => {
        res.status(503)
        res.send(error)
    });

    let top3:Array<String>=definirTop3(moderadoresApariciones);
    moderadoresApariciones.clear()
    res.send(top3);
});

function listarModeradores(nombreModerador){
    if(!moderadoresUnicos.includes(nombreModerador)){
        moderadoresUnicos.push(nombreModerador)
    }
}


function cantidadDeApariciones(personaje){
    let cantidadPersonaje= 0;
    if(personajesApariciones.has(personaje)){
        cantidadPersonaje=personajesApariciones.get(personaje)
    }

    personajesApariciones.set(personaje, cantidadPersonaje+1)

}

function cantidadDeApariencias(nombreModerador){
    let cantidadModerador = 0;
    if(moderadoresApariciones.has(nombreModerador)){
        cantidadModerador = moderadoresApariciones.get(nombreModerador);
    }

    moderadoresApariciones.set(nombreModerador, cantidadModerador+1 );
}

function definirTop3(moderadoresConCantidad:Map<any,any>):Array<String>{
    let valorMayor: Number = 0;
    let moderador: String;
    let top3:Array<String>= new Array();
    for (let i = 0; i < 3; i++) {
        moderadoresConCantidad.forEach((values,keys)=>{
            if(values>valorMayor){
                valorMayor=values;
                moderador=keys;
            }
        })
        top3.push(moderador);
        moderadoresConCantidad.delete(moderador);
        moderador="";
        valorMayor=0;
        
    }
    return top3;

}