package com.arbc.usermanagement.services.spec;

import com.arbc.usermanagement.models.dtos.residents.LoginResidentDTO;
import com.arbc.usermanagement.models.dtos.UpdatePasswordDTO;
import com.arbc.usermanagement.models.otds.JwtToken;
import com.arbc.usermanagement.models.otds.Message;
import com.arbc.usermanagement.models.dtos.residents.CreateResidentDTO;
import com.arbc.usermanagement.models.otds.residents.ResidentOTD;
import jakarta.ws.rs.core.SecurityContext;

import java.util.List;

public interface ResidentsService {

    Message createResident(CreateResidentDTO createResidentDTO, String jwt);

    Message deleteResident(String residentId, String jwt);

    ResidentOTD readResidentByEmail(String email);

    ResidentOTD readResidentByEmail(SecurityContext securityContext);

    Message resetResidentPassword(String id);

    Message updatePassword(UpdatePasswordDTO updatePasswordDTO, SecurityContext securityContext);

    Message updateBannedForReservations(SecurityContext securityContext, String residentId);

    List<ResidentOTD> readResidentsByEmail(String email, int page, int size);

    JwtToken login(LoginResidentDTO loginResidentDto);
}
