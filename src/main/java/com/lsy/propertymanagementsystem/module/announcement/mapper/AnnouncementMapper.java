package com.lsy.propertymanagementsystem.module.announcement.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lsy.propertymanagementsystem.module.announcement.domain.AnnouncementDomain;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AnnouncementMapper extends BaseMapper<AnnouncementDomain> {
}