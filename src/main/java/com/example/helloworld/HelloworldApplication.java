package com.example.helloworld;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.Arrays;
import java.util.List;

class Listing{
  public String name;
  public String description;
  public String location;
  public float price;
  public Listing(String name, String desc, String location, float price){
    this.name=name;
    description=desc;
    this.location=location;
    this.price=price;
  }
}

@SpringBootApplication
public class HelloworldApplication {

  @Value("${NAME:World}")
  String name;

  @RestController
  class HelloworldController {

    @RequestMapping(value="/login/{user}/{password}", method = RequestMethod.GET)
    String login(@PathVariable("user") String user, @PathVariable("password") String password){
      if (user.equalsIgnoreCase("fab") && password.equals("fab"))
      {
        return "OK";
      }
      else {
        return "Username o password errati.";
      }
    }

    @RequestMapping(value="/listings/{keyword}", method = RequestMethod.GET)
    List<Listing> getListings(@PathVariable("keyword") String keyword){
      return Arrays.asList(new Listing("Castello di Hogwarts", "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor", "Napoli (NA)", 3500000f),
                            new Listing("Casa dello Hobbit", "Lorem ipsum", "Pioppaino (NA)", 1350000f));
    }

    @GetMapping("/thumbnails/{filename}")
    public ResponseEntity<Resource> getThumbnails(@PathVariable("filename") String filename) throws Exception {
        // Path path = Paths.get("./thumbnails/"+filename+".jpg");
        // Resource resource = new UrlResource(path.toUri());
        Resource resource = new UrlResource("https://upload.wikimedia.org/wikipedia/commons/thumb/c/cd/Hogwarts_(29353868725).jpg/1200px-Hogwarts_(29353868725).jpg");
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(resource);
    }
  }

  public static void main(String[] args) {
    SpringApplication.run(HelloworldApplication.class, args);
  }
}

