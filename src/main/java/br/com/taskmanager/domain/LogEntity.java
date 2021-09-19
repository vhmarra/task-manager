package br.com.taskmanager.domain;

import lombok.Data;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static java.util.UUID.randomUUID;

@Entity
@Data
@Table(name = "log")
public class LogEntity {

    @Id
    @Column(name = "id")
    private String id = randomUUID().toString();

    @Column(name = "description")
    private String description;

    @Column(name = "ip")
    private String ip;

    @Column(name = "date")
    private LocalDateTime date;

    @ManyToOne
    UserEntity user;


}
