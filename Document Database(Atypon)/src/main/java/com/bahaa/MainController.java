package com.bahaa;

import com.bahaa.exceptions.ResourceNotFound;
import com.google.gson.JsonObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

@Controller
public class MainController {

    private MainService mainService;
    public MainController(){
        mainService = new MainService();
    }

    @RequestMapping(value="/login.do", method= RequestMethod.POST)
    public String addDocument(ModelMap model, @RequestBody String document, @RequestParam String schema)  {

        JsonObject jsonData = mainService.addDocument(document,schema);
        model.put("document",jsonData);
        model.put("documentId",jsonData.get("id"));
        model.put("name",jsonData.get("name"));
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
    public String getDocumentByIndex(ModelMap model, @RequestParam String schema,
                                     @RequestParam String attribute,
                                     @RequestParam String value
                                     ){
        System.out.println(value);
        try{
            String id = (mainService.getIdFromIndex(schema,attribute,value)).toString();
            JsonObject document = mainService.getDocument(id,schema);
            model.put("document",document);
            model.put("documentId",document.get("id"));
            model.put("name",document.get("name"));
            return "login";
        }catch (Exception e){
            System.out.println("Index for attribute: "+ attribute+ ", for schema: " +
                    schema + " isn't found!");
        }
        return "error";
    }
}
