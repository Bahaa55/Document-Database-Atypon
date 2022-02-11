package com.db.node;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.*;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.concurrent.*;

@RestController
@SpringBootApplication
@EnableAsync
@RequestMapping("")
public class ReadController {
	private ReadService readService = ReadService.getInstance();

	public static void main(String[] args) {
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
		if(readService.importDb(file))
			return new ResponseEntity<String>(HttpStatus.valueOf(200));
		else
			return new ResponseEntity<String>(HttpStatus.valueOf(405));
	}
}
