package com.example.practicacoches_eduardo.DAO;

import com.example.practicacoches_eduardo.DataBaseManager.ConnectionBD;
import com.example.practicacoches_eduardo.Model.Coche;
import com.google.gson.Gson;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.ArrayList;

public class CocheDAO {
    static MongoClient con;
    static MongoCollection<Document> collection = null;
    static String jsonCoche;
    static Document documento;

    public static void crearBaseDatos() {
        try {
            con = ConnectionBD.conectar(); // Establezco la conexión con la base de datos
            MongoDatabase database = con.getDatabase("Taller"); // Obtengo la base de datos llamada "Taller"
            database.createCollection("coches"); // Creo una colección llamada "coches" en la base de datos
            collection = database.getCollection("coches"); // Asigno la colección "coches" a la variable collection

            // Muestro un mensaje de error si no es posible crear la base de datos
        } catch (Exception e) {
            System.out.println("ERROR AL CREAR LA BASE DE DATOS" + e.getMessage());
        }
    }

    public static boolean insertarCoche(Coche coche) {
        Gson gson = new Gson(); // Creo una instancia de Gson para convertir objetos a JSON
        boolean insertado = false; // Variable para verificar si el coche fue insertado
        if (coche != null) { // Compruebo si el objeto coche no es nulo
            jsonCoche = gson.toJson(coche); // Convierto el objeto coche a formato JSON
            documento = Document.parse(jsonCoche); // Convierto el JSON a un documento de MongoDB
            collection.insertOne(documento); // Inserto el documento en la colección
            insertado = true; // Cambio el valor a true si se inserto correctamente
        }
        return insertado; // Retorno el estado de la inserción
    }

    public static boolean borrarCoche(Coche coche) {
        Gson gson = new Gson(); // Creo una instancia de Gson para convertir objetos a JSON
        boolean eliminado = false; // Variable para verificar si el coche fue eliminado
        if (coche != null) { // Compruebo si el objeto coche no es nulo
            jsonCoche = gson.toJson(coche); // Convierto el objeto coche a formato JSON
            documento = Document.parse(jsonCoche); // Convierto el JSON a un documento de MongoDB
            collection.deleteOne(documento); // Elimino el documento de la colección
            eliminado = true; // Cambio el valor a true ya que se ha borrado correctamente
        }
        return eliminado; // Retorno el estado de la eliminación
    }

    public static boolean actualizarCoche(Coche coche) {
        boolean actualizado = false; // Variable para verificar si el coche fue actualizado
        Gson gson = new Gson(); // Creo una instancia de Gson para convertir objetos a JSON
        jsonCoche = gson.toJson(coche); // Convierto el objeto coche a formato JSON

        Document filtro = new Document("matricula", coche.getMatricula()); // Creo un filtro basado en la matrícula del coche

        Document actualizaciones = new Document() // Creo un documento para las actualizaciones
                // Actualizo los datos del coche, excepto la matrícula (que no puedo)
                .append("marca", coche.getMarca())
                .append("modelo", coche.getModelo())
                .append("tipo", coche.getTipo());
        try {
            collection.updateOne(filtro, new Document("$set", actualizaciones)); // Actualizo el documento en la colección según el filtro
            actualizado = true; // Cambio el valor a true ya que la actualización fue exitosa
        } catch (Exception e) {
            System.out.println("ERROR AL ACTUALIZAR" + e.getMessage()); // Muestro mensaje en el caso de que no se haya podido actualizar
        }
        return actualizado; // Retorno el estado de la actualización
    }

    public static ArrayList<Coche> obtenerCoche() {
        ArrayList<Coche> cocheDesdeBD = new ArrayList<>(); // Creo una lista para almacenar los coches obtenidos de la base de datos
        Gson gson = new Gson(); // Creo una instancia de Gson para convertir documentos a objetos

        for (Document documento : collection.find()) { // Itero sobre cada documento en la colección
            Coche coche = gson.fromJson(documento.toJson(), Coche.class); // Convierto el documento a un objeto Coche
            cocheDesdeBD.add(coche); // Agrego el coche a la lista
        }
        return cocheDesdeBD; // Retorno la lista de coches
    }

    public static boolean verificarExisteMatricula(String matricula) {
        boolean existe = false; // Variable para verificar si la matrícula existe
        Document verificoMatricula = new Document("matricula", matricula); // Creo un documento con la matrícula a verificar
        Document documento = collection.find(verificoMatricula).first(); // Busco en la colección el primer documento que coincida con la matrícula

        if (documento != null) { // Si se encuentra un documento
            existe = true; // Cambio el valor a true ya que la matrícula existe
        }
        return existe; // Retorno si existe la matrícula
    }
}