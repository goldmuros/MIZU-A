package com.example.cosas_faciles.mizu_a;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.List;


public class FragmentoListaProgramas extends Fragment {
    private ListView listaProgramas;

    private List<Programa> programas;

    private Callbacks callback;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        programas = new ProgramaFactory().obtenerProgramas();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            callback = (Callbacks) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.getClass().getName() + " debe implementar la " +
                    "interface Callbacks");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragmento_lista_programas, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        listaProgramas = (ListView) getView();
        listaProgramas.setAdapter(new ProgramaAdapter(programas));
        listaProgramas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                callback.onProgramaSeleccionado((Programa) listaProgramas.getItemAtPosition(i));
            }
        });
    }

    public interface Callbacks {
        public void onProgramaSeleccionado(Programa programa);
    }

}
