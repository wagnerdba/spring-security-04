package br.com.wagnerpires.springsecurityjwt.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import br.com.wagnerpires.springsecurityjwt.model.User;

public interface UserRepository extends CrudRepository<User, String> {
  Optional<User> findByUsername(String username);
}
