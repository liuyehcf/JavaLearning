package org.liuyehcf.spring.validate.dto;

import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.Range;
import org.liuyehcf.spring.validate.group.Create;
import org.liuyehcf.spring.validate.group.Update;

/**
 * @author hechenfeng
 * @date 2018/7/10
 */
public class UserDTO {
    @NotBlank(groups = {Create.class, Update.class}, message = "name cannot be blank")
    private String name;

    @Range(min = 1, max = 100, groups = Create.class, message = "age must between 1 and 100")
    private Integer age;

    @NotBlank(groups = Update.class, message = "address cannot be blank")
    private String address;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
}
