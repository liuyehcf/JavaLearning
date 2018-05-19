package org.liuyehcf.test.annotation;

import org.liuyehcf.annotation.source.annotation.AllArgsConstructor;
import org.liuyehcf.annotation.source.annotation.Builder;
import org.liuyehcf.annotation.source.annotation.Data;
import org.liuyehcf.annotation.source.annotation.NoArgsConstructor;


@Data
@NoArgsConstructor
//@AllArgsConstructor
//@Builder
public class TestUserDTO {
    private String firstName;

    private String lastName;

    private Integer age;

    private String address;

    public TestUserDTO(String firstName, String lastName, Integer age, String address) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.address = address;
    }

//    public static void main(String[] args) {
//        TestUserDTO.TestUserDTOBuilder builder = TestUserDTO.builder();
//
//        TestUserDTO userDTO = builder.firstName("辰枫")
//                .lastName("贺")
//                .age(25)
//                .address("杭州")
//                .build();
//
//        System.out.println(userDTO);
//    }


    @Override
    public String toString() {
        return "firstName: " + firstName + "\n" +
                "lastName: " + lastName + "\n" +
                "age: " + age + "\n" +
                "address: " + address;
    }
}
