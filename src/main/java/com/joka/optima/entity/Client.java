package com.joka.optima.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "clients")
public class Client extends BaseEntity{

    @Column(name = "name")
    private String name;

    @Column(name = "last_name")
    private String lastName;
}
