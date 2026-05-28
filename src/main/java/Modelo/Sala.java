package Modelo;

public class Sala {

    private int codigoSala;
    private String nombreSala;
    private boolean estado;
    private int cantidadJugadores;
    private Usuario usuario;

    public Sala(int codigoSala, String nombreSala, boolean estado, int cantidadJugadores) {
        this.codigoSala = codigoSala;
        this.nombreSala = nombreSala;
        this.estado = estado;
        this.cantidadJugadores = cantidadJugadores;
        this.usuario= usuario;//AGREGACION DE USUARIO A LA SALA
    }

    public int getCodigoSala() {
        return codigoSala;
    }

    public void setCodigoSala(int codigoSala) {
        this.codigoSala = codigoSala;
    }

    public String getNombreSala() {
        return nombreSala;
    }

    public void setNombreSala(String nombreSala) {
        this.nombreSala = nombreSala;
    }

    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }

    public int getCantidadJugadores() {
        return cantidadJugadores;
    }

    public void setCantidadJugadores(int cantidadJugadores) {
        this.cantidadJugadores = cantidadJugadores;
    }

}
