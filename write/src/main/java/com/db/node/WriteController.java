package com.db.node;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


@SpringBootApplication
@RestController
@RequestMapping("")
public class WriteController {
	private WriteService writeService = WriteService.getInstance();

	public static void main(String[] args) {
		SpringApplication.run(WriteController.class, args);
	}

	@PostMapping("")
	public String create(@RequestBody String document, @RequestParam String schema){
		return this.writeService.addDocument(document,schema).toString();
	}

	@PostMapping("/index")
	public String makeIndex(@RequestParam String schema, @RequestParam String attribute){
		if(writeService.makeIndex(schema, attribute) == true)
			return "Success";
		return "Failure";
	}

	@PostMapping("/schema")
	public String makeSchema(@RequestParam String schema){
		if(writeService.addSchema(schema))
			return "Success";
		return "Failure";
	}

	@GetMapping("/{id}")
	public RedirectView get(@RequestParam String schema , @PathVariable String id){
		return new RedirectView("http://localhost:1026/" + id + "?schema="+schema);
	}

	@GetMapping("/index")
	public RedirectView getByIndex(@RequestParam String schema,
							 @RequestParam String attribute,
							 @RequestParam String value){
		return new RedirectView("http://localhost:1026/index" + "?schema="+schema
		+ "&attribute=" + attribute + "&value=" +value);
	}

	@DeleteMapping("/{id}")
	public String delete(@RequestParam String schema , @PathVariable String id){
		return writeService.deleteDocument(schema,id);
	}

	@PostMapping(value = "/update")
	public ResponseEntity<String> updateDB(@RequestParam(value = "file") MultipartFile file) {
		if (file.isEmpty()) {
			return new ResponseEntity<String>(HttpStatus.valueOf(200));
		}

		try {
			byte[] bytes = file.getBytes();
			Path path = Paths.get("./" + file.getOriginalFilename());
			Files.write(path, bytes);

		} catch (IOException e) {
			return new ResponseEntity<String>(HttpStatus.valueOf(200));
		}
		writeService.importDb();

		return new ResponseEntity<String>(HttpStatus.valueOf(200));
	}
}
