package com.example.c.testwebapi;

public interface OnOprationSuccess
{
    void beforStart();
    void afterFinish();

    void OnSuccess(String result);
    void OnFail(int code,String result);
}