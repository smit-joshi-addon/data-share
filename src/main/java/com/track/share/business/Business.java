package com.track.share.business;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
class Business {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer businessId;
	private String name;
}
