package com.bahaa;

import com.bahaa.exceptions.ResourceNotFound;
import com.google.gson.JsonObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class MainController {

    private MainService mainService;
    public MainController(){
        mainService = new MainService();
    }

    @RequestMapping(value="/", method= RequestMethod.POST)
    public String addDocument(ModelMap model, @RequestBody String document, @RequestParam String schema)  {

        JsonObject jsonData = mainService.addDocument(document,schema);
        model.put("document",jsonData);
        model.put("documentId",jsonData.get("id"));
        model.put("name",jsonData.get("name"));
        return "login";
    }

    @RequestMapping(value= "/index", method= RequestMethod.POST)
    public String makeIndex(@RequestParam String schema, @RequestParam String attribute){
        mainService.makeIndex(schema,attribute);
        return "login";
    }

    @RequestMapping(value= "/{id}", method= RequestMethod.GET)
    public String getDocument(ModelMap model, @RequestParam String schema , @PathVariable String id) {
        try {
            JsonObject document = mainService.getDocument(id, schema);
            model.put("document",document);
            model.put("documentId",document.get("id"));
            model.put("name",document.get("name"));
            return "login";
        }catch(Exception e){
            throw new ResourceNotFound("Can't find such document");
        }
    }
    @RequestMapping(value= "/", method= RequestMethod.GET)
    public String getDocumentsByIndex(ModelMap model, @RequestParam String schema,
                                     @RequestParam String attribute,
                                     @RequestParam String value
                                     ){

        try{
            List<String> id = mainService.getIdFromIndex(schema,attribute,value);
            List<JsonObject> documents = mainService.getDocuments(id,schema);
            model.put("documents",documents);
            return "login";
        }catch (Exception e){
            System.out.println("Index for attribute: "+ attribute+ " with value: " + value + ", for schema: " +
                    schema + " isn't found!");
        }
        return "error";
    }

    @RequestMapping(value= "/{id}", method= RequestMethod.DELETE)
    public String deleteDocument(@RequestParam String schema , @PathVariable String id){
        mainService.deleteDocument(id,schema);
        return "login";
    }
}
