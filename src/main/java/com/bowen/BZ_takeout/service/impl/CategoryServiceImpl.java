package com.bowen.BZ_takeout.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bowen.BZ_takeout.entity.Category;
import com.bowen.BZ_takeout.mapper.CategoryMapper;
import com.bowen.BZ_takeout.service.CategoryService;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
}
