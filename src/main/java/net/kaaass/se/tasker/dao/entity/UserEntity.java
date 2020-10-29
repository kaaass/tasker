package net.kaaass.se.tasker.dao.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.kaaass.se.tasker.util.Constants;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Set;

/**
 * 用户鉴权对象表
 */
@Entity
@Data
@Table(name = "user_auth")
@EqualsAndHashCode(of = "id")
public class UserEntity {
    @Id
    @GenericGenerator(name = Constants.ID_GENERATOR, strategy = Constants.UUID)
    @GeneratedValue(generator = Constants.ID_GENERATOR)
    private String id;

    @Column(name = "username",
            length = 20,
            unique = true)
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "roles")
    private String roles;

    @Column(name = "enable",
            columnDefinition = "BOOLEAN DEFAULT TRUE")
    boolean enable = true;

    @Column(name = "validate",
            columnDefinition = "BOOLEAN DEFAULT FALSE")
    boolean validate = false;

    @Column(name = "register_time",
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP",
            insertable = false,
            updatable = false)
    @Generated(GenerationTime.INSERT)
    private Timestamp registerTime;

    @Column(name = "last_login_time",
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Timestamp lastLoginTime;

    @ManyToOne
    @JoinColumn(name = "avatar_rid")
    private ResourceEntity avatar;

    /*
     * 外键相关
     */

    @OneToMany(mappedBy = "uploader",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    private Set<ResourceEntity> resources;

    @OneToOne(mappedBy = "user",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    private EmployeeEntity employee;

    @OneToOne(mappedBy = "user",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    private ManagerEntity manager;
}
