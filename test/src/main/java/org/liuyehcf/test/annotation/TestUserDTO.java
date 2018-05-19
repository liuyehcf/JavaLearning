package org.liuyehcf.test.annotation;

import org.liuyehcf.annotation.source.annotation.AllArgsConstructor;
import org.liuyehcf.annotation.source.annotation.Builder;
import org.liuyehcf.annotation.source.annotation.Data;
import org.liuyehcf.annotation.source.annotation.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TestUserDTO {
    private String firstName;

    private String lastName;

    private Integer age;

    private String address;

    public String getLastName() {
        return lastName;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public static void main(String[] args) {
        TestUserDTO.TestUserDTOBuilder builder = TestUserDTO.builder();

        TestUserDTO userDTO = builder.firstName("明")
                .lastName("小")
                .age(25)
                .address("火星")
                .build();

        System.out.println(userDTO);
    }

    @Override
    public String toString() {
        return "firstName: " + firstName + "\n" +
                "lastName: " + lastName + "\n" +
                "age: " + age + "\n" +
                "address: " + address;
    }
}
