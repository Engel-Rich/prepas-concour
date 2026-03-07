package com.mutrix.prepa.application.usecases.cours;
import com.mutrix.prepa.application.dto.response.CoursResponse;
import com.mutrix.prepa.cors.PageResponse;
import com.mutrix.prepa.domaines.models.Cours;
import com.mutrix.prepa.domaines.interfaces.CoursServices;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetAllCoursUseCase {

    private final CoursServices coursRepository;

    public PageResponse<CoursResponse> execute(Integer page, Integer size) {
        Page<Cours> coursPage = coursRepository.getAllCours(page, size);
        return PageResponse.fromPage(coursPage.map(CoursResponse::fromDomain));
    }
}