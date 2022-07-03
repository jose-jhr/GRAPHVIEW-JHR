# GRAPHVIEW-JHR
GRAFICAR  DATOS PROVENIENTES DESDE ARDUINO CON ANDROID


![5](https://user-images.githubusercontent.com/66834393/177058447-7d51e8c5-ab95-44dd-8a31-81ef6b10263a.png)

para graficar datos provenientes de arduino en android realiza los siguiente pasos.



1) Importa las siguientes librerias

SweetAlert(opcional) esta libreria nos permitira generar advertencias y mensajes en pantalla

```

 implementation 'com.github.f0ris.sweetalert:library:1.5.6'
 
```

Nos permitira graficar los datos que reciban por bluetooth

```

implementation 'com.jjoe64:graphview:4.2.2'

```

Permite la conexion entre android y arduino

```

implementation 'com.github.jose-jhr:blueJhrLibrary:0.1.0'

```

2) realiza los pasos de conexion bluetooth, usando la libreria BLUEJHR Disponible en el siguiente enlace https://github.com/jose-jhr/blueJhrLibrary
en esta caso vamos a iniciar con la parte grafica para ello contamos con 2 elementos escenciales 

2.1) GraphView nos indicara los valores recibidos por bluetooth en las graficas 

```xml
   <com.jjoe64.graphview.GraphView
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            android:id="@+id/graph"
            android:layout_marginStart="20dp"
            android:layout_marginTop="10dp"
            android:layout_marginBottom="10dp"
            android:layout_marginEnd="10dp"
            android:translationX="5dp"
            />

    </LinearLayout>
```

2.2) ListView nos indicara los dispositivos disponibles para conectarse.

```xml
    <ListView
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            android:id="@+id/listDeviceBluetooth"
            >
        </ListView>
```

2.3) en este caso este sera el archivo xml completo, para ello vamos a hacer visible o invisible las vistas que vayamos necesitando, es decir mostraremos la grafica cuando estemos conectados a un dispositivo bluetooth de lo contrario mostraremos la lista de conexion, a continuacion archivo completo


```xml

 <?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".MainActivity"
    android:orientation="horizontal"
    >

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:id="@+id/containerGraph"
        android:orientation="vertical"
        android:layout_weight="20"
        >

        <com.jjoe64.graphview.GraphView
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            android:id="@+id/graph"
            android:layout_marginStart="20dp"
            android:layout_marginTop="10dp"
            android:layout_marginBottom="10dp"
            android:layout_marginEnd="10dp"
            android:translationX="5dp"
            />

    </LinearLayout>


    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:id="@+id/containerDevice"
        android:orientation="vertical"
        android:layout_weight="20"
        android:visibility="gone"
        >

        <ListView
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            android:id="@+id/listDeviceBluetooth"
            >
        </ListView>

    </LinearLayout>


    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:layout_weight="80"
        android:orientation="vertical"
        android:id="@+id/containerControll"
        >

        <Button
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginStart="5dp"
            android:layout_marginEnd="5dp"
            android:text="Device"
            android:id="@+id/btnViewDevice"
            >
        </Button>

        <Button
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginStart="5dp"
            android:layout_marginEnd="5dp"
            android:text="START"
            android:id="@+id/btnInitStop"
            >
        </Button>

        <TextView
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:id="@+id/txtPot"
            android:text="Potenciometro"
            android:textAlignment="center"
            android:textColor="#039BE5"
            >
        </TextView>

        <TextView
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:id="@+id/txtTemp"
            android:text="Temperatura"
            android:textAlignment="center"
            android:layout_marginTop="10dp"
            android:textColor="#E53935"
            >
        </TextView>


    </LinearLayout>

</LinearLayout>

```


3)  cabe resaltar que al usar kotlin no estamos haciendo uso de findViewById ya que el plugin id 'kotlin-android-extensions' nos permite enlazarnos sin necesidad de esta funcion, puedes agregarlo en build.gradle(:app)

4) una vez ya tenemos la interfaz grafica completa, procederemos a inicializar algunas variables que usaremos

```kotlin

  //bluetooth var
    lateinit var blue: BluJhr
    var devicesBluetooth = ArrayList<String>()

    //visible ListView
    var graphviewVisible = true

    //graphviewSeries
    lateinit var temperatura: LineGraphSeries<DataPoint?>
    lateinit var potenciometro:LineGraphSeries<DataPoint>

    //nos indica si estamos recibiendo datos o no
    var initGraph = false
    //nos almacena el estado actual de la conexion bluetooth
    var stateConn = BluJhr.Connected.False

    //valor que se suma al eje x despues de cada actualizacion
    var ejeX = 0.6

    //sweet alert necesarios
    lateinit var loadSweet : SweetAlertDialog
    lateinit var errorSweet : SweetAlertDialog
    lateinit var okSweet : SweetAlertDialog
    lateinit var disconnection : SweetAlertDialog

```






