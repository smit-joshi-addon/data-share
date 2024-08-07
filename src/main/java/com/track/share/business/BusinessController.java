package com.track.share.business;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/businesses")
class BusinessController {

    @Autowired
    private BusinessService businessService;
    
    @Autowired
    private BusinessMapper mapper;

    @PostMapping
    public ResponseEntity<BusinessDTO> addBusiness(@RequestBody Business business) {
        BusinessDTO createdBusiness = businessService.addBusiness(business);
        return new ResponseEntity<>(createdBusiness, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<BusinessDTO>> getBusinesses() {
        List<BusinessDTO> businesses = businessService.getBusinesses();
        return new ResponseEntity<>(businesses, HttpStatus.OK);
    }

    @PutMapping("/{businessId}")
    public ResponseEntity<BusinessDTO> updateBusiness(@PathVariable Integer businessId, @RequestBody Business business) {
        try {
            BusinessDTO updatedBusiness = businessService.updateBusiness(businessId, business);
            return new ResponseEntity<>(updatedBusiness, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{businessId}")
    public ResponseEntity<Void> deleteBusiness(@PathVariable Integer businessId) {
        boolean isDeleted = businessService.deleteBusiness(businessId);
        if (isDeleted) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{businessId}")
    public ResponseEntity<BusinessDTO> getBusiness(@PathVariable Integer businessId) {
        try {
            BusinessDTO business = mapper.toDTO(businessService.getBusiness(businessId));
            return new ResponseEntity<>(business, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
