//import * as admin from 'firebase-admin';
var __awaiter = (this && this.__awaiter) || function (thisArg, _arguments, P, generator) {
    function adopt(value) { return value instanceof P ? value : new P(function (resolve) { resolve(value); }); }
    return new (P || (P = Promise))(function (resolve, reject) {
        function fulfilled(value) { try { step(generator.next(value)); } catch (e) { reject(e); } }
        function rejected(value) { try { step(generator["throw"](value)); } catch (e) { reject(e); } }
        function step(result) { result.done ? resolve(result.value) : adopt(result.value).then(fulfilled, rejected); }
        step((generator = generator.apply(thisArg, _arguments || [])).next());
    });
};
var __generator = (this && this.__generator) || function (thisArg, body) {
    var _ = { label: 0, sent: function() { if (t[0] & 1) throw t[1]; return t[1]; }, trys: [], ops: [] }, f, y, t, g;
    return g = { next: verb(0), "throw": verb(1), "return": verb(2) }, typeof Symbol === "function" && (g[Symbol.iterator] = function() { return this; }), g;
    function verb(n) { return function (v) { return step([n, v]); }; }
    function step(op) {
        if (f) throw new TypeError("Generator is already executing.");
        while (_) try {
            if (f = 1, y && (t = op[0] & 2 ? y["return"] : op[0] ? y["throw"] || ((t = y["return"]) && t.call(y), 0) : y.next) && !(t = t.call(y, op[1])).done) return t;
            if (y = 0, t) op = [op[0] & 2, t.value];
            switch (op[0]) {
                case 0: case 1: t = op; break;
                case 4: _.label++; return { value: op[1], done: false };
                case 5: _.label++; y = op[1]; op = [0]; continue;
                case 7: op = _.ops.pop(); _.trys.pop(); continue;
                default:
                    if (!(t = _.trys, t = t.length > 0 && t[t.length - 1]) && (op[0] === 6 || op[0] === 2)) { _ = 0; continue; }
                    if (op[0] === 3 && (!t || (op[1] > t[0] && op[1] < t[3]))) { _.label = op[1]; break; }
                    if (op[0] === 6 && _.label < t[1]) { _.label = t[1]; t = op; break; }
                    if (t && _.label < t[2]) { _.label = t[2]; _.ops.push(op); break; }
                    if (t[2]) _.ops.pop();
                    _.trys.pop(); continue;
            }
            op = body.call(thisArg, _);
        } catch (e) { op = [6, e]; y = 0; } finally { f = t = 0; }
        if (op[0] & 5) throw op[1]; return { value: op[0] ? op[1] : void 0, done: true };
    }
};
var _this = this;
var admin = require("firebase-admin");
var serviceAccount = require("./clave.json");
var moderadoresApariciones = new Map();
var personajesApariciones = new Map();
var moderadoresUnicos = new Array();
admin.initializeApp({
    credential: admin.credential.cert(serviceAccount),
    databaseURL: "https://qves-ddf27-default-rtdb.firebaseio.com"
});
var db = admin.firestore();
var a = db.collection('plantilas');
var express = require('express');
var bp = require('body-parser');
var app = express();
var port = 3000;
app.use(bp.json());
app.use(bp.urlencoded({ extended: true }));
app.listen(port, function () {
    console.log("App listening at http://localhost:" + port);
});
app.get('/promEquipos', function (req, res) { return __awaiter(_this, void 0, void 0, function () {
    return __generator(this, function (_a) {
        switch (_a.label) {
            case 0: return [4 /*yield*/, db.collection('plantillas').get()
                    .then(function (querySnapshot) {
                    var sumaEquipos = 0;
                    var cantidadPlantillas = 0;
                    querySnapshot.forEach(function (doc) {
                        var prueba = doc.data().cantEquipos;
                        var cantEquipos = parseInt(prueba);
                        sumaEquipos = sumaEquipos + cantEquipos;
                        cantidadPlantillas++;
                    });
                    var promedio = sumaEquipos / cantidadPlantillas;
                    res.send(promedio.toString());
                })["catch"](function (error) {
                    res.status(503);
                    res.send(error);
                })];
            case 1:
                _a.sent();
                return [2 /*return*/];
        }
    });
}); });
app.get('/promPartidas', function (req, res) { return __awaiter(_this, void 0, void 0, function () {
    return __generator(this, function (_a) {
        switch (_a.label) {
            case 0: return [4 /*yield*/, db.collection('plantillas').get()
                    .then(function (querySnapshot) {
                    var sumaPartidas = 0;
                    var cantidadPlantillas = 0;
                    querySnapshot.forEach(function (doc) {
                        var cantPartidasString = doc.data().cantPartidas;
                        var cantPartidas = parseInt(cantPartidasString);
                        sumaPartidas = sumaPartidas + cantPartidas;
                        cantidadPlantillas++;
                    });
                    var promedio = sumaPartidas / cantidadPlantillas;
                    res.send(promedio.toString());
                })["catch"](function (error) {
                    res.status(503);
                    res.send(error);
                })];
            case 1:
                _a.sent();
                return [2 /*return*/];
        }
    });
}); });
app.get('/mods', function (req, res) { return __awaiter(_this, void 0, void 0, function () {
    var listaModeradores;
    return __generator(this, function (_a) {
        switch (_a.label) {
            case 0: return [4 /*yield*/, db.collection('plantillas').get()
                    .then(function (querySnapshot) {
                    querySnapshot.forEach(function (doc) {
                        listarModeradores(doc.data().usuario);
                    });
                })["catch"](function (error) {
                    res.status(503);
                    res.send(error);
                })];
            case 1:
                _a.sent();
                listaModeradores = moderadoresUnicos;
                moderadoresUnicos = [];
                res.send(listaModeradores);
                return [2 /*return*/];
        }
    });
}); });
app.get('/topPersonajes', function (req, res) { return __awaiter(_this, void 0, void 0, function () {
    var top3;
    return __generator(this, function (_a) {
        switch (_a.label) {
            case 0: return [4 /*yield*/, db.collection('plantillas').get()
                    .then(function (querySnapshot) {
                    querySnapshot.forEach(function (doc) {
                        var personajes = doc.data().personajes;
                        for (var i = 0; i < personajes.length; i++) {
                            var personaje = personajes[i];
                            cantidadDeApariciones(personaje);
                        }
                    });
                })["catch"](function (error) {
                    res.status(503);
                    res.send(error);
                })];
            case 1:
                _a.sent();
                top3 = definirTop3(personajesApariciones);
                personajesApariciones.clear();
                res.send(top3);
                return [2 /*return*/];
        }
    });
}); });
app.get('/topMods', function (req, res) { return __awaiter(_this, void 0, void 0, function () {
    var top3;
    return __generator(this, function (_a) {
        switch (_a.label) {
            case 0: return [4 /*yield*/, db.collection('plantillas').get()
                    .then(function (querySnapshot) {
                    querySnapshot.forEach(function (doc) {
                        cantidadDeApariencias(doc.data().usuario);
                        console.log(doc.id, " => ", doc.data().usuario);
                    });
                })["catch"](function (error) {
                    res.status(503);
                    res.send(error);
                })];
            case 1:
                _a.sent();
                top3 = definirTop3(moderadoresApariciones);
                moderadoresApariciones.clear();
                res.send(top3);
                return [2 /*return*/];
        }
    });
}); });
function listarModeradores(nombreModerador) {
    if (!moderadoresUnicos.includes(nombreModerador)) {
        moderadoresUnicos.push(nombreModerador);
    }
}
function cantidadDeApariciones(personaje) {
    var cantidadPersonaje = 0;
    if (personajesApariciones.has(personaje)) {
        cantidadPersonaje = personajesApariciones.get(personaje);
    }
    personajesApariciones.set(personaje, cantidadPersonaje + 1);
}
function cantidadDeApariencias(nombreModerador) {
    var cantidadModerador = 0;
    if (moderadoresApariciones.has(nombreModerador)) {
        cantidadModerador = moderadoresApariciones.get(nombreModerador);
    }
    moderadoresApariciones.set(nombreModerador, cantidadModerador + 1);
}
function definirTop3(moderadoresConCantidad) {
    var valorMayor = 0;
    var moderador;
    var top3 = new Array();
    for (var i = 0; i < 3; i++) {
        moderadoresConCantidad.forEach(function (values, keys) {
            if (values > valorMayor) {
                valorMayor = values;
                moderador = keys;
            }
        });
        top3.push(moderador);
        moderadoresConCantidad["delete"](moderador);
        moderador = "";
        valorMayor = 0;
    }
    return top3;
}
