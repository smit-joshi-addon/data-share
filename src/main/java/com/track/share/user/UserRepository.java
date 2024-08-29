package com.track.share.user;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<Users, Long> {
	Optional<Users> findByEmail(String username);

	Boolean existsByEmail(String email);
}
