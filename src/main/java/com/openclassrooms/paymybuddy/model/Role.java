package com.openclassrooms.paymybuddy.model;

import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
//@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode  //used for equals unit test in User
@Entity
@Table(name = "role")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String rolename;
    //Don't need bidirectional JPA mapping on roles, only needed on User...
/*    @ManyToMany(mappedBy = "roles") //load it on-demand (i.e. lazily) when we call getUsers()
    private Set<User> users;
*/
}


