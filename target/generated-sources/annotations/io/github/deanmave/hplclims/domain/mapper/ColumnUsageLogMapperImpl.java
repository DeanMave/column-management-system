package io.github.deanmave.hplclims.domain.mapper;

import io.github.deanmave.hplclims.domain.ColumnUsageLog;
import io.github.deanmave.hplclims.domain.HplcColumn;
import io.github.deanmave.hplclims.domain.User;
import io.github.deanmave.hplclims.domain.dto.request.CorrectUsageLogRequest;
import io.github.deanmave.hplclims.domain.dto.request.StartUsageRequest;
import io.github.deanmave.hplclims.domain.dto.response.UsageLogResponseDto;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-10-05T19:32:15+0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.12.1 (Amazon.com Inc.)"
)
@Component
public class ColumnUsageLogMapperImpl implements ColumnUsageLogMapper {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private HplcColumnMapper hplcColumnMapper;

    @Override
    public ColumnUsageLog toColumnUsageLog(StartUsageRequest request, User user, HplcColumn hplcColumn) {
        if ( request == null && user == null && hplcColumn == null ) {
            return null;
        }

        ColumnUsageLog columnUsageLog = new ColumnUsageLog();

        if ( request != null ) {
            columnUsageLog.setTaskNumber( request.getTaskNumber() );
            columnUsageLog.setDrugName( request.getDrugName() );
        }
        if ( hplcColumn != null ) {
            columnUsageLog.setMaxPressure( hplcColumn.getMaxPressure() );
        }
        columnUsageLog.setUser( user );

        return columnUsageLog;
    }

    @Override
    public UsageLogResponseDto toUsageLogResponseDto(ColumnUsageLog columnUsageLog) {
        if ( columnUsageLog == null ) {
            return null;
        }

        UsageLogResponseDto usageLogResponseDto = new UsageLogResponseDto();

        usageLogResponseDto.setId( columnUsageLog.getId() );
        usageLogResponseDto.setUser( userMapper.toUserShortResponseDto( columnUsageLog.getUser() ) );
        usageLogResponseDto.setHplcColumn( hplcColumnMapper.toColumnResponseDto( columnUsageLog.getHplcColumn() ) );
        usageLogResponseDto.setTaskNumber( columnUsageLog.getTaskNumber() );
        usageLogResponseDto.setDrugName( columnUsageLog.getDrugName() );
        usageLogResponseDto.setAnalysisParameters( columnUsageLog.getAnalysisParameters() );
        usageLogResponseDto.setStoragePhase( columnUsageLog.getStoragePhase() );
        usageLogResponseDto.setMinPressure( columnUsageLog.getMinPressure() );
        usageLogResponseDto.setMaxPressure( columnUsageLog.getMaxPressure() );
        usageLogResponseDto.setStartDate( columnUsageLog.getStartDate() );
        usageLogResponseDto.setEndDate( columnUsageLog.getEndDate() );
        usageLogResponseDto.setRejectionReason( columnUsageLog.getRejectionReason() );

        return usageLogResponseDto;
    }

    @Override
    public ColumnUsageLog updateFromDto(ColumnUsageLog columnUsageLog, CorrectUsageLogRequest dto) {
        if ( dto == null ) {
            return columnUsageLog;
        }

        if ( dto.getTaskNumber() != null ) {
            columnUsageLog.setTaskNumber( dto.getTaskNumber() );
        }
        if ( dto.getDrugName() != null ) {
            columnUsageLog.setDrugName( dto.getDrugName() );
        }
        if ( dto.getAnalysisParameters() != null ) {
            columnUsageLog.setAnalysisParameters( dto.getAnalysisParameters() );
        }
        if ( dto.getStoragePhase() != null ) {
            columnUsageLog.setStoragePhase( dto.getStoragePhase() );
        }
        if ( dto.getMinPressure() != null ) {
            columnUsageLog.setMinPressure( dto.getMinPressure() );
        }
        if ( dto.getMaxPressure() != null ) {
            columnUsageLog.setMaxPressure( dto.getMaxPressure() );
        }

        return columnUsageLog;
    }
}
