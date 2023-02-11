package com.driver;

import java.util.*;

import org.springframework.stereotype.Repository;

@Repository
public class OrderRepository {
    
    HashMap<String,Order> orderDB = new HashMap<>();
    HashMap<String,DeliveryPartner> partnerDb = new HashMap<>();
    HashMap<String,List<String>> orderPartnerPair = new HashMap<>();
    HashMap<String,Order> unsignnedOrder = new HashMap<>();
    
    public void addOrder(Order order){
        orderDB.put(order.getId(), order);
    }

    public void addDeliveryPartner(String id){
        DeliveryPartner partner = new DeliveryPartner(id);
        partnerDb.put(id, partner);
    }

    public void addOrderPartnerPair(String orderId, String partnerId){
        if(orderDB.containsKey(orderId) && partnerDb.containsKey(partnerId)){
            List<String> order = new ArrayList<>();
            if(orderPartnerPair.containsKey(partnerId))
                order = orderPartnerPair.get(partnerId);
            order.add(orderId);
            orderPartnerPair.put(partnerId,order);
        }
    }

    public Order getOrderById(String orderId){
        return orderDB.get(orderId);
    }

    public DeliveryPartner getPartnerById(String partnerId){
        return partnerDb.get(partnerId);
    }

    public int getOrderCountByPartnerId(String partnerId){
        int ans = 0;
        if(orderPartnerPair.containsKey(partnerId)){
            ans = orderPartnerPair.get(partnerId).size();
        }
        return ans;
    }

    public List<String> getOrdersByPartnerId(String partnerId){
        List<String> order = new ArrayList<>();
        if(orderPartnerPair.containsKey(partnerId)){
            order = orderPartnerPair.get(partnerId);
        }

        return order;
    }

    public List<String> getAllOrders(){
        return new ArrayList<>(orderDB.keySet());
    }

    public int getCountOfUnassignedOrders(){
        int count = 0;
        for(String partnerId: orderPartnerPair.keySet()){
            count += orderPartnerPair.get(partnerId).size();
        }

        int orderCount = orderDB.size();
        return orderCount-count;
    }

    public int getOrdersLeftAfterGivenTimeByPartnerId(String time,String partnerId){
        int hour = Integer.parseInt(time.substring(0,2));
        int min = Integer.parseInt(time.substring(3, 5));
        int deadline = hour*60+min;
        int count = 0;
        List<String> order = orderPartnerPair.get(partnerId);
        for(String orderId:order){
            if(orderDB.containsKey(orderId)){
                int deliveryTime = orderDB.get(orderId).getDeliveryTime();
                if(deliveryTime<deadline)
                    count++;
            }
        }
        return count;
    }

    public String getLastDeliveryTimeByPartnerId(String partnerId){
        int time = 0;
        if(orderPartnerPair.containsKey(partnerId)){
            for(String orderId:orderPartnerPair.get(partnerId)){
                int timeOfDelivery = orderDB.get(orderId).getDeliveryTime();
                if(timeOfDelivery>time)
                    time = timeOfDelivery;
            }
        }

        int hour = time/60;
        int min = time%60;
        String res = String.format("%d:%d", hour,min);
        return res;
    }

    public void deletePartnerById(String partnerId){
        if(orderPartnerPair.containsKey(partnerId)){
            for(String orderId:orderPartnerPair.get(partnerId)){
                if(orderDB.containsKey(orderId)){
                    orderPartnerPair.get(partnerId).remove(orderId);
                    Order order = orderDB.get(orderId);
                    unsignnedOrder.put(orderId,order);
                }
            }

            orderPartnerPair.remove(partnerId);
        }

        if(partnerDb.containsKey(partnerId))
            partnerDb.remove(partnerId);
    }

    public void deleteOrderById(String orderId){
        if(orderDB.containsKey(orderId))
            orderDB.remove(orderId);

        for(String partnerId:orderPartnerPair.keySet()){
            for(String orderIdInorder:orderPartnerPair.get(partnerId)){
                if(orderId.equals(orderIdInorder))
                    orderPartnerPair.get(partnerId).remove(orderId);
            }
        }
    }
}