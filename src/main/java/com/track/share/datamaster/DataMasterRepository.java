package com.track.share.datamaster;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.track.share.business.Business;


public interface DataMasterRepository extends JpaRepository<DataMaster, Integer>{

	Optional<DataMaster> findByBusiness(Business business);
}
