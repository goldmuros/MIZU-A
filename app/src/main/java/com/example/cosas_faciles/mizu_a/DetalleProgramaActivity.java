package com.example.cosas_faciles.mizu_a;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Durgrim on 21/2/2017.
 */

public class DetalleProgramaActivity extends AppCompatActivity {
    //Objetos de la pantalla
    private Button btAceptar, btCancelar, btEliminar;//, btActivo, btSuspender;
    private CheckBox chbLun, chbMar, chbMie, chbJue, chbVie, chbSab, chbDom;
    private EditText txtHsComienzo, txtMinComienzo;
    private Spinner spinTiempoRiego;

    //Variables
    private String duracion;
    private int duracionPosicion;
    private static String nombreBluetooth;
    private ConnectedThread hiloConectado;
    private Programa programaModificar;
    private String accion;

    /** Variables para el reloj*/
    private Button pickTime;
    private int pHour;
    private int pMinute;
    /** This integer will uniquely define the dialog to be used for displaying time picker.*/
    static final int TIME_DIALOG_ID = 0;

    /*Switch button Encendido Manual*/
    Switch switchButtonManual, switchButtonEstadoRiego;
    TextView textViewManual;
    TextView textViewEstadoRiego;
    String switchOnManual = "Encendido";
    String switchOffManual = "Apagado";
    String switchOnEstadoRiego = "Activo";
    String switchOffEstadoRiego = "Suspendido";


    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }




    /**
     * RELOJ
     * Callback received when the user "picks" a time in the dialog */
    private TimePickerDialog.OnTimeSetListener mTimeSetListener =
            new TimePickerDialog.OnTimeSetListener() {
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    pHour = hourOfDay;
                    pMinute = minute;
                    updateDisplay();
                    displayToast();
                }
            };

    /**
     * RELOJ
     * Updates the time in the TextView */
    private void updateDisplay() {
        txtHsComienzo.setText(pad(pHour));
        txtMinComienzo.setText(pad(pMinute));
        //displayTime.setText(
         //       new StringBuilder()
         //               .append(pad(pHour)).append(":")
         //               .append(pad(pMinute)));
    }

    /**
     * RELOJ
     * Displays a notification when the time is updated */
    private void displayToast() {
        String displayTime = txtHsComienzo.getText() + ":" + txtMinComienzo.getText();
        Toast.makeText(this, new StringBuilder().append("Time choosen is ").append(displayTime),	Toast.LENGTH_SHORT).show();

    }

    /**
     * RELOJ
     * Add padding to numbers less than ten */
    private static String pad(int c) {
        if (c >= 10)
            return String.valueOf(c);
        else
            return "0" + String.valueOf(c);
    }




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_detalle_programa);

        //Recuperamos el objeto de conexion con el BT
        hiloConectado = Singleton.getInstance().getConnectedThread();

        //Botones
        btAceptar = (Button) findViewById(R.id.btnAceptarDetalle);
        btEliminar = (Button) findViewById(R.id.btnEliminarProgramaDetalle);
        btCancelar = (Button) findViewById(R.id.btnCancelarDetalle);
        //btActivo = (Button) findViewById(R.id.btnActivoProgramaDetalle);
        //btSuspender = (Button) findViewById(R.id.btnSuspenderrogramaDetalle);

        //Checkboxs
        chbLun = (CheckBox) findViewById(R.id.chbLun);
        chbMar = (CheckBox) findViewById(R.id.chbMar);
        chbMie = (CheckBox) findViewById(R.id.chbMie);
        chbJue = (CheckBox) findViewById(R.id.chbJue);
        chbVie = (CheckBox) findViewById(R.id.chbVie);
        chbSab = (CheckBox) findViewById(R.id.chbSab);
        chbDom = (CheckBox) findViewById(R.id.chbDom);

        //EditView
        txtHsComienzo = (EditText) findViewById(R.id.txtHsComienzo);
        txtMinComienzo = (EditText) findViewById(R.id.txtMinComienzo);

        //Spinner
        spinTiempoRiego = (Spinner) findViewById(R.id.spinTiempoRiego);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.aTiempoRiego, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinTiempoRiego.setAdapter(adapter);

        //Nombre del Bluetooth
        nombreBluetooth = getIntent().getExtras().getString("nombreBluetooth");

        TextView txtNombreDispositivo = (TextView) findViewById(R.id.txtNombreDispositivo);
        txtNombreDispositivo.setText(nombreBluetooth);

        btCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Volvemos al activity anterior
                onBackPressed();
            }
        });

        //Eliminamos el programa
        btEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    ProgramaFactory.getInstance(DetalleProgramaActivity.this).eliminarPrograma(programaModificar);
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                //Volvemos al activity anterior
                onBackPressed();
            }
        });

        chbLun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (accion.equals("M")) {
                    //Si esta chequeado se deshabilitan los otros checks
                    if (chbLun.isChecked()) {
                        deshabilitarChecks(1);
                    } else {
                        //Si NO esta chequeado se habilitan los otros checks
                        habilitarChecks(1);
                    }
                }
            }
        });

        chbMar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (accion.equals("M")) {
                    //Si esta chequeado se deshabilitan los otros checks
                    if (chbMar.isChecked()) {
                        deshabilitarChecks(2);
                    } else {
                        //Si NO esta chequeado se habilitan los otros checks
                        habilitarChecks(2);
                    }
                }
            }
        });

        chbMie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (accion.equals("M")) {
                    //Si esta chequeado se deshabilitan los otros checks
                    if (chbMie.isChecked()) {
                        deshabilitarChecks(3);
                    } else {
                        //Si NO esta chequeado se habilitan los otros checks
                        habilitarChecks(3);
                    }
                }
            }
        });

        chbJue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (accion.equals("M")) {
                    //Si esta chequeado se deshabilitan los otros checks
                    if (chbJue.isChecked()) {
                        deshabilitarChecks(4);
                    } else {
                        //Si NO esta chequeado se habilitan los otros checks
                        habilitarChecks(4);
                    }
                }
            }
        });

        chbVie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (accion.equals("M")) {
                    //Si esta chequeado se deshabilitan los otros checks
                    if (chbVie.isChecked()) {
                        deshabilitarChecks(5);
                    } else {
                        //Si NO esta chequeado se habilitan los otros checks
                        habilitarChecks(5);
                    }
                }
            }
        });

        chbSab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (accion.equals("M")) {
                    //Si esta chequeado se deshabilitan los otros checks
                    if (chbSab.isChecked()) {
                        deshabilitarChecks(6);
                    } else {
                        //Si NO esta chequeado se habilitan los otros checks
                        habilitarChecks(6);
                    }
                }
            }
        });

        chbDom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (accion.equals("M")) {
                    //Si esta chequeado se deshabilitan los otros checks
                    if (chbDom.isChecked()) {
                        deshabilitarChecks(7);
                    } else {
                        //Si NO esta chequeado se habilitan los otros checks
                        habilitarChecks(7);
                    }
                }
            }
        });

        btAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String dias = "";
                int programaId = 0;

                try {
                    //Si es Añadir obtenemos la cantidad de programas para generar un nuevo ID
                    programaId = ProgramaFactory.getInstance(DetalleProgramaActivity.this).cantidadProgramas();
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                if (validarHoraComienzo(txtHsComienzo.getText().toString(),
                                        txtMinComienzo.getText().toString())) {

                    String horaComienzo = txtHsComienzo.getText().toString() + ":" +
                            txtMinComienzo.getText().toString();

                    if (accion.equals("A")) {
                        //Preparar logica que lea todos los dias si estan chequeados y que los vaya guardando
                        if (chbLun.isChecked()) {
                            guardarPrograma(programaId++, "Lun", horaComienzo,
                                    duracion, duracionPosicion, "A");

                            dias = "Lun";
                        }

                        if (chbMar.isChecked()) {
                            guardarPrograma(programaId++, "Mar", horaComienzo,
                                    duracion, duracionPosicion, "A");

                            if (dias.isEmpty()) {
                                dias = "Mar";
                            } else {
                                dias += ",Mar";
                            }
                        }

                        if (chbMie.isChecked()) {
                            guardarPrograma(programaId++, "Mie", horaComienzo,
                                    duracion, duracionPosicion, "A");

                            if (dias.isEmpty()) {
                                dias = "Mie";
                            } else {
                                dias += ",Mie";
                            }
                        }

                        if (chbJue.isChecked()) {
                            guardarPrograma(programaId++, "Jue", horaComienzo,
                                    duracion, duracionPosicion, "A");

                            if (dias.isEmpty()) {
                                dias = "Jue";
                            } else {
                                dias += ",Jue";
                            }
                        }

                        if (chbVie.isChecked()) {
                            guardarPrograma(programaId++, "Vie", horaComienzo,
                                    duracion, duracionPosicion, "A");

                            if (dias.isEmpty()) {
                                dias = "Vie";
                            } else {
                                dias += ",Vie";
                            }
                        }

                        if (chbSab.isChecked()) {
                            guardarPrograma(programaId++, "Sab", horaComienzo,
                                    duracion, duracionPosicion, "A");

                            if (dias.isEmpty()) {
                                dias = "Sab";
                            } else {
                                dias += ",Sab";
                            }
                        }

                        if (chbDom.isChecked()) {

                            guardarPrograma(programaId++, "Dom", horaComienzo,
                                    duracion, duracionPosicion, "A");

                            if (dias.isEmpty()) {
                                dias = "Dom";
                            } else {
                                dias += ",Dom";
                            }
                        }
                    } else {
                        dias = "";
                        programaModificar.setDuracion(duracion);
                        programaModificar.setDuracionPosicion(duracionPosicion);
                        programaModificar.setHora(horaComienzo);
                        programaModificar.setActivo("A");

                        //Preparar logica que lea todos los dias si estan chequeados y que los vaya guardando
                        if (chbLun.isChecked()) {
                            programaModificar.setDia("Lun");

                            modificarPrograma(programaModificar);

                            dias = "Lun";
                        }

                        if (chbMar.isChecked()) {
                            programaModificar.setDia("Mar");

                            modificarPrograma(programaModificar);

                            if (dias.isEmpty()) {
                                dias = "Mar";
                            } else {
                                dias += ",Mar";
                            }
                        }

                        if (chbMie.isChecked()) {
                            programaModificar.setDia("Mie");

                            modificarPrograma(programaModificar);

                            if (dias.isEmpty()) {
                                dias = "Mie";
                            } else {
                                dias += ",Mie";
                            }
                        }

                        if (chbJue.isChecked()) {
                            programaModificar.setDia("Jue");

                            modificarPrograma(programaModificar);

                            if (dias.isEmpty()) {
                                dias = "Jue";
                            } else {
                                dias += ",Jue";
                            }
                        }

                        if (chbVie.isChecked()) {
                            programaModificar.setDia("Vie");

                            modificarPrograma(programaModificar);

                            if (dias.isEmpty()) {
                                dias = "Vie";
                            } else {
                                dias += ",Vie";
                            }
                        }

                        if (chbSab.isChecked()) {
                            programaModificar.setDia("Sab");

                            modificarPrograma(programaModificar);

                            if (dias.isEmpty()) {
                                dias = "Sab";
                            } else {
                                dias += ",Sab";
                            }
                        }

                        if (chbDom.isChecked()) {
                            programaModificar.setDia("Dom");

                            modificarPrograma(programaModificar);

                            if (dias.isEmpty()) {
                                dias = "Dom";
                            } else {
                                dias += ",Dom";
                            }
                        }
                    }

                    String datosEnviar = dias + ";" + horaComienzo + ";" +
                            duracion + ";A";

                    //hiloConectado.write(datosEnviar);

                    onBackPressed();
                }
            }
        });

        spinTiempoRiego.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                //Guardamos el valor de lo que se seleccion en el combo/spinner
                duracion = (String) parent.getItemAtPosition(pos);

                duracionPosicion = pos;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //Guardamos el valor de lo que se seleccion en el combo/spinner
                duracion = (String) parent.getItemAtPosition(0);

                duracionPosicion = 0;
            }
        });

        /**Agregado reloj para seleccionar la hora de comienzo del programa */
        addQuickTime();

        /*Switch button Encendido Manual*/
        //addSwitchWidgetManual();
        //addSwitchWidget(R.id.switchButtonManual, R.id.textViewManual, switchOnManual, switchOffManual);
        addSwitchWidgetManual();
        /*Switch button Estado de Riego*/
        //addSwitchWidgetEstadoRiego();

        //addSwitchWidget(R.id.switchButtonEstadoRiego, R.id.textViewEstadoRiego, switchOnEstadoRiego, switchOffEstadoRiego);
        addSwitchWidgetEstadoRiego();
    }

    /**
     *
     */
    private void addQuickTime(){
        /** Ini : agregado reloj dialogo para seleccionar la hora*/
        /** Capture our View elements */
        pickTime = (Button) findViewById(R.id.pickTime);

        /** Listener for click event of the button */
        pickTime.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDialog(TIME_DIALOG_ID);
            }
        });

        /** Get the current time */
        final Calendar cal = Calendar.getInstance();
        pHour = cal.get(Calendar.HOUR_OF_DAY);
        pMinute = cal.get(Calendar.MINUTE);

        updateDisplay();
        /** Display the current time in the TextView */
    }

    /**
     * Funcionalidad Switch Manual
     */
    /**/
    private void addSwitchWidgetManual(){
        // For first switch button
        switchButtonManual = (Switch) findViewById(  R.id.switchButtonManual);
        //textViewManual = (TextView) findViewById(R.id.textViewManual);

        switchButtonManual.setChecked(true);
        switchButtonManual.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean bChecked) {
                if (bChecked) {
                    //textViewManual.setText(switchOnManual);
                } else {
                    //textViewManual.setText(switchOffManual);
                }
            }
        });

        if (switchButtonManual.isChecked()) {
           // textViewManual.setText(switchOnManual);
        } else {
            //textViewManual.setText(switchOffManual);
        }
    }
    /**/

    /**
     * Funcionalidad Switch Estado de Riego
     */
    /**/
    private void addSwitchWidgetEstadoRiego(){
        // For first switch button
        switchButtonEstadoRiego = (Switch) findViewById( R.id.switchButtonEstadoRiego );
        //textViewEstadoRiego = (TextView) findViewById(R.id.textViewEstadoRiego);

        switchButtonEstadoRiego.setChecked(true);
        switchButtonEstadoRiego.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean bChecked) {
                if (bChecked) {
                   // textViewEstadoRiego.setText(switchOnEstadoRiego);
                } else {
                   // textViewEstadoRiego.setText(switchOffEstadoRiego);
                }
            }
        });

        if (switchButtonEstadoRiego.isChecked()) {
           /// textViewEstadoRiego.setText(switchOnEstadoRiego);
        } else {
           // textViewEstadoRiego.setText(switchOffEstadoRiego);
        }

   /*     int delay = 5000; // delay for 5 sec.
        int period = 1000; // repeat every sec.

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask()
        {
            public void run()
            {
                textViewEstadoRiego.setText("");

            }
        }, delay, period);
        */
    }
    /**/

    /**
     * Funcionalidad Switch para
     * Encendido Manual
     * y
     * Estado de Riego
     */
    /*
    private void addSwitchWidget(int idSwicth, int idTexto, final String switchOn, final String switchOff){
        // For first switch button
        switchButton = (Switch) findViewById( idSwicth );
        textView = (TextView) findViewById(idTexto);

        switchButton.setChecked(true);
        switchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean bChecked) {
                if (bChecked) {
                    textView.setText(switchOn);
                } else {
                    textView.setText(switchOff);
                }
            }
        });

        if (switchButton.isChecked()) {
            textView.setText(switchOn);
        } else {
            textView.setText(switchOff);
        }
    }
    */
    /** Create a new dialog for time picker */

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case TIME_DIALOG_ID:
                return new TimePickerDialog(this,
                        mTimeSetListener, pHour, pMinute, false);
        }
        return null;
    }

    @Override
    protected void onResume() {
        super.onResume();

        accion = getIntent().getStringExtra("ACCION");

        if (accion.equals("M")) {
            //programaModificar = (Programa) getIntent().getSerializableExtra("BT");
            Bundle extras = getIntent().getExtras();

            int programaId = extras.getInt("id");

            try {
                programaModificar = ProgramaFactory.getInstance(this).buscarPrograma(programaId);

                setearModificaciones(programaModificar);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void guardarPrograma(int id, String dia, String hora, String duracion, int duracionPosicion,
                                 String activo) {
        Programa programa = new Programa(id, dia, hora, duracion, duracionPosicion, activo);

        try {
            ProgramaFactory.getInstance(DetalleProgramaActivity.this).guardarPrograma(programa);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void modificarPrograma(Programa programaModificar) {
        try {
            ProgramaFactory.getInstance(DetalleProgramaActivity.this).modificarPrograma(programaModificar);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void setearModificaciones(Programa programa) {
        String dia = programa.getDia();
        String hora = programa.getHora();
        int duracionPosicion = programa.getDuracionPosicion();

        if (dia.equals("Lun")) {
            chbLun.setChecked(true);

            deshabilitarChecks(1);
        } else if (dia.equals("Mar")) {
            chbMar.setChecked(true);

            deshabilitarChecks(2);
        } else if (dia.equals("Mie")) {
            chbMie.setChecked(true);

            deshabilitarChecks(3);
        } else if (dia.equals("Jue")) {
            chbJue.setChecked(true);

            deshabilitarChecks(4);
        } else if (dia.equals("Vie")) {
            chbVie.setChecked(true);

            deshabilitarChecks(5);
        } else if (dia.equals("Sab")) {
            chbSab.setChecked(true);

            deshabilitarChecks(6);
        } else {
            chbDom.setChecked(true);

            deshabilitarChecks(7);
        }

        String [] arrayHora = hora.split(":");

        txtHsComienzo.setText(arrayHora[0]);
        txtMinComienzo.setText(arrayHora[1]);
        spinTiempoRiego.setSelection(duracionPosicion);
    }

    private void habilitarChecks(int diaCheckeado) {
        switch (diaCheckeado) {
            //Lunes
            case 1:
                chbMar.setEnabled(true);
                chbMie.setEnabled(true);
                chbJue.setEnabled(true);
                chbVie.setEnabled(true);
                chbSab.setEnabled(true);
                chbDom.setEnabled(true);
                break;
            //Martes
            case 2:
                chbLun.setEnabled(true);
                chbMie.setEnabled(true);
                chbJue.setEnabled(true);
                chbVie.setEnabled(true);
                chbSab.setEnabled(true);
                chbDom.setEnabled(true);
                break;
            //Miercoles
            case 3:
                chbLun.setEnabled(true);
                chbMar.setEnabled(true);
                chbJue.setEnabled(true);
                chbVie.setEnabled(true);
                chbSab.setEnabled(true);
                chbDom.setEnabled(true);
                break;
            //Jueves
            case 4:
                chbLun.setEnabled(true);
                chbMar.setEnabled(true);
                chbMie.setEnabled(true);
                chbVie.setEnabled(true);
                chbSab.setEnabled(true);
                chbDom.setEnabled(true);
                break;
            //Viernes
            case 5:
                chbLun.setEnabled(true);
                chbMar.setEnabled(true);
                chbMie.setEnabled(true);
                chbJue.setEnabled(true);
                chbSab.setEnabled(true);
                chbDom.setEnabled(true);
                break;
            //Sabado
            case 6:
                chbLun.setEnabled(true);
                chbMar.setEnabled(true);
                chbMie.setEnabled(true);
                chbJue.setEnabled(true);
                chbVie.setEnabled(true);
                chbDom.setEnabled(true);
                break;
            //Domingo
            case 7:
                chbLun.setEnabled(true);
                chbMar.setEnabled(true);
                chbMie.setEnabled(true);
                chbJue.setEnabled(true);
                chbVie.setEnabled(true);
                chbSab.setEnabled(true);
                break;
        }
    }

    private void deshabilitarChecks(int diaCheckeado) {
        switch (diaCheckeado) {
            //Lunes
            case 1:
                chbMar.setEnabled(false);
                chbMie.setEnabled(false);
                chbJue.setEnabled(false);
                chbVie.setEnabled(false);
                chbSab.setEnabled(false);
                chbDom.setEnabled(false);
                break;
            //Martes
            case 2:
                chbLun.setEnabled(false);
                chbMie.setEnabled(false);
                chbJue.setEnabled(false);
                chbVie.setEnabled(false);
                chbSab.setEnabled(false);
                chbDom.setEnabled(false);
                break;
            //Miercoles
            case 3:
                chbLun.setEnabled(false);
                chbMar.setEnabled(false);
                chbJue.setEnabled(false);
                chbVie.setEnabled(false);
                chbSab.setEnabled(false);
                chbDom.setEnabled(false);
                break;
            //Jueves
            case 4:
                chbLun.setEnabled(false);
                chbMar.setEnabled(false);
                chbMie.setEnabled(false);
                chbVie.setEnabled(false);
                chbSab.setEnabled(false);
                chbDom.setEnabled(false);
                break;
            //Viernes
            case 5:
                chbLun.setEnabled(false);
                chbMar.setEnabled(false);
                chbMie.setEnabled(false);
                chbJue.setEnabled(false);
                chbSab.setEnabled(false);
                chbDom.setEnabled(false);
                break;
            //Sabado
            case 6:
                chbLun.setEnabled(false);
                chbMar.setEnabled(false);
                chbMie.setEnabled(false);
                chbJue.setEnabled(false);
                chbVie.setEnabled(false);
                chbDom.setEnabled(false);
                break;
            //Domingo
            case 7:
                chbLun.setEnabled(false);
                chbMar.setEnabled(false);
                chbMie.setEnabled(false);
                chbJue.setEnabled(false);
                chbVie.setEnabled(false);
                chbSab.setEnabled(false);
                break;
        }
    }

    private boolean validarHoraComienzo(String sHora, String sMinuto){
        int iHora = (new Integer(sHora)).intValue();
        int iMinuto = (new Integer(sMinuto)).intValue();

        if (23 < iHora){
            AlertDialog dialog = crearDialogoErrorHora(getString(R.string.ErrorHora),
                    getString(R.string.MsgErrorHora), 1);

            dialog.show();

            return false;
        } else if (59 < iMinuto){
            AlertDialog dialog = crearDialogoErrorHora(getString(R.string.ErrorMinutos),
                    getString(R.string.MsgErrorMinutos), 2);

            dialog.show();

            return false;
        } else {
            return true;
        }

    }

    /**
     * Dialogo para confirmar salir d ela app
     */
    private AlertDialog crearDialogoErrorHora(String titulo, String mensaje, final int iCampo) {
        // Instanciamos un nuevo AlertDialog Builder y le asociamos titulo y mensaje
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(titulo);
        alertDialogBuilder.setMessage(mensaje);

        // Creamos un nuevo OnClickListener para el boton OK
        DialogInterface.OnClickListener listenerOk = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (iCampo) {
                    case 1:
                        txtHsComienzo.setFocusable(true);
                        break;
                    case 2:
                        txtMinComienzo.setFocusable(true);
                        break;
                }

            }
        };

        // Asignamos los botones positivo y negativo a sus respectivos listeners
        alertDialogBuilder.setPositiveButton(R.string.Ok, listenerOk);


        return alertDialogBuilder.create();
    }
}
