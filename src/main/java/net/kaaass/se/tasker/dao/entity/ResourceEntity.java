package net.kaaass.se.tasker.dao.entity;

import lombok.Data;
import net.kaaass.se.tasker.util.Constants;
import net.kaaass.se.tasker.util.ResourceType;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Data
@Table(name = "resource")
public class ResourceEntity {
    @Id
    @GenericGenerator(name = Constants.ID_GENERATOR, strategy = Constants.UUID)
    @GeneratedValue(generator = Constants.ID_GENERATOR)
    private String id;

    @Column(name = "type")
    private ResourceType type;

    @Column(name = "url", unique = true)
    private String url;

    @ManyToOne
    @JoinColumn(name = "uploader_uid")
    private UserAuthEntity uploader;

    @Column(name = "upload_time",
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @Generated(GenerationTime.INSERT)
    private Timestamp uploadTime;
}
