package com.track.share.datamaster;

import org.springframework.data.jpa.repository.JpaRepository;

import com.track.share.business.Business;


interface DataMasterRepository extends JpaRepository<DataMaster, Integer>{

	DataMaster findByBusiness(Business business);
}
