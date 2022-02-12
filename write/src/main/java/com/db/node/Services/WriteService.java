package com.db.node.Services;

import com.db.node.IndexManager;
import com.google.gson.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.*;
import java.nio.file.*;
import java.util.Scanner;

public class WriteService {
    private static WriteService instance = new WriteService();
    private static Integer id;
    private LoadBalancer loadBalancer = LoadBalancer.getInstance();
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

        try(Scanner scanner = new Scanner(System.in)) {
            synchronized (this){
                System.out.println("Inter github token to access the repository: ");
                String GitHubToken = scanner.nextLine();
                Runtime.getRuntime().exec("../bin/initCluster.sh "+ GitHubToken);

                loadBalancer.update();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public JsonObject addDocument(String schema, String document) {
        JsonObject success = writeDocumentToDb(schema,document);
        indexManager.updateIndex(schema, success);
        exportDb();
        loadBalancer.update();
        return success;
    }

    private JsonObject writeDocumentToDb(String schema, String document) {
        int currentId = getNewId(schema);
        JsonObject jsonData = new JsonParser().parse(document).getAsJsonObject();
        JsonElement element = new JsonPrimitive(currentId);
        jsonData.add("id", element);
        File file = new File("./db/" + schema + "/" + currentId + ".json");

        try(FileWriter fileWriter = new FileWriter(file)) {
            fileWriter.write(jsonData.toString());
            fileWriter.flush();
            return jsonData;
        }catch(Exception e){
            System.out.println("Couldn't write the document");
            return new JsonObject();
        }
    }

    public String deleteDocument(String schema, String id) {
        JsonObject json = getDocument(schema,id);
        indexManager.deleteIndexes(schema,json);
        String response;
        if(!deleteDocumentFromDb(schema,id))
            response = "Failure";
        response = "Success";
        exportDb();
        loadBalancer.update();
        return response;
    }

    private boolean deleteDocumentFromDb(String schema, String id){
        File document = new File("./db/" + schema + "/" + id + ".json");
        if(!document.delete()){
            System.out.println("Couldn't delete this document. Try again!");
            return false;
        }
        return true;
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

            try(FileReader fileReader = new FileReader(new File("./db/config.json"))) {
                config = (JsonObject) parser.parse(fileReader);
            }catch (IOException e) {
                return false;
            }

            config.add(schema,new JsonPrimitive(0));
            updateConfig();
            return true;
        }
        return false;
    }

    private JsonObject getDocument(String schema, String id) {
        JsonParser parser = new JsonParser();
        File file = new File("./db/" + schema + "/" + id + ".json");
        try(FileReader fileReader = new FileReader(file)) {
            return (JsonObject) parser.parse(fileReader);
        } catch (IOException e) {
            System.out.println("Resource not found.");
            return new JsonObject();
        }
    }

    private int getNewId(String schema){
        id = config.get(schema + "_id").getAsInt();
        JsonElement newId = new JsonPrimitive(++id);
        config.remove(schema + "_id");
        config.add(schema + "_id",newId);
        updateConfig();
        return id;
    }

    private boolean updateConfig()  {
        File file = new File("./db/config.json");
        try(FileWriter fileWriter = new FileWriter(file)){

            fileWriter.write(config.toString());
            fileWriter.flush();
            return true;
        }catch (IOException e) {
            return false;
        }
    }

    public boolean exportDb(){
        return ZipService.zipDirectory("db.zip","./db");
    }

    public boolean importDb(MultipartFile file){

        if (file.isEmpty()) {
            return false;
        }

        try {
            byte[] bytes = file.getBytes();
            Path path = Paths.get("./" + file.getOriginalFilename());
            Files.write(path, bytes);

            ZipService.unzipDirectory("./db.zip","./");
            new File("./db.zip").delete();

            return true;
        } catch (Exception e) {
            System.out.println("Couldn't update normally");
            return false;
        }
    }


}
