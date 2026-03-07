package com.mutrix.prepa.cors;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PageResponse<T> {

    private  List<T> content;
    private  int page;
    private  int size;
    private  long totalElements;
    private  int totalPages;
    private  boolean last;

    public static <T> PageResponse<T> fromPage(Page<T> page) {
        return PageResponse.<T>builder()
                .content(page.getContent())
                .page(page.getNumber())          // numéro de page courant (0-based)
                .size(page.getSize())            // taille de la page
                .totalElements(page.getTotalElements()) // nb total d'éléments
                .totalPages(page.getTotalPages())       // nb total de pages
                .last(page.isLast())             // est-ce la dernière page ?
                .build();
    }
}