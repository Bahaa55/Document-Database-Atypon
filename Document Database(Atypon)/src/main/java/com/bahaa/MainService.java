package com.bahaa;

import com.google.gson.JsonObject;

import java.io.IOException;

public class MainService {
    private WriteService writeService;
    private ReadService readService;
    private IndexManager indexManager;

    public MainService(){
        writeService = new WriteService();
        readService = new ReadService();
        indexManager = new IndexManager();
    }
    public JsonObject addDocument(String document, String schema) {
        JsonObject success;
        try{
           success = writeService.addDocument(document,schema);
           return success;
        }catch(IOException e){
            // TODO: the catch based on the exception status code.
            return new JsonObject();
        }
    }

    public JsonObject getDocument(String id, String schema) throws IOException{
        return readService.getDocument(id,schema);
    }

    public Integer getIdFromIndex(String schema,String attribute,String value){
        if(schema.equals("client")){
            return indexManager.getClientId(attribute,value);
        }else{
            return indexManager.getProductId(attribute,value);
        }
    }
}
