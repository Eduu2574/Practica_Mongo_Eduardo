package com.example.practicacoches_eduardo.DataBaseManager;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

import java.io.FileInputStream;
import java.util.Properties;

public class ConnectionBD {
    public static MongoClient conectar(){
        MongoClient con;
        Properties properties=new Properties();
        String host = "";
        String port = "";
        String name = "";
        String username = "";
        String password = "";

        try {
            properties.load(new FileInputStream("src/main/resources/Configuration/database.properties"));
            host = String.valueOf(properties.get("host"));
            port = String.valueOf(properties.get("port"));
            name = String.valueOf(properties.get("name"));
            username = String.valueOf(properties.get("username"));
            password = String.valueOf(properties.get("password"));
            System.out.println(host + "  " + port + "  " + name + "  " + username + "  " + password);
            con = new MongoClient(new MongoClientURI("mongodb://" + username + ":" + password + "@" + host + ":" + port + "/?authSource=admin"));
            return con;
        } catch (Exception e) {
            System.out.println("Error conexión"+e.getMessage());
            return null;
        }
    }

    public static void desconectar(MongoClient con) {
        con.close();
    }
}