package net.kaaass.se.tasker.conf.handler;

import lombok.extern.slf4j.Slf4j;
import net.kaaass.se.tasker.controller.response.GlobalResponse;
import net.kaaass.se.tasker.exception.BadRequestException;
import net.kaaass.se.tasker.exception.BaseException;
import net.kaaass.se.tasker.exception.ForbiddenException;
import net.kaaass.se.tasker.util.StatusEnum;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * 通用请求处理类
 *
 * 用于接管所有API请求，统一请求返回格式与错误响应
 */
@Slf4j
@ControllerAdvice
public class GlobalResponseHandler implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter methodParameter, Class<? extends HttpMessageConverter<?>> aClass) {
        // 判断是否需要使用 GlobalResponse 接管返回格式
        final var returnType = methodParameter.getParameterType();
        return !returnType.isAssignableFrom(GlobalResponse.class)
                && !returnType.equals(ResponseEntity.class);
    }

    @Override
    public Object beforeBodyWrite(Object o, MethodParameter methodParameter, MediaType mediaType, Class<? extends HttpMessageConverter<?>> aClass, ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse) {
        // GlobalResponse 包装请求返回
        final var returnType = methodParameter.getParameterType();
        // 处理 void 返回方法
        if (returnType.equals(Void.TYPE)) {
            return GlobalResponse.success(null);
        }
        // 忽略非 Json 返回
        if (!mediaType.includes(MediaType.APPLICATION_JSON)) {
            return o;
        }
        return GlobalResponse.success(o);
    }

    @ResponseBody
    @ExceptionHandler({BaseException.class})
    public <T> GlobalResponse<T> handleBaseException(BaseException e) {
        log.info("发生异常", e); // 部分问题用exception丢出，较为常见
        return GlobalResponse.fail(e.getStatus(), e.getMessage());
    }

    @ResponseBody
    @ExceptionHandler({HttpMessageNotReadableException.class, MissingServletRequestParameterException.class})
    public <T> GlobalResponse<T> handleBadRequestException(Exception e) {
        log.info("请求格式错误", e);
        String message = e.getMessage();
        if (e instanceof HttpMessageNotReadableException) {
            message = "请求体格式错误";
        }
        return handleBaseException(new BadRequestException(message)); // e.getMessage()不够友好
    }

    @ResponseBody
    @ExceptionHandler({AccessDeniedException.class})
    public <T> GlobalResponse<T> handleAccessDeniedException(AccessDeniedException e) {
        log.info("访问未登录", e);
        return handleBaseException(new ForbiddenException(null)); // e.getMessage()不够友好
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler({Exception.class})
    public <T> GlobalResponse<T> handleException(Exception e) {
        log.error("发生未知异常", e);
        var cause = e.getCause();
        if (cause != null && BaseException.class.isAssignableFrom(cause.getClass())) {
            return handleBaseException((BaseException) cause);
        }
        // 不应该暴露栈信息给Rest接口
        return GlobalResponse.fail(StatusEnum.INTERNAL_ERROR, e.getMessage());
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler({Throwable.class})
    public <T> GlobalResponse<T> handleException(Throwable e) {
        log.error("发生Throwable错误", e);
        // 不应该暴露栈信息给Rest接口
        return GlobalResponse.fail(StatusEnum.INTERNAL_ERROR, e.getMessage());
    }
}
