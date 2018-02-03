package org.liuyehcf.lombok;

import org.liuyehcf.lombok.annotation.Builder;

@Builder
public class UserDTO {
    private String firstName;
    private String lastName;
    private Integer age;
    private String address;
    private String country;
    private String language;
    private String education;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public static void main(String[] args) throws Exception {
        Class.forName("org.liuyehcf.lombok.UserDTO$UserDTOBuilder");
    }
}
