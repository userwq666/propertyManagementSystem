package com.lsy.propertymanagementsystem.module.inspection.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lsy.propertymanagementsystem.module.inspection.domain.InspectionPlanDomain;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface InspectionPlanMapper extends BaseMapper<InspectionPlanDomain> {
}