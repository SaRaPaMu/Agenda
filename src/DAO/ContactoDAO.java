/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DAO;

import modelo.Contacto;
import java.util.List;

/**
 *
 * @author Castelao
 */
public interface ContactoDAO {
    //Operaciones CRUD de Contacto
    List<Contacto> getAllContacto();
    Contacto getContactoByNIF(String NIF);
    boolean addContacto(Contacto c);
    boolean removeContacto(Contacto c);
    boolean updateContacto(Contacto c);
}
