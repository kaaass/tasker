package net.kaaass.se.tasker.dao.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.kaaass.se.tasker.util.Constants;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Set;

/**
 * 经理表
 */
@Entity
@Data
@Table(name = "manager")
@EqualsAndHashCode(of = "id")
public class ManagerEntity {

    @Id
    @GenericGenerator(name = Constants.ID_GENERATOR, strategy = Constants.UUID)
    @GeneratedValue(generator = Constants.ID_GENERATOR)
    private String id;

    @Column(name = "name",
            length = 20)
    private String name;

    @OneToOne
    @JoinColumn(name = "uid", unique = true)
    private UserAuthEntity user;

    @OneToMany(mappedBy = "manager",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    private Set<EmployeeEntity> employeeGroup;
}
