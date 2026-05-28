package Modelo;

public class Administrador implements Interfaces.ItipoUsuario {

    private String nombreDelAdministrador;

    public Administrador(String nombreDelAdministrador) {
        this.nombreDelAdministrador = nombreDelAdministrador;
    }
    
    @Override
    public String getTipo() {
        return "Administrador";
    }

}
