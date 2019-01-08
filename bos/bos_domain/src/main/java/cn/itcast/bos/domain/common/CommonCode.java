package cn.itcast.bos.domain.common;

import lombok.ToString;

/**
 * @Author: mrt.
 * @Description:
 * @Date:Created in 2018/1/24 18:33.
 * @Modified By:
 */

@ToString
public enum CommonCode implements ResultCode{

    SUCCESS(true,"操作成功！"),
    FAIL(false,"操作失败！"),
    UNAUTHENTICATED(false,"此操作需要登陆系统！"),
    UNAUTHORISE(false,"权限不足，无权操作！"),
    INVALID_PARAM(false,"参数错误！"),
    SERVER_ERROR(false,"抱歉，系统繁忙，请稍后重试！");

    //操作是否成功
    boolean success;
    //提示信息
    String message;
    private CommonCode(boolean success, String message){
        this.success = success;

        this.message = message;
    }

    @Override
    public boolean success() {
        return success;
    }

    @Override
    public String message() {
        return message;
    }


}
