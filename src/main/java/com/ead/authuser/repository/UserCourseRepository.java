package com.ead.authuser.repository;

import com.ead.authuser.models.UserCourseModel;
import com.ead.authuser.models.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface UserCourseRepository extends JpaRepository<UserCourseModel, UUID> {

    boolean existsByUserAndCourseId(UserModel userModel, UUID courseId);

    @Query(value = "select * from tb_user_course where user_id = :userId", nativeQuery = true)
    List<UserCourseModel> findAllUserCourseIntoUser(@Param("userId") UUID userId);

}
