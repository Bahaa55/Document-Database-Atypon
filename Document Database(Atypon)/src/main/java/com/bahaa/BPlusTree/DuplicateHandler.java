package com.bahaa.BPlusTree;

import java.io.*;
import java.util.*;

public class DuplicateHandler<T extends Comparable> implements Serializable {

    private static DuplicateHandler instance = new DuplicateHandler<>();
    private HashMap<String,HashMap<T, TreeSet<String>>> sets;
    private Map<T, TreeSet<String>> client_set;
    private Map<T, TreeSet<String>> product_set;

    private DuplicateHandler(){
        sets = new HashMap<>();

        File directory = new File("./db/sets");
        for(File file : directory.listFiles()){
            try{
                String schema = file.getName();
                FileInputStream inputFile = new FileInputStream(file);
                ObjectInputStream inputOis = new ObjectInputStream(inputFile);
                sets.put(schema,(HashMap<T, TreeSet<String>>) inputOis.readObject());
            }catch(Exception e){
                throw new RuntimeException("Can't load index for schema: "+ file.getName());
            }
        }
    }

    public static DuplicateHandler getInstance(){
        return instance;
    }

    public void addDuplicate(String schema, T key, Integer value){
        if(sets.get(schema).get(key) == null)
            sets.get(schema).put(key,new TreeSet<>());
        sets.get(schema).get(key).add(value.toString());
        update(schema);
    }

    public List<String> getValues(String schema, T key){
        if(sets.get(schema) == null)
            sets.put(schema,new HashMap<>());

        if(sets.get(schema).get(key) == null)
            return new ArrayList<>();
        return new ArrayList<>(sets.get(schema).get(key));
    }

    public String getFirst(String schema, T key){
        String first = null;
        if(sets.get(schema).get(key) == null || sets.get(schema).get(key).isEmpty())
            return null;
        first = sets.get(schema).get(key).pollFirst();
        update(schema);
        return first;
    }

    public boolean delete(String schema, T key, String value){
        if(sets.get(schema).get(key).contains(value)){
            sets.get(schema).get(key).remove(value);
            return true;
        }
        return false;
    }

    private void update(String schema){
        try {
            FileOutputStream outputFile = new FileOutputStream("./db/" + schema + "_set.dat");
            ObjectOutputStream outputOos = new ObjectOutputStream(outputFile);
            outputOos.writeObject(sets.get(schema));
            outputOos.flush();
            outputOos.close();
        }catch(Exception e){
            System.out.println("Couldn't update the set for schema: "+schema);
        }
    }
}
