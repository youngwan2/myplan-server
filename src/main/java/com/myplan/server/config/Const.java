package com.myplan.server.config;

public enum Const {
    REFRESH_EXP(60 * 60 * 24 * 30* 1000L),
    ACCESS_EXP(60 * 60 * 10 * 1000L);

    Const(long exp){}
}
