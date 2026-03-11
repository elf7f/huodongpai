package com.huodongpai.common.constant;

public final class RedisKeyConstants {

    public static final String LOGIN_TOKEN_PREFIX = "login:token:";
    public static final String EVENT_HOT_LIST_KEY = "event:hot:list";
    public static final String EVENT_DETAIL_PREFIX = "event:detail:";

    private RedisKeyConstants() {
    }

    public static String buildLoginTokenKey(String token) {
        return LOGIN_TOKEN_PREFIX + token;
    }

    public static String buildEventDetailKey(Long eventId) {
        return EVENT_DETAIL_PREFIX + eventId;
    }
}
