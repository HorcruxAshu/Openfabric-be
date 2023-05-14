package ai.openfabric.api.controller;

import ai.openfabric.api.dto.WorkerListDTO;
import ai.openfabric.api.model.Worker;
import ai.openfabric.api.service.WorkerService;
import com.github.dockerjava.api.model.Statistics;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${node.api.path}/worker")
public class WorkerController {

    private WorkerService workerService;

    // Constructor initialization
    public WorkerController(WorkerService workerService) {
        this.workerService = workerService;
        this.workerService.syncWorkersFromDocker();
    }


    // To get the list of workers 
    @GetMapping(path = "/list")
    public ResponseEntity<Page<WorkerListDTO>> getWorkersFromDb(@RequestParam(defaultValue = "0") int page,
                                                                @RequestParam(defaultValue = "10") int size) {
        Page<WorkerListDTO> workerPage = workerService.getWorkersFromDb(page, size);
        return new ResponseEntity<>(workerPage, HttpStatus.OK);
    }

    // To sync worker info from docker to the database
    @GetMapping(path = "/syncWithDocker")
    public String syncWorkersFromDocker() {
        return this.workerService.syncWorkersFromDocker();
    }

    // To start the docker container with the respective container id provided
    @PostMapping("/start/{containerId}")
    public ResponseEntity<String> startContainer(@PathVariable String containerId) {
        return this.workerService.startContainer(containerId);
    }

    // To stop the docker container with the respective container id provided
    @PostMapping("/stop/{containerId}")
    public ResponseEntity<String> stopContainer(@PathVariable String containerId) {
        return this.workerService.stopContainer(containerId);
    }

    // To get the information about the container with a container id
    @GetMapping(path = "/info/{containerId}")
    public Worker getWorkerInformation(@PathVariable String containerId) {
        return this.workerService.getWorkerInformation(containerId);
    }

    // To geth the statistics of a docker container with a container id
    @GetMapping(path = "/stats/{containerId}")
    public ResponseEntity<Object> getContainerStatistics(@PathVariable String containerId) {
        try {
            Statistics statistics = this.workerService.getContainerStatistics(containerId);
            return ResponseEntity.ok(statistics);
        } catch (Exception e) {
            String errorMessage = "Failed to get container statistics: " + e.getMessage();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
        }
    }

}
