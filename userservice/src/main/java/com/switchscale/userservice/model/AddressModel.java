package com.switchscale.userservice.model;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "addresses")
@Data
@NoArgsConstructor
@AllArgsConstructor

public class AddressModel {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserModel user;

    private String houseNumber;
    private String street;
    private String city;
    private String pincode;
    
    private Double latitude;
    private Double longitude;
    
    private String label; // Home, Work, Other

}
