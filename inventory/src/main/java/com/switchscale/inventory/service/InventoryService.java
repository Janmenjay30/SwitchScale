package com.switchscale.inventory.service;

import java.util.concurrent.TimeUnit;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import com.switchscale.inventory.model.Product;
import com.switchscale.inventory.repository.ProductRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryService {
    private final ProductRepository productRepository;
    private final RedissonClient redissonClient;

    public void deductStock(Long id,int qty){
        String lockKey="product_lock_"+id;
        RLock lock=redissonClient.getLock(lockKey);
        try {
            // trylock(waitTime,leaseTime,TimeUnit)
            // waitTime:how long to wait for lock to become free
            // leaseTime:how long to hold the lock before auto-release
            if(lock.tryLock(10,30,TimeUnit.SECONDS)){
                log.info("lock acquired for product id: {}",id);

                Product product = productRepository.findById(id).orElseThrow(() -> new RuntimeException("Product not found"));
                if(product.getStockQuantity() < qty){
                    throw new RuntimeException("Insufficient stock");
                }
                product.setStockQuantity(product.getStockQuantity() - qty);
                productRepository.save(product);
            }else{
                throw new RuntimeException("Could not acquire lock, try again later");
            }
        } catch(InterruptedException e){
            Thread.currentThread().interrupt();
            throw new RuntimeException("transaction interrupted",e);
        } finally {
            if(lock.isHeldByCurrentThread() && lock.isLocked()){
                lock.unlock();
                log.info("lock released for product id: {}",id);
            }
        }  
    }

    public Product updateStock(Long id,int qty){
        Product product = productRepository.findById(id).orElseThrow(() -> new RuntimeException("Product not found"));
        product.setStockQuantity(product.getStockQuantity() + qty);
        return productRepository.save(product);
    }
}
