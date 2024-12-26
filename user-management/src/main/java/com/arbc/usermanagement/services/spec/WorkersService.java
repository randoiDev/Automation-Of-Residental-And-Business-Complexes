package com.arbc.usermanagement.services.spec;

import com.arbc.usermanagement.models.dtos.UpdatePasswordDTO;
import com.arbc.usermanagement.models.dtos.workers.*;
import com.arbc.usermanagement.models.otds.JwtToken;
import com.arbc.usermanagement.models.otds.Message;
import com.arbc.usermanagement.models.otds.workers.WorkerOTD;
import jakarta.ws.rs.core.SecurityContext;

import java.util.List;

public interface WorkersService {

    Message createWorker(CreateWorkerDTO createWorkerDTO);

    Message deleteWorker(String workerId);

    WorkerOTD readWorkerByUsername(SecurityContext securityContext);

    Message updatePassword(UpdatePasswordDTO updatePasswordDTO, SecurityContext securityContext);

    Message updateMobileNumber(UpdateMobileNumberDTO updateMobileNumberDTO, SecurityContext securityContext);

    List<WorkerOTD> readWorkersByUsername(String username, int page, int size);

    JwtToken login(LoginWorkerDTO loginWorkerDTO);
}
