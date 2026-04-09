package com.switchscale.cart.repository;

import org.springframework.data.repository.CrudRepository;
import com.switchscale.cart.model.CartModel;


public interface CartRepository extends CrudRepository<CartModel, String> {
    
}
