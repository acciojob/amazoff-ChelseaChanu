package com.driver;

public class Order {

    private String id;
    private int deliveryTime;

    public Order(String id, String deliveryTime) {

        // The deliveryTime has to converted from string to int and then stored in the attribute
        //deliveryTime  = HH*60 + MM
        this.id = id;
        int hour = Integer.parseInt(deliveryTime.substring(0,2));
        int min = Integer.parseInt(deliveryTime.substring(3, 5));
        this.deliveryTime = hour*60+min;

    }

    public String getId() {
        //int orderId = Integer.parseInt(id);
        return id;
    }

    public int getDeliveryTime() {return deliveryTime;}
}
