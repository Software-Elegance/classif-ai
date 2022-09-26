package net.softel.ai.classify.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import net.softel.ai.classify.entity.jpa.Pose;

import java.util.Optional;

@Repository
public interface PoseRepository extends JpaRepository<Pose, Long> {

    Optional<Pose> findByIncidentId(String incidentId);

    }
