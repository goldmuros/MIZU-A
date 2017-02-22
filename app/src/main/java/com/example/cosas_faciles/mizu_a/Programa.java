package com.example.cosas_faciles.mizu_a;

/**
 * Created by Durgrim on 15/1/2017.
 */

public class Programa {
    private String dia;
    private String hora;
    private String duracion;
    private String activo;
    private int id;

    public Programa(int id, String dia, String hora, String duracion, String activo) {
        this.id = id;
        this.dia = dia;
        this.hora = hora;
        this.duracion = duracion;//Renombrarlo para que se llame duracion
        this.activo = activo;
    }

    public String getDuracion() {
        return duracion;
    }

    public void setDuracion(String duracion) {
        this.duracion = duracion;
    }

    public int getId() {
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
