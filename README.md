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

Nota, se crearon antes del onCreate

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


5) creamos una funcion que nos inicia los SweetAlert a usar, en esta ocasion usaremos 4 sweet conexion exitosa, error en conexion, conexion pendiente y conexion perdida correspondientemente okSweet, errorSweet, loadSweet, disconnection.

```kotlin
private fun initSweet() {
        loadSweet = SweetAlertDialog(this,SweetAlertDialog.PROGRESS_TYPE)
        okSweet = SweetAlertDialog(this,SweetAlertDialog.SUCCESS_TYPE)
        errorSweet = SweetAlertDialog(this,SweetAlertDialog.ERROR_TYPE)
        disconnection = SweetAlertDialog(this,SweetAlertDialog.NORMAL_TYPE)

        loadSweet.titleText = "Conectando"
        loadSweet.setCancelable(false)
        errorSweet.titleText = "Algo salio mal"

        okSweet.titleText = "Conectado"
        disconnection.titleText = "Desconectado"
    }
```

6) luego de esto vamos a inicializar la parte grafica de la vista graph, para que todo esto funcione debemos decirle a la vista que las modificaciones las realizaremos de forma manual, es decir los ejes x (y) y maximos o minimos en nuestra grafica, o si deseamos que nuesta vista sea ajustable o reaccione a la interacción de nuestros dedos, para esta actividad tenemos dos objetos LineGrahSeries() uno para graficar el valor de una temperatura y otro para graficar el valor de un potenciometro.

```kotlin
 private fun initGraph() {
        //permitime controlar los ejes manualmente
        graph.viewport.isXAxisBoundsManual = true;
        graph.viewport.setMinX(0.0);
        graph.viewport.setMaxX(10.0);
        graph.viewport.setMaxY(1024.0)
        graph.viewport.setMinY(0.0)

        //permite realizar zoom y ajustar posicion eje x
        graph.viewport.isScalable = true
        graph.viewport.setScalableY(true)

        temperatura = LineGraphSeries()
        //draw points
        temperatura.isDrawDataPoints = true;
        //draw below points
        temperatura.isDrawBackground = true;
        //color series
        temperatura.color = Color.RED

        potenciometro = LineGraphSeries()
        //draw points
        potenciometro.isDrawDataPoints = true;
        //draw below points
        potenciometro.isDrawBackground = true;
        //color series
        potenciometro.color = Color.BLUE

        graph.addSeries(temperatura);
        graph.addSeries(potenciometro)
    }


```

7) las siguientes funciones realizan la operación de hacer invisible o no a las 2 views que se cuentan, es decir su interacción va desde mostrar la listView que contiene los dispositivos bluetooth a desaparecer la vista grafica graphview y tambien en el sentido inverso con la funcion invisibleListDevice()

```kotlin
/**
     * invisible listDevice
     */
    private fun invisibleListDevice() {
        containerGraph.visibility = View.VISIBLE
        containerDevice.visibility = View.GONE
        graphviewVisible = true
        btnViewDevice.text = "DEVICE"
    }

    /**
     * visible list device
     */
    private fun visibleListDevice() {
        containerGraph.visibility = View.GONE
        containerDevice.visibility = View.VISIBLE
        graphviewVisible = false
        btnViewDevice.text = "GraphView"

    }
```

8) haciendo uso de la libreria bluJhr vamos a recibir los datos probenientes de arduino o cualquier microcontrolador con la funcion rxReceived(), esta cada vez que recibe un dato evalua si contiene una t o una p, t para temperatura, p para potenciometro, logrando asi generar un valor para agragarse a la respectiva serie de grafica lineal, las condiciones plasmadas en esta funcion separan los datos de llegada y adicionan un data point con el valor que ha llegado; DataPoint recibe 3 atributos, la posicion del eje X, los datos a graficar en el eje y, si desea que la actualizacion desate un scroll automatico al final y la cantidad de puntos que desea mostrar.

