package com.heima.search.dto;

import com.heima.search.entity.ApUserSearch;
import lombok.Data;

import java.util.List;

@Data
public class UserSearchDto extends ApArticleSearchDto {

    List<ApUserSearch> hisList;
}
