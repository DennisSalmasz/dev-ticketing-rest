package com.cyber.entity;

import com.cyber.enums.Gender;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Where;

import javax.persistence.*;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "users")
@Where(clause = "is_deleted=false") //this will automatically be added to each UserRepository query !!
@JsonIgnoreProperties(value = {"hibernateLazyInitializer"},ignoreUnknown = true)
public class User extends BaseEntity{

    private String firstName;
    private String lastName;
    private String userName;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String passWord;

    private Boolean enabled; //checks if user is confirmed through email !!
    private String phone;

    //many roles can be assigned to one user
    @ManyToOne //not @Lazy, @Eager !!
    @JoinColumn(name = "role_id")
    private Role role;

    @Enumerated(EnumType.STRING)
    private Gender gender;
}
