package com.db.node;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.scheduling.annotation.Async;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

public class ReadService {
    private Semaphore dbLock = new Semaphore(1);
    private static int dbPointer;
    private static ReadService instance = new ReadService();
    private IndexManager indexManager;

    private ReadService(){
        File file = new File("./db0/db");
        boolean exists = file.exists();
       try {
           dbLock.acquire();
           if(exists == true)
               dbPointer = 0;
           else
               dbPointer = 1;
           dbLock.release();
       } catch (InterruptedException e) {
           e.printStackTrace();
       }
       indexManager = IndexManager.getInstance();
    }

    public static ReadService getInstance(){
        return instance;
    }

    public static String getDbPath(){
        return "./db"+dbPointer + "/db";
    }

    @Async()
    public JsonObject getDocument(String id, String schema) {
        try {
            JsonParser parser = new JsonParser();
            File file = new File(ReadService.getDbPath() + "/" + schema + "/" + id + ".json");
            JsonObject document = (JsonObject) parser.parse(new FileReader(file));
            return document;
        }catch(Exception e){
            System.out.println("Document not found!");
            return new JsonObject();
        }
    }

    @Async()
    public List<JsonObject> getDocumentsFromIndex(String schema, String attribute, String value){
        List<String> ids = getIdsFromIndex(schema,attribute,value);
        return getDocuments(ids,schema);
    }

    @Async()
    private List<JsonObject> getDocuments(List<String> ids, String schema) {
        List<JsonObject> documents = new ArrayList<>();
        for(String id: ids){
            documents.add(getDocument(id,schema));
        }
        return documents;
    }

    @Async()
    private List<String> getIdsFromIndex(String schema, String attribute, String value){
        return indexManager.getIds(schema,attribute,value);
    }

    public void importDb(){
        try {
            dbLock.acquire();

            int newDbPointer = 0;
            if(dbPointer == 0)
                newDbPointer = 1;

            ZipDirectory.unzipDirectory("./db.zip","./db"+newDbPointer);
            deleteDirectory(new File(ReadService.getDbPath()));
            new File("./db.zip").delete();

            dbPointer = newDbPointer;
            dbLock.release();
        } catch (Exception e) {
            System.out.println("Couldn't update normally");
        }
    }

    private boolean deleteDirectory(File directoryToBeDeleted) {
        File[] allContents = directoryToBeDeleted.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteDirectory(file);
            }
        }
        return directoryToBeDeleted.delete();
    }

}
