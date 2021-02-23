package br.com.taskmanager.domain;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "change_user_password")
public class ChangeUserDataEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "code")
    private String code;

    @Column(name = "date_created")
    private LocalDateTime dateCreated;

    @Column(name = "date_used")
    private LocalDateTime dateUsed;

    @Column(name = "used")
    private Integer used;

    @ManyToOne
    private UserEntity user;

}
