package com.db.node;

import com.db.node.BPlusTree.BPlusTree;
import java.io.*;
import java.util.*;

public class IndexManager implements Serializable{
    private static IndexManager instance = new IndexManager();
    private HashMap<String,HashMap<String, BPlusTree<String>>> indexes;
    private static final long serialVersionUID = 5;

    private IndexManager(){
        indexes = new HashMap<>();
        File directory = new File(ReadService.getDbPath() + "/indexes");
        for(File file : directory.listFiles()){

            if(file.getName().startsWith("."))
                continue;

            try{
                String schema = file.getName().split("_")[0];
                FileInputStream inputFile = new FileInputStream(file);
                ObjectInputStream inputOis = new ObjectInputStream(inputFile);
                indexes.put(schema,(HashMap<String, BPlusTree<String>>) inputOis.readObject());
            }catch(Exception e){
                System.out.println("Can't load index for schema: "+ file.getName());
            }
        }
    }

    public static IndexManager getInstance(){
        return instance;
    }

    public List<String> getIds(String schema, String attribute, String value){
        if(indexes.get(schema) == null)
            indexes.put(schema,new HashMap<>());

        if(indexes.get(schema).get(attribute) == null)
            return new ArrayList<>();

        return indexes.get(schema).get(attribute).search(value);
    }

    public IndexManager reset(){
        instance = new IndexManager();
        return instance;
    }
}
