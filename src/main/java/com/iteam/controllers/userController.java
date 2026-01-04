package com.iteam.controllers;

import com.iteam.entities.User;
import com.iteam.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@Tag(name = "users" , description = "Gestions Des Utilisateurs")
public class userController {


    private final UserService userService;

    public userController(UserService userService) {
        this.userService = userService;
    }


    //Get All Users
    @Operation(summary = "Get All Users")
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers(){
        return ResponseEntity.ok(userService.findAll());
    }

    // Get User By Id
    @Operation(summary = "Get User By Id")
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable(name = "id") Long id){
        return ResponseEntity.ok(userService.findUserById(id));
    }

    // Create User
    @Operation(summary = "Create user")
    @ApiResponses({
            @ApiResponse(responseCode = "201",description = "User Created"),
            @ApiResponse(responseCode = "400",description = "invalid request"),
    })
    @PostMapping("/create")
    public ResponseEntity<User> createUser(@RequestBody User user){
        User createdUser = userService.createUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    // Update User
    @Operation(summary = "Update User")
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable(name = "id") Long id,
                                           @RequestBody User user){
        return ResponseEntity.ok(userService.updateUser(id, user));
    }

    // Delete User
    @Operation(summary = "Delete User")
    @DeleteMapping("/{id}")
    public ResponseEntity<User> deleteUser(@PathVariable(name = "id") Long id){
        userService.deleteUserById(id);
        return ResponseEntity.noContent().build(); // 204
    }

    // Gestion globale des exceptions
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException e) {
        Map<String, String> error = new HashMap<>();
        error.put("message", e.getMessage());
        error.put("status", "NOT_FOUND");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

















}
