package com.cyber.dto;

import com.cyber.enums.Gender;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@JsonIgnoreProperties(value = {"hibernateLazyInitializer"},ignoreUnknown = true)
public class UserDTO {

    private Long id;

    private String firstName;
    private String lastName;
    private String userName; // unique property --- PK in DB!!

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String passWord;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String confirmPassword;

    private boolean enabled;
    private String phone;
    private RoleDTO role;
    private Gender gender;

}
