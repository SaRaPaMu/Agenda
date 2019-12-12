/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DAO;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import modelo.Contacto;

/**
 *
 * @author Castelao
 */
public class FicheroObjetosContactoDAO implements ContactoDAO {

    private ArrayList<Contacto> contactos;
    
    private File file;
    
    public FicheroObjetosContactoDAO(File file) {
        this.file = file;
        
        contactos = new ArrayList<>();
        readFile(file);        
        
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
            writeFile(file);
            return true;
        }
    }

    @Override
    public boolean removeContacto(Contacto c) {
        boolean remove = contactos.remove(c);
        writeFile(file);
        return remove;
    }

    @Override
    public boolean updateContacto(Contacto c) {
       int indice;
        if ((indice = contactos.indexOf(c)) == -1) {
            return false;
        } else {
            contactos.set(indice, c);
            writeFile(file);
            return true;
        }
    }
        
    private boolean readFile(File file) {

        try (ObjectInputStream reader = new ObjectInputStream(new FileInputStream(file))) {
           
            try {             
                contactos =(ArrayList<Contacto>) reader.readObject();
            } catch (ClassNotFoundException ex) {
                JOptionPane.showMessageDialog(null, "El fichero no contiene datos validos", "ERROR", JOptionPane.ERROR_MESSAGE);
                return false;
            }

        } catch (FileNotFoundException ex) {
            JOptionPane.showMessageDialog(null, "No se ha podido encontrar el fichero", "ERROR", JOptionPane.ERROR_MESSAGE);
            return false;

        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "No se ha podido leer el fichero", "ERROR", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }
    
    private boolean writeFile(File file) {

        try (ObjectOutputStream writer = new ObjectOutputStream(new FileOutputStream(file))) {
            
                writer.writeObject(contactos);            

        } catch (FileNotFoundException ex) {
            JOptionPane.showMessageDialog(null, "No se ha podido encontrar el fichero", "ERROR", JOptionPane.ERROR_MESSAGE);
            return false;

        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "No se ha podido escribir en el fichero", "ERROR", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }
    
}
