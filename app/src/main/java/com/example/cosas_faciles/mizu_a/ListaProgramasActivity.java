package com.example.cosas_faciles.mizu_a;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Durgrim on 3/12/2016.
 */

public class ListaProgramasActivity extends AppCompatActivity
        implements FragmentoListaProgramas.Callbacks {
    // SPP UUID service - this should work for most devices
    private static UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    ;
    //Otras variables
    private static String nombreBluetooth;
    private static String direccionMAC;
    //Objetos de la pantalla
    private Button btEncender, btApagar, btBorrarPrograma, btAnadirPrograma;
    private CheckBox chbPrograma;
    //Bluetooth
    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    private ConnectedThread hiloConectado;

    //Es para la conexion async, borrar si no se usa
    private ProgressDialog progresoConexion;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //BTMODULEUUID = generarUUID();
        //BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

        setContentView(R.layout.activity_lista_programas);

        //Obtenemos los parametros del activity/pantalla anterior (ListaDispositivosActivity)
        nombreBluetooth = getIntent().getExtras().getString("nombreBluetooth");
        direccionMAC = getIntent().getExtras().getString("direccionMAC");

        TextView textViewNombreDispositivo = (TextView) findViewById(R.id.textoNombreDispositivo);

        textViewNombreDispositivo.setText(nombreBluetooth);

        btEncender = (Button) findViewById(R.id.btnEncender);
        btEncender.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Encendemos el regador
                hiloConectado.write("H");
                Toast.makeText(ListaProgramasActivity.this,
                        "ENCENDIDO", Toast.LENGTH_SHORT).show();
            }
        });

        btApagar = (Button) findViewById(R.id.btnApagar);
        btApagar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Apagamos el regador
                hiloConectado.write("L");
                Toast.makeText(ListaProgramasActivity.this,
                        "APAGADO", Toast.LENGTH_SHORT).show();
            }
        });

        btAnadirPrograma = (Button) findViewById(R.id.btnAnadirPrograma);
        btAnadirPrograma.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Hacer la llamada al otro fragment
                Toast.makeText(ListaProgramasActivity.this,
                        "Anadir", Toast.LENGTH_SHORT).show();
            }
        });

        chbPrograma = (CheckBox) findViewById(R.id.chbPrograma);


        btBorrarPrograma = (Button) findViewById(R.id.btnBorrarPrograma);
        btBorrarPrograma.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Hacer la llamada al otro fragment
                Toast.makeText(ListaProgramasActivity.this,
                        "Borrar", Toast.LENGTH_SHORT).show();
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
    protected void onResume() {
        super.onResume();

        //Se realiza la conexion con el BT
        //conectarBT();

        /*List<Programa> listaProgramas = new ArrayList<>();

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        FragmentoListaProgramas fragmentListaProgramas = new FragmentoListaProgramas();
        fragmentTransaction.replace(R.id.layoutFragments, fragmentListaProgramas).commit();

        ListView listViewProgramas = (ListView) findViewById(R.id.listaProgramas);

        listViewProgramas.setAdapter(new ProgramaAdapter(listaProgramas));*/
    }

    @Override
    public void onPause() {
        super.onPause();

        //desconectarBT();
    }

    //Implementamos la interfaz de FragmentoListaProgramas
    @Override
    public void onProgramaSeleccionado(Programa programa){

    }

    private BluetoothSocket crearBluetoothSocket(BluetoothDevice device) throws IOException {
        //Creamos una conexion segura con el Bluetooth usando el UUID
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

    //Realiza la conexion con el Bluetooth
    private void conectarBT() {
        progresoConexion = ProgressDialog.show(ListaProgramasActivity.this, getString(R.string.Conexion), getString(R.string.Espere));  //show a progress dialog
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
                progresoConexion.dismiss();
                finish();
            } catch (IOException e2) {
                //insert code to deal with this
                progresoConexion.dismiss();
            }
        }

        //Asociamos el hilo con el socket
        hiloConectado = new ConnectedThread(btSocket);
        hiloConectado.start();//Lo iniciamos

        progresoConexion.dismiss();
    }

    private void desconectarBT() {
        try {
            //Cuando nos vamos del activity no olvidarnos de cerrar el socket
            btSocket.close();
        } catch (IOException e) {
            //insert code to deal with this
        }
    }

    /*private class ConnectBT extends AsyncTask<Void, Void, Void>  // UI thread
    {
        private boolean ConnectSuccess = true; //if it's here, it's almost connected

        @Override
        protected void onPreExecute()
        {
            progress = ProgressDialog.show(ListaProgramasActivity.this, "Connecting...", "Please wait!!!");  //show a progress dialog
        }

        @Override
        protected Void doInBackground(Void... devices) //while the progress dialog is shown, the connection is done in background
        {
            try
            {
                if (btSocket == null || !isBtConnected)
                {
                    btAdapter = BluetoothAdapter.getDefaultAdapter();//get the mobile bluetooth device
                    BluetoothDevice dispositivo = btAdapter.getRemoteDevice(direccionMAC);//connects to the device's address and checks if it's available
                    btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(BTMODULEUUID);//create a RFCOMM (SPP) connection
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    btSocket.connect();//start connection
                }
            }
            catch (IOException e)
            {
                ConnectSuccess = false;//if the try failed, you can check the exception here
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) //after the doInBackground, it checks if everything went fine
        {
            super.onPostExecute(result);

            if (!ConnectSuccess)
            {
                msg("Connection Failed. Is it a SPP Bluetooth? Try again.");
                finish();
            }
            else
            {
                msg("Connected.");
                isBtConnected = true;
            }
            progress.dismiss();
        }
    }*/

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

        public void run() {

        }

        //write method
        public void write(String input) {
            byte[] msgBuffer = input.getBytes();//Convierte el string a bytes
            try {
                mmOutStream.write(msgBuffer);//Envia bytes atravez de la conexion BT via outstream
            } catch (IOException e) {
                //Excepsion
                Toast.makeText(getBaseContext(), "La Conexión fallo", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }
}
