# Q-ves
Aplicación mobile desarrollada en Android.
Atias, Simón-Barbieri, Martin-Blum, Damian-Cristobo, Magali-Fernandez, Pedro

## Descripcion

Juego basado en el proyecto [Q’ ves](https://lasotrasvoces.org.ar/blog/2018/02/27/q-ves-un-juego-sobre-estereotipos-de-genero/) de la empresa Las Otras Voces.

## Uso

Para poder correr la aplicacion sera necesario trabajar en la Aplicacion [Android Studio](https://developer.android.com/studio) en la rama *main*.

## Deployment

Hay dos formas para convertir la aplicacion en un apk que se puede probar:

- **Github Actions**

  Cada vez que se haga un cambio en la rama *main* se deployara un script que automaticamente actualize el apk en github.
  
- **Jenkins** 

  Programa que utilizamos para realizar la automatizacion de forma local en el ambito de una computador o maquina virtual, sera necesario descargarlo ya sea como una [imagen docker](https://hub.docker.com/r/jenkins/jenkins) o por [si mismo](https://www.jenkins.io/download/), luego se debera pasar el archivo *config.xml* que tenemos en main hacia Jenkins y listo.
