package io.github.deanmave.hplclims.domain.mapper;

import io.github.deanmave.hplclims.domain.HplcColumn;
import io.github.deanmave.hplclims.domain.dto.request.ColumnCreateDto;
import io.github.deanmave.hplclims.domain.dto.request.ColumnUpdateDto;
import io.github.deanmave.hplclims.domain.dto.response.ColumnResponseDto;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-09-28T20:20:25+0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.7 (Amazon.com Inc.)"
)
@Component
public class HplcColumnMapperImpl implements HplcColumnMapper {

    @Override
    public ColumnResponseDto toColumnResponseDto(HplcColumn hplcColumn) {
        if ( hplcColumn == null ) {
            return null;
        }

        ColumnResponseDto columnResponseDto = new ColumnResponseDto();

        columnResponseDto.setId( hplcColumn.getId() );
        columnResponseDto.setManufacturer( hplcColumn.getManufacturer() );
        columnResponseDto.setSerialNumber( hplcColumn.getSerialNumber() );
        columnResponseDto.setPartNumber( hplcColumn.getPartNumber() );
        columnResponseDto.setLength( hplcColumn.getLength() );
        columnResponseDto.setDiameter( hplcColumn.getDiameter() );
        columnResponseDto.setParticleSize( hplcColumn.getParticleSize() );
        columnResponseDto.setInstallationDate( hplcColumn.getInstallationDate() );
        columnResponseDto.setPhMin( hplcColumn.getPhMin() );
        columnResponseDto.setPhMax( hplcColumn.getPhMax() );
        columnResponseDto.setStationaryPhase( hplcColumn.getStationaryPhase() );
        columnResponseDto.setMaxPressure( hplcColumn.getMaxPressure() );
        columnResponseDto.setOwnerOrganization( hplcColumn.getOwnerOrganization() );
        columnResponseDto.setReturnDate( hplcColumn.getReturnDate() );
        columnResponseDto.setStatus( hplcColumn.getStatus() );
        columnResponseDto.setInternalCode( hplcColumn.getInternalCode() );
        columnResponseDto.setStorageLocation( hplcColumn.getStorageLocation() );

        return columnResponseDto;
    }

    @Override
    public HplcColumn toHplcColumn(ColumnCreateDto dto) {
        if ( dto == null ) {
            return null;
        }

        HplcColumn hplcColumn = new HplcColumn();

        hplcColumn.setManufacturer( dto.getManufacturer() );
        hplcColumn.setSerialNumber( dto.getSerialNumber() );
        hplcColumn.setPartNumber( dto.getPartNumber() );
        hplcColumn.setLength( dto.getLength() );
        hplcColumn.setDiameter( dto.getDiameter() );
        hplcColumn.setParticleSize( dto.getParticleSize() );
        hplcColumn.setInstallationDate( dto.getInstallationDate() );
        hplcColumn.setPhMin( dto.getPhMin() );
        hplcColumn.setPhMax( dto.getPhMax() );
        hplcColumn.setStationaryPhase( dto.getStationaryPhase() );
        hplcColumn.setMaxPressure( dto.getMaxPressure() );
        hplcColumn.setOwnerOrganization( dto.getOwnerOrganization() );
        hplcColumn.setStorageLocation( dto.getStorageLocation() );

        return hplcColumn;
    }

    @Override
    public HplcColumn updateFromDto(HplcColumn hplcColumn, ColumnUpdateDto dto) {
        if ( dto == null ) {
            return hplcColumn;
        }

        if ( dto.getManufacturer() != null ) {
            hplcColumn.setManufacturer( dto.getManufacturer() );
        }
        if ( dto.getSerialNumber() != null ) {
            hplcColumn.setSerialNumber( dto.getSerialNumber() );
        }
        if ( dto.getPartNumber() != null ) {
            hplcColumn.setPartNumber( dto.getPartNumber() );
        }
        if ( dto.getLength() != null ) {
            hplcColumn.setLength( dto.getLength() );
        }
        if ( dto.getDiameter() != null ) {
            hplcColumn.setDiameter( dto.getDiameter() );
        }
        if ( dto.getParticleSize() != null ) {
            hplcColumn.setParticleSize( dto.getParticleSize() );
        }
        if ( dto.getInstallationDate() != null ) {
            hplcColumn.setInstallationDate( dto.getInstallationDate() );
        }
        if ( dto.getPhMin() != null ) {
            hplcColumn.setPhMin( dto.getPhMin() );
        }
        if ( dto.getPhMax() != null ) {
            hplcColumn.setPhMax( dto.getPhMax() );
        }
        if ( dto.getStationaryPhase() != null ) {
            hplcColumn.setStationaryPhase( dto.getStationaryPhase() );
        }
        if ( dto.getMaxPressure() != null ) {
            hplcColumn.setMaxPressure( dto.getMaxPressure() );
        }
        if ( dto.getOwnerOrganization() != null ) {
            hplcColumn.setOwnerOrganization( dto.getOwnerOrganization() );
        }
        if ( dto.getReturnDate() != null ) {
            hplcColumn.setReturnDate( dto.getReturnDate() );
        }
        if ( dto.getStatus() != null ) {
            hplcColumn.setStatus( dto.getStatus() );
        }
        if ( dto.getStorageLocation() != null ) {
            hplcColumn.setStorageLocation( dto.getStorageLocation() );
        }

        return hplcColumn;
    }
}
