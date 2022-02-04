
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ReadService {
    private IndexManager indexManager;

    public JsonObject getDocument(String id, String schema) throws IOException {
        JsonParser parser = new JsonParser();
        File file = new File("./db/" + schema + "/" + id + ".json");
        JsonObject document = (JsonObject) parser.parse(new FileReader(file));
        return document;
    }



    public List<JsonObject> getDocuments(List<String> ids, String schema) throws IOException {
        List<JsonObject> documents = new ArrayList<>();
        for(String id: ids){
            documents.add(getDocument(id,schema));
        }
        return documents;
    }

    public List<String> getIdFromIndex(String schema, String attribute, String value){
        return indexManager.getIds(schema,attribute,value);
    }

}
