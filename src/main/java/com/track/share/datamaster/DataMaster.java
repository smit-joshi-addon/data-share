package com.track.share.datamaster;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.track.share.business.Business;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DataMaster {


	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer sharingId;
	
	@OneToOne
	private Business business;
	
	private String secret;
	// Logged In User
	private String ceatedById;
	// Logged In User IP
	private String createdByIp;
	
	private Boolean status;
	
	@CreationTimestamp
	private LocalDateTime createdAt;
	
	@UpdateTimestamp
	private LocalDateTime updatedAt;
	
	private RequestType type;
}



