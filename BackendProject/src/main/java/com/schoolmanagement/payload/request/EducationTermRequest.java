package com.schoolmanagement.payload.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.schoolmanagement.entity.enums.Term;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true) // kendimiz bir annotation olusturabilir miyiz? mesela bu dort annotationu hep kullaniyoruz. Bunlari tek bir annotation icine koyabilir miyiz?
//evet. Ancak bu annotationu sadece biz biliyoruz. Bizden sonraki developer bilmiyor :D
public class EducationTermRequest implements Serializable {

    @NotNull(message = "Education Term must not be empty")
    private Term term;

    @NotNull(message ="Start Date must not be empty") //burda column annotationu kullandik diyelim. ne olur? hicbir sey olmaz. Bosa ugrasmis olur app.
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @NotNull(message ="End Date must not be empty")
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    @NotNull(message ="Last Registration Date must not be empty")
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate lastRegistrationDate;

}
