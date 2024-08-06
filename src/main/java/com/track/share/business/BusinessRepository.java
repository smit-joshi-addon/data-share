package com.track.share.business;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BusinessRepository extends JpaRepository<Business, Integer> {
	Optional<Business> findByUsername(String username);
	Boolean existsByUsername(String username);
}
