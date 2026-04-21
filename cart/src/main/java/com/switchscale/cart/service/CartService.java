package com.switchscale.cart.service;

import feign.FeignException;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.switchscale.cart.repository.CartRepository;
import com.switchscale.cart.config.CatalogClient;
import com.switchscale.cart.dto.ProductDTO;
import com.switchscale.cart.exception.ExternalServiceException;
import com.switchscale.cart.exception.ResourceNotFoundException;
import com.switchscale.cart.model.CartModel;
import com.switchscale.cart.model.Cartitem;

@Service
public class CartService {
    
    
    private final CartRepository cartRepository;
    private final CatalogClient catalogClient;

    public CartService(CartRepository cartRepository, CatalogClient catalogClient) {
        this.cartRepository = cartRepository;
        this.catalogClient = catalogClient;
    }

    public CartModel getCart(String userId){
        validateUserId(userId);
        return cartRepository.findById(userId).orElseGet(()->{
            CartModel cart=new CartModel();
            cart.setUserId(userId);
            return cart;
        });
    }

    public CartModel addItemToCart(String userId,String productId,int quantity){
        validateUserId(userId);
        validateProductAndQuantity(productId, quantity);

        CartModel cart=getCart(userId);


        // Check if item already exists in cart
        Optional<Cartitem> existingItem = cart.getItems().stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst();
                
        if(existingItem.isPresent()){
            Cartitem item=existingItem.get();
            item.setQuantity(item.getQuantity()+quantity);

        }   
        else{
            // Fetch product details from catalog service
            ProductDTO product;
            try {
                product=catalogClient.getProductById(productId);
            } catch (FeignException.NotFound ex) {
                throw new ResourceNotFoundException("Product not found for id: " + productId);
            } catch (FeignException ex) {
                throw new ExternalServiceException("Catalog service is unavailable. Please try again later.", ex);
            }

            if(product==null || product.getId()==null || product.getName()==null || product.getPrice()==null){
                throw new ExternalServiceException("Catalog service returned incomplete product data.");
            }

            Cartitem newItem=new Cartitem();
            newItem.setProductId(product.getId());
            newItem.setProductName(product.getName());
            newItem.setPrice(product.getPrice());
            newItem.setImageurl(product.getImageUrl());
            newItem.setQuantity(quantity);

            cart.getItems().add(newItem);
        }     

        cart.calculateTotal();
        return cartRepository.save(cart);

        
    }

    public CartModel removeItem(String userId,String productId){
        validateUserId(userId);
        if (productId == null || productId.isBlank()) {
            throw new IllegalArgumentException("productId must not be blank");
        }
        
        CartModel cart=getExistingCartOrThrow(userId);
        boolean removed = cart.getItems().removeIf(item ->item.getProductId().equals(productId));
        if (!removed) {
            throw new ResourceNotFoundException("Product not found in cart for id: " + productId);
        }

        cart.calculateTotal();
        return cartRepository.save(cart);
    }

    public void removeCart(String userId){
        validateUserId(userId);
        if(!cartRepository.existsById(userId)){
            throw new ResourceNotFoundException("Cart not found for userId: " + userId);
        }

        cartRepository.deleteById(userId); //removes key from redis entirely
    }

    public boolean clearCartIfExists(String userId) {
        validateUserId(userId);
        if (!cartRepository.existsById(userId)) {
            return false;
        }
        cartRepository.deleteById(userId);
        return true;
    }

    private CartModel getExistingCartOrThrow(String userId) {
        return cartRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found for userId: " + userId));
    }

    private void validateUserId(String userId) {
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("userId must not be blank");
        }
    }

    private void validateProductAndQuantity(String productId, int quantity) {
        if (productId == null || productId.isBlank()) {
            throw new IllegalArgumentException("productId must not be blank");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("quantity must be greater than 0");
        }
    }

    

}
