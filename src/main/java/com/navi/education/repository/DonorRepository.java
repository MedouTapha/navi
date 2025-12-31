package com.navi.education.repository;

import com.navi.education.model.entity.Donor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DonorRepository extends JpaRepository<Donor, Long> {

    Optional<Donor> findByEmail(String email);

    Optional<Donor> findByTelephone(String telephone);

    @Query("SELECT d FROM Donor d WHERE LOWER(d.fullName) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Donor> searchByName(String searchTerm);

    List<Donor> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(
            String firstName, String lastName);
}
