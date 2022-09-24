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
public class Detection{

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String label;

    @Column(nullable = false, unique = true)
    private String incidentId;      //unique domain identifier ?

    @Column(nullable = false)
    private String jobName;         

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(nullable = false, columnDefinition="CLOB")
    private String base64;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    @Column(nullable = false, columnDefinition = "timestamp default CURRENT_TIMESTAMP")
    LocalDateTime createdAt = LocalDateTime.now();

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    @Column(nullable = false, columnDefinition = "timestamp default CURRENT_TIMESTAMP")
    LocalDateTime updatedAt = LocalDateTime.now();
    
}