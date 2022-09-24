package net.softel.ai.classify.util;

public class ResultFactory {

    private ResultFactory() {
        throw new IllegalStateException("Instance not allowed");
    }

    public static <T> Result<T> getSuccessResult(T data){
        return new Result(true,data);
    }

    public static <T> Result<T> getSuccessResult(T data, String msg){
        return new Result(true,data, msg);
    }

    public static <T> Result<T> getSuccessResult(String msg){
        return new Result(true,msg);
    }

    public static <T> Result<T> getFailResult(String msg){
        return new Result(false,msg);
    }

    public static <T> Result<T> getFailResult(T data, String msg){
        return new Result(false,data,msg);
    }

}
