package com.arbc.residencemanagement.mappers;

import com.arbc.residencemanagement.models.otd.ResidenceOTD;
import org.bson.Document;

import java.util.Optional;

public class ResidenceMapper {

    public static Optional<ResidenceOTD> toOtd(Document document) {

        return Optional.ofNullable(document)
                .map(doc -> new ResidenceOTD(
                        document.getString("residents_email"),
                        document.getString("residence_number")
                ));
    }
}

