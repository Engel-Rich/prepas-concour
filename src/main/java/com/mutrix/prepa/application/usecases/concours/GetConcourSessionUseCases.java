package com.mutrix.prepa.application.usecases.concours;

import com.mutrix.prepa.application.dto.mappers.ConcoursSessionResponseMapper;
import com.mutrix.prepa.application.dto.response.ConcourSessionResponse;
import com.mutrix.prepa.cors.UseCase;
import com.mutrix.prepa.domaines.models.Concours;
import com.mutrix.prepa.domaines.models.ConcoursSessions;
import com.mutrix.prepa.domaines.interfaces.ConcoursServices;
import com.mutrix.prepa.domaines.interfaces.ConcoursSessionServices;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.UUID;


@RequiredArgsConstructor
@UseCase
public class GetConcourSessionUseCases {
    private final ConcoursSessionServices concoursSessionServices;
    private final ConcoursServices concoursServices;



    public ConcourSessionResponse getById(String id){
        final UUID uuid = UUID.fromString(id);
        final ConcoursSessions sessions = concoursSessionServices.getConcoursSessionById(uuid)
                .orElseThrow(() -> new RuntimeException("Session not found with this id"));
        final Concours concours = concoursServices.getConcoursById(sessions.getConcoursId())
                .orElseThrow(() -> new RuntimeException("Concour not found with this id"));
        return ConcoursSessionResponseMapper.toDto(sessions, concours);
    }


    ///
    ///  Get All [ConcourSessionResponse] of One [Concours] paginate by default it's the first page and size it's 25
    ///
    public Page<ConcourSessionResponse> getByConcourId(String id, Integer page, Integer size){
        final UUID uuid = UUID.fromString(id);
        final Page<ConcoursSessions> sessions = concoursSessionServices.getAllByConcoursId(uuid, page,size);
        return sessions.map((s)-> {
             final Concours concours = concoursServices.getConcoursById(s.getConcoursId())
                     .orElseThrow(() -> new RuntimeException("Concour not found with this id"));
            return ConcoursSessionResponseMapper.toDto(s, concours);
         });
    }


    ///
    /// Get All [ConcourSessionResponse]  of all [Concours]  paginate
    ///
    public Page<ConcourSessionResponse> list( Integer page, Integer size){
        final Page<ConcoursSessions> sessions = concoursSessionServices.getAllConcoursSessions(page,size);
        return sessions.map((s)-> {
             final Concours concours = concoursServices.getConcoursById(s.getConcoursId())
                     .orElseThrow(() -> new RuntimeException("Concour not found with this id"));
            return ConcoursSessionResponseMapper.toDto(s, concours);
         });
    }
}
