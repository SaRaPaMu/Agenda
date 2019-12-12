package principal;

import DAO.ContactoDAO;
import DAO.FicheroObjetosContactoDAO;
import DAO.RAMContactoDAO;
import java.io.File;
import modelo.Contacto;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Castelao
 */
public class Principal extends javax.swing.JFrame {

    private enum Estado {
        NAVEGANDO,
        EDITANDO,
        ANHADIENDO,
        BORRANDO
    }

    private int posicion = 0;
    private Estado estado;
    private DefaultComboBoxModel<String> tipo;
    ContactoDAO dao;
    private ArrayList<Contacto> contactos;

    /**
     * Creates new form Principal
     *
     * @throws java.text.ParseException
     */
    public Principal() throws ParseException {
        initComponents();

        tipo = new DefaultComboBoxModel<>();
        dao = new RAMContactoDAO();

        //dao = new FicheroObjetosContactoDAO(file);
        contactos = (ArrayList<Contacto>) dao.getAllContacto();

        tipo.addElement("Amigo");
        tipo.addElement("Enemigo");
        tipo.addElement("Familiar");
        tipo.addElement("Trabajo");

        tiposComboBox.setModel(tipo);
        tiposComboBox.setSelectedIndex(0);

        fechaNacimientoTextField.setToolTipText("dd-mm-aaaa");
        nifTextField.setToolTipText("12345678Z");
        nombreTextField.setToolTipText("Nombre y Apellidos");
        telefonoTextField.setToolTipText("+34 900 00 00 00");

        if (contactos.isEmpty()) {
            Controlador(estado = Estado.ANHADIENDO);
        } else {
            Controlador(estado = Estado.NAVEGANDO);
            mostrarContacto(contactos.get(posicion));
        }

    }

    private void mostrarContacto(Contacto contacto) {

        fechaNacimientoTextField.setText(contacto.getFechaNacimiento());
        nifTextField.setText(contacto.getNIF());
        nombreTextField.setText(contacto.getNombre());
        telefonoTextField.setText(contacto.getTelefono());
        foto.setIcon(contacto.getFoto());
        switch (contacto.getTipo()) {
            case "Amigo":
                tiposComboBox.setSelectedIndex(0);
                break;
            case "Enemigo":
                tiposComboBox.setSelectedIndex(1);
                break;
            case "Familiar":
                tiposComboBox.setSelectedIndex(2);
                break;
            case "Trabajo":
                tiposComboBox.setSelectedIndex(3);
                break;

        }

    }

    private boolean validarNIF(String nif) {
        boolean correcto = true;

        String[] numeroYletra = {nif.substring(0, 8), nif.substring(8, 9)};
        String[] letrasComprobar = {"T", "R", "W", "A", "G", "M", "Y", "F", "P", "D", "X", "B", "N", "J", "Z", "S", "Q", "V", "H", "L", "C", "K", "E"};
        int modulo;
        int numero = Integer.parseInt(numeroYletra[0]);

        modulo = numero % 23;
        String letra = numeroYletra[1].toUpperCase();

        if (!letra.equals(letrasComprobar[modulo])) {
            correcto = false;
        }

        return correcto;
    }

    private boolean validarContacto(Contacto contacto) {
        boolean correcto = true;

        if (contacto.getNombre().isEmpty() || contacto.getNIF().isEmpty() || contacto.getTelefono().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Campo vacio", "ERROR", JOptionPane.ERROR_MESSAGE);
            correcto = false;
        } else if (!contacto.getNIF().matches("^[0-9]{8}[a-zA-Z]")) {
            correcto = false;
            JOptionPane.showMessageDialog(this, "NIF no valido deben ser 8 numeros y una letra", "ERROR", JOptionPane.ERROR_MESSAGE);
        } else if (!validarNIF(contacto.getNIF())) {
            correcto = false;
            JOptionPane.showMessageDialog(this, "NIF no valido", "ERROR", JOptionPane.ERROR_MESSAGE);

        } else if (!contacto.getTelefono().matches("^(\\+34|0034|34)?[\\s]?[6|7|8|9][\\s]?([0-9][\\s]?){8}$")) {
            correcto = false;
            JOptionPane.showMessageDialog(this, "Telefono no valido", "ERROR", JOptionPane.ERROR_MESSAGE);
        }

        return correcto;
    }

