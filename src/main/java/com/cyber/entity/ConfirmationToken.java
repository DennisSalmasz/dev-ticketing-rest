package com.cyber.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Where;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "confirmation_email")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Where(clause = "is_deleted=false")
public class ConfirmationToken extends BaseEntity{

    private String token;

    @OneToOne(targetEntity = User.class) //uni-directional 1-2-1 relationship
    @JoinColumn(name = "user_id")
    private User user;

    private LocalDate expiryDate;

    public Boolean isTokenValid(LocalDate date){
        LocalDate now = LocalDate.now();
        return date.isEqual(now) || date.isEqual(now.plusDays(1));
    }

    public ConfirmationToken(User user){
        this.user = user;
        expiryDate = LocalDate.now().plusDays(1);
        token = UUID.randomUUID().toString();
    }
}
