package com.bahaa;

import com.google.gson.JsonObject;

import java.io.IOException;

public class MainService {
    private WriteService writeService;
    private ReadService readService;

    public MainService(WriteService writeService, ReadService readService){
        this.writeService = writeService;
        this.readService = readService;
    }
    public JsonObject addDocument(String document, String schema) {
        JsonObject success;
        try{
           success = writeService.addDocument(document,schema);
           return success;
        }catch(IOException e){
            // do the catch based on the exception status code.
            return new JsonObject();
        }
    }

    public JsonObject getDocument(int id, String schema) {
        JsonObject success;
        try {
            success = readService.getDocument(id,schema);
            return success;
        }catch(IOException e){
            // do the catch based on the exception status code.
            return new JsonObject();
        }
    }
}
