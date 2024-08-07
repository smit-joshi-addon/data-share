package com.track.share.business;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Business {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer businessId;
	private String name;
	@Column(unique = true)
	private String username;
	private String password;

}
