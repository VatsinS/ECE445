package com.example.pollutionmapper


import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.util.Log
import android.widget.TextView
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

val uuid: UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb")

class BluetoothServerController(activity: MapsActivity) : Thread() {
    private var cancelled: Boolean
    private val serverSocket: BluetoothServerSocket?
    private var socket: BluetoothSocket? = null
    private val activity = activity

    init {
        val btAdapter : BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (btAdapter != null) {
            var pairedDevices = btAdapter.bondedDevices
            var device : BluetoothDevice? = null
            for (bt in pairedDevices){
                if (bt.name == "AIR_MAP_BAND") {
                    Log.d("bluetooth address", bt.address)
                    device = bt
                }
            }
            if (device != null){
                this.socket = device.createRfcommSocketToServiceRecord(uuid)
            } else{
                this.socket = null
                this.cancelled = true
                Log.d("bluetooth", "failed bt socket")
            }

            this.serverSocket = btAdapter.listenUsingRfcommWithServiceRecord("test", uuid) // 1
            this.cancelled = false
            Log.d("bluetooth", "got bt adapter")
            if (btAdapter.isEnabled){
                Log.d("bluetooth", "adapter enabled")
            } else{
                Log.d("bluetooth", "adapter disabled")
            }
        } else {
            this.serverSocket = null
            this.cancelled = true
            Log.d("bluetooth", "failed bt adapter")
        }

    }

    override fun run() {
//        var socket: BluetoothSocket
        Log.d("bluetooth", "entered run")
        while(true) {
            Log.d("bluetooth", "entered while loop")
            if (this.cancelled) {
                Log.d("bluetooth", "CANCELLED")
                break
            }

            try {
                //socket = serverSocket!!.accept()  // 2
                socket!!.connect()
                Log.d("bluetooth", "socket try")
            } catch(e: IOException) {
                Log.d("bluetooth", "AMODH")
                continue
            }

            if (!this.cancelled && socket != null) {
                Log.d("bluetooth", "Connecting")
//                BluetoothServer(this.activity, socket).start() // 3]
//                    Log.d("bluetooth", "entered try")
                val buffer = ByteArray(1024)
                var len: Int
                val message: String = "Connected"
                try{
                    socket!!.outputStream.write(message.toByteArray())
                    socket!!.outputStream.flush()
                } catch (e: Exception){
                    Log.e("bluetooth", "error in sending the connect message", e)
                }

                while (true) {
                    Log.d("bluetooth", "entered while 2 loop")
                    try{
                        if (socket!=null){
                            Log.d("bluetooth", "socket available")
                        } else{
                            Log.d("bluetooth", "socket not available")
                        }
                        len = socket!!.inputStream.read(buffer)
                        val data = buffer.copyOf(len)
                        val string = String(data)
                        Bluetooth_data.sensor_data = string.split("+").take(3).map { it.toFloat() }.toList()
//                        Log.d("bluetooth", Bluetooth_data.sensor_data.joinToString(","))
                        activity.findViewById<TextView>(R.id.text_view_id).text = Bluetooth_data.sensor_data.joinToString(",")
                    } catch (e: Exception) {
                        Log.e("bluetooth", "error in extracting data", e)
                        continue
//                        this.cancelled = true
//    //                    if (listener != null) listener.onSerialIoError(e)
//                        try {
//                            socket!!.close()
//                        } catch (ignored: Exception) {
//                        }
//                        socket = null
                    }

//                        if (listener != null) listener.onSerialRead(data)
                }
            }
        }
        Log.d("bluetooth", "exited while loop")
    }

    fun cancel() {
        this.cancelled = true
        this.serverSocket!!.close()
    }
}






//package com.example.pollutionmapper
//
//import android.bluetooth.BluetoothAdapter
//import android.bluetooth.BluetoothDevice
//import android.bluetooth.BluetoothServerSocket
//import android.bluetooth.BluetoothSocket
//import android.util.Log
//import java.io.IOException
//import java.util.*
//
//val uuid: UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb")
//class BluetoothServerController(activity: MapsActivity) : Thread() {
//    private var cancelled: Boolean
//    private val serverSocket: BluetoothServerSocket?
//    private val activity = activity
//
//    init {
//        val btAdapter : BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
//        if (btAdapter != null) {
//            var pairedDevices = btAdapter.bondedDevices
//            var device : BluetoothDevice?
//            for (bt in pairedDevices){
//                if (bt.name == "AIR_MAP_BAND2") {
//                    Log.d("bluetooth address", bt.address)
//
//                }
//            }
//            this.serverSocket = btAdapter.listenUsingRfcommWithServiceRecord("test", uuid) // 1
//            this.cancelled = false
//            Log.d("bluetooth", "got bt adapter")
//            if (btAdapter.isEnabled){
//                Log.d("bluetooth", "adapter enabled")
//            } else{
//                Log.d("bluetooth", "adapter disabled")
//            }
//
//
//
//
//        } else {
//            this.serverSocket = null
//            this.cancelled = true
//            Log.d("bluetooth", "failed bt adapter")
//        }
//
//    }
//
//    override fun run() {
//        var socket: BluetoothSocket
//        Log.d("bluetooth", "entered run")
//        while(true) {
//            Log.d("bluetooth", "entered while loop")
//            if (this.cancelled) {
//                Log.d("bluetooth", "CANCELLED")
//                break
//            }
//
//            try {
//                socket = serverSocket!!.accept()  // 2
//                Log.d("bluetooth", "socket try")
//            } catch(e: IOException) {
//                Log.d("bluetooth", "AMODH")
//                break
//            }
//
//            if (!this.cancelled && socket != null) {
//                Log.d("server", "Connecting")
//                BluetoothServer(this.activity, socket).start() // 3
//            }
//        }
//        Log.d("bluetooth", "exited while loop")
//    }
//
//    fun cancel() {
//        this.cancelled = true
//        this.serverSocket!!.close()
//    }
//}
