package com.linkme.backend.mapper;

import com.linkme.backend.entity.Hobby;
import com.linkme.backend.entity.HobbyCategory;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface HobbyMapper {

    List<HobbyCategory> selectAllCategories();

    List<Hobby> selectAllHobbies();
}
