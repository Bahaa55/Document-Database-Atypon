package com.db.node;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import java.io.*;
import java.util.List;
import java.util.zip.ZipInputStream;

public class WriteService {
    private static WriteService instance = new WriteService();
    private static Integer id;
    private LoadBalancer loadBalancer = new LoadBalancer();
    private JsonObject config;
    private IndexManager indexManager;

    public static WriteService getInstance(){
        return instance;
    }

    private WriteService() {
        indexManager = IndexManager.getInstance();
        JsonParser parser = new JsonParser();
        File file = new File("./db/config.json");
        try{
            config = (JsonObject) parser.parse(new FileReader(file));
        }catch(Exception e){
            throw new RuntimeException("Error loading configuration file.\n" +
                    "Make sure the file config.json is available.");
        }

        // TODO : Make sure there's at least 3 nodes in the cluster
        ReadNode node = new ReadNode("http://localhost:2/update");
        loadBalancer.addObserver(node);
    }

    public JsonObject addDocument(String document, String schema) {
        JsonObject success;
        try{
            success = write(document,schema);
            indexManager.updateIndex(schema, success);
            exportDb();
            loadBalancer.update();
            return success;
        }catch(IOException e){
            System.out.println(e);
            System.out.println("Something wrong happened");
            return new JsonObject();
        }
    }

    private JsonObject write(String document, String schema) throws IOException {

        try {
            int currentId = getNewId(schema);
            JsonObject jsonData = new JsonParser().parse(document).getAsJsonObject();
            JsonElement element = new JsonPrimitive(currentId);
            jsonData.add("id", element);

            File file = new File("./db/" + schema + "/" + currentId + ".json");
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(jsonData.toString());
            fileWriter.flush();
            fileWriter.close();

            return jsonData;
        }catch(Exception e){
            System.out.println("Couldn't write the document");
            return new JsonObject();
        }
    }

    private void delete(String id, String schema){
        File document = new File("./db/" + schema + "/" + id + ".json");
        if(!document.delete())
            System.out.println("Couldn't delete this document. Try again!");
    }

    private int getNewId(String schema){
        id = config.get(schema + "_id").getAsInt();
        JsonElement newId = new JsonPrimitive(++id);
        config.remove(schema + "_id");
        config.add(schema + "_id",newId);
        try{
            updateConfig();
        }catch(Exception e){
            System.out.println("Didn't update the configuration correctly.\n");
        }
        return id;
    }

    public String deleteDocument(String schema, String id) {
        try{
            JsonObject json = getDocument(id,schema);
            delete(id,schema);
            indexManager.deleteIndexes(schema,json);
            exportDb();
            loadBalancer.update();
            return "Success";
        }catch(Exception e){
            System.out.println("Document not found!");
            return "Failure";
        }
    }

    public boolean makeIndex(String schema, String attribute){
        boolean response = indexManager.makeIndex(schema,attribute);
        exportDb();
        loadBalancer.update();
        return response;
    }

    public boolean addSchema(String schema){
        File file = new File("./db/"+schema);
        if(!file.exists()){
            file.mkdirs();

            JsonParser parser = new JsonParser();
            try{
                config = (JsonObject) parser.parse(new FileReader(new File("./db/config.json")));
                config.add(schema,new JsonPrimitive(0));
                updateConfig();
            }catch(Exception e){
                return false;
            }
            return true;
        }
        return false;
    }

    private JsonObject getDocument(String id, String schema) throws IOException {
        JsonParser parser = new JsonParser();
        File file = new File("./db/" + schema + "/" + id + ".json");
        return (JsonObject) parser.parse(new FileReader(file));
    }

    private void updateConfig() throws IOException {
        File file = new File("./db/config.json");
        FileWriter fileWriter = new FileWriter(file);
        fileWriter.write(config.toString());
        fileWriter.flush();
        fileWriter.close();
    }

    public void exportDb(){
        try{
            ZipDirectory.zipDirectory("db.zip","./db");
        }catch(Exception e){
            System.out.println("Can't find the db data.");
        }
    }

    public void importDb(){
        try {

            ZipDirectory.unzipDirectory("./db.zip","./");
            new File("./db.zip").delete();

        } catch (Exception e) {
            System.out.println("Couldn't update normally");
        }
    }
}
