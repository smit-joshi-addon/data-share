package com.track.share.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.track.share.exceptions.NotFoundException;
import com.track.share.exceptions.UsernameUnavailableException;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

	private UserService underTest;

	@Mock
	private UserRepository userRepository;

	@Captor
	ArgumentCaptor<Users> argumentCaptor;

	@BeforeEach
	void setUp() {
		underTest = new UserServiceImpl(userRepository);
	}

	@Test
	void shouldCreateUser() {
		// given
		Users givenUser = Users.builder().name("smit").email("smit@gmail.com")
				.password(new BCryptPasswordEncoder().encode("password")).build();
		// when
		underTest.createUser(givenUser);
		// then
		verify(userRepository).save(argumentCaptor.capture());
		Users capturedUser = argumentCaptor.getValue();
		assertThat(capturedUser.getName()).isEqualTo(givenUser.getName());
		assertThat(capturedUser.getEmail()).isEqualTo(givenUser.getEmail());
		assertThat(capturedUser.getPassword()).isEqualTo(givenUser.getPassword());
	}

	@Test
	void shouldThrowUsernameUnavailableExceptionWhenEmailAlreadyPresent() {
		// given
		Users givenUser = Users.builder().name("smit").email("smit@gmail.com")
				.password(new BCryptPasswordEncoder().encode("password")).build();
		when(userRepository.existsByEmail("smit@gmail.com")).thenReturn(Boolean.TRUE);
		// when
		// then
		assertThatThrownBy(() -> underTest.createUser(givenUser)).isInstanceOf(UsernameUnavailableException.class)
				.hasMessage("email is Unavailable, please try with another email");
	}

	@Test
	void shouldGetAllUsers() {
		// given
		// when
		underTest.getUsers();
		// then
		verify(userRepository).findAll();
	}

	@Test
	void shouldReturnUserAssociatedWithEamil() {
		// given
		Users user = Users.builder().name("smit").email("smit@gmail.com")
				.password(new BCryptPasswordEncoder().encode("password")).build();
		when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
		// when
		underTest.getUser("smit@gmail.com");
		// then
		verify(userRepository).findByEmail(anyString());
	}

	@Test
	void shouldThrowNotFoundExceptionWhenThereIsNoUserAssociatedWithEmail() {
		// given
		String providedEmail = "unknown@gmail.com";
		// when
		// then
		assertThatThrownBy(() -> underTest.getUser(providedEmail)).isInstanceOf(NotFoundException.class)
				.hasMessage("user not found with username " + providedEmail);
	}

	@Test
	void shouldRemoveUserWhenUserAssociatedWithIdIsPresent() {
		// given
		Long id = 5l;
		when(userRepository.existsById(id)).thenReturn(Boolean.TRUE);
		// when
		underTest.removeUser(id);
		// then
		verify(userRepository).deleteById(id);
	}

	@Test
	void shouldThrowNotFoundExceptionWhenUserAssociatedWithIdIsNotPresent() {
		// given
		Long id = 5l;
		when(userRepository.existsById(id)).thenReturn(Boolean.FALSE);
		// when
		// then
		assertThatThrownBy(() -> underTest.removeUser(id)).isInstanceOf(NotFoundException.class)
				.hasMessage("user not found with userId " + id);
	}

}
