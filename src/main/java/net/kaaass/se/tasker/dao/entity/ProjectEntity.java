package net.kaaass.se.tasker.dao.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.kaaass.se.tasker.dto.ProjectStatus;
import net.kaaass.se.tasker.util.Constants;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Set;

/**
 * 项目表
 */
@Entity
@Data
@Table(name = "project")
@EqualsAndHashCode(of = "id")
public class ProjectEntity {

    @Id
    @GenericGenerator(name = Constants.ID_GENERATOR, strategy = Constants.UUID)
    @GeneratedValue(generator = Constants.ID_GENERATOR)
    private String id;

    @Column(name = "name",
            length = 20)
    private String name;

    @ManyToOne
    @JoinColumn(name = "undertaker_mid")
    private ManagerEntity undertaker;

    @Column(name = "status")
    private ProjectStatus status;

    @ManyToOne
    @JoinColumn(name = "doc_rid")
    private ResourceEntity doc;

    @Column(name = "create_time",
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @Generated(GenerationTime.INSERT)
    private Timestamp createTime;

    /*
     * 外键
     */

    @OneToMany(mappedBy = "project",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    private Set<TaskEntity> tasks;
}
