package org.liuyehcf.annotation.source;

@Builder
public class UserDTO {
    private String firstName;

    private String lastName;

    private Integer age;

    private String address;

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

    public static void main(String[] args) {
        UserDTO.UserDTOBuilder builder = new UserDTO.UserDTOBuilder();

        UserDTO userDTO = builder.setFirstName("贺")
                .setLastName("辰枫")
                .setAge(25)
                .setAddress("杭州")
                .build();

        System.out.println(userDTO);
    }
}
