package com.example.cosas_faciles.mizu_a;

/**
 * Created by Durgrim on 15/1/2017.
 */

public class Programa {
    private String dia, hora, tiempo, activo;

    public Programa(String dia, String hora, String tiempo, String activo) {
        this.dia = dia;
        this.hora = hora;
        this.tiempo = tiempo;//Renombrarlo para que se llame duracion
        this.activo = activo;
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

    public String getTiempo() {
        return tiempo;
    }

    public void setTiempo(String tiempo) {
        this.tiempo = tiempo;
    }

    public String getActivo() {
        return activo;
    }

    public void setActivo(String activo) {
        this.activo = activo;
    }
}
