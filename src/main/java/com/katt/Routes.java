package com.katt;


public class Routes {
    @WebRoute(path = "/")
    public String mainPage(){
        return "from main page";
    }

    @WebRoute(path = "/test1")
    public String test1(){
        return "from test 1";
    }

    @WebRoute(path = "/test2")
    public String test2(){
        return "from test 2";
    }
}