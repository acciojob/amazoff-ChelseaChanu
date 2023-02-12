package com.driver;

import java.util.*;

import org.springframework.stereotype.Repository;

@Repository
public class OrderRepository {
    
    HashMap<String,Order> orderDB;
    HashMap<String,DeliveryPartner> partnerDB;
    HashMap<String,List<String>> orderPartnerDB;
    HashMap<String,String> orderPartnerPair;

    public OrderRepository(){
        this.orderDB = new HashMap<>();
        this.partnerDB = new HashMap<>();
        this.orderPartnerDB = new HashMap<>();
        this.orderPartnerPair = new HashMap<>();
    }

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
            orderPartnerPair.put(orderId,partnerId);
            DeliveryPartner partner = partnerDB.get(partnerId);
            partner.setNumberOfOrders(orders.size());
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
        Integer countOfOrder = 0 ;
        if(orderPartnerDB.containsKey(partnerId))
            countOfOrder = orderPartnerDB.get(partnerId).size();
        return countOfOrder;
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
        Integer countOfOrders = orderPartnerPair.size();
        //Count of orders that have not been assigned to any DeliveryPartner
        return orderDB.size() - countOfOrders;
    }

    public Integer getOrdersLeftAfterGivenTimeByPartnerId(String time, String partnerId){

        Integer countOfOrders = 0;
        //countOfOrders that are left after a particular time of a DeliveryPartner
        Integer hour = Integer.parseInt(time.substring(0,2));
        Integer min = Integer.parseInt(time.substring(3));
        Integer currTime = (hour*60)+min;
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
        String hourInStr = String.valueOf(hour);
        String minInStr = String.valueOf(min);
        if(hourInStr.length()==1){
            hourInStr = String.format("0%s",hourInStr);
        }
        if(minInStr.length()==1){
            minInStr = String.format("0%s",minInStr);
        }
        return String.format("%s:%s",hourInStr,minInStr);
    }

    public void deletePartnerById(String partnerId){
        //Delete the partnerId
        //And push all his assigned orders to unassigned orders.
        if(orderPartnerDB.containsKey(partnerId)){
            for(String orderId:orderPartnerDB.get(partnerId)){
                if(orderPartnerPair.containsKey(orderId))
                    orderPartnerPair.remove(orderId);
            }
            orderPartnerDB.remove(partnerId);
        }
        if(partnerDB.containsKey(partnerId))
            partnerDB.remove(partnerId);
    }

    public void deleteOrderById(String orderId){
        //Delete an order and also
        // remove it from the assigned order of that partnerId
        String partnerId = orderPartnerPair.get(orderId);
        List<String> orders = orderPartnerDB.get(partnerId);
        orders.remove(orderId);
        orderPartnerDB.put(partnerId,orders);
        orderPartnerPair.remove(orderId);

        DeliveryPartner partner = partnerDB.get(partnerId);
        partner.setNumberOfOrders(orders.size());
        if(orderDB.containsKey(orderId))
            orderDB.remove(orderId);
    }
}