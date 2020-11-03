package com.alexcorp.bloggers.statistics.repositories;

import com.alexcorp.bloggers.statistics.domains.OverviewSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface OverviewSnapshotRepository extends JpaRepository<OverviewSnapshot, Long> {

    List<OverviewSnapshot> findAllByDateGreaterThanEqualAndDateLessThanOrderByDateAsc(Date startRangeDate, Date endRangeDate);
}
