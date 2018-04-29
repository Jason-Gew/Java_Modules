package gew.management.model;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;


/**
 * @author Jason/GeW
 */
@Document(collection = "UserInfo")
public class UserInfo {

    @Id
    @Field("_id")
    private String id;

    @Indexed(unique = true)
    private String username;
    private String password;
    @Indexed(unique = true)
    private String email;
    private String phone;
    private Integer group;

    @Field("register_date")
    private String registerDate;

    private Address address;
    private List<String> note;

    public UserInfo() {

    }

    public UserInfo(String username, String password, String email, String registerDate) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.registerDate = registerDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Integer getGroup() {
        return group;
    }

    public void setGroup(Integer group) {
        this.group = group;
    }

    public String getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(String registerDate) {
        this.registerDate = registerDate;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public List<String> getNote() {
        return note;
    }

    public void setNote(List<String> note) {
        this.note = note;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", group=" + group +
                ", registerDate='" + registerDate + '\'' +
                ", address=" + address +
                ", note=" + note +
                '}';
    }
}
