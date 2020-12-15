package com.kwin.forum.contants;

public abstract class LoginTicketContent {
    //有效
    public static final int TICKET_ACTIVE = 0;

    //无效
    public static final int TICKET_INACTIVE = 1;

    //默认状态的登录凭证的超时时间
    public static final int DEFAULT_EXPIRED_SECONDS = 3600 * 12;

    //记住状态的登录凭证超时时间
    public static final int REMEMBER_EXPIRED_SECONDS = 3600 * 24 * 100;
}
