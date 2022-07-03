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
en esta caso vamos a iniciar con la parte grafica para ello contamos con 3 elementos escenciales 

2.1) GraphView nos indicara los valores recibidos en las graficas 
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


