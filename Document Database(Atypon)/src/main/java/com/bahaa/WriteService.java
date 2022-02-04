package com.bahaa;

import com.google.gson.*;
import org.springframework.stereotype.Service;

import java.io.*;

@Service
public class WriteService {
    private static Integer id;
    private JsonObject config;

    WriteService() {
        JsonParser parser = new JsonParser();
        File file = new File("./db/config.json");
        try{
            config = (JsonObject) parser.parse(new FileReader(file));
        }catch(Exception e){
            throw new RuntimeException("Error loading configuration file.\n" +
                    "Make sure the file config.json is available.");
        }
    }

    public JsonObject addDocument(String document, String schema) throws IOException {
        int currentId = getNewId(schema);
        JsonObject jsonData = new JsonParser().parse(document).getAsJsonObject();
        JsonElement element = new JsonPrimitive(currentId);
        jsonData.add("id",element);

        File file = new File("./db/" + schema + "/" + currentId + ".json");
        FileWriter fileWriter = new FileWriter(file);
        fileWriter.write(jsonData.toString());
        fileWriter.flush();
        fileWriter.close();

        return jsonData;
    }

    public void deleteDocument(String id, String schema){
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
            System.out.println("Didn't update the configuration correctly.\n" +
                    "Maybe it'll work next time without the need to terminate :)");
        }
        return id;
    }

    private void updateConfig() throws IOException {
        File file = new File("./db/config.json");
        FileWriter fileWriter = new FileWriter(file);
        fileWriter.write(config.toString());
        fileWriter.flush();
        fileWriter.close();
    }
}
