package com.example.cosas_faciles.mizu_a;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

/**
 * Created by Durgrim on 3/12/2016.
 */

public class ListaProgramasActivity extends AppCompatActivity {
    // SPP UUID service - this should work for most devices
    private static UUID BTMODULEUUID;
    //Otras variables
    private static String nombreBluetooth;
    private static String direccionMAC;
    //Objetos de la pantalla
    private Button btEncender;
    private Button btApagar;
    //Bluetooth
    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    private ConnectedThread hiloConectado;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        BTMODULEUUID = generarUUID();

        setContentView(R.layout.activity_lista_programas);

        //Obtenemos los parametros del activity/pantalla anterior (ListaDispositivosActivity)
        nombreBluetooth = getIntent().getExtras().getString("nombreBluetooth");
        direccionMAC = getIntent().getExtras().getString("direccionMAC");

        TextView textViewNombreDispositivo = (TextView) findViewById(R.id.textoNombreDispositivo);

        textViewNombreDispositivo.setText(nombreBluetooth);

        btEncender = (Button) findViewById(R.id.btnEncender);
        btApagar = (Button) findViewById(R.id.btnApagar);

        btEncender.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Encendemos el regador
                hiloConectado.write("H");
                Toast.makeText(ListaProgramasActivity.this,
                        "ENCENDIDO", Toast.LENGTH_SHORT).show();
            }
        });

        btApagar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Apagamos el regador
                hiloConectado.write("L");
                Toast.makeText(ListaProgramasActivity.this,
                        "APAGADO", Toast.LENGTH_SHORT).show();
            }
        });

        btAdapter = BluetoothAdapter.getDefaultAdapter();
        checkBTState();
    }

    public void conectarDispositivo(String direccion) {
        Toast.makeText(this, "Conectando a " + direccion, Toast.LENGTH_LONG).show();
       /* if (servicio != null) {
            BluetoothDevice dispositivoRemoto = bAdapter.getRemoteDevice(direccion);
            servicio.solicitarConexion(dispositivoRemoto);
            this.ultimoDispositivo = dispositivoRemoto;
        }*/
    }

    @Override
    protected void onStart() {
        super.onStart();

        //Se crea un dispostivo y se le setea la dirección MAC
        BluetoothDevice dispositivo = btAdapter.getRemoteDevice(direccionMAC);

        try {
            btSocket = crearBluetoothSocket(dispositivo);
        } catch (IOException e) {
            Toast.makeText(getBaseContext(), "La creacción del Socket fallo", Toast.LENGTH_LONG).show();
        }

        // Establecemos la conexion Bluetooth socket
        try {
            btSocket.connect();
        } catch (IOException e) {
            try {
                btSocket.close();
                Log.println(Log.ERROR, "hola", e.getMessage());
                finish();
            } catch (IOException e2) {
                //insert code to deal with this
            }
        }

        //Asociamos el hilo con el socket
        hiloConectado = new ConnectedThread(btSocket);
        hiloConectado.start();//Lo iniciamos

        //I send a character when resuming.beginning transmission to check device is connected
        //If it is not an exception will be thrown in the write method and finish() will be called
        //hiloConectado.write("x");
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            //Cuando nos vamos del activity no olvidarnos de cerrar el socket
            btSocket.close();
        } catch (IOException e) {
            //insert code to deal with this
        }
    }

    private BluetoothSocket crearBluetoothSocket(BluetoothDevice device) throws IOException {
        //Creamos una conexion segura con el Bluetooth usando el UUID
        //return device.createRfcommSocketToServiceRecord(BTMODULEUUID);

        return device.createInsecureRfcommSocketToServiceRecord(BTMODULEUUID);
    }

    //Checks that the Android device Bluetooth is available and prompts to be turned on if off
    private void checkBTState() {
        if (btAdapter == null) {
            Toast.makeText(getBaseContext(), "El dispositivo no soporta bluetooth", Toast.LENGTH_LONG).show();
        } else {
            //Si no esta hablitado el Bluetooth le pedimos al usuario que lo conecte
            if (!btAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);
            }
        }
    }

    private UUID generarUUID() {
        ContentResolver appResolver = getApplicationContext().getContentResolver();
        String id = Settings.Secure.getString(appResolver, Settings.Secure.ANDROID_ID);
        final TelephonyManager tManager = (TelephonyManager) getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);
        final String deviceId = String.valueOf(tManager.getDeviceId());
        final String simSerialNumber = String.valueOf(tManager.getSimSerialNumber());
        final String androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        UUID uuid = new UUID(androidId.hashCode(), ((long) deviceId.hashCode() << 32) | simSerialNumber.hashCode());
        return uuid;
    }

    //Creamos una nueva clase para el hilo que se conecta
    private class ConnectedThread extends Thread {
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

        public void run(){

        }

        //write method
        public void write(String input) {
            byte[] msgBuffer = input.getBytes();//Convierte el string a bytes
            try {
                mmOutStream.write(msgBuffer);//Envia bytes atravez de la conexion BT via outstream
            } catch (IOException e) {
                //Excepsion
                Toast.makeText(getBaseContext(), "La Conexión fallo", Toast.LENGTH_LONG).show();
                //finish();
            }
        }
    }
}
