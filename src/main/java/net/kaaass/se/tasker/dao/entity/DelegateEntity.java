package net.kaaass.se.tasker.dao.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.kaaass.se.tasker.util.Constants;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * 委托任务表
 */
@Entity
@Data
@Table(name = "delegate")
@EqualsAndHashCode(of = "id")
public class DelegateEntity {

    @Id
    @GenericGenerator(name = Constants.ID_GENERATOR, strategy = Constants.UUID)
    @GeneratedValue(generator = Constants.ID_GENERATOR)
    private String id;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "task_id")
    private TaskEntity task;

    @ManyToOne
    @JoinColumn(name = "from_eid")
    private EmployeeEntity from;

    @ManyToOne
    @JoinColumn(name = "delegate_to_eid")
    private EmployeeEntity delegateTo;

    @Column(name = "create_time",
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @Generated(GenerationTime.INSERT)
    private Timestamp createTime;
}
