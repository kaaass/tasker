package net.kaaass.se.tasker.dao.entity;

import lombok.Data;
import net.kaaass.se.tasker.dto.EmployeeType;
import net.kaaass.se.tasker.util.Constants;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * 员工表
 */
@Entity
@Data
@Table(name = "employee")
public class EmployeeEntity {

    @Id
    @GenericGenerator(name = Constants.ID_GENERATOR, strategy = Constants.UUID)
    @GeneratedValue(generator = Constants.ID_GENERATOR)
    private String id;

    @Column(name = "name",
            length = 20)
    private String name;

    @Column(name = "type")
    private EmployeeType type;

    @OneToOne
    @JoinColumn(name = "uid", unique = true)
    private UserAuthEntity user;

    @ManyToOne
    @JoinColumn(name = "manager_id")
    private ManagerEntity manager;
}
