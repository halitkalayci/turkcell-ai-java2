package com.turkcell.productservice;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/products")
public class ProductController {

    @GetMapping
    public ResponseEntity<String> getAll() {
        return ResponseEntity.ok("OK");
    }

    @GetMapping("/{id}")
    public ResponseEntity<String> getById(@PathVariable int id) {
        return ResponseEntity.ok("OK");
    }

    @PostMapping
    public ResponseEntity<String> create(@RequestBody Object request) {
        return ResponseEntity.ok("OK");
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> update(@PathVariable int id, @RequestBody Object request) {
        return ResponseEntity.ok("OK");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable int id) {
        return ResponseEntity.ok("OK");
    }
}
