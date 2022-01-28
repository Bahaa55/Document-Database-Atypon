package com.bahaa;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class ReadService {
    public JsonObject getDocument(String id, String schema) throws IOException {
        JsonParser parser = new JsonParser();
        File file = new File("./db/" + schema + "/" + id + ".json");
        JsonObject document = (JsonObject) parser.parse(new FileReader(file));
        return document;
    }
}
