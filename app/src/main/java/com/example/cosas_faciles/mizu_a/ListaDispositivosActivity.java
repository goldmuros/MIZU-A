package com.example.cosas_faciles.mizu_a;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Set;


/**
 * Created by Durgrim on 4/12/2016.
 */

public class ListaDispositivosActivity extends AppCompatActivity {
    //Variables globales
    //Objetos de pantalla
    private LinearLayout layoutDispositivosVinculados;
    private ListView listaDispositivosVinculados;
    private Button btnSalir, btnBluetooth;

    //Otros objetos
    private BluetoothAdapter btAdapter = null;
    private Set<BluetoothDevice> dispositivosVinculados;

    private ArrayAdapter<String> dispositivosVinculadosArrayAdapter;

    private ArrayList<BluetoothDevice> arrayDispositivosVincuados = new ArrayList<>();

    private ProgressDialog progresoVinculando;//Mensaje para buscar dispositivos

    // Instanciamos un BroadcastReceiver que se encargara de detectar si el estado
    // del Bluetooth del dispositivo ha cambiado mediante su handler onReceive
    private final BroadcastReceiver bReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            // BluetoothAdapter.ACTION_STATE_CHANGED
            // Codigo que se ejecutara cuando el Bluetooth cambie su estado.
            // Manejaremos los siguientes estados:
            //		- STATE_OFF: El Bluetooth se desactiva
            //		- STATE ON: El Bluetooth se activa
            switch (action) {
                case BluetoothAdapter.ACTION_STATE_CHANGED: {
                    final int estado = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                            BluetoothAdapter.ERROR);
                    switch (estado) {
                        // Apagado
                        case BluetoothAdapter.STATE_OFF: {
                            Toast.makeText(ListaDispositivosActivity.this, "Apagado BT", Toast.LENGTH_SHORT).show();
                            //Apagado
                            break;
                        }

                        // Encendido
                        case BluetoothAdapter.STATE_ON: {
                            //Encendido
                            buscarDispositivosVinculados();
                            Toast.makeText(ListaDispositivosActivity.this, "Encendido BT", Toast.LENGTH_SHORT).show();

                        /*Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 120);
                        startActivity(discoverableIntent);*/

                            break;
                        }
                        default:
                            break;
                    }

                    break;
                }
                default:
                    break;
            }

        } // Fin onReceive
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Cargamos el layout de donde se van a tomar los elementos de la vista
        setContentView(R.layout.activity_lista_dispositivos);

        //Definimos la toolbar
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        configurarControles();

        //Ocultamos los layout de las listas
        layoutDispositivosVinculados.setVisibility(View.GONE);

        registrarEventosBluetooth();

        btnSalir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog dialog = crearDialogoSalir(getString(R.string.Salir),
                        getString(R.string.MsgConfirmarApagar));

                dialog.show();
            }
        });

        btnBluetooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (btAdapter.isEnabled()) {
                    btnBluetooth.setText(R.string.Conectar);
                } else {
                    btnBluetooth.setText(R.string.Desconectar);

                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, 1);
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        btAdapter = BluetoothAdapter.getDefaultAdapter();//Obtenemos el BT adapter

        //Inicializamos los array adapter para las listas de dispositivos
        dispositivosVinculadosArrayAdapter = new ArrayAdapter<>(this, R.layout.nombre_dispositivo);

        //Se asignan los arrays a las listas
        listaDispositivosVinculados.setAdapter(dispositivosVinculadosArrayAdapter);

        if (btAdapter == null) {
            Toast.makeText(getBaseContext(), "El dispositivo no soporta bluetooth", Toast.LENGTH_LONG).show();
        } else {
            //Si no esta hablitado el Bluetooth le pedimos al usuario que lo conecte
            if (btAdapter.isEnabled()) {
                buscarDispositivosVinculados();
                btnBluetooth.setText(R.string.Desconectar);
            }
        }

        listaDispositivosVinculados.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int posicion, long id) {

                BluetoothDevice dispositivo = (BluetoothDevice) arrayDispositivosVincuados.get(posicion);

                Toast.makeText(ListaDispositivosActivity.this,
                        "Nombre " + dispositivo.getName() + " Direccion: " + dispositivo.getAddress(), Toast.LENGTH_SHORT).show();

                //Preparamos los parametros para pasarselos al siguiente activity(pantalla)
                Intent intent = new Intent(ListaDispositivosActivity.this, ListaProgramasActivity.class);
                intent.putExtra("nombreBluetooth", dispositivo.getName());
                intent.putExtra("direccionMAC", dispositivo.getAddress());

                startActivity(intent);
            }
        });
    }

    private void pairDevice(BluetoothDevice device) {
        try {
            Method method = device.getClass().getMethod("createBond", (Class[]) null);
            method.invoke(device, (Object[]) null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Busca y carga en la lista los dispostivos que estan vinculados
    private void buscarDispositivosVinculados() {
        //Obtenemso los dispositivos vinculados
        dispositivosVinculados = btAdapter.getBondedDevices();

        layoutDispositivosVinculados.setVisibility(View.VISIBLE);

        // Agragamos los dispositivos vinculados al array corespondiente
        if (dispositivosVinculados.size() > 0) {
            for (BluetoothDevice dispositivo : dispositivosVinculados) {
                //Habiliitar cuando se tenga un mizu cerca
                if (dispositivo.getName().contains("MIZU")) {
                    dispositivosVinculadosArrayAdapter.add(dispositivo.getName());
                    //Para obtener los datos del dispositivo sin necesidad de ponerlos en la lista
                    arrayDispositivosVincuados.add(dispositivo);
                }
            }
        } else {
            String noHayDispositivos = getResources().getText(R.string.no_vinculados).toString();
            dispositivosVinculadosArrayAdapter.add(noHayDispositivos);
        }
    }

    /*Suscribe el BroadcastReceiver a los eventos relacionados con Bluetooth que queremos
      controlar.*/
    private void registrarEventosBluetooth() {
        // Registramos el BroadcastReceiver que instanciamos previamente para
        // detectar los distintos eventos que queremos recibir
        IntentFilter filtro = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        filtro.addAction(BluetoothDevice.ACTION_FOUND);
        filtro.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);

        this.registerReceiver(bReceiver, filtro);
    }

    /**
     * Metodo de configuracion de la actividad
     */
    private void configurarControles() {
        referenciarControles();
    }

    /**
     * Referencia los elementos de interfaz
     */
    private void referenciarControles() {
        // Referenciamos los elementos de interfaz
        layoutDispositivosVinculados = (LinearLayout) findViewById(R.id.layoutDispositivosVinculados);

        listaDispositivosVinculados = (ListView) findViewById(R.id.listaDispositivosVinculados);

        btnBluetooth = (Button) findViewById(R.id.btnBluetooth);
        btnSalir = (Button) findViewById(R.id.btnSalir);
    }

    /**
     * Dialogo para confirmar salir d ela app
     */
    private AlertDialog crearDialogoSalir(String titulo, String mensaje) {
        // Instanciamos un nuevo AlertDialog Builder y le asociamos titulo y mensaje
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(titulo);
        alertDialogBuilder.setMessage(mensaje);

        // Creamos un nuevo OnClickListener para el boton OK que realice la salida de la app
        DialogInterface.OnClickListener listenerOk = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Deshabilitamos el Bluetooth
                btAdapter.disable();

                //Cerramos la app
                finish();
            }
        };

        // Creamos un nuevo OnClickListener para el boton Cancelar
        DialogInterface.OnClickListener listenerSalir = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        };

        // Asignamos los botones positivo y negativo a sus respectivos listeners
        alertDialogBuilder.setPositiveButton(R.string.Si, listenerOk);
        alertDialogBuilder.setNegativeButton(R.string.No, listenerSalir);

        return alertDialogBuilder.create();
    }

    /*Ademas de realizar la destruccion de la actividad, eliminamos el registro del
    BroadcastReceiver.*/
    @Override
    public void onDestroy() {
        super.onDestroy();
/*        if(bReceiver != null)
            this.unregisterReceiver(bReceiver);*/
       /* if (servicio != null)
            servicio.finalizarServicio();*/
    }

    @Override
    protected void onStop() {
        super.onStop();

 /*       if(bReceiver != null)
            this.unregisterReceiver(bReceiver);*/
    }
}
