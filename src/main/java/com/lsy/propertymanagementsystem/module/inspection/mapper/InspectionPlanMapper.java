package com.lsy.propertymanagementsystem.module.inspection.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lsy.propertymanagementsystem.module.inspection.domain.InspectionPlanDomain;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface InspectionPlanMapper extends BaseMapper<InspectionPlanDomain> {

    @Select("<script>SELECT * FROM inspection_plan WHERE id IN " +
            "<foreach collection='planIds' item='pid' open='(' separator=',' close=')'>#{pid}</foreach> " +
            "ORDER BY create_time DESC</script>")
    List<InspectionPlanDomain> selectByIdsIgnoreDeleted(@Param("planIds") List<Long> planIds);
}
