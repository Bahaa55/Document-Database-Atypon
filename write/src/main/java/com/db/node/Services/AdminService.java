package com.db.node.Services;

import com.db.node.ReadNode;
import com.google.gson.*;
import java.io.*;
import java.util.Scanner;

public class AdminService {
    private JsonObject config;
    private LoadBalancer loadBalancer = LoadBalancer.getInstance();
    private static AdminService instance = new AdminService();

    public static AdminService getInstance(){return instance;}

    private AdminService(){
        JsonParser parser = new JsonParser();
        File file = new File("./db/config.json");
        try(FileReader fileReader = new FileReader(file)){
            config = (JsonObject) parser.parse(fileReader);
        }catch(Exception e){
            throw new RuntimeException("Error loading configuration file.\n" +
                    "Make sure the file config.json is available.");
        }
    }
    public void addNodeToCluster(String port){
        ReadNode node = new ReadNode("http://localhost:"+port);
        loadBalancer.addObserver(node);
    }

    public void scaleHorizontally(){
        Integer nodesCount = config.get("nodes_config").getAsInt();
        int updated_node_count = nodesCount + 1;
        config.remove("nodes_config");
        config.add("nodes_config",new JsonPrimitive(updated_node_count));
        updateConfig();
        nodesCount += 2001;

        try(Scanner scanner = new Scanner(System.in)) {
            System.out.println("Inter github token to access the repository: ");
            String token = scanner.nextLine();
            Runtime.getRuntime().exec("./bin/scale.sh "+ nodesCount.toString() + " " + token);
            updateConfig();
        } catch (IOException e) {
            System.out.println("Couldn't scale the cluster, try again.");
        }

    }

    private void updateConfig() {
        File file = new File("./db/config.json");
        try(FileWriter fileWriter = new FileWriter(file);){
            fileWriter.write(config.toString());
            fileWriter.flush();
        } catch (IOException e) {
            return;
        }

    }
}
