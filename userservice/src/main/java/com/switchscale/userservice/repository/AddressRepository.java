package com.switchscale.userservice.repository;
import java.util.List;
import com.switchscale.userservice.model.AddressModel;
import org.springframework.data.jpa.repository.JpaRepository;


public interface AddressRepository extends JpaRepository<AddressModel, Long> {
    List<AddressModel> findByUserId(Long userId);
}
