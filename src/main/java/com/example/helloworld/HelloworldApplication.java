package com.example.helloworld;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;

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
  }

  public static void main(String[] args) {
    SpringApplication.run(HelloworldApplication.class, args);
  }
}

