package com.db.node;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@RestController
@SpringBootApplication
@EnableAsync
@RequestMapping("")
public class ReadController {
	private ReadService readService = ReadService.getInstance();

	public static void main(String[] args) {
		// close the application context to shut down the custom ExecutorService
		SpringApplication.run(ReadController.class, args);
	}

	@Bean
	public Executor taskExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(2);
		executor.setMaxPoolSize(2);
		executor.setQueueCapacity(500);
		executor.setThreadNamePrefix("GithubLookup-");
		executor.initialize();
		return executor;
	}

	@Async
	@GetMapping("/{id}")
	public CompletableFuture<String> getDocument(ModelMap model, @RequestParam String schema , @PathVariable String id) {
		return CompletableFuture.completedFuture(this.readService.getDocument(id,schema).toString());
	}

	@Async
	@GetMapping("/index")
	public CompletableFuture<String> getDocumentsByIndex(@RequestParam String schema,
									  @RequestParam String attribute,
									  @RequestParam String value){
		return CompletableFuture.completedFuture(this.readService.getDocumentsFromIndex(schema,attribute,value).toString());
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
		readService.importDb();

		return new ResponseEntity<String>(HttpStatus.valueOf(200));
	}
}
