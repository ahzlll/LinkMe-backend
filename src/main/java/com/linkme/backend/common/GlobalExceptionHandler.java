package com.linkme.backend.common;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

/**
 * 全局异常处理器
 * 
 * 功能描述：
 * - 统一处理应用程序中的异常
 * - 提供统一的错误响应格式
 * 
 * @author Ahz, riki
 * @version 1.0
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    /**
     * 处理通用异常
     * 
     * @param e 异常对象
     * @return 错误响应
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<R<String>> handleException(Exception e) {
        // 打印完整的错误堆栈，便于调试
        System.err.println("服务器内部错误: " + e.getMessage());
        e.printStackTrace();
        
        // 返回错误信息（开发环境可以返回详细错误，生产环境应该返回通用错误）
        String errorMessage = "服务器内部错误";
        if (e.getMessage() != null) {
            errorMessage += ": " + e.getMessage();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(R.fail(errorMessage));
    }
    
    /**
     * 处理空指针异常
     * 
     * @param e 空指针异常
     * @return 错误响应
     */
    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<R<String>> handleNullPointerException(NullPointerException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(R.fail("请求参数不能为空"));
    }
    
    /**
     * 处理非法参数异常
     * 
     * @param e 非法参数异常
     * @return 错误响应
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<R<String>> handleIllegalArgumentException(IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(R.fail("请求参数错误: " + e.getMessage()));
    }
    
    /**
     * 处理404异常
     * 
     * @param e 404异常
     * @return 错误响应
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<R<String>> handleNoHandlerFoundException(NoHandlerFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(R.fail("请求的资源不存在"));
    }
}