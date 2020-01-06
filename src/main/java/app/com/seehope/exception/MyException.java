package app.com.seehope.exception;

public class MyException extends Exception {

    private String msg;

    private Integer statusCode;

    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }

    public String getMsg() {
        return msg;
    }

    public void setMessage(String msg) {
        this.msg = msg;
    }

    public MyException() {
    }

    public MyException(Integer statusCode,String msg ) {
        this.msg = msg;
        this.statusCode = statusCode;
    }

    @Override
    public String toString() {
        return "ExceptionUtils{" +
                "msg='" + msg + '\'' +
                ", statusCode=" + statusCode +
                '}';
    }

}
