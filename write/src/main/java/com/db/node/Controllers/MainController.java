package com.db.node.Controllers;

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
        SpringApplication.run(WriteController.class, args);
    }

    @PostMapping("")
    public String addDocument(@RequestBody String document, @RequestParam String schema){
        return this.writeController.addDocument(document,schema);
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
        return new RedirectView(LoadBalancer.getNode() + "/" + id + "?schema="+schema);
    }

    @GetMapping("/index")
    public RedirectView getByIndex(@RequestParam String schema,
                                   @RequestParam String attribute,
                                   @RequestParam String value){
        return new RedirectView(  LoadBalancer.getNode() + "/index" + "?schema="+schema
                + "&attribute=" + attribute + "&value=" +value);
    }

    @GetMapping("/add-node")
    public void addNode(@RequestParam String port){
        adminController.addNode(port);
    }

    @GetMapping("/scale")
    public void scale(){
        adminController.scale();
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
