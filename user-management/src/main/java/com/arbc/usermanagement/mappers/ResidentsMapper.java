package com.arbc.usermanagement.mappers;

import com.arbc.usermanagement.models.enums.Role;
import com.arbc.usermanagement.models.dtos.residents.CreateResidentDTO;
import com.arbc.usermanagement.models.otds.residents.ResidentOTD;
import org.bson.Document;

import java.util.Optional;

public class ResidentsMapper {

    public static Document toDocument(CreateResidentDTO dto, String password) {

        return new Document("name", dto.name())
                .append("surname", dto.surname())
                .append("email", dto.email())
                .append("password", password)
                .append("role", Role.RESIDENT)
                .append("banned_for_reservations", false);
    }

    public static Optional<ResidentOTD> toResidentOTDOptional(Document document) {

        return Optional.ofNullable(document)
                .map(doc -> new ResidentOTD(
                        doc.getObjectId("_id").toHexString(),
                        doc.getString("name"),
                        doc.getString("surname"),
                        doc.getString("email"),
                        doc.getBoolean("banned_for_reservations")));
    }

    public static ResidentOTD toResidentOTD(Document document) {

        return new ResidentOTD(
                document.getObjectId("_id").toHexString(),
                document.getString("name"),
                document.getString("surname"),
                document.getString("email"),
                document.getBoolean("banned_for_reservations"));
    }
}