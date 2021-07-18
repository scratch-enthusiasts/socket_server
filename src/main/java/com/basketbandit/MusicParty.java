package com.basketbandit;

import ch.vorburger.exec.ManagedProcessException;
import com.basketbandit.connection.DatabaseServer;
import com.basketbandit.connection.SocketServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Scanner;

public class MusicParty {
    private static final Logger log = LoggerFactory.getLogger(MusicParty.class);
    private DatabaseServer databaseServer;
    private SocketServer socketServer;

    public static void main(String[] args) {
        new MusicParty();
    }

    public MusicParty() {
        try {
            this.databaseServer = new DatabaseServer(3306);
            this.databaseServer.start();
        } catch(ManagedProcessException e) {
            log.error("There was an error starting the database, message: {}", e.getMessage(), e);
        }

        try(InputStream inputStream = new FileInputStream("./config.yaml")) {
            Map<String, Object> config = new Yaml().load(inputStream);

            this.socketServer = new SocketServer((int)config.get("socket_server_port"));
            this.socketServer.setName("Socket Server Thread");
            this.socketServer.start();

            Scanner sc = new Scanner(System.in);
            while(sc.hasNextLine()) {
                if(sc.nextLine().equals("exit")) {
                    shutdown();
                }
            }

        } catch(IOException e) {
            log.error("There was an error loading the configuration file, message: {}", e.getMessage(), e);
        }
    }

    public void shutdown() {
        if(databaseServer != null) {
            databaseServer.shutdown();
        }
        if(socketServer != null) {
            socketServer.shutdown();
        }
        System.exit(0);
    }
}
