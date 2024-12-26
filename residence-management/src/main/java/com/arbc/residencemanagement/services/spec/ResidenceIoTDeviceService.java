package com.arbc.residencemanagement.services.spec;

import com.arbc.residencemanagement.models.Message;
import com.arbc.residencemanagement.models.dto.AirConditionerOperationDTO;
import com.arbc.residencemanagement.models.dto.InteriorHeaterOperationDTO;
import com.arbc.residencemanagement.models.dto.WindowsOperationDTO;
import jakarta.ws.rs.core.SecurityContext;

public interface ResidenceIoTDeviceService {

    Message heaterOperation(InteriorHeaterOperationDTO interiorHeaterOperationDto, SecurityContext securityContext);

    Message airConditionOperation(AirConditionerOperationDTO airConditionerOperationDto, SecurityContext securityContext);

    Message windowsOperation(WindowsOperationDTO windowsOperationDto, SecurityContext securityContext);
}
