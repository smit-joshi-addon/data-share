package com.track.share.datadetail;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.track.share.datamaster.DataMaster;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class DataDetail {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer detailId;

	@ManyToOne
	private DataMaster master;
	private String secret;
	
	private LocalDateTime validTill;
	
	@CreationTimestamp
	private LocalDateTime createdAt;
	
	private String createdById;
	private String createdByName;
	private String createdByIp;
	
	private Boolean status;
}
