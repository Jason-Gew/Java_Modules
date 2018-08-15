package gew.webview.model;

import lombok.Data;
import java.util.UUID;

@Data
public class User {

    private String id;
    private String firstName;
    private String lastName;
    private String userName;
    private String email;

    public User() {
        this.id = UUID.randomUUID().toString();
    }

    public User(String firstName, String lastName, String userName, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.userName = userName;
        this.email = email;
        this.id = UUID.randomUUID().toString();
    }

    public String validateUserInfo() {
        if (firstName == null || firstName.isEmpty() ) {
            return "Invalid First Name";
        } else if (lastName == null || lastName.isEmpty()) {
            return "Invalid Last Name";
        } else if (userName == null || userName.isEmpty()) {
            return "Invalid User Name";
        } else if (email == null || email.length() < 4 || !email.contains("@")) {
            return "Invalid Email Address";
        } else {
            return Status.SUCCESS.value();
        }
    }
}
