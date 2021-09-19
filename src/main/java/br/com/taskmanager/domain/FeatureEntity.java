package br.com.taskmanager.domain;

import lombok.Data;

import javax.persistence.*;


@Entity
@Table(name = "feature")
@Data
public class FeatureEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "feature_name")
    private String name;

    @Column(name = "is_active")
    private Boolean isActive;

}
