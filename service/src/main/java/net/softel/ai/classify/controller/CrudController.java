
package net.softel.ai.classify.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import net.softel.ai.classify.exception.ResourceNotFoundException;
import org.springframework.web.bind.annotation.ResponseBody;

import net.softel.ai.classify.entity.jpa.Detection;
import net.softel.ai.classify.repository.jpa.DetectionRepository;

import net.softel.ai.classify.entity.jpa.Pose;
import net.softel.ai.classify.repository.jpa.PoseRepository;

import java.util.Optional;
import org.springframework.web.bind.annotation.CrossOrigin;

import net.softel.ai.classify.util.Result;
import net.softel.ai.classify.util.ResultFactory;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/crud")
public class CrudController {


    @Autowired
    private DetectionRepository detectionRepository;
    
    @Autowired
    private PoseRepository poseRepository;

    //a. detection
    @GetMapping(path="/detection/{id}")
	  public @ResponseBody Detection getDetection(@PathVariable Long id) {
      return detectionRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Detection", "id", id));
      }
    
    @GetMapping(path="/detection/domain/{incidentId}")
	  public @ResponseBody Optional<Detection> getDetection(@PathVariable String incidentId) {
      return detectionRepository.findByIncidentId(incidentId);
      }
      
    @PostMapping(path="/detection/add")
		public Result<String> addDetection (@RequestBody Detection obj){
			detectionRepository.save(obj);
			return ResultFactory.getSuccessResult("OK");
			}

    //b. pose estimation
    @GetMapping(path="/pose/{id}")
	  public @ResponseBody Pose getPose(@PathVariable Long id) {
      return poseRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Pose", "id", id));
      }
    
    @GetMapping(path="/pose/domain/{incidentId}")
	  public @ResponseBody Optional<Pose> getPose(@PathVariable String incidentId) {
      return poseRepository.findByIncidentId(incidentId);
      }
      
    @PostMapping(path="/pose/add")
		public Result<String> addPose (@RequestBody Pose obj){
			poseRepository.save(obj);
			return ResultFactory.getSuccessResult("OK");
			}


    //c. classification

}