    private void borrarContacto() {
        dao.removeContacto(dao.getContactoByNIF(nifTextField.getText()));
        contactos = (ArrayList<Contacto>) dao.getAllContacto();
        posicion = 0;
        if (dao.getAllContacto().isEmpty()) {
            Controlador(estado = Estado.ANHADIENDO);
        } else {
            Controlador(estado = Estado.NAVEGANDO);
            mostrarContacto(contactos.get(posicion));
        }
    }

    private void añadirContacto() {

        Contacto c = null;
        boolean correcto;

        try {
            c = new Contacto(nifTextField.getText(), nombreTextField.getText(), telefonoTextField.getText(), fechaNacimientoTextField.getText(), (String) tiposComboBox.getSelectedItem(), (ImageIcon) foto.getIcon());
            correcto = validarContacto(c);

        } catch (ParseException ex) {
            correcto = false;
            JOptionPane.showMessageDialog(this, "Fecha incorrecta", "ERROR", JOptionPane.ERROR_MESSAGE);

        }

        if (correcto) {
            dao.addContacto(c);
            contactos = (ArrayList<Contacto>) dao.getAllContacto();
            posicion = 0;
            Controlador(estado = Estado.NAVEGANDO);
            mostrarContacto(contactos.get(posicion));
        }

    }

    private void editarContacto() {
        boolean correcto;
        Contacto c = null;

        try {
            c = new Contacto(dao.getAllContacto().get(posicion).getNIF(), nombreTextField.getText(), telefonoTextField.getText(), fechaNacimientoTextField.getText(), (String) tiposComboBox.getSelectedItem(), (ImageIcon) foto.getIcon());

            correcto = validarContacto(c);

        } catch (ParseException ex) {
            JOptionPane.showMessageDialog(this, "Fecha incorrecta", "ERROR", JOptionPane.ERROR_MESSAGE);
            correcto = false;
        }

        if (correcto) {
            dao.updateContacto(c);
            contactos = (ArrayList<Contacto>) dao.getAllContacto();
            Controlador(estado = Estado.NAVEGANDO);
            mostrarContacto(contactos.get(posicion));
        }

    }

