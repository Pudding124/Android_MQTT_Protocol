package a06.cse.ntou.androidmqtt;

/**
 * Created by surpr on 2017/11/27.
 */
public class OrderRespondBean {

    private String id;
    private String orderStatus;
    private int orderSerialNumber;

    public void setId(String id) {
        this.id = id;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public void setOrderSerialNumber(int orderSerialNumber) {
        this.orderSerialNumber = orderSerialNumber;
    }

    public String getId(){
        return id;
    }

    public String getOrderStatus(){
        return orderStatus;
    }

    public int getOrderSerialNumber(){
        return orderSerialNumber;
    }
}