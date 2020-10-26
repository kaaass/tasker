package net.kaaass.se.tasker.service;

import net.kaaass.se.tasker.dao.entity.ResourceEntity;
import net.kaaass.se.tasker.dto.ResourceDto;
import net.kaaass.se.tasker.exception.BadRequestException;
import net.kaaass.se.tasker.exception.NotFoundException;
import net.kaaass.se.tasker.dto.ResourceType;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * 资源管理相关服务
 */
public interface ResourceService {

    /**
     * 通过 url 创建资源
     */
    Optional<ResourceDto> createByUrl(String url, ResourceType type, String uid) throws NotFoundException, BadRequestException;

    /**
     * 根据 rid 获得资源
     */
    Optional<ResourceDto> getResource(String rid);

    /**
     * 根据 rid 获得资源实体
     */
    Optional<ResourceEntity> getEntity(String rid);

    /**
     * 获得全部资源
     */
    List<ResourceDto> getAllResources(Pageable page);
}
