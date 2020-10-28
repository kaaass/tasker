package net.kaaass.se.tasker.mapper;

import net.kaaass.se.tasker.controller.request.EmployeeRequest;
import net.kaaass.se.tasker.controller.request.UserRegisterRequest;
import net.kaaass.se.tasker.controller.request.UserRequest;
import net.kaaass.se.tasker.dao.entity.UserEntity;
import net.kaaass.se.tasker.dto.UserDto;
import net.kaaass.se.tasker.vo.UserVo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * 用户相关数据结构映射器
 */
@Mapper(componentModel = "spring", uses = {UserTransform.class})
public interface UserMapper {

    UserVo dtoToVo(UserDto authDto);

    @Mapping(source = "roles", target = "roles", qualifiedByName = "mapRoles")
    UserDto entityToDto(UserEntity authEntity);

    default EmployeeRequest mapEmployeeRequest(UserRegisterRequest registerRequest) {
        return new EmployeeRequest(
                registerRequest.getName(),
                registerRequest.getType());
    }

    default UserRequest mapUserRequest(UserRegisterRequest registerRequest) {
        return new UserRequest(
                registerRequest.getUsername(),
                registerRequest.getPassword());
    }
}
