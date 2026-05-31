package servidor_cliente;

import MySQL.ConexionBaseDeDatos;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class ManejadorDeUsuarios extends Thread {

    private Socket socketCliente;

    public ManejadorDeUsuarios(Socket socketCliente) {
        this.socketCliente = socketCliente;
    }

    @Override
    public void run() {
        try {
            BufferedReader lector = new BufferedReader(new InputStreamReader(socketCliente.getInputStream()));
            PrintWriter escritor = new PrintWriter(socketCliente.getOutputStream(), true);

            String datosRecibidos;
            while ((datosRecibidos = lector.readLine()) != null) {
                System.out.println("TRAMA RECIBIDA:");
                System.out.println(datosRecibidos);

                String[] partes = datosRecibidos.split("\\|");
                if (partes[0].equals("Pregunta")) {
                    try {
                        guardarPreguntaNuevobd(partes);
                        escritor.println("OK: Pregunta guardada");
                    } catch (Exception e) {
                        System.out.println("Error al guardar: " + e.getMessage());
                        escritor.println("Error: No se pudo guardar el producto");
                    }
                } // 2. SI ES UNA VENTA (Ejemplo trama: VENTA|idProd,cant,precio,sub,provN,provT,provC,provTip|idProd2...)
                /* if (partes[0].equals("VENTA")) {
                    guardarVentaCompletaBD(partes);
                    escritor.println("OK: Venta procesada correctamente");
                } else {
                    escritor.println("Error: Comando desconocido o formato incorrecto");
                }*/
            }

        } catch (Exception e) {
            System.out.println("Error en el Socket: " + e.getMessage());
        }
    }

    private void guardarPreguntaNuevobd(String[] partes) throws Exception {

        Connection conexion = ConexionBaseDeDatos.conectar();

        String sql = "INSERT INTO preguntas (enunciado, respuesta1, respuesta2, respuesta3, respuesta4) VALUES (?, ?, ?, ?, ?)";

        PreparedStatement ps = conexion.prepareStatement(sql);

        ps.setString(1, partes[1]);
        ps.setString(2, partes[2]);
        ps.setString(3, partes[3]);
        ps.setString(4, partes[4]);
        ps.setString(5, partes[5]);

        ps.executeUpdate();

        System.out.println("Pregunta guardada correctamente");

        ps.close();
        conexion.close();
    }

    private void guardarVentaCompletaBD(String[] partes) throws Exception {

        Connection conexion = ConexionBaseDeDatos.conectar();
        //String sql = "{call obtener_usuarios_por_rol(?)}";//PARA PROCEDIMIENTOS ALMACENADOS

        String sqlVenta
                = "INSERT INTO ventas "
                + "(nombreP, cantidad, precio_unitario, subtotal, fecha_hora) "
                + "VALUES (?, ?, ?, ?, NOW())";

        String sqlActualizarStock
                = "UPDATE productos "
                + "SET cantidad_producto = cantidad_producto - ? "
                + "WHERE nombre_producto = ?";

        PreparedStatement psVenta
                = conexion.prepareStatement(sqlVenta);

        PreparedStatement psStock
                = conexion.prepareStatement(sqlActualizarStock);
        System.out.println("TRAMA RECIBIDA:");

        for (int i = 1; i < partes.length; i++) {

            String[] item = partes[i].split(",");

            String nombreProducto = item[0];

            int cantidad = Integer.parseInt(item[1]);

            double precio = Double.parseDouble(item[2]);

            double subtotal = Double.parseDouble(item[3]);

            // INSERTAR EN VENTAS
            psVenta.setString(1, nombreProducto);
            psVenta.setInt(2, cantidad);
            psVenta.setDouble(3, precio);
            psVenta.setDouble(4, subtotal);

            psVenta.executeUpdate();

            // ACTUALIZAR STOCK
            psStock.setInt(1, cantidad);//esto es lo que se coloca en el cantidad_producto - ? 
            psStock.setString(2, nombreProducto);

            psStock.executeUpdate();
        }

        psVenta.close();
        psStock.close();
        conexion.close();

        System.out.println("Venta procesada correctamente.");
    }
}
