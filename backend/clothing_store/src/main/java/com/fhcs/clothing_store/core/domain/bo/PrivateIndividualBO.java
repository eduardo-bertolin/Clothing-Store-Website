package com.fhcs.clothing_store.core.domain.bo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import com.fhcs.clothing_store.core.domain.bo.address.IndividualAddressBO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PrivateIndividualBO {
    private Integer individualId;
    private UserBO user;
    private String individualName;
    private LocalDate birthDate;
    private String CPF;
    private String phoneNumber;
    private List<IndividualAddressBO> individualAddress = new ArrayList<>();
}
