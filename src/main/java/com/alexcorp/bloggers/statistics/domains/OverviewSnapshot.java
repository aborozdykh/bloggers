package com.alexcorp.bloggers.statistics.domains;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "SNAPSHOTS")
public class OverviewSnapshot implements Serializable {

    @JsonIgnore
    @Id
    @GeneratedValue(generator = "increment")
    @GenericGenerator(name = "increment", strategy = "increment")
    private Long id;

    private Date date;

    private int adminsOnline;
    private int teachersOnline;
    private int studentsOnline;
    private int guestsOnline;

    private float processCpuUsage;
    private float totalCpuUsage;

    private float ramUsage;

    public OverviewSnapshot(Date date, int adminsOnline, int teachersOnline, int studentsOnline, int guestsOnline, float processCpuUsage, float totalCpuUsage, float ramUsage) {
        this.processCpuUsage = processCpuUsage;
        this.totalCpuUsage = totalCpuUsage;
        this.date = date;
        this.adminsOnline = adminsOnline;
        this.teachersOnline = teachersOnline;
        this.studentsOnline = studentsOnline;
        this.guestsOnline = guestsOnline;
        this.ramUsage = ramUsage;
    }
}