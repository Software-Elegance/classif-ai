package net.softel.ai.classify.entity.jpa;


import java.time.LocalDateTime;

import javax.persistence.Id;
import javax.persistence.Entity;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Lob;
import javax.persistence.FetchType;
import javax.persistence.Basic;
import javax.persistence.Table;
import javax.persistence.Index;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import com.fasterxml.jackson.annotation.JsonFormat;




@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(indexes = {
  @Index(columnList = "incidentId"),
  @Index(name = "idx_label", columnList = "label")
})
public class Pose{

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;

    @Column(nullable = false, unique = true)
    private String incidentId;      //unique domain identifier ?

    @Column(nullable = false)
    private String label;

    @Column(nullable = false)
    private String jobName;     

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(nullable = true, columnDefinition="CLOB")
    private String poses;               //can be reused later to train anomalies/postures from existing data

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(nullable = false, columnDefinition="CLOB")
    private String base64;      //we can join the keypoints with lines here or just dots for a start

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    @Column(nullable = false, columnDefinition = "timestamp default CURRENT_TIMESTAMP")
    LocalDateTime createdAt = LocalDateTime.now();

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    @Column(nullable = false, columnDefinition = "timestamp default CURRENT_TIMESTAMP")
    LocalDateTime updatedAt = LocalDateTime.now();
    
}