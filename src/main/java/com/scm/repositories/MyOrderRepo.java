package com.scm.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.scm.enities.MyOrder;

public interface MyOrderRepo extends JpaRepository<MyOrder, Long>{

    public MyOrder findByOrderId(String orderId);

}
