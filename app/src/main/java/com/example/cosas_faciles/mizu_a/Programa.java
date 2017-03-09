package com.example.cosas_faciles.mizu_a;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;


/**
 * Created by Durgrim on 15/1/2017.
 */

@DatabaseTable(tableName = "programas")
public class Programa {
    @DatabaseField(id = true)
    private int id;
    @DatabaseField
    private String dia;
    @DatabaseField
    private String hora;
    @DatabaseField
    private String duracion;
    @DatabaseField
    private int duracionPosicion;
    @DatabaseField
    private String activo;

    public Programa() {
    }

    public Programa(int id, String dia, String hora, String duracion, int duracionPosicion, String activo) {
        this.id = id;
        this.dia = dia;
        this.hora = hora;
        this.duracion = duracion;
        this.duracionPosicion = duracionPosicion;
        this.activo = activo;
    }

    public int getDuracionPosicion() {
        return duracionPosicion;
    }

    public void setDuracionPosicion(int duracionPosicion) {
        this.duracionPosicion = duracionPosicion;
    }

    public String getDuracion() {
        return duracion;
    }

    public void setDuracion(String duracion) {
        this.duracion = duracion;
    }

    public long getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDia() {
        return dia;
    }

    public void setDia(String dia) {
        this.dia = dia;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getActivo() {
        return activo;
    }

    public void setActivo(String activo) {
        this.activo = activo;
    }
}
