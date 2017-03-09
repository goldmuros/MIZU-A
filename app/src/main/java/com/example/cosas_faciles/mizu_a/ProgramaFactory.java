package com.example.cosas_faciles.mizu_a;

import android.content.Context;
import java.sql.SQLException;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.table.TableUtils;

//import java.util.ArrayList;
import java.util.List;

/**
 * Created by Durgrim on 27/01/17.
 */
public class ProgramaFactory {

    private static ProgramaFactory instance;
    //private List<Programa> programas;
    private Dao<Programa, Integer> dao;//Lo usamos para la BD ormlite

    static ProgramaFactory getInstance(Context context) throws SQLException {
        if (instance == null) {
            //instance = new ProgramaFactory();//Sacamos el contexto para que utilice el constructor que hicimos
            instance = new ProgramaFactory(context);
        }
        return instance;
    }

    //Habilitarlo cuando hagamos la conexion a la BD
    private ProgramaFactory(Context context) throws SQLException {
       OrmLiteSqliteOpenHelper helper = OpenHelperManager.getHelper(context, DatabaseHelper.class);

       dao = helper.getDao(Programa.class);
    }

    public void guardarPrograma(Programa programa) throws SQLException {
        dao.create(programa);
    }

    public List<Programa> obtenerProductos() throws SQLException {
        return dao.queryForAll();
    }

    public Programa buscarPrograma(int id) throws SQLException {
        return dao.queryForId(id);
    }

    public void eliminarPrograma(Programa programa) throws SQLException {
        dao.delete(programa);
    }

    public void modificarPrograma(Programa programa) throws SQLException {
        dao.update(programa);
    }

    public int cantidadProgramas() throws  SQLException {
        Long cant = new Long(dao.countOf());

        //Devolvemos un int para que se pueda buscar por ID en el metodo buscarPrograma, cosas de Android
        return cant.intValue();
    }

}

