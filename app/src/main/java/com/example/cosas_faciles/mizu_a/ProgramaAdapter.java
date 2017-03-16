package com.example.cosas_faciles.mizu_a;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Durgrim on 15/1/2017.
 */

public class ProgramaAdapter extends BaseAdapter {
    private List<Programa> programas;

    public ProgramaAdapter(List<Programa> programas) {
        this.programas = programas;
    }

    public void setProgramas(List<Programa> programas){
        this.programas = programas;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return programas.size();
    }

    @Override
    public Object getItem(int posicion) {
        return programas.get(posicion);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int posicion, View convertView, ViewGroup viewGroup) {
        View view;

        if(convertView == null){
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_programa, viewGroup, false);
        }else{
            view = convertView;
        }

        Programa programa = (Programa) getItem(posicion);

        //CheckBox chbPrograma = (CheckBox) view.findViewById(R.id.chbPrograma);


        TextView dia = (TextView) view.findViewById(R.id.diaListaPrograma);

        if (programa.getDia().equals("Lun")){
            dia.setText("Lunes");
        } else if (programa.getDia().equals("Mar")) {
            dia.setText("Martes");
        } else if (programa.getDia().equals("Mie")) {
            dia.setText("Miércoles");
        } else if (programa.getDia().equals("Jue")) {
            dia.setText("Jueves");
        } else if (programa.getDia().equals("Vie")) {
            dia.setText("Viernes");
        } else if (programa.getDia().equals("Sab")) {
            dia.setText("Sábado");
        } else {
            dia.setText("Domingo");
        }

        TextView hora = (TextView) view.findViewById(R.id.horaComienzoListaprograma);
        hora.setText(String.valueOf(programa.getHora()));

        TextView duracion = (TextView) view.findViewById(R.id.duracionListaprograma);
        duracion.setText(String.valueOf(programa.getDuracion()));

        TextView suspender = (TextView) view.findViewById(R.id.suspenderActivarListaPrograma);
        if(programa.getActivo().equals("A")){
            suspender.setText(R.string.Activo);
        } else {
            suspender.setText(R.string.Suspender);
        }

        return view;
    }
}
