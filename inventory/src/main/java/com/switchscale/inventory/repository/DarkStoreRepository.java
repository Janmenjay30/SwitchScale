package com.switchscale.inventory.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.switchscale.inventory.model.DarkStore;

public interface DarkStoreRepository extends JpaRepository<DarkStore, Long> {

    List<DarkStore> findByPincodeAndActiveTrue(String pincode);
}
