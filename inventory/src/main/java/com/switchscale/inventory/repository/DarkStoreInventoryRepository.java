package com.switchscale.inventory.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.switchscale.inventory.model.DarkStoreInventory;

public interface DarkStoreInventoryRepository extends JpaRepository<DarkStoreInventory, Long> {

    Optional<DarkStoreInventory> findByDarkStoreIdAndProductId(Long darkStoreId, Long productId);

    List<DarkStoreInventory> findByDarkStoreId(Long darkStoreId);

    List<DarkStoreInventory> findByProductId(Long productId);
}
