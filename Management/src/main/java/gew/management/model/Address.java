package gew.management.model;

import org.springframework.data.mongodb.core.mapping.Field;

/**
 * @author Jason/GeW
 * @since 2018-04-20
 */
public class Address {

    private String country;

    @Field("state/province")
    private String state;
    private String city;
    private String street;
    @Field("zipcode")
    private Integer zipCode;

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public Integer getZipCode() {
        return zipCode;
    }

    public void setZipCode(Integer zipCode) {
        this.zipCode = zipCode;
    }

    @Override
    public String toString() {
        return "Address{" +
                "country='" + country + '\'' +
                ", state='" + state + '\'' +
                ", city='" + city + '\'' +
                ", street='" + street + '\'' +
                ", zipCode=" + zipCode +
                '}';
    }
}
