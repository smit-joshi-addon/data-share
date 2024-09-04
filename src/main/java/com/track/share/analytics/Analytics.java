package com.track.share.analytics;

import java.time.ZonedDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

import com.track.share.business.Business;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Size;
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
	@Size(max = 255)
	private String methodName;

	@CreationTimestamp
	@Column(columnDefinition = "TIMESTAMPTZ", unique = true, nullable = false)
	private ZonedDateTime calledAt;
	
	@Size(max = 255)
	private String calledByIp;

}
