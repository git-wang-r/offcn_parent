package com.offcn.user.exception;

import com.offcn.user.enums.UserExceptionEnum;

//自定义异常类
public class UserException extends RuntimeException{
    public UserException(UserExceptionEnum exceptionEnum) {
        super(exceptionEnum.getMsg());
    }
}
