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
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.io.IOException;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

//import android.support.v7.widget.AppCompatCheckBox;

/**
 * Created by Durgrim on 3/12/2016.
 * <p>
 * Se comenta todo lo relacionado con los fragmento, hay que refactorizar para que funcionen los fragmentos
 */

public class ListaProgramasActivity extends AppCompatActivity {
    private static UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    //Otras variables
    private static String nombreBluetooth;
    //Objetos de la pantalla
    private Button btEncender, btApagar, btBorrarPrograma, btAnadirPrograma;
    //private CheckBox chbPrograma; por el momento lo comentamos
    //Bluetooth
    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    private ConnectedThread hiloConectado;

    //Es para la conexion async, borrar si no se usa
    private ProgressDialog progresoConexion;

    private ListView listaProgramas;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //BTMODULEUUID = generarUUID(); //Se comenta porque aun no funciona, revisarlo cuando se tenga tiempo

        BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

        setContentView(R.layout.activity_lista_programas);

        listaProgramas = (ListView) findViewById(R.id.listaProgramas);

        List<Programa> programas = null;
        try {
            programas = (List<Programa>) ProgramaFactory.getInstance(ListaProgramasActivity.this).obtenerProgramas();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        /* Posibilidad de mostrar mensaje de lista vacia, no funciona , hay que testearlo mas
        if (programas==null || programas.isEmpty()) {
            TextView vacio = (TextView) findViewById(R.id.emptyListView);
            listaProgramas.setEmptyView(vacio);
        }
        else  {
        */
            //Obtenemos los datos de la BD
            listaProgramas.setAdapter(new ProgramaAdapter(programas));

            listaProgramas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int posicion, long id) {
                    Programa programa = (Programa) listaProgramas.getItemAtPosition(posicion);

                Intent detalle = new Intent(ListaProgramasActivity.this, DetalleProgramaActivity.class);
                detalle.putExtra("id", programa.getId());
                detalle.putExtra("ACCION","M");
                detalle.putExtra("nombreBluetooth", nombreBluetooth);

                startActivity(detalle);
            }
        });

        //Obtenemos los parametros del activity/pantalla anterior (ListaDispositivosActivity)
        nombreBluetooth = getIntent().getExtras().getString("nombreBluetooth");

        //Recuperamos el objeto de conexion con el BT
        hiloConectado = Singleton.getInstance().getConnectedThread();

        TextView txtNombreDispositivo = (TextView) findViewById(R.id.txtNombreDispositivo);

        txtNombreDispositivo.setText(nombreBluetooth);

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
                Toast.makeText(ListaProgramasActivity.this,
                        "Anadir", Toast.LENGTH_SHORT).show();

                Intent intent  = new Intent(ListaProgramasActivity.this, DetalleProgramaActivity.class);

                intent.putExtra("ACCION","A");
                intent.putExtra("nombreBluetooth", nombreBluetooth);

                startActivity(intent);
            }
        });

        //Por el momento lo comentamos
        //chbPrograma = (CheckBox) findViewById(R.id.chbPrograma);

        /*btBorrarPrograma = (Button) findViewById(R.id.btnBorrarPrograma);
        btBorrarPrograma.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent  = new Intent(ListaProgramasActivity.this, DetalleProgramaActivity.class);

                intent.putExtra("ACCION","M");
                intent.putExtra("BT", (Serializable) hiloConectado);

                startActivity(intent);

                Toast.makeText(ListaProgramasActivity.this,
                        "Borrar", Toast.LENGTH_SHORT).show();
            }
        });*/

        btAdapter = BluetoothAdapter.getDefaultAdapter();
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
        try {
            ((ProgramaAdapter)listaProgramas.getAdapter()).setProgramas(ProgramaFactory.getInstance(this).obtenerProgramas());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
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

    private void desconectarBT() {
        try {
            //Cuando nos vamos del activity no olvidarnos de cerrar el socket
            btSocket.close();
        } catch (IOException e) {
            //insert code to deal with this
        }
    }


}
