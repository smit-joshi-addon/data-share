package com.track.share.user;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

interface UserRepository extends JpaRepository<Users, Long> {
	Optional<Users> findByEmail(String username);
}
