package com.alexcorp.bloggers.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Data
@NoArgsConstructor
@Entity
@Table(name = "CONFIRM_CODE")
public class ConfirmCode {

    @Id
    @GeneratedValue(generator = "increment")
    @GenericGenerator(name = "increment", strategy = "increment")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private Integer code;

    @Column(length = 16)
    @Enumerated(EnumType.STRING)
    private ConfirmType type;


    public enum ConfirmType {
        SIGNUP
    }
}
