package com.bahaa;

import com.google.gson.JsonObject;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipOutputStream;

public class MainService {
    private WriteService writeService;
    private ReadService readService;
    private IndexManager indexManager;

    public MainService(){
        writeService = new WriteService();
        readService = new ReadService();
        indexManager = IndexManager.getInstance();
    }
    public JsonObject addDocument(String document, String schema) {
        JsonObject success;
        try{
           success = writeService.addDocument(document,schema);
           indexManager.updateIndex(schema, success);
           return success;
        }catch(IOException e){
            System.out.println("Something wrong happened");
            return new JsonObject();
        }
    }

    public JsonObject getDocument(String id, String schema) throws IOException{
        return readService.getDocument(id,schema);
    }

    public List<JsonObject> getDocuments(List<String> ids, String schema) throws IOException {
        List<JsonObject> documents = new ArrayList<>();
        for(String id: ids){
            System.out.println(id);
            documents.add(readService.getDocument(id,schema));
        }
        return documents;
    }

    public List<String> getIdFromIndex(String schema, String attribute, String value){
        return indexManager.getIds(schema,attribute,value);
    }

    public void deleteDocument(String id, String schema) {
        try{
            JsonObject json = readService.getDocument(id,schema);
            writeService.deleteDocument(id,schema);
            indexManager.deleteIndexes(schema,json);
        }catch(Exception e){
            System.out.println("Document not found!");
        }

    }
    public void deleteDocuments(List<String> ids, String schema){
        for(String id: ids)
            writeService.deleteDocument(id,schema);
    }

    public void makeIndex(String schema, String attribute){
        indexManager.makeIndex(schema,attribute);
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
            ZipDirectory.unzipDirectory("./db.zip","./db2");
        }catch(Exception e){
            System.out.println("Can't import the zipped file.");
        }
    }
}
