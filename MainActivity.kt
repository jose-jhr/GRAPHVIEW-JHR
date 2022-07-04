

import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import cn.pedant.SweetAlert.SweetAlertDialog
import com.ingenieriajhr.blujhr.BluJhr
import com.jjoe64.graphview.series.DataPoint
import kotlinx.android.synthetic.main.activity_main.*
import com.jjoe64.graphview.series.LineGraphSeries




class MainActivity : AppCompatActivity() {

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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


    }

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

}

