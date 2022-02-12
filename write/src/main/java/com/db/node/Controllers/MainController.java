package com.db.node.Controllers;

import com.db.node.ReadNode;
import com.db.node.Services.LoadBalancer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.view.RedirectView;

@SpringBootApplication
@RestController
@RequestMapping("")
public class MainController {
    private WriteController writeController = new WriteController();
    private AdminController adminController = new AdminController();

    public static void main(String[] args) {
        SpringApplication.run(MainController.class, args);
    }

    @PostMapping("")
    public String addDocument(@RequestParam String schema, @RequestBody String document){
        return this.writeController.addDocument(schema,document);
    }

    @PostMapping("/index")
    public String makeIndex(@RequestParam String schema, @RequestParam String attribute){
        return writeController.makeIndex(schema, attribute);
    }

    @PostMapping("/schema")
    public String makeSchema(@RequestParam String schema){
        return writeController.makeSchema(schema);
    }

    @GetMapping("/{id}")
    public RedirectView getDocument(@RequestParam String schema , @PathVariable String id){
        return new RedirectView(LoadBalancer.getNodeUrl() + "/" + id + "?schema="+schema);
    }

    @GetMapping("/index")
    public RedirectView getByIndex(@RequestParam String schema,
                                   @RequestParam String attribute,
                                   @RequestParam String value){
        return new RedirectView(  LoadBalancer.getNodeUrl() + "/index" + "?schema="+schema
                + "&attribute=" + attribute + "&value=" +value);
    }

    @GetMapping("/add-node")
    public void addNode(@RequestParam String port){
        adminController.addNode(port);
    }

    @GetMapping("/scale")
    public void scale(@RequestParam String token){
        adminController.scale(token);
    }

    @DeleteMapping("/{id}")
    public String deleteDocument(@RequestParam String schema , @PathVariable String id){
        return writeController.deleteDocument(schema,id);
    }

    @PostMapping(value = "/update")
    public ResponseEntity<String> updateDB(@RequestParam(value = "file") MultipartFile file) {
       return writeController.updateDB(file);
    }

}
