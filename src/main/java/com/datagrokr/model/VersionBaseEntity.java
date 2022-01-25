package com.datagrokr.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.MappedSuperclass;
import javax.persistence.Version;

@Setter
@Getter
@MappedSuperclass
public class VersionBaseEntity {
    @Version
    private Long version;
}
