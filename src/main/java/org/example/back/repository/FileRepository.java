package org.example.back.repository;

import org.example.back.entity.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FileRepository extends JpaRepository<File, Long> {
    @Query(value = "SELECT f FROM File f")
    List<File> listTopNFiles(@Param("limit") int limit);

    Optional<File> findFileByFilename(String filename);
}
