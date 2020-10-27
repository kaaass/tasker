package net.kaaass.se.tasker.service.impl;

import lombok.extern.slf4j.Slf4j;
import net.kaaass.se.tasker.dao.entity.ResourceEntity;
import net.kaaass.se.tasker.dao.repository.ResourceRepository;
import net.kaaass.se.tasker.dto.ResourceDto;
import net.kaaass.se.tasker.dto.ResourceType;
import net.kaaass.se.tasker.exception.BadRequestException;
import net.kaaass.se.tasker.exception.NotFoundException;
import net.kaaass.se.tasker.mapper.ResourceMapper;
import net.kaaass.se.tasker.service.ResourceService;
import net.kaaass.se.tasker.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 资源相关服务的具体实现
 */
@Service
@Slf4j
public class ResourceServiceImpl implements ResourceService {

    @Autowired
    private ResourceRepository resourceRepository;

    @Autowired
    private ResourceMapper resourceMapper;

    @Autowired
    private UserService userService;

    public Optional<ResourceEntity> getEntity(String rid) {
        return resourceRepository.findById(rid);
    }

    @Override
    public Optional<ResourceDto> createByUrl(String url, ResourceType type, String uid) throws NotFoundException, BadRequestException {
        var entity = new ResourceEntity();
        var uploader = userService.getEntity(uid)
                .orElseThrow(() -> new NotFoundException("用户不存在！"));
        entity.setType(type);
        entity.setUrl(url);
        entity.setUploader(uploader);
        try {
            var result = resourceRepository.save(entity);
            return Optional.ofNullable(resourceMapper.entityToDto(result));
        } catch (IllegalArgumentException e) {
            log.error("插入资源失败", e);
        } catch (DataIntegrityViolationException e) {
            throw new BadRequestException("已经添加过这个图片！");
        }
        return Optional.empty();
    }

    @Override
    public Optional<ResourceDto> getResource(String rid) {
        return getEntity(rid).map(resourceMapper::entityToDto);
    }

    @Override
    public List<ResourceDto> getAllResources(Pageable page) {
        return resourceRepository.findAllByOrderByUploadTimeDesc(page)
                .stream()
                .map(resourceMapper::entityToDto)
                .collect(Collectors.toList());
    }
}
