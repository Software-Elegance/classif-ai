
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

    @GetMapping(path="/detection/{id}")
	  public @ResponseBody Detection getDetection(@PathVariable Long id) {
      return detectionRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Detection", "id", id));
      }
    
    @GetMapping(path="/detection/domain/{incidentId}")
	  public @ResponseBody Optional<Detection> getDetection(@PathVariable String incidentId) {
      return detectionRepository.findByIncidentId(incidentId);
      }
      
    @PostMapping(path="/detection/add")
		public Result<String> addDetection (@RequestBody Detection dt){
			detectionRepository.save(dt);
			return ResultFactory.getSuccessResult("OK");
			}
}