```kotlin
private fun rxReceived() {
        blue.loadDateRx(object:BluJhr.ReceivedData{
            override fun rxDate(rx: String) {
                println("------------------- RX $rx --------------------")
                ejeX+=0.6
                if (rx.contains("t")){
                    val date = rx.replace("t","")
                    txtTemp.text = "Temp: $date"
                    temperatura.appendData(DataPoint(ejeX, date.toDouble()), true, 22)
                }else{
                    if (rx.contains("p")){
                        val date = rx.replace("p","")
                        txtPot.text = "Pot: $date"
                        potenciometro.appendData(DataPoint(ejeX, date.toDouble()), true, 22)
                    }
                }

            }
        })
    }
```

9) las siguientes dos funciones estan realizadas con el fin de tener una conexion exitosa en bluetooth ver https://github.com/jose-jhr/blueJhrLibrary para mayor informacion.

```kotlin
 override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (!blue.stateBluetoooth() && requestCode == 100){
            blue.initializeBluetooth()
        }else{
            if (requestCode == 100){
                devicesBluetooth = blue.deviceBluetooth()
                if (devicesBluetooth.isNotEmpty()){
                    val adapter = ArrayAdapter(this,android.R.layout.simple_expandable_list_item_1,devicesBluetooth)
                    listDeviceBluetooth.adapter = adapter
                }else{
                    Toast.makeText(this, "No tienes vinculados dispositivos", Toast.LENGTH_SHORT).show()
                }

            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (blue.checkPermissions(requestCode,grantResults)){
            Toast.makeText(this, "Exit", Toast.LENGTH_SHORT).show()
            blue.initializeBluetooth()
        }else{
            if(Build.VERSION.SDK_INT < Build.VERSION_CODES.S){
                blue.initializeBluetooth()
            }else{
                Toast.makeText(this, "Algo salio mal", Toast.LENGTH_SHORT).show()
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
```

10) una vez ya creamos las funciones necesarias para tener exitor en graficar datos provenientes de arduino o cualquier microcontrolador usando Bluetooth
vamos a usar todas la funciones y objetos anteriormente inicializados.

Nota: las siguientes lineas de codigo van dentro del onCreate()

```kotlin

        //init var sweetAlert
        initSweet()

        blue = BluJhr(this)
        blue.onBluetooth()

        btnViewDevice.setOnClickListener {
            when (graphviewVisible) {
                false -> invisibleListDevice()
                true -> visibleListDevice()
            }
        }

        listDeviceBluetooth.setOnItemClickListener { adapterView, view, i, l ->
            if (devicesBluetooth.isNotEmpty()) {
                blue.connect(devicesBluetooth[i])
                //genera error si no se vuelve a iniciar los objetos sweet
                initSweet()
                blue.setDataLoadFinishedListener(object : BluJhr.ConnectedBluetooth {
                    override fun onConnectState(state: BluJhr.Connected) {
                        stateConn = state
                        when (state) {
                            BluJhr.Connected.True -> {
                                loadSweet.dismiss()
                                okSweet.show()
                                invisibleListDevice()
                                rxReceived()
                            }

                            BluJhr.Connected.Pending -> {
                                loadSweet.show()
                            }

                            BluJhr.Connected.False -> {
                                loadSweet.dismiss()
                                errorSweet.show()
                            }

                            BluJhr.Connected.Disconnect -> {
                                loadSweet.dismiss()
                                disconnection.show()
                                visibleListDevice()
                            }

                        }
                    }
                })
            }
        }

        //graphview
        initGraph()

        btnInitStop.setOnClickListener {
            if (stateConn == BluJhr.Connected.True){
                initGraph = when(initGraph){
                    true->{
                        blue.bluTx("0")
                        btnInitStop.text = "START"
                        false
                    }
                    false->{
                        blue.bluTx("1")
                        btnInitStop.text = "STOP"
                        true
                    }
                }
            }
        }

```


Gracias por tu apoyo, si aun no te has suscrito te invito a que lo hagas en https://www.youtube.com/ingenieriajhr.
