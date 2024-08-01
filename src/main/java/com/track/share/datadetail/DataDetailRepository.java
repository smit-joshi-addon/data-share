package com.track.share.datadetail;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.track.share.datamaster.DataMaster;


@Repository
interface DataDetailRepository extends JpaRepository<DataDetail, Integer> {

	@Modifying
	@Transactional
	@Query("UPDATE DataDetail d SET d.status = :status WHERE d.master = :master")
	Integer updateByStatus(DataMaster master, Boolean status);
	
	Boolean existsByMasterAndStatusAndSecret(DataMaster master, Boolean status,String token);

}