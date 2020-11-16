package com.alexcorp.bloggers.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
@Table(name = "YOU_TUBE_CHANNEL")
public class InstagramAccount {

    @Id
    @GeneratedValue(generator = "increment")
    @GenericGenerator(name = "increment", strategy = "increment")
    private Long id;

    private Integer followersCount;
    private Integer mediaCount;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    protected LocalDateTime lastUpdate;

    private String accessToken;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User blogger;
}
