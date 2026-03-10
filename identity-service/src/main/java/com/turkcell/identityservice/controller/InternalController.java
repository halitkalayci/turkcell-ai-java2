package com.turkcell.identityservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/internal")
public class InternalController {

    @GetMapping("/validate")
    public ResponseEntity<Map<String, String>> validateService() {
        return ResponseEntity.ok(Map.of("status", "ok"));
    }
}
