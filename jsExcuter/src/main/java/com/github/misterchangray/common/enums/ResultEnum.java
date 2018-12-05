package com.github.misterchangray.common.enums;

/**
 * 错误消息，请注意此类只应该包含错误消息：
 *
 * ID格式：
 * 1 01 001
 * 1 代表错误等级
 * 01 代表第01个模块
 * 001 代表第10模块内的第一个错误消息序号
 *
 * @author Rui.Zhang/misterchangray@hotmail.com
 * @author Created on 3/20/2018.
 */
public enum ResultEnum {
    SUCCESS(0, "成功"), //成功
    FAILURE(1, "失败"), //失败
    INVALID_REQUEST(101001, "参数字段错误或参数格式错误"), //无效请求,请求有格式或数据错误
    NEED_AUTH(101002, "需要权限认证"), //需要认证,没有权限
    DISABLED(101003, "资源已被禁用"),//资源已被禁用
    INVALID(101004, "资源无效"),//资源无效
    NOT_FOUND(101005, "资源不存在"),//资源不存在
    EXIST(101006, "资源已存在"),//资源已存在
    GONE(1001007, "资源已被删除"), //资源已经被删除
    SERVER_ERROR(101008, "系统未知错误"), //服务器错误
    AccountBalanceInsufficient(9476825, "余额不足;下单失败"),
    RESTOREFAILURE(100001, "恢复失败"),
    HASSTOPPED(100002, "停止"),
    HASSTARTED(100003, "启动"),
    NOTREPEATSTART(100004, "请不要重复启动"),
    INSERVICE(100005, "客户端运行中"),
    OFFLINE(100006, "客户端离线"),
    BOOTFAILURE(100007, "启动失败"),
    STARTUPSUCCESS(100008, "启动成功"),
    STOPFAILURECLIENTOFFLINE(100009, "停止失败,客户端离线"),
    THREADNOTCLOSEORNOTFOUND(100010, "找不到该线程，或该线程已经关闭"),
    IPNORUNNINGTHREADS(100011, "该IP无可运行线程"),
    NOTFOUNDTHREAD(100012, "找不到该线程"),
    IPNONE(100013, "IP为空"),
    IDNONE(100014, "ID为空"),
    VALIDATEPARAMETER(100015, "验证失败,请确认提交参数是否正确"),
    DONTRESUBMIT(100016, "不要重复提交"),
    NONETHREADRESOURCES(100017, "找不到线程资源"),
    STRATEGYNONE(100018,"策略不存在"),
    CONFIGNONE(100019,"配置不存在"),
    OFFLINE_ONE(100020,"离线");
    
    private int code;
    private String msg;

    public int getCode() {
        return code;
    }
    public String getMsg() {
        return msg;
    }
    private ResultEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
