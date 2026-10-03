package com.franchise.project.domain.franchise.api;

import com.franchise.project.domain.franchise.model.Franchise;
import reactor.core.publisher.Mono;

public interface UpdateFranchiseNameServicePort {
    Mono<Franchise> updateFranchiseName(Franchise franchise);
}
