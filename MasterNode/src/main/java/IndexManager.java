import BPlusTree.BPlusTree;
import com.google.gson.*;

import java.io.*;
import java.util.*;

public class IndexManager {
    private static IndexManager instance = new IndexManager();
    private HashMap<String,HashMap<String, BPlusTree<String>>> indexes;

    private IndexManager(){
        indexes = new HashMap<>();
        File directory = new File("./db/indexes");
        for(File file : directory.listFiles()){
            try{
                    String schema = file.getName();
                    FileInputStream inputFile = new FileInputStream(file);
                    ObjectInputStream inputOis = new ObjectInputStream(inputFile);
                    indexes.put(schema,(HashMap<String, BPlusTree<String>>) inputOis.readObject());
            }catch(Exception e){
                throw new RuntimeException("Can't load index for schema: "+ file.getName());
            }
        }
    }

    public static IndexManager getInstance(){
        return instance;
    }

    public List<String> getIds(String schema, String attribute, String value){
        if(indexes.get(schema).get(attribute) == null)
            return new ArrayList<>();
        return indexes.get(schema).get(attribute).search(value);
    }

    public boolean makeIndex(String schema, String attribute){
        if(indexes.get(schema) == null)
            indexes.put(schema,new HashMap<>());

        if(indexes.get(schema).get(attribute) != null)
            return false;

        ArrayList<Pair<String,Integer>> values = getValues(schema,attribute);
        createTree(values,schema,attribute);
        update(schema);
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
        BPlusTree<String> tree = new BPlusTree<String>(3,schema);
        for(Pair<String,Integer> val : values)
            tree.insert(val.getKey(), val.getValue());

        indexes.get(schema).put(attribute,tree);
    }

    private void update(String schema){
        try{
            FileOutputStream outputFile = new FileOutputStream("./db/indexes/"+ schema + "_index.dat");
            ObjectOutputStream outputOos = new ObjectOutputStream(outputFile);
            outputOos.writeObject(indexes.get(schema));
            outputOos.flush();
            outputOos.close();
        }catch(Exception e){
            System.out.println("Couldn't update index \""+ schema + "\".");
        }
    }

    public void updateIndex(String schema, JsonObject document){
        if(indexes.get(schema) == null)
            indexes.put(schema,new HashMap<>());

        int id = document.get("id").getAsInt();
        Set<Map.Entry<String, JsonElement>> entrySet = document.entrySet();
        for(Map.Entry<String,JsonElement> entry : entrySet){
            String key = entry.getKey();
            String value = document.get(key).getAsString();
            if(indexes.get(schema).get(key) == null)
                continue;
            indexes.get(schema).get(key).insert(value,id);
        }
        update(schema);
    }


    private class Pair<Key, Value> implements Serializable{
        private Key key;
        private Value value;
        Pair(Key key, Value value){
            this.key = key;
            this.value = value;
        }
        Key getKey(){ return key; }
        Value getValue(){ return value; }
    }

    public void deleteIndexes(String schema,JsonObject document){
        Set<Map.Entry<String, JsonElement>> entrySet = document.entrySet();
        String id = document.get("id").getAsString();
        for(Map.Entry<String,JsonElement> entry : entrySet){
            String key = entry.getKey();
            String value = document.get(key).getAsString();
            if(indexes.get(schema).get(key) == null)
                continue;
            if(indexes.get(schema).get(key).deleteFromDuplicates(schema,value,id) == false){
                indexes.get(schema).get(key).delete(value);
                indexes.get(schema).get(key).replaceDuplicate(value);
            }
        }
        update(schema);
    }

}


