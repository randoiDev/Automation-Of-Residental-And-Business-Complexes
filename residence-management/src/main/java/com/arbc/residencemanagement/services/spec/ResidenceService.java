package com.arbc.residencemanagement.services.spec;

import com.arbc.residencemanagement.models.Message;
import com.arbc.residencemanagement.models.dto.ResidenceUpdateDTO;

import java.util.List;

public interface ResidenceService {

    Message attachResident(ResidenceUpdateDTO residenceUpdateDTO, String jwt);

    Message detachResident(ResidenceUpdateDTO residenceUpdateDTO, String jwt);

    List<String> readAvailableResidences();

    List<String> readResidencesByResidentsEmail(String residentsEmail);
}
