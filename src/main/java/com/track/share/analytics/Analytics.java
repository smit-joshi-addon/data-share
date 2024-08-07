package com.track.share.analytics;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

import com.track.share.business.Business;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
public class Analytics {
	
	@Id
	@UuidGenerator
	private String uuid;
	
	@ManyToOne
	private Business business;

	private String methodName;

	@CreationTimestamp
	private LocalDateTime calledAt;

	private String calledByIp;
	
	
}
