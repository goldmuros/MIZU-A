package com.example.cosas_faciles.mizu_a;

import android.bluetooth.BluetoothSocket;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by Durgrim on 5/3/2017.
 */

//Creamos una nueva clase para el hilo que se conecta
public class ConnectedThread extends Thread {
    private final OutputStream mmOutStream;

    //creation of the connect thread
    public ConnectedThread(BluetoothSocket socket) {
        OutputStream tmpOut = null;

        try {
            //Create I/O streams for connection
            tmpOut = socket.getOutputStream();
        } catch (IOException e) {
        }

        mmOutStream = tmpOut;
    }

    public void run() {

    }

    //write method
    public void write(String input) {
        byte[] msgBuffer = input.getBytes();//Convierte el string a bytes
        try {
            mmOutStream.write(msgBuffer);//Envia bytes atravez de la conexion BT via outstream
        } catch (IOException e) {
            //Excepsion
            //Toast.makeText(getBaseContext(), "La Conexión fallo", Toast.LENGTH_LONG).show();
        }
    }
}