    private void Controlador(Estado estado) {

        switch (estado) {
            case ANHADIENDO:
                fechaNacimientoTextField.setEditable(true);
                nifTextField.setEditable(true);
                nifTextField.setEnabled(true);
                nombreTextField.setEditable(true);
                telefonoTextField.setEditable(true);
                tiposComboBox.setEnabled(true);

                fechaNacimientoTextField.setText("");
                nifTextField.setText("");
                nombreTextField.setText("");
                telefonoTextField.setText("");
                foto.setIcon(null);
                foto.setText("FOTO CON RESOLUCION DE 215X238");

                if (contactos.isEmpty()) {
                    cargarButton.setEnabled(true);
                    cancelarButton.setEnabled(false);
                } else {
                    cargarButton.setEnabled(false);
                    cancelarButton.setEnabled(true);
                }
                guardarButton.setEnabled(false);
                anadirButton.setEnabled(false);
                borrarButton.setEnabled(false);
                editarButton.setEnabled(false);
                aceptarButton.setEnabled(true);
                primeroButton.setEnabled(false);
                previousButton.setEnabled(false);
                nextButton.setEnabled(false);
                ultimoButton.setEnabled(false);

                break;
            case BORRANDO:
                fechaNacimientoTextField.setEditable(false);
                nifTextField.setEditable(false);
                nifTextField.setEditable(false);
                nombreTextField.setEditable(false);
                telefonoTextField.setEditable(false);
                tiposComboBox.setEnabled(false);
                foto.setText("");

                cargarButton.setEnabled(false);
                guardarButton.setEnabled(false);
                anadirButton.setEnabled(false);
                borrarButton.setEnabled(false);
                editarButton.setEnabled(false);
                aceptarButton.setEnabled(true);
                cancelarButton.setEnabled(true);
                primeroButton.setEnabled(false);
                previousButton.setEnabled(false);
                nextButton.setEnabled(false);
                ultimoButton.setEnabled(false);
                break;
            case EDITANDO:
                fechaNacimientoTextField.setEditable(true);
                nifTextField.setEditable(false);                
                nombreTextField.setEditable(true);
                telefonoTextField.setEditable(true);
                tiposComboBox.setEnabled(true);

                cargarButton.setEnabled(false);
                guardarButton.setEnabled(false);
                anadirButton.setEnabled(false);
                borrarButton.setEnabled(false);
                editarButton.setEnabled(false);
                aceptarButton.setEnabled(true);
                cancelarButton.setEnabled(true);
                primeroButton.setEnabled(false);
                previousButton.setEnabled(false);
                nextButton.setEnabled(false);
                ultimoButton.setEnabled(false);
                break;
            case NAVEGANDO:
                fechaNacimientoTextField.setEditable(false);
                nifTextField.setEditable(false);                
                nombreTextField.setEditable(false);
                telefonoTextField.setEditable(false);
                tiposComboBox.setEnabled(false);
                foto.setText("");

                cargarButton.setEnabled(true);
                guardarButton.setEnabled(true);
                anadirButton.setEnabled(true);
                borrarButton.setEnabled(true);
                editarButton.setEnabled(true);
                aceptarButton.setEnabled(false);
                cancelarButton.setEnabled(false);
                primeroButton.setEnabled(true);
                previousButton.setEnabled(true);
                nextButton.setEnabled(true);
                ultimoButton.setEnabled(true);

                if (posicion == (dao.getAllContacto().size() - 1) || dao.getAllContacto().isEmpty()) {
                    nextButton.setEnabled(false);
                    ultimoButton.setEnabled(false);
                }
                if (posicion == 0 || dao.getAllContacto().isEmpty()) {
                    previousButton.setEnabled(false);
                    primeroButton.setEnabled(false);
                }

                break;

        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        fileOpenChooser = new javax.swing.JFileChooser();
        fileSaveChooser = new javax.swing.JFileChooser();
        anadirButton = new javax.swing.JButton();
        borrarButton = new javax.swing.JButton();
        editarButton = new javax.swing.JButton();
        aceptarButton = new javax.swing.JButton();
        cancelarButton = new javax.swing.JButton();
        previousButton = new javax.swing.JButton();
        primeroButton = new javax.swing.JButton();
        nextButton = new javax.swing.JButton();
        ultimoButton = new javax.swing.JButton();
        cargarButton = new javax.swing.JButton();
        guardarButton = new javax.swing.JButton();
        foto = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        nifTextField = new javax.swing.JTextField();
        nombreTextField = new javax.swing.JTextField();
        fechaNacimientoTextField = new javax.swing.JTextField();
        telefonoTextField = new javax.swing.JTextField();
        nifLabel1 = new javax.swing.JLabel();
        nombreLabel1 = new javax.swing.JLabel();
        fechaNacimientoLabel1 = new javax.swing.JLabel();
        telefonoLabel1 = new javax.swing.JLabel();
        tipoLabel1 = new javax.swing.JLabel();
        tiposComboBox = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();

        fileOpenChooser.setAccessory(guardarButton);
        fileOpenChooser.setDialogType(javax.swing.JFileChooser.SAVE_DIALOG);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        anadirButton.setText("Añadir");
        anadirButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                anadirButtonActionPerformed(evt);
            }
        });

        borrarButton.setText("Borrar");
        borrarButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                borrarButtonActionPerformed(evt);
            }
        });

        editarButton.setText("Editar");
        editarButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editarButtonActionPerformed(evt);
            }
        });

        aceptarButton.setText("Aceptar");
        aceptarButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aceptarButtonActionPerformed(evt);
            }
        });

        cancelarButton.setText("Cancelar");
        cancelarButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelarButtonActionPerformed(evt);
            }
        });

        previousButton.setText("<--");
        previousButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                previousButtonActionPerformed(evt);
            }
        });

        primeroButton.setText("|<-");
        primeroButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                primeroButtonActionPerformed(evt);
            }
        });

        nextButton.setText("-->");
        nextButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nextButtonActionPerformed(evt);
            }
        });

        ultimoButton.setText("->|");
        ultimoButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ultimoButtonActionPerformed(evt);
            }
        });

        cargarButton.setText("Cargar Fichero Contactos");
        cargarButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cargarButtonActionPerformed(evt);
            }
        });

        guardarButton.setText("Guardar Fichero Contactos");
        guardarButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                guardarButtonActionPerformed(evt);
            }
        });

        foto.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        foto.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fotoMouseClicked(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        nifLabel1.setText("NIF");

        nombreLabel1.setText("Nombre");

        fechaNacimientoLabel1.setText("Fecha Nacimiento");

        telefonoLabel1.setText("Telefono");

        tipoLabel1.setText("Tipo");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 305, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel1Layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(nombreLabel1)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                    .addComponent(fechaNacimientoLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(29, 29, 29))
                                .addComponent(nifLabel1)
                                .addComponent(tipoLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(telefonoLabel1))
                            .addGap(46, 46, 46)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(fechaNacimientoTextField)
                                .addComponent(nombreTextField)
                                .addComponent(nifTextField)
                                .addComponent(telefonoTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 118, Short.MAX_VALUE)
                                .addComponent(tiposComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addContainerGap()))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 255, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel1Layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(nifTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(nifLabel1))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                            .addGap(89, 89, 89)
                            .addComponent(fechaNacimientoLabel1))
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addGap(89, 89, 89)
                            .addComponent(fechaNacimientoTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addGap(29, 29, 29)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(nombreTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(nombreLabel1))))
                    .addGap(34, 34, 34)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(telefonoTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(telefonoLabel1))
                    .addGap(30, 30, 30)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(tipoLabel1)
                        .addComponent(tiposComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );

        jLabel2.setText("DATOS");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(35, 35, 35)
                        .addComponent(primeroButton, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(previousButton)
                        .addGap(68, 68, 68)
                        .addComponent(nextButton)
                        .addGap(41, 41, 41)
                        .addComponent(ultimoButton))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(85, 85, 85)
                        .addComponent(anadirButton, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(38, 38, 38)
                        .addComponent(borrarButton, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(118, 118, 118)
                        .addComponent(aceptarButton)
                        .addGap(62, 62, 62)
                        .addComponent(cancelarButton))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(61, 61, 61)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(editarButton, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(61, 61, 61)
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 68, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(foto, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(77, 77, 77))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(cargarButton)
                            .addComponent(guardarButton))
                        .addGap(95, 95, 95))))
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {nextButton, previousButton, primeroButton, ultimoButton});

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {cargarButton, guardarButton});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(53, 53, 53)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(foto, javax.swing.GroupLayout.PREFERRED_SIZE, 238, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(9, 9, 9)))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(60, 60, 60)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(anadirButton)
                            .addComponent(borrarButton)
                            .addComponent(editarButton))
                        .addGap(35, 35, 35)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(aceptarButton)
                            .addComponent(cancelarButton)
                            .addComponent(cargarButton))
                        .addGap(47, 47, 47)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(previousButton)
                            .addComponent(primeroButton)
                            .addComponent(nextButton)
                            .addComponent(ultimoButton))
                        .addContainerGap(77, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(47, 175, Short.MAX_VALUE)
                        .addComponent(guardarButton)
                        .addGap(90, 90, 90))))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void anadirButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_anadirButtonActionPerformed
        Controlador(estado = Estado.ANHADIENDO);
    }//GEN-LAST:event_anadirButtonActionPerformed

    private void borrarButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_borrarButtonActionPerformed
        Controlador(estado = Estado.BORRANDO);
    }//GEN-LAST:event_borrarButtonActionPerformed

    private void editarButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editarButtonActionPerformed
        Controlador(estado = Estado.EDITANDO);
    }//GEN-LAST:event_editarButtonActionPerformed

    private void cancelarButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelarButtonActionPerformed

        switch (estado) {
            case BORRANDO:
                Controlador(estado = Estado.NAVEGANDO);
                mostrarContacto(contactos.get(posicion));
                break;
            case ANHADIENDO:
                if (contactos.isEmpty()) {
                    Controlador(estado = Estado.ANHADIENDO);
                } else {
                    Controlador(estado = Estado.NAVEGANDO);
                    mostrarContacto(contactos.get(posicion));
                }
                break;
            case EDITANDO:
                Controlador(estado = Estado.NAVEGANDO);
                mostrarContacto(contactos.get(posicion));
                break;
        }
    }//GEN-LAST:event_cancelarButtonActionPerformed

    private void aceptarButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aceptarButtonActionPerformed

        switch (estado) {
            case BORRANDO:
                borrarContacto();
                break;
            case ANHADIENDO:
                añadirContacto();
                break;
            case EDITANDO:
                editarContacto();
                break;
        }

    }//GEN-LAST:event_aceptarButtonActionPerformed

    private void previousButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_previousButtonActionPerformed
        if (posicion >= 1) {
            posicion--;

        }
        mostrarContacto(contactos.get(posicion));
        Controlador(estado = Estado.NAVEGANDO);
    }//GEN-LAST:event_previousButtonActionPerformed

    private void nextButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nextButtonActionPerformed
        if (posicion <= (contactos.size() - 2)) {
            posicion++;
        }
        mostrarContacto(contactos.get(posicion));
        Controlador(estado = Estado.NAVEGANDO);
    }//GEN-LAST:event_nextButtonActionPerformed

    private void primeroButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_primeroButtonActionPerformed
        posicion = 0;
        mostrarContacto(contactos.get(posicion));
        Controlador(estado = Estado.NAVEGANDO);
    }//GEN-LAST:event_primeroButtonActionPerformed

    private void ultimoButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ultimoButtonActionPerformed
        posicion = (contactos.size() - 1);
        mostrarContacto(contactos.get(posicion));
        Controlador(estado = Estado.NAVEGANDO);
    }//GEN-LAST:event_ultimoButtonActionPerformed

    private void cargarButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cargarButtonActionPerformed

        int opcion = fileOpenChooser.showOpenDialog(this);
        File selected = fileOpenChooser.getSelectedFile();

        if (opcion == JFileChooser.APPROVE_OPTION) {
            dao = new FicheroObjetosContactoDAO(selected);
            contactos = (ArrayList<Contacto>) dao.getAllContacto();
        }
        if (contactos.isEmpty()) {
            Controlador(estado = Estado.ANHADIENDO);
        } else {
            Controlador(estado = Estado.NAVEGANDO);
            mostrarContacto(contactos.get(posicion));
        }
    }//GEN-LAST:event_cargarButtonActionPerformed

    private void guardarButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_guardarButtonActionPerformed
        int opcion = fileSaveChooser.showSaveDialog(this);
        File selected = fileSaveChooser.getSelectedFile();

        if (opcion == JFileChooser.APPROVE_OPTION) {
            dao = new FicheroObjetosContactoDAO(selected);
            for (Contacto c : contactos) {
                dao.addContacto(c);
            }

        }
        if (contactos.isEmpty()) {
            Controlador(estado = Estado.ANHADIENDO);
        } else {
            Controlador(estado = Estado.NAVEGANDO);
            mostrarContacto(contactos.get(posicion));
        }
    }//GEN-LAST:event_guardarButtonActionPerformed

    private void fotoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fotoMouseClicked

        if (estado == Estado.ANHADIENDO) {
            
            int opcion = fileOpenChooser.showOpenDialog(this);
            File selected = fileOpenChooser.getSelectedFile();

            if (opcion == JFileChooser.APPROVE_OPTION) {
                ImageIcon ii = new ImageIcon(selected.getPath());
                foto.setIcon(ii);
            }
            
        } else if (estado == Estado.EDITANDO) {
            
            int opcion = fileOpenChooser.showOpenDialog(this);
            File selected = fileOpenChooser.getSelectedFile();

            if (opcion == JFileChooser.APPROVE_OPTION) {
                ImageIcon ii = new ImageIcon(selected.getPath());
                foto.setIcon(ii);
            }
        }

    }//GEN-LAST:event_fotoMouseClicked

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Principal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Principal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Principal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Principal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    new Principal().setVisible(true);
                } catch (ParseException ex) {
                    Logger.getLogger(Principal.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton aceptarButton;
    private javax.swing.JButton anadirButton;
    private javax.swing.JButton borrarButton;
    private javax.swing.JButton cancelarButton;
    private javax.swing.JButton cargarButton;
    private javax.swing.JButton editarButton;
    private javax.swing.JLabel fechaNacimientoLabel1;
    private javax.swing.JTextField fechaNacimientoTextField;
    private javax.swing.JFileChooser fileOpenChooser;
    private javax.swing.JFileChooser fileSaveChooser;
    private javax.swing.JLabel foto;
    private javax.swing.JButton guardarButton;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JButton nextButton;
    private javax.swing.JLabel nifLabel1;
    private javax.swing.JTextField nifTextField;
    private javax.swing.JLabel nombreLabel1;
    private javax.swing.JTextField nombreTextField;
    private javax.swing.JButton previousButton;
    private javax.swing.JButton primeroButton;
    private javax.swing.JLabel telefonoLabel1;
    private javax.swing.JTextField telefonoTextField;
    private javax.swing.JLabel tipoLabel1;
    private javax.swing.JComboBox<String> tiposComboBox;
    private javax.swing.JButton ultimoButton;
    // End of variables declaration//GEN-END:variables
}
