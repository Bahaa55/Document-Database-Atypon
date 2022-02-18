package com.db.node.BPlusTree;

import com.db.node.ReadService;
import java.io.*;
import java.util.*;

public class DuplicateHandler<T extends Comparable> implements Serializable {

    private static DuplicateHandler instance = new DuplicateHandler<>();
    private HashMap<String, HashMap<T, TreeSet<String>>> sets;
    private static final long serialVersionUID = 5;
    
    public DuplicateHandler(){
        sets = new HashMap<>();

        File directory = new File(ReadService.getDbPath() + "/sets");
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

    public List<String> getValues(String schema, T key){
        if(sets.get(schema) == null)
            sets.put(schema,new HashMap<>());

        if(sets.get(schema).get(key) == null)
            return new ArrayList<>();
        return new ArrayList<>(sets.get(schema).get(key));
    }

}
