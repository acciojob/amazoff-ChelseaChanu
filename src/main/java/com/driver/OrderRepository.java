package com.driver;

import java.util.*;

import org.springframework.stereotype.Repository;

@Repository
public class OrderRepository {
    
    HashMap<String,Order> orderDB = new HashMap<>();
    HashMap<String,DeliveryPartner> partnerDB = new HashMap<>();
    HashMap<String,List<String>> orderPartnerDB = new HashMap<>();

    public void addOrder(Order order){
        orderDB.put(order.getId(),order);
    }

    public void addPartner(String partnerId){
        DeliveryPartner partner = new DeliveryPartner(partnerId);
        partnerDB.put(partnerId,partner);
    }

    public void addOrderPartnerPair(String orderId, String partnerId){
        //This is basically assigning that order to that partnerId
        if(orderDB.containsKey(orderId) && partnerDB.containsKey(partnerId)){
            List<String> orders = new ArrayList<>();
            if(orderPartnerDB.containsKey(partnerId))
                orders = orderPartnerDB.get(partnerId);
            orders.add(orderId);
            orderPartnerDB.put(partnerId,orders);
        }
    }

    public Order getOrderById(String orderId){
        //order should be returned with an orderId.
        return orderDB.get(orderId);
    }

    public DeliveryPartner getPartnerById(String partnerId){
        //deliveryPartner should contain the value given by partnerId
        return partnerDB.get(partnerId);
    }

    public Integer getOrderCountByPartnerId(String partnerId){
        //orderCount should denote the orders given by a partner-id
        Integer orderCount = orderPartnerDB.get(partnerId).size();
        return orderCount;
    }

    public List<String> getOrdersByPartnerId(String partnerId){
        //orders should contain a list of orders by PartnerId
        List<String> orders = new ArrayList<>();
        if(orderPartnerDB.containsKey(partnerId))
            orders = orderPartnerDB.get(partnerId);
        return orders;
    }

    public List<String> getAllOrders(){
        //Get all orders
        return new ArrayList<>(orderDB.keySet());
    }

    public Integer getCountOfUnassignedOrders(){
        Integer countOfOrders = 0;
        //Count of orders that have not been assigned to any DeliveryPartner
        for(String partnerId:orderPartnerDB.keySet()){
            countOfOrders += orderPartnerDB.get(partnerId).size();
        }
        return orderDB.size() - countOfOrders;
    }

    public Integer getOrdersLeftAfterGivenTimeByPartnerId(String time, String partnerId){

        Integer countOfOrders = 0;
        //countOfOrders that are left after a particular time of a DeliveryPartner
        int hour = Integer.parseInt(time.substring(0,2));
        int min = Integer.parseInt(time.substring(3,5));
        int currTime = (hour*60)+min;
        for(String orderId:orderPartnerDB.get(partnerId)){
            if(orderDB.get(orderId).getDeliveryTime()>currTime)
                countOfOrders++;
        }
        return countOfOrders;
    }

    public String getLastDeliveryTimeByPartnerId(String partnerId){
        //Return the time when that partnerId will deliver his last delivery order.
        List<Integer> timeList = new ArrayList<>();
        for(String orderId:orderPartnerDB.get(partnerId)){
            timeList.add(orderDB.get(orderId).getDeliveryTime());
        }
        int lastDeliveryTime = Collections.max(timeList);
        int hour = lastDeliveryTime/60;
        int min = lastDeliveryTime%60;
        return String.format("%d:%d",hour,min);
    }

    public void deletePartnerById(String partnerId){
        //Delete the partnerId
        //And push all his assigned orders to unassigned orders.
        orderPartnerDB.remove(partnerId);
        partnerDB.remove(partnerId);
    }

    public void deleteOrderById(String orderId){
        //Delete an order and also
        // remove it from the assigned order of that partnerId
        orderDB.remove(orderId);
        for(String partnerId: orderPartnerDB.keySet()){
            for(String order: orderPartnerDB.get(partnerId)){
                if(order.equals(orderId)){
                    if(orderPartnerDB.get(partnerId).size()==1)
                        orderPartnerDB.remove(partnerId);
                    else
                        orderPartnerDB.get(partnerId).remove(orderId);
                }
            }
        }
    }
}