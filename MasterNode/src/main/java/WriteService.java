import com.google.gson.*;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class WriteService {
    private static WriteService instance = new WriteService();
    private static Integer id;
    private JsonObject config;
    private IndexManager indexManager;

    public static WriteService getInstance(){
        return instance;
    }

    private WriteService() {
        indexManager = IndexManager.getInstance();
        JsonParser parser = new JsonParser();
        File file = new File("./db/config.json");
        try{
            config = (JsonObject) parser.parse(new FileReader(file));
        }catch(Exception e){
            throw new RuntimeException("Error loading configuration file.\n" +
                    "Make sure the file config.json is available.");
        }
    }

    public JsonObject addDocument(String document, String schema) {
        JsonObject success;
        try{
            success = write(document,schema);
            indexManager.updateIndex(schema, success);
            return success;
        }catch(IOException e){
            System.out.println("Something wrong happened");
            return new JsonObject();
        }
    }

    private JsonObject write(String document, String schema) throws IOException {

        try {


            int currentId = getNewId(schema);
            JsonObject jsonData = new JsonParser().parse(document).getAsJsonObject();
            JsonElement element = new JsonPrimitive(currentId);
            jsonData.add("id", element);

            File file = new File("./db/" + schema + "/" + currentId + ".json");
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(jsonData.toString());
            fileWriter.flush();
            fileWriter.close();

            return jsonData;
        }catch(Exception e){
            System.out.println("Couldn't write the document");
            return new JsonObject();
        }
    }

    private void delete(String id, String schema){
        File document = new File("./db/" + schema + "/" + id + ".json");
        if(!document.delete())
            System.out.println("Couldn't delete this document. Try again!");
    }

    private int getNewId(String schema){
        id = config.get(schema + "_id").getAsInt();
        JsonElement newId = new JsonPrimitive(++id);
        config.remove(schema + "_id");
        config.add(schema + "_id",newId);
        try{
            updateConfig();
        }catch(Exception e){
            System.out.println("Didn't update the configuration correctly.\n");
        }
        return id;
    }

    public void deleteDocument(String id, String schema) {
        try{
            JsonObject json = getDocument(id,schema);
            delete(id,schema);
            indexManager.deleteIndexes(schema,json);
        }catch(Exception e){
            System.out.println("Document not found!");
        }
    }
    public void deleteDocuments(List<String> ids, String schema){
        for(String id: ids)
            deleteDocument(id,schema);
    }

    public void makeIndex(String schema, String attribute){
        indexManager.makeIndex(schema,attribute);
    }

    private JsonObject getDocument(String id, String schema) throws IOException {
        JsonParser parser = new JsonParser();
        File file = new File("./db/" + schema + "/" + id + ".json");
        return (JsonObject) parser.parse(new FileReader(file));
    }

    private void updateConfig() throws IOException {
        File file = new File("./db/config.json");
        FileWriter fileWriter = new FileWriter(file);
        fileWriter.write(config.toString());
        fileWriter.flush();
        fileWriter.close();
    }

}
