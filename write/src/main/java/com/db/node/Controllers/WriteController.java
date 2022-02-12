package com.db.node.Controllers;

import com.db.node.Services.WriteService;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


public class WriteController {
	private WriteService writeService = WriteService.getInstance();

	public String addDocument(String schema, String document){
		return this.writeService.addDocument(schema,document).toString();
	}

	public String makeIndex(String schema, String attribute){
		if(writeService.makeIndex(schema, attribute) == true)
			return "Success";
		return "Failure";
	}

	public String makeSchema(String schema){
		if(writeService.addSchema(schema))
			return "Success";
		return "Failure";
	}

	@DeleteMapping("/{id}")
	public String deleteDocument(String schema, String id){
		return writeService.deleteDocument(schema,id);
	}

	@PostMapping(value = "/update")
	public ResponseEntity<String> updateDB(MultipartFile file) {
		if(writeService.importDb(file))
			return new ResponseEntity<String>(HttpStatus.valueOf(200));
		return new ResponseEntity<String>(HttpStatus.valueOf(405));
	}
}
