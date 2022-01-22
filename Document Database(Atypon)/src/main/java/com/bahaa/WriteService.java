package com.bahaa;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

@Service
public class WriteService {
    private static int id;
    WriteService(){ id = 1; }

    public JsonObject addDocument(String document, String schema) throws IOException {
        int currentId = getNewId();
        JsonObject jsonData = new JsonParser().parse(document).getAsJsonObject();
        JsonElement element = new JsonPrimitive(currentId);
        jsonData.add("id",element);

        File file = new File("/home/bahaa/Desktop/db/" + schema + "/" + currentId + ".json");
        FileWriter fileWriter = new FileWriter(file);
        fileWriter.write(jsonData.toString());
        fileWriter.flush();
        fileWriter.close();

        return jsonData;
    }

    private int getNewId(){ return id++; }

}
