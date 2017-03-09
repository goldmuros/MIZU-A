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
        dia.setText(String.valueOf(programa.getDia()));

        TextView hora = (TextView) view.findViewById(R.id.horaComienzoListaprograma);
        hora.setText(String.valueOf(programa.getHora()));

        TextView duracion = (TextView) view.findViewById(R.id.duracionListaprograma);
        duracion.setText(String.valueOf(programa.getDuracion()));

        Button btnSuspender = (Button) view.findViewById(R.id.btnSuspender);
        if(programa.getActivo().equals("S")){
            btnSuspender.setText(R.string.Activo);
        } else {
            btnSuspender.setText(R.string.Suspender);
        }

        return view;
    }
}
