package me.silloy.oauth2.client.controller;

import java.time.Instant;
import org.apache.shiro.SecurityUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

  @GetMapping("/user")
  public String success() {
    // Pac4jPrincipal
    Object p = SecurityUtils.getSubject().getPrincipal();
    return "SUCCESS";
  }


  @GetMapping
  public Instant now() {
    // Pac4jPrincipal
    return Instant.now();
  }
}
