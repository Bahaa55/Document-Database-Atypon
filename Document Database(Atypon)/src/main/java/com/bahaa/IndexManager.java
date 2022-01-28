package com.bahaa;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class IndexManager {
    private HashMap<String,BPlusTree<String>> client_indexes = new HashMap<>();
    private HashMap<String,BPlusTree<String>> product_indexes;
    private Type indexType = new TypeToken<HashMap<String,BPlusTree<String>>>() {}.getType();
    private Gson gson;
    public IndexManager(){
        gson = new Gson();
        File clientFile = new File("./db/client_index.json");
        File productFile = new File("./db/product_index.json");

        try {
            client_indexes = gson.fromJson(new FileReader(clientFile), indexType);
            product_indexes = gson.fromJson(new FileReader(productFile), indexType);
        }catch(Exception e){
            System.out.println("Can't load indexes, make sure that files (product_index / client_index) are available.");
        }
    }

    public Integer getClientId(String attribute, String value){
        if(client_indexes.get(attribute) == null)
            return -1;
        return client_indexes.get(attribute).search(value);
    }

    public Integer getProductId(String attribute, String value){
        if(product_indexes.get(attribute) == null)
            return -1;
        return product_indexes.get(attribute).search(value);
    }

    public boolean makeIndex(String schema, String attribute){
        if(schema.equals("client") && client_indexes.get(attribute) != null)
            return false;
        if(schema.equals("product") && product_indexes.get(attribute) != null)
            return false;

        ArrayList<Pair<String,Integer>> values = getValues(schema,attribute);
        createTree(values,schema,attribute);
        update();
        return true;
    }

    private ArrayList<Pair<String,Integer>> getValues(String schema, String attribute) {
        ArrayList<Pair<String,Integer>> ret = new ArrayList<>();
        File directory = new File("./db/"+schema);
        try{
            JsonParser parser = new JsonParser();
            for(File file : directory.listFiles()){
                JsonObject document = (JsonObject) parser.parse(new FileReader(file));
                String attributeValue = document.get(attribute).getAsString();
                int referenceValue = document.get("id").getAsInt();
                Pair<String,Integer> val = new Pair<>(attributeValue,referenceValue);
                ret.add(val);
            }
        }catch(Exception e){
            throw new RuntimeException("Schema not found in database.");
        }
        return ret;
    }
    private void createTree(ArrayList<Pair<String,Integer>> values, String schema, String attribute){
        BPlusTree<String> tree = new BPlusTree<>(3);
        for(Pair<String,Integer> val : values) {
            tree.insert(val.getKey(), val.getValue());
        }
        if(schema.equals("client"))
            client_indexes.put(attribute,tree);
        else
            product_indexes.put(attribute,tree);
    }

    private void update(){
        try {
            String client = gson.toJson(client_indexes);
            String product = gson.toJson(product_indexes);

            File clientFile = new File("./db/client_index.json");
            File productFile = new File("./db/product_index.json");

            FileWriter fileWriter = new FileWriter(clientFile);
            fileWriter.write(client);
            fileWriter.flush();
            fileWriter.close();

            fileWriter = new FileWriter(productFile);
            fileWriter.write(product);
            fileWriter.flush();
            fileWriter.close();

        }catch(Exception e){
            System.out.println("Couldn't update the indexes.");
        }
    }
}

class Pair<Key, Value>{
    private Key key;
    private Value value;
    Pair(Key key, Value value){
        this.key = key;
        this.value = value;
    }
    Key getKey(){ return key; }
    Value getValue(){ return value; }
}
