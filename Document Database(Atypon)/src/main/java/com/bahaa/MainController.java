package com.bahaa;

import com.google.gson.JsonObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

@Controller
public class MainController {

    private MainService service;
    MainController(MainService service) {
        this.service = service;
    }

    @RequestMapping(value="/login.do", method= RequestMethod.POST)
    public String addDocument(ModelMap model, @RequestBody String document, @RequestParam String schema)  {

        JsonObject jsonData = service.addDocument(document,schema);
        model.put("document",jsonData);
        model.put("documentId",jsonData.get("id"));
        model.put("name",jsonData.get("name"));
        return "login";
    }

    @RequestMapping(value= "/{id}", method= RequestMethod.GET)
    public String getDocument(ModelMap model, @RequestParam String schema , @RequestHeader int id) {
        JsonObject document = service.getDocument(id,schema);
        model.put("document",document);
        model.put("documentId",document.get("id"));
        model.put("name",document.get("name"));
        return "login";
    }
}
