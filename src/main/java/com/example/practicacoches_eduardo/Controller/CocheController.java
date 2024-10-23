package com.example.practicacoches_eduardo.Controller;

import com.example.practicacoches_eduardo.DAO.CocheDAO;
import com.example.practicacoches_eduardo.Model.Coche;
import com.example.practicacoches_eduardo.Util.Alerta;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class CocheController implements Initializable {

    @FXML
    private TableView<Coche> CochesTv;

    @FXML
    private TableColumn<Coche, String> marcaCol;

    @FXML
    private TextField marcaTf;

    @FXML
    private TableColumn<Coche, String> matriculaCol;

    @FXML
    private TextField matriculaTf;

    @FXML
    private TableColumn<Coche, String> modeloCol;

    @FXML
    private TextField modeloTf;

    @FXML
    private ComboBox<String> tipoCB;

    @FXML
    private TableColumn<Coche, String> tipoCol;
    ObservableList<Coche> listadoCoches;


    // Método inicial del programa
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ObservableList<String> tiposCoche = FXCollections.observableArrayList("Hibrido", "Gasolina", "Diésel", "Eléctrico", "GLP"); // Creo una lista observable con los tipos de coche
        tipoCB.setItems(tiposCoche); // Establezco la lista de tipos de coche en el ComboBox

        // Cargo los datos del TableView asignando cada propiedad a la columna
        matriculaCol.setCellValueFactory(new PropertyValueFactory<>("matricula"));
        marcaCol.setCellValueFactory(new PropertyValueFactory<>("marca"));
        modeloCol.setCellValueFactory(new PropertyValueFactory<>("modelo"));
        tipoCol.setCellValueFactory(new PropertyValueFactory<>("tipo"));

        // Creo el acceso a la base de datos
        CocheDAO.crearBaseDatos(); // Llamo al metodo para conectar y crear la base de datos

        // Muestro los coches y los inserto en mi TableView
        ArrayList<Coche> listarCoches = CocheDAO.obtenerCoche(); // Obtengo la lista de coches de la base de datos
        listadoCoches = FXCollections.observableArrayList(listarCoches); // Creo una lista observable con los coches
        CochesTv.setItems(listadoCoches); // Establezco la lista de coches en el TableView
    }


    // Método para actualizar los datos de un coche
    @FXML
    void onActualizarClic(ActionEvent event) {
        // Almaceno en una variable el coche que ha seleccionado el usuario
        Coche cocheSeleccionado = CochesTv.getSelectionModel().getSelectedItem();

        // En el caso de que no haya seleccionado nada muestro una alerta
        if (cocheSeleccionado == null) {
            Alerta.mostrarError("Debes seleccionar un coche"); // Muestra un mensaje de error si no se selecciona un coche
            return;
        }

        // Almaceno los datos de mis textfield en unas variables
        String matricula = matriculaTf.getText();
        String marca = marcaTf.getText();
        String modelo = modeloTf.getText();
        String tipo = tipoCB.getSelectionModel().getSelectedItem();

        // Verifico que no se haya modificado la matricula que se ha introducido
        if (!cocheSeleccionado.getMatricula().equals(matricula)) {
            Alerta.mostrarError("No se puede cambiar la matrícula");
            return;
        }

        // Modifico los datos del coche
        cocheSeleccionado.setMarca(marca);
        cocheSeleccionado.setModelo(modelo);
        cocheSeleccionado.setTipo(tipo);

        // Actualizo en la base de datos y muestro mensaje
        if (CocheDAO.actualizarCoche(cocheSeleccionado)) {
            Alerta.mostrarInformacion("Coche actualizado correctamente"); // Muestra mensaje de éxito si la actualización se ha hecho correctamente
            actualizarTabla(); // Llamo al método para actualizar la tabla para reflejar los cambios
            onLimpiarClic(event);
        } else {
            Alerta.mostrarError("No se ha podido actualizar el coche"); // Muestro mensaje de error si la actualización falla
        }
    }

    // Método para borrar de nuestra base de datos un coche
    @FXML
    void onBorrarClic(ActionEvent event) {

        // Obtengo y almaceno en unas variables el coche seleccionado por el usuario
        Coche coche = CochesTv.getSelectionModel().getSelectedItem();

        // Verifico si no se ha seleccionado ningún coche
        if (coche == null) {
            Alerta.mostrarError("Debes seleccionar un coche"); // Muestra un mensaje de error si no se selecciona un coche
            return; // Salgo del método si no hay coche seleccionado
        }

        // Elimino el coche de la base de datos
        if (CocheDAO.borrarCoche(coche)) {
            actualizarTabla(); // Actualizo la tabla para reflejar los cambios
            Alerta.mostrarInformacion("Coche eliminado correctamente"); // Muestra un mensaje de éxito si la eliminación se ha hecho correctamente
        } else {
            Alerta.mostrarError("No se ha podido eliminar el coche"); // Muestra un mensaje de error si la eliminación no se ha podido realizar
        }
        // Llamo al método para limpiar los datos del textfield
        onLimpiarClic(event);
    }


    @FXML
    void onInsertarClic(ActionEvent event) {
        // Inserto los datos de los textfield y combobox en variables
        String matricula = matriculaTf.getText();
        String marca = marcaTf.getText();
        String modelo = modeloTf.getText();
        String tipo = tipoCB.getSelectionModel().getSelectedItem();

        // Compruebo que todos los datos están rellenados
        if (matricula.isEmpty() || marca.isEmpty() || modelo.isEmpty() || tipo.isEmpty()) {
            Alerta.mostrarError("Tienes que rellenar todos los datos"); // Muestra un mensaje de error si falta algún dato
            // Compruebo que la matrícula no se encuentre en la base de datos
        } else if (CocheDAO.verificarExisteMatricula(matricula)) {
            Alerta.mostrarError("La matricula coincide con un vehiculo de nuestra base de datos!"); // Muestra un mensaje de error si la matrícula ya existe
        } else {
            // Creo el nuevo objeto con los datos introducidos por el usuario
            Coche coche1 = new Coche(matricula, marca, modelo, tipo);
            // Inserto en mi base de datos el nuevo coche
            if (CocheDAO.insertarCoche(coche1)) {
                actualizarTabla(); // Llamo al método para que actualice los datos en la tabla
                onLimpiarClic(event); // Llamo al método para que limpie los datos
                Alerta.mostrarInformacion("Coche insertado correctamente"); // Muestra un mensaje de éxito si la inserción fue correcta
            } else {
                Alerta.mostrarError("No se ha podido insertar el coche."); // Muestra un mensaje de error si la inserción no se ha podido realizar
            }
        }
    }

    @FXML
    void onSeleccionCocheClic(MouseEvent event) {

        // Almaceno el coche que ha seleccionado el usuario
        Coche cocheSeleccion = CochesTv.getSelectionModel().getSelectedItem();

        if (cocheSeleccion != null) {
            // Si selecciona un coche añado los datos en los textfields correspondiente
            matriculaTf.setText(cocheSeleccion.getMatricula());
            marcaTf.setText(cocheSeleccion.getMarca());
            modeloTf.setText(cocheSeleccion.getModelo());
            tipoCB.getSelectionModel().select(cocheSeleccion.getTipo());
        }
    }

    void actualizarTabla() {
        // Método para actualizar los datos de la tabla trás hacer cambios
        ArrayList<Coche> listadoCoches = CocheDAO.obtenerCoche(); // Obtengo la lista de coches desde la base de datos
        CochesTv.getItems().setAll(listadoCoches); // Actualizo el TableView con la nueva lista de coches
    }

    // Método para limpiar los datos de los textfield
    @FXML
    void onLimpiarClic(ActionEvent event) {
        matriculaTf.clear();
        marcaTf.clear();
        modeloTf.clear();
        tipoCB.getSelectionModel().clearSelection();
    }
}