package com.track.share.business;

import org.springframework.data.jpa.repository.JpaRepository;

interface BusinessRepository extends JpaRepository<Business, Integer> {
	Business findByUsername(String username);
}
