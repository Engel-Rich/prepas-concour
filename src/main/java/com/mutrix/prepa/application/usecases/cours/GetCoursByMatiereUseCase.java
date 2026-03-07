package com.mutrix.prepa.application.usecases.cours;

import com.mutrix.prepa.application.dto.response.CoursResponse;
import com.mutrix.prepa.cors.PageResponse;
import com.mutrix.prepa.domaines.models.Cours;
import com.mutrix.prepa.domaines.interfaces.CoursServices;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetCoursByMatiereUseCase {

    private final CoursServices coursRepository;

    public PageResponse<CoursResponse> execute(UUID matiereId, Pageable pageable) {
        Page<Cours> coursPage = coursRepository.getAllByMatiereId(matiereId, pageable);
        return PageResponse.fromPage(coursPage.map(CoursResponse::fromDomain));
    }
}