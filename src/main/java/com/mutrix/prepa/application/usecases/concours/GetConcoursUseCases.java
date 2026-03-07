package com.mutrix.prepa.application.usecases.concours;

import com.mutrix.prepa.application.dto.mappers.ConcoursResponseMapper;
import com.mutrix.prepa.application.dto.response.ConcourResponseDTO;
import com.mutrix.prepa.cors.UseCase;
import com.mutrix.prepa.domaines.models.Concours;
import com.mutrix.prepa.domaines.interfaces.ConcoursServices;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.UUID;

@UseCase
@RequiredArgsConstructor
public class GetConcoursUseCases {

    private final ConcoursServices concoursServices;

    public ConcourResponseDTO getById(String id){
        final UUID uuid = UUID.fromString(id);
        final Concours concours = concoursServices.getConcoursById(uuid)
                .orElseThrow(() -> new RuntimeException("Concour not found with this id"));
        return ConcoursResponseMapper.toDto(concours);
    }


    public Page<ConcourResponseDTO> list(Integer page, Integer size){
        Page<Concours> concoursPage = concoursServices.getAllConcours(page,size);
        return  concoursPage.map(ConcoursResponseMapper::toDto);
    }
}
