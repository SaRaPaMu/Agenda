/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DAO;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import modelo.Contacto;

/**
 *
 * @author Castelao
 */
public class RAMContactoDAO implements ContactoDAO{

    private ArrayList<Contacto> contactos;
    
    public RAMContactoDAO() throws ParseException {
        contactos = new ArrayList<>();
        
//        Contacto a = new Contacto("19382746W", "Pepe Perez", "981223344", "12-12-1999", "Amigo");
//        Contacto b = new Contacto("12048151S", "Juan Perez", "981223344", "12-12-1999", "Enemigo");
//        Contacto c = new Contacto("92467124V", "Jose Perez", "981223344", "12-12-1999", "Familiar");
//
//        contactos.add(a);
//        contactos.add(b);
//        contactos.add(c);
    }

    @Override
    public List<Contacto> getAllContacto() {
        return contactos;
    }

    @Override
    public Contacto getContactoByNIF(String NIF) {
        for (Contacto p : contactos) {
            if (p.getNIF().equals(NIF)) {
                return p;
            }
        }
        return null;
    }

    @Override
    public boolean addContacto(Contacto c) {
        if (contactos.contains(c)) {
            return false;
        } else {
            contactos.add(c);
            return true;
        }
    }

    @Override
    public boolean removeContacto(Contacto c) {
         return contactos.remove(c);
    }

    @Override
    public boolean updateContacto(Contacto c) {
        int indice;
        if ((indice = contactos.indexOf(c)) == -1) {
            return false;
        } else {
            contactos.set(indice, c);
            return true;
        }
    }
    
}
