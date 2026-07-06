package com.easyjava.controller;

import com.easyjava.entity.vo.ResponseVO;
import com.easyjava.enums.ResponseCodeEnum;
import com.easyjava.exception.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.net.BindException;
/*这是一个全局异常处理器 AGlobalExceptionHandlerController，它统一处理应用中抛出的各种异常，确保返回统一的错误响应格式*/
@ResponseBody
@ControllerAdvice
public class AGlobalExceptionHandlerController  {
    protected static final String STATUC_ERROR = "error";

    private static final Logger logger = LoggerFactory.getLogger(AGlobalExceptionHandlerController.class);

    @ExceptionHandler(value = Exception.class)
    Object handleException(Exception e, HttpServletRequest request) {
        logger.error("请求错误，请求地址{}，错误信息：", request.getRequestURL(), e);
        ResponseVO ajaxResponse = new ResponseVO();

        if (e instanceof NoHandlerFoundException) {
            ajaxResponse.setCode(ResponseCodeEnum.CODE_404.getCode());
            ajaxResponse.setInfo(ResponseCodeEnum.CODE_404.getMsg());
            ajaxResponse.setStatus(STATUC_ERROR);
        } else if (e instanceof BusinessException) {
            BusinessException biz = (BusinessException) e;
            ajaxResponse.setCode(biz.getCode() == null ? ResponseCodeEnum.CODE_600.getCode() : biz.getCode());
            ajaxResponse.setInfo(biz.getMessage());
            ajaxResponse.setStatus(STATUC_ERROR);
        } else if (e instanceof ConstraintViolationException) {
            // 专门处理验证异常，显示具体的验证错误信息
            ConstraintViolationException cve = (ConstraintViolationException) e;
            StringBuilder errorMsg = new StringBuilder();
            for (ConstraintViolation<?> violation : cve.getConstraintViolations()) {
                if (errorMsg.length() > 0) {
                    errorMsg.append("; ");
                }
                errorMsg.append(violation.getMessage());
            }
            ajaxResponse.setCode(ResponseCodeEnum.CODE_600.getCode());
            ajaxResponse.setInfo(errorMsg.toString()); // 显示具体的验证错误信息
            ajaxResponse.setStatus(STATUC_ERROR);
        } else if (e instanceof BindException || e instanceof MethodArgumentNotValidException) {
            ajaxResponse.setCode(ResponseCodeEnum.CODE_600.getCode());
            ajaxResponse.setInfo(ResponseCodeEnum.CODE_600.getMsg());
            ajaxResponse.setStatus(STATUC_ERROR);
        } else if (e instanceof DuplicateKeyException) {
            ajaxResponse.setCode(ResponseCodeEnum.CODE_601.getCode());
            ajaxResponse.setInfo(ResponseCodeEnum.CODE_601.getMsg());
            ajaxResponse.setStatus(STATUC_ERROR);
        } else {
            ajaxResponse.setCode(ResponseCodeEnum.CODE_500.getCode());
            ajaxResponse.setInfo(ResponseCodeEnum.CODE_500.getMsg());
            ajaxResponse.setStatus(STATUC_ERROR);
        }
        return ajaxResponse;
    }
}