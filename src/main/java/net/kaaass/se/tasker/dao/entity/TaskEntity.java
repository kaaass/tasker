package net.kaaass.se.tasker.dao.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.kaaass.se.tasker.dto.TaskType;
import net.kaaass.se.tasker.dto.TaskStatus;
import net.kaaass.se.tasker.util.Constants;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Set;

/**
 * 任务表
 */
@Entity
@Data
@Table(name = "task")
@EqualsAndHashCode(of = "id")
public class TaskEntity {

    @Id
    @GenericGenerator(name = Constants.ID_GENERATOR, strategy = Constants.UUID)
    @GeneratedValue(generator = Constants.ID_GENERATOR)
    private String id;

    @Column(name = "name",
            length = 20)
    private String name;

    @Column(name = "type")
    private TaskType type;

    @ManyToOne
    @JoinColumn(name = "undertaker_mid")
    private EmployeeEntity undertaker;

    @Column(name = "status")
    private TaskStatus status;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private ProjectEntity project;

    /**
     * 待审核的项目文档
     */
    @ManyToOne
    @JoinColumn(name = "pending_doc_rid")
    private ResourceEntity pending;

    @Column(name = "create_time",
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @Generated(GenerationTime.INSERT)
    private Timestamp createTime;

    /*
     * 外键
     */

    @OneToOne(mappedBy = "task", cascade = CascadeType.ALL)
    private DelegateEntity delegate;

    @ManyToMany
    @JoinTable(
            name = "task_dependency",
            joinColumns = {@JoinColumn(name = "next_tid")},
            inverseJoinColumns = {@JoinColumn(name = "required_tid")}
    )
    private Set<TaskEntity> previous;
}
