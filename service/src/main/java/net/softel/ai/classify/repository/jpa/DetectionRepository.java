package net.softel.ai.classify.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import net.softel.ai.classify.entity.jpa.Detection;

import java.util.Optional;

@Repository
public interface DetectionRepository extends JpaRepository<Detection, Long> {

    Optional<Detection> findByIncidentId(String incidentId);

    }
