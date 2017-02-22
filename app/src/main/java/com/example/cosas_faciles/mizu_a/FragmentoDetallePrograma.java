package com.example.cosas_faciles.mizu_a;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class FragmentoDetallePrograma extends Fragment {

    public static final String DIA = "dia";
    public static final String HORA = "hora";
    public static final String TIEMPO = "tiempo";
    public static final String ACTIVO = "activo";
    public static final String ID = "id";

    private Programa programa;

    public static FragmentoDetallePrograma newInstance(Programa programa) {

        FragmentoDetallePrograma frag = new FragmentoDetallePrograma();

        Bundle args = new Bundle();

        //Preguntamos si el objeto tiene datos
            args.putInt(ID, programa.getId());
            args.putString(DIA, programa.getDia());
            args.putString(HORA, programa.getHora());
            args.putString(TIEMPO, programa.getDuracion());
            args.putString(ACTIVO, programa.getActivo());
            frag.setArguments(args);


        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(DIA)) {
            programa = new Programa(
                    getArguments().getInt(ID),
                    getArguments().getString(DIA),
                    getArguments().getString(HORA),
                    getArguments().getString(TIEMPO),
                    getArguments().getString(ACTIVO));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View vista = inflater.inflate(R.layout.fragmento_detalle_programa, container, false);

        ViewGroup detalle = (ViewGroup) vista.findViewById(R.id.layoutDetallePrograma);

        detalle.addView(container);

        return detalle;
        //return inflater.inflate(R.layout.fragmento_detalle_programa, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //Chequear las cosas que son correctas, primero ver si el fragmento funciona
       /* TextView telefono = (TextView) getView().findViewById(R.id.diaListaPrograma);
        telefono.setText(String.valueOf(contacto.getTelefono()));

        TextView nombre = (TextView) getView().findViewById(R.id.nombre);
        nombre.setText(contacto.getNombre());

        TextView apellido = (TextView) getView().findViewById(R.id.apellido);
        apellido.setText(contacto.getApellido());*/
    }
}
