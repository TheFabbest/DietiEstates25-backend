package com.example.helloworld;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

class Listing{
  public String description;
  public Listing(String desc){
    description=desc;
  }
}

@SpringBootApplication
public class HelloworldApplication {

  @Value("${NAME:World}")
  String name;

  @RestController
  class HelloworldController {
    @GetMapping("/")
    String hello() {
      return "Hello " + name + "!";
    }

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

    @RequestMapping(value="/listings", method = RequestMethod.GET)
    List<Listing> listings(){
      return Arrays.asList(new Listing("Lorem"), new Listing("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor"));
    }
  }

  public static void main(String[] args) {
    SpringApplication.run(HelloworldApplication.class, args);
  }
}

