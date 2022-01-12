package com.cyber.repository;

import com.cyber.dto.TaskDTO;
import com.cyber.entity.Task;
import com.cyber.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {

    //@Where -- findByUserNameAndIsDeletedFalse();
    User findByUserName(String username);

    //hard delete - not good practice - when we delete anything in UI, we should not be deleting data from DB - we should keep saving data!!
    //@Where -- deleteByUserNameAndIsDeletedFalse();
    @Transactional // we need this annotation for deleting
    void deleteByUserName(String username);

    List<User> findAllByRoleDescriptionIgnoreCase(String description);




}
