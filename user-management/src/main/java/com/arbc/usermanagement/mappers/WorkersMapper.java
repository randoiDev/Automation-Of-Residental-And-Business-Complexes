package com.arbc.usermanagement.mappers;

import com.arbc.usermanagement.models.dtos.workers.CreateWorkerDTO;
import com.arbc.usermanagement.models.enums.Role;
import com.arbc.usermanagement.models.otds.workers.WorkerOTD;
import org.bson.Document;

import java.util.Optional;

public class WorkersMapper {

    public static Document toDocument(CreateWorkerDTO dto, String username, String password) {

        return new Document("name", dto.name())
                .append("surname", dto.surname())
                .append("username", username)
                .append("mobile_number", dto.mobileNumber())
                .append("password", password)
                .append("role", Role.SPORTS_AND_WELLNESS_CENTER_WORKER);
    }

    public static Optional<WorkerOTD> toWorkerOTDOptional(Document document) {

        return Optional.ofNullable(document)
                .map(doc -> new WorkerOTD(
                        doc.getObjectId("_id").toHexString(),
                        doc.getString("name"),
                        doc.getString("surname"),
                        doc.getString("username"),
                        doc.getString("mobile_number"),
                        Role.valueOf(doc.getString("role")).getRole()));
    }

    public static WorkerOTD toWorkerOTD(Document document) {

        return  new WorkerOTD(
                document.getObjectId("_id").toHexString(),
                document.getString("name"),
                document.getString("surname"),
                document.getString("username"),
                document.getString("mobile_number"),
                Role.valueOf(document.getString("role")).getRole());
    }
}