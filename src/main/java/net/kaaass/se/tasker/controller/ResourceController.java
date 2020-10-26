package net.kaaass.se.tasker.controller;

import net.kaaass.se.tasker.dto.ResourceDto;
import net.kaaass.se.tasker.exception.BadRequestException;
import net.kaaass.se.tasker.exception.NotFoundException;
import net.kaaass.se.tasker.mapper.ResourceMapper;
import net.kaaass.se.tasker.service.ResourceService;
import net.kaaass.se.tasker.util.Constants;
import net.kaaass.se.tasker.dao.entity.ResourceType;
import net.kaaass.se.tasker.util.StringUtils;
import net.kaaass.se.tasker.vo.ResourceVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/resource")
@PreAuthorize("isAuthenticated()")
public class ResourceController extends BaseController {

    @Value("${file.uploadFolder}")
    private String uploadFolder;

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private ResourceMapper resourceMapper;

    @GetMapping("/")
    @PreAuthorize("hasRole('ADMIN')")
    public List<ResourceDto> getAllResource(Pageable page) {
        return resourceService.getAllResources(page);
    }

    @PostMapping("/")
    @PreAuthorize("hasRole('ADMIN')")
    public ResourceVo addNetworkResource(@RequestParam String url, @RequestParam String type)
            throws BadRequestException, NotFoundException {
        ResourceType resourceType;
        try {
            resourceType = ResourceType.valueOf(type);
        } catch (IllegalArgumentException ignore) {
            throw new BadRequestException("资源类型不存在！");
        }
        return resourceService.createByUrl(url, resourceType, getUid())
                .map(resourceMapper::resourceDtoToVo).orElseThrow();
    }

    @PutMapping("/image/")
    @PreAuthorize("authenticated")
    public ResourceVo uploadImage(@RequestParam MultipartFile file) throws BadRequestException, IOException, NotFoundException {
        if (file.isEmpty()) {
            throw new BadRequestException("无文件传送！");
        }

        var originalFilename = file.getOriginalFilename();
        var suffix = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
        var newFileName = StringUtils.uuid() + "." + suffix;
        // TODO 检查suffix

        File destFile = new File(uploadFolder + Constants.IMAGE_PATH + newFileName);
        file.transferTo(destFile);

        var url = Constants.UPLOAD_BASE + Constants.IMAGE_PATH + newFileName;
        return resourceService.createByUrl(url, ResourceType.IMAGE, getUid())
                .map(resourceMapper::resourceDtoToVo).orElseThrow();
    }

    @PutMapping("/document/")
    @PreAuthorize("authenticated")
    public ResourceVo uploadDocument(@RequestParam MultipartFile file) throws BadRequestException, IOException, NotFoundException {
        if (file.isEmpty()) {
            throw new BadRequestException("无文件传送！");
        }

        var originalFilename = file.getOriginalFilename();
        var suffix = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
        var newFileName = StringUtils.uuid() + "." + suffix;
        // TODO 检查suffix

        File destFile = new File(uploadFolder + Constants.IMAGE_PATH + newFileName);
        file.transferTo(destFile);

        var url = Constants.UPLOAD_BASE + Constants.DOCUMENT_PATH + newFileName;
        return resourceService.createByUrl(url, ResourceType.DOCUMENT, getUid())
                .map(resourceMapper::resourceDtoToVo).orElseThrow();
    }
}
