package org.liuyehcf.test.annotation;

import org.liuyehcf.annotation.source.Builder;

@Builder
public class TestUserDTO {
    private String firstName;

    private String lastName;

    private Integer age;

    private String address;

    public static void main(String[] args) {
        TestUserDTO.TestUserDTOBuilder builder = TestUserDTO.builder();

        TestUserDTO userDTO = builder.firstName("辰枫")
                .lastName("贺")
                .age(25)
                .address("杭州")
                .build();

        System.out.println(userDTO);
    }

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

    @Override
    public String toString() {
        return "firstName: " + firstName + "\n" +
                "lastName: " + lastName + "\n" +
                "age: " + age + "\n" +
                "address: " + address;
    }
}
