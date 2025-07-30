package com.example.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.entity.UserEntity;
import com.example.exceptions.ResourceNotFoundException;
import com.example.repository.UserRepository;

@RestController
@RequestMapping("/api/users")
public class UserController {

//	@GetMapping
//	public String getUsers() {
//		return "Welcome Suchitra";
//	} 
	
//	@GetMapping
//	public List<User> getUsers(){
//		return Arrays.asList(new User(1L,"John","john@gmail.com"),
//				             new User(2L,"Joe","joe@gmail.com"),
//				             new User(3L,"Alice","alice@gmail.com"),
//				             new User(4L,"Ahaan","ahaan@gmail.com")
//				             );
//	}
	//Component Scanning
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@GetMapping
	public List<UserEntity> getUsers(){
		return userRepository.findAll();
	}
	
	@PostMapping
	public UserEntity createUser(@RequestBody UserEntity user ) {
//		System.out.println("POST create user called!");
//		System.out.println("User Data "+user.getName()+","+user.getEmail());
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		return userRepository.save(user);
	}
	
//	@GetMapping("/{id}")
//	public Optional<UserEntity> getUserById(@PathVariable Long id){
//		return userRepository.findById(id);
//	}
	
	@GetMapping("/{id}")
	public UserEntity getUserById(@PathVariable Long id){
		return userRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("User not found with this id:"+id));
	}
	
	@PutMapping("/{id}")
	public UserEntity updateUser(@PathVariable Long id,@RequestBody UserEntity user) {
		UserEntity userData=userRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("User not found with this id:"+id));
		userData.setName(user.getName());
		userData.setEmail(user.getEmail());
		return userRepository.save(userData);
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteUser(@PathVariable Long id){
		UserEntity userData=userRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("User not found with this id:"+id));
		userRepository.delete(userData);
		return ResponseEntity.ok().build();
	}
}
