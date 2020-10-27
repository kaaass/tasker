package net.kaaass.se.tasker.dao.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.kaaass.se.tasker.dto.TaskType;
import net.kaaass.se.tasker.util.Constants;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Set;

/**
 * 员工表
 */
@Entity
@Data
@Table(name = "employee")
@EqualsAndHashCode(of = "id")
public class EmployeeEntity {

    @Id
    @GenericGenerator(name = Constants.ID_GENERATOR, strategy = Constants.UUID)
    @GeneratedValue(generator = Constants.ID_GENERATOR)
    private String id;

    @Column(name = "name",
            length = 20)
    private String name;

    @Column(name = "type")
    private TaskType type;

    @OneToOne
    @JoinColumn(name = "uid", unique = true)
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "manager_id")
    private ManagerEntity manager;

    /*
     * 外键
     */

    @OneToMany(mappedBy = "undertaker",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    private Set<TaskEntity> ownedTasks;

    @OneToMany(mappedBy = "from",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    private Set<DelegateEntity> giveOutDelegates;

    @OneToMany(mappedBy = "delegateTo",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    private Set<DelegateEntity> ownedDelegates;
}
