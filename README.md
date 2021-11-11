# Q-ves
Aplicación mobile desarrollada en Android.
Atias, Simón-Barbieri, Martin-Blum, Damian-Cristobo, Magali-Fernandez, Pedro

## Descripción

Juego basado en el proyecto [Q’ ves](https://lasotrasvoces.org.ar/blog/2018/02/27/q-ves-un-juego-sobre-estereotipos-de-genero/) de la empresa Las Otras Voces. El desarrollo de la aplicación entera se encuentra en la carpeta llamada "App" dentro de la rama *main*. Fue desarrollada utilizando [Android Studio](https://developer.android.com/studio).

## Deployment

Hay dos formas para convertir la aplicación en un apk y así instalarla en cualquier dispositivo:

- **Github Actions**

  Cada vez que se haga un cambio en la rama *main* se deployará un script que automáticamente actualice el apk en github.
  
- **Jenkins** 

  Programa que utilizamos para realizar la automatización de forma local en el ámbito de una computadora o máquina virtual. Es necesario descargarlo como una [imagen docker](https://hub.docker.com/r/jenkins/jenkins) o por [sí mismo](https://www.jenkins.io/download/). Finalmente, se deberá pasar el archivo *config.xml* que tenemos en main hacia Jenkins.
