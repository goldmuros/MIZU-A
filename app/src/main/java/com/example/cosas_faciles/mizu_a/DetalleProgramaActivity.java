package com.example.cosas_faciles.mizu_a;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import java.sql.SQLException;

/**
 * Created by Durgrim on 21/2/2017.
 */

public class DetalleProgramaActivity extends AppCompatActivity {

    int programaId = 0;
    //Objetos de la pantalla
    private Button btAceptar, btCancelar, btEliminar;
    private CheckBox chbLun, chbMar, chbMie, chbJue, chbVie, chbSab, chbDom;
    private EditText txtHsComienzo;
    private Spinner spinTiempoRiego;

    //Variables
    private String duracion;
    private int duracionPosicion;
    //private ConnectedThread hiloConectado;
    private Programa programaModificar;
    private String accion;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_detalle_programa);

        //Recuperamos el objeto de conexion con el BT
        //hiloConectado = (ConnectedThread) getIntent().getSerializableExtra("BT");

        //Botones
        btAceptar = (Button) findViewById(R.id.btnAceptarDetalle);
        btEliminar = (Button) findViewById(R.id.btnEliminarProgramaDetalle);
        btCancelar = (Button) findViewById(R.id.btnCancelarDetalle);

        //Checkboxs
        chbLun = (CheckBox) findViewById(R.id.chbLun);
        chbMar = (CheckBox) findViewById(R.id.chbMar);
        chbMie = (CheckBox) findViewById(R.id.chbMie);
        chbJue = (CheckBox) findViewById(R.id.chbJue);
        chbVie = (CheckBox) findViewById(R.id.chbVie);
        chbSab = (CheckBox) findViewById(R.id.chbSab);
        chbDom = (CheckBox) findViewById(R.id.chbDom);

        //TextView
        txtHsComienzo = (EditText) findViewById(R.id.txtHsComienzo);

        //Spinner
        spinTiempoRiego = (Spinner) findViewById(R.id.spinTiempoRiego);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.aTiempoRiego, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinTiempoRiego.setAdapter(adapter);


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

        btAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String dias = "";

                if (accion.equals("A")) {
                    //Preparar logica que lea todos los dias si estan chequeados y que los vaya guardando
                    if (chbLun.isChecked()) {
                        //implementar la logica para el spinner, es un bardo
                        guardarPrograma(programaId + 1, "Lun", txtHsComienzo.getText().toString(),
                                duracion, duracionPosicion, "A");

                        dias = "Lun";
                    }

                    if (chbMar.isChecked()) {
                        //implementar la logica para el spinner, es un bardo
                        guardarPrograma(programaId + 1, "Mar", txtHsComienzo.getText().toString(),
                                duracion, duracionPosicion, "A");

                        if (dias.isEmpty()) {
                            dias = "Mar";
                        } else {
                            dias += ",Mar";
                        }
                    }

                    if (chbMie.isChecked()) {
                        //implementar la logica para el spinner, es un bardo
                        guardarPrograma(programaId + 1, "Mie", txtHsComienzo.getText().toString(),
                                duracion, duracionPosicion, "A");

                        if (dias.isEmpty()) {
                            dias = "Mie";
                        } else {
                            dias += ",Mie";
                        }
                    }

                    if (chbJue.isChecked()) {
                        //implementar la logica para el spinner, es un bardo
                        guardarPrograma(programaId + 1, "Jue", txtHsComienzo.getText().toString(),
                                duracion, duracionPosicion, "A");

                        if (dias.isEmpty()) {
                            dias = "Jue";
                        } else {
                            dias += ",Jue";
                        }
                    }

                    if (chbVie.isChecked()) {
                        //implementar la logica para el spinner, es un bardo
                        guardarPrograma(programaId + 1, "Vie", txtHsComienzo.getText().toString(),
                                duracion, duracionPosicion, "A");

                        if (dias.isEmpty()) {
                            dias = "Vie";
                        } else {
                            dias += ",Vie";
                        }
                    }

                    if (chbSab.isChecked()) {
                        //implementar la logica para el spinner, es un bardo
                        guardarPrograma(programaId + 1, "Sab", txtHsComienzo.getText().toString(),
                                duracion, duracionPosicion, "A");

                        if (dias.isEmpty()) {
                            dias = "Sab";
                        } else {
                            dias += ",Sab";
                        }
                    }

                    if (chbDom.isChecked()) {
                        //implementar la logica para el spinner, es un bardo
                        guardarPrograma(programaId + 1, "Dom", txtHsComienzo.getText().toString(),
                                duracion, duracionPosicion, "A");

                        if (dias.isEmpty()) {
                            dias = "Dom";
                        } else {
                            dias += ",Dom";
                        }
                    }
                } else {
                    setearModificaciones(programaModificar);
                }

                String datosEnviar = dias + ";" + txtHsComienzo.getText().toString() + ";" +
                        duracion + ";A";

                //hiloConectado.write(datosEnviar);

                onBackPressed();
            }
        });

        spinTiempoRiego.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                //Guardamos el valor de lo que se seleccion en el combo/spinner
                duracion = (String) parent.getItemAtPosition(pos);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //Guardamos el valor de lo que se seleccion en el combo/spinner
                duracion = (String) parent.getItemAtPosition(0);
            }
        });

        accion = getIntent().getStringExtra("ACCION");

        if (accion.equals("A")) {
            try {
                //Si es Añadir obtenemos la cantidad de programas para generar un nuevo ID
                programaId = ProgramaFactory.getInstance(DetalleProgramaActivity.this).cantidadProgramas();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            //programaModificar = (Programa) getIntent().getSerializableExtra("BT");
            Bundle extras = getIntent().getExtras();

            programaId = extras.getInt("id");

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

    private void setearModificaciones(Programa programa) {
        String dia = programa.getDia();
        String hora = programa.getHora();
        int duracionPosicion = programa.getDuracionPosicion();

        if (dia.equals("Lun")) {
            chbLun.setChecked(true);
        } else if (dia.equals("Mar")) {
            chbMar.setChecked(true);
        } else if (dia.equals("Mie")) {
            chbMie.setChecked(true);
        } else if (dia.equals("Jue")) {
            chbJue.setChecked(true);
        } else if (dia.equals("Vie")) {
            chbVie.setChecked(true);
        } else if (dia.equals("Sab")) {
            chbSab.setChecked(true);
        } else {
            chbDom.setChecked(true);
        }

        txtHsComienzo.setText(hora);
        spinTiempoRiego.setSelection(duracionPosicion);
    }
}
