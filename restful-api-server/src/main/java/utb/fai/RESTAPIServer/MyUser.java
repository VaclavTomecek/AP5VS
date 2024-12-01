package utb.fai.RESTAPIServer;  

import jakarta.persistence.*;  

@Entity  
@Table(name = "users")  
public class MyUser {  

    @Id  
    private Long id;  

    @Column(name = "name", nullable = false)  
    private String name;  

    @Column(name = "email", nullable = false)  
    private String email;  

    @Column(name = "phone_number", nullable = false)  
    private String phoneNumber;  

    public MyUser() {}  

    public MyUser(Long id, String name, String email, String phoneNumber) {  
        this.id = id;  
        this.name = name;  
        this.email = email;  
        this.phoneNumber = phoneNumber;  
    }  

    public boolean isUserDataValid() {  
        return id != null && name != null && email != null && phoneNumber != null &&   
               id >= 0 && !name.isEmpty() && !email.isEmpty() && !phoneNumber.isEmpty() &&   
               isEmailValid() && isPhoneNumberValid();  
    }  

    private boolean isEmailValid() {  
        return email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");  
    }  

    private boolean isPhoneNumberValid() {  
        return phoneNumber.matches("^\\+?[0-9\\s-]+$");  
    }  

    public Long getId() {  
        return id;  
    }  

    public void setId(Long id) {  
        this.id = id;  
    }  

    public String getName() {  
        return name;  
    }  

    public void setName(String name) {  
        this.name = name;  
    }  

    public String getEmail() {  
        return email;  
    }  

    public void setEmail(String email) {  
        this.email = email;  
    }  

    public String getPhoneNumber() {  
        return phoneNumber;  
    }  

    public void setPhoneNumber(String phoneNumber) {  
        this.phoneNumber = phoneNumber;  
    }  
}