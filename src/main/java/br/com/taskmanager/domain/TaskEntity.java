package br.com.taskmanager.domain;

import lombok.Data;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.io.File;
import java.time.LocalDateTime;

@Entity
@Table(name = "task")
@Data
public class TaskEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "date_created")
    private LocalDateTime dateCreated;

    @Column(name = "description")
    private String taskDescription;

    @Column(name = "date_end")
    private LocalDateTime dateEnd = LocalDateTime.MAX;

    @Column(name = "finalized")
    private Boolean finalized;

    @Column(name = "priority")
    private Integer priority = 0;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    UserEntity user;

    public String toStringNoUser() {
        return "Task {" +
                "id=" + id +
                ", dateCreated=" + dateCreated +
                ", taskDescription='" + taskDescription + '\'' +
                ", dateEnd=" + dateEnd +
                ", finalized=" + finalized +
                ", priority=" + priority +
                '}';
    }
}
