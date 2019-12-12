/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modelo;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import javax.swing.ImageIcon;

/**
 *
 * @author Castealo
 */
public class Contacto implements Serializable {

    private String NIF;
    private String nombre;
    private String telefono;
    private Date fechaNacimiento;
    private String tipo;
    private ImageIcon foto;
    public static SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

    public Contacto(String NIF, String nombre, String telefono, String fechaNacimiento, String tipo, ImageIcon foto) throws ParseException {
        sdf.setLenient(false);//valida fecha
        this.NIF = NIF;
        this.nombre = nombre;
        this.telefono = telefono;
        this.fechaNacimiento = sdf.parse(fechaNacimiento);
        this.tipo = tipo;
        if (foto == null) {
            this.foto = new ImageIcon("2Prueba.jpg");
        } else {
            this.foto = foto;
        }
    }

    public ImageIcon getFoto() {
        return foto;
    }

    public void setFoto(ImageIcon foto) {
        this.foto = foto;
    }

    public String getNIF() {
        return NIF;
    }

    private void setNIF(String NIF) {
        this.NIF = NIF;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getFechaNacimiento() {
        return sdf.format(fechaNacimiento);
    }

    public void setFechaNacimiento(String fechaNacimiento) throws ParseException {
        sdf.setLenient(false);
        this.fechaNacimiento = sdf.parse(fechaNacimiento);
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Contacto other = (Contacto) obj;
        if (!Objects.equals(this.NIF, other.NIF)) {
            return false;
        }
        return true;
    }

}
