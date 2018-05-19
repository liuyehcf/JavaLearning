import org.liuyehcf.annotation.source.annotation.AllArgsConstructor;
import org.liuyehcf.annotation.source.annotation.Builder;
import org.liuyehcf.annotation.source.annotation.Data;

@AllArgsConstructor
@Data
@Builder
public class UserDTO {
    private String firstName;

    private String lastName;

    private Integer age;

    private String address;

    public static void main(String[] args) {
        UserDTO userDTO = UserDTO.builder()
                .firstName("辰枫")
                .lastName("贺")
                .age(25)
                .address("杭州")
                .build();

        System.out.println(userDTO);
    }

    @Override
    public String toString() {
        return "UserDTO{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", age=" + age +
                ", address='" + address + '\'' +
                '}';
    }
}
