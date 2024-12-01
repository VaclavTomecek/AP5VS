package utb.fai.RESTAPIServer;  

import java.util.List;  

import org.springframework.beans.factory.annotation.Autowired;  
import org.springframework.http.HttpStatus;  
import org.springframework.http.ResponseEntity;  
import org.springframework.web.bind.annotation.*;  

import java.util.Optional;  

@RestController  
public class UserController {  

    @Autowired  
    private UserRepository userRepository;  

    @GetMapping("/users")  
    public List<MyUser> getAllUsers() {  
        try {  
            return userRepository.findAll();  
        } catch (Exception e) {  
            return null;  
        }  
    }  

    @GetMapping("/getUser")  
    public ResponseEntity<MyUser> getUserById(@RequestParam Long id) {  
        try {  
            MyUser user = userRepository.findById(id)  
                    .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + id));  
            return new ResponseEntity<>(user, HttpStatus.OK);  
        } catch (IllegalArgumentException e) {  
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);  
        } catch (Exception e) {  
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);  
        }  
    }  

    @PostMapping("/createUser")  
    public ResponseEntity<MyUser> createUser(@RequestBody MyUser newUser) {  
        try {  
            System.out.println("Received user: " + newUser);  
            if (!newUser.isUserDataValid()) {  
                System.out.println("Invalid user data");  
                return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);  
            }  
            System.out.println("Valid user data");  
            MyUser savedUser = userRepository.save(newUser);  
            System.out.println("User saved successfully");  
            return new ResponseEntity<>(savedUser, HttpStatus.CREATED);  
        } catch (Exception e) {  
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);  
        }  
    }  

    @PutMapping("/editUser")  
    public ResponseEntity<MyUser> editUser(@RequestParam Long id, @RequestBody MyUser newUser) {  
        try {  
            Optional<MyUser> existingUserOpt = userRepository.findById(id);  
            if (existingUserOpt.isEmpty()) {  
                return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);  
            }  

            MyUser existingUser = existingUserOpt.get();  
            existingUser.setName(newUser.getName());  
            existingUser.setEmail(newUser.getEmail());  
            existingUser.setPhoneNumber(newUser.getPhoneNumber());  

            if (!existingUser.isUserDataValid()) {  
                return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);  
            }  

            MyUser updatedUser = userRepository.save(existingUser);  

            return new ResponseEntity<>(updatedUser, HttpStatus.OK);  
        } catch (Exception e) {  
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);  
        }  
    }  

    @DeleteMapping("/deleteUser")  
    public ResponseEntity<MyUser> deleteUser(@RequestParam Long id) {  
        try {  
            Optional<MyUser> existingUserOpt = userRepository.findById(id);  
            if (existingUserOpt.isEmpty()) {  
                return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);  
            }  

            MyUser existingUser = existingUserOpt.get();  
            userRepository.delete(existingUser);  

            return new ResponseEntity<>(existingUser, HttpStatus.OK);  
        } catch (Exception e) {  
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);  
        }  
    }  

    @DeleteMapping("/deleteAll")  
    public ResponseEntity<String> deleteAllUsers() {  
        try {  
            userRepository.deleteAll();  
            return new ResponseEntity<>("All users deleted successfully.", HttpStatus.OK);  
        } catch (Exception e) {  
            return new ResponseEntity<>("Error occurred while deleting users.", HttpStatus.INTERNAL_SERVER_ERROR);  
        }  
    }  
}