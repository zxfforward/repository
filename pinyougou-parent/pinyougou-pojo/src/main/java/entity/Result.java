package entity;

import com.alibaba.dubbo.common.utils.StringUtils;

import java.io.Serializable;

/**
 * Created by crowndint on 2018/10/14.
 */
public class Result implements Serializable {

    private Boolean success;
    private String message;

    public Result() {
    }

    public Result(Boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
