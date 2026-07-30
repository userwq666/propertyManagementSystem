package com.lsy.propertymanagementsystem.module.fee.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lsy.propertymanagementsystem.module.fee.domain.FeeRecordDomain;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Mapper;

import java.math.BigDecimal;

@Mapper
public interface FeeRecordMapper extends BaseMapper<FeeRecordDomain> {

    @Select("SELECT COALESCE(SUM(amount), 0) FROM fee_record WHERE status = #{status} AND deleted = 0")
    BigDecimal sumAmountByStatus(@Param("status") Integer status);
}