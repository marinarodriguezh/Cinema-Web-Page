package cine.model;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface ScreenRepository extends CrudRepository<Screen, Integer> {

    Screen findByNumber(Integer number);
  
}