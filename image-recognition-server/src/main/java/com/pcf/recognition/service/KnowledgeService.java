package com.pcf.recognition.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pcf.recognition.dto.AuthDto.*;
import com.pcf.recognition.dto.KnowledgeDto.*;
import com.pcf.recognition.entity.KnowledgeCategory;
import com.pcf.recognition.entity.KnowledgeItem;
import com.pcf.recognition.repository.KnowledgeCategoryRepository;
import com.pcf.recognition.repository.KnowledgeItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 知识库服务
 * 处理知识库浏览、搜索等业务逻辑
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class KnowledgeService {

    private final KnowledgeCategoryRepository knowledgeCategoryRepository;
    private final KnowledgeItemRepository knowledgeItemRepository;

    /**
     * 获取知识分类
     */
    public List<KnowledgeCategoryDto> getKnowledgeCategories(Integer status, String keyword) {
        log.info("获取知识分类: status={}, keyword={}", status, keyword);

        LambdaQueryWrapper<KnowledgeCategory> queryWrapper = new LambdaQueryWrapper<>();
        
        // 状态筛选
        if (status != null) {
            try {
                KnowledgeCategory.CategoryStatus categoryStatus = KnowledgeCategory.CategoryStatus.fromValue(status);
                queryWrapper.eq(KnowledgeCategory::getStatus, categoryStatus);
            } catch (IllegalArgumentException e) {
                log.warn("无效的状态值: {}", status);
            }
        }
        
        // 关键词搜索（搜索名称、键值和描述）
        if (keyword != null && !keyword.isEmpty()) {
            queryWrapper.and(wrapper -> 
                wrapper.like(KnowledgeCategory::getName, keyword)
                       .or()
                       .like(KnowledgeCategory::getKey, keyword)
                       .or()
                       .like(KnowledgeCategory::getDescription, keyword)
            );
        }
        
        queryWrapper.orderByAsc(KnowledgeCategory::getSortOrder, KnowledgeCategory::getId);

        List<KnowledgeCategory> categories = knowledgeCategoryRepository.selectList(queryWrapper);

        return categories.stream()
                .map(this::convertToCategoryDto)
                .collect(Collectors.toList());
    }

    /**
     * 获取知识条目列表
     */
    public KnowledgePageDto getKnowledgeItems(String category, Integer page, Integer size, String keyword) {
        log.info("获取知识条目列表: category={}, page={}, size={}, keyword={}", category, page, size, keyword);

        Page<KnowledgeItem> pageRequest = new Page<>(page, size);

        LambdaQueryWrapper<KnowledgeItem> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(KnowledgeItem::getStatus, KnowledgeItem.ItemStatus.PUBLISHED);

        // 按分类筛选
        if (category != null && !category.isEmpty()) {
            // 先查找分类ID
            KnowledgeCategory categoryEntity = knowledgeCategoryRepository.selectOne(
                    new LambdaQueryWrapper<KnowledgeCategory>()
                            .eq(KnowledgeCategory::getKey, category)
            );
            if (categoryEntity != null) {
                queryWrapper.eq(KnowledgeItem::getCategoryId, categoryEntity.getId());
            }
        }

        // 按关键词搜索
        if (keyword != null && !keyword.isEmpty()) {
            queryWrapper.and(wrapper -> wrapper
                    .like(KnowledgeItem::getName, keyword)
                    .or()
                    .like(KnowledgeItem::getDescription, keyword)
                    .or()
                    .like(KnowledgeItem::getTags, keyword)
            );
        }

        queryWrapper.orderByDesc(KnowledgeItem::getViewCount, KnowledgeItem::getId);

        Page<KnowledgeItem> result = knowledgeItemRepository.selectPage(pageRequest, queryWrapper);

        List<KnowledgeItemDto> itemDtos = result.getRecords().stream()
                .map(this::convertToItemDto)
                .collect(Collectors.toList());

        return KnowledgePageDto.builder()
                .items(itemDtos)
                .total(result.getTotal())
                .pages(result.getPages())
                .current(result.getCurrent())
                .size(result.getSize())
                .keyword(keyword)
                .category(category)
                .build();
    }

    /**
     * 获取知识条目详情
     */
    public KnowledgeItemDto getKnowledgeDetail(String id) {
        log.info("获取知识条目详情: id={}", id);

        KnowledgeItem item = knowledgeItemRepository.selectById(Long.parseLong(id));

        if (item == null || item.getStatus() != KnowledgeItem.ItemStatus.PUBLISHED) {
            return null;
        }

        // 增加浏览量
        knowledgeItemRepository.update(null,
                new LambdaUpdateWrapper<KnowledgeItem>()
                        .eq(KnowledgeItem::getId, id)
                        .setSql("view_count = view_count + 1")
        );

        return convertToItemDto(item);
    }

    /**
     * 搜索知识条目
     */
    public KnowledgePageDto searchKnowledge(String keyword, Integer page, Integer size) {
        log.info("搜索知识条目: keyword={}, page={}, size={}", keyword, page, size);

        Page<KnowledgeItem> pageRequest = new Page<>(page, size);

        LambdaQueryWrapper<KnowledgeItem> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(KnowledgeItem::getStatus, KnowledgeItem.ItemStatus.PUBLISHED)
                .and(wrapper -> wrapper
                        .like(KnowledgeItem::getName, keyword)
                        .or()
                        .like(KnowledgeItem::getDescription, keyword)
                        .or()
                        .like(KnowledgeItem::getContent, keyword)
                        .or()
                        .like(KnowledgeItem::getTags, keyword)
                        .or()
                        .like(KnowledgeItem::getScientificName, keyword)
                )
                .orderByDesc(KnowledgeItem::getViewCount);

        Page<KnowledgeItem> result = knowledgeItemRepository.selectPage(pageRequest, queryWrapper);

        List<KnowledgeItemDto> itemDtos = result.getRecords().stream()
                .map(this::convertToItemDto)
                .collect(Collectors.toList());

        return KnowledgePageDto.builder()
                .items(itemDtos)
                .total(result.getTotal())
                .pages(result.getPages())
                .current(result.getCurrent())
                .size(result.getSize())
                .keyword(keyword)
                .build();
    }

    /**
     * 获取热门知识条目
     */
    public List<KnowledgeItemDto> getPopularKnowledge(Integer limit) {
        log.info("获取热门知识条目: limit={}", limit);

        List<KnowledgeItem> popularItems = knowledgeItemRepository.selectList(
                new LambdaQueryWrapper<KnowledgeItem>()
                        .eq(KnowledgeItem::getStatus, KnowledgeItem.ItemStatus.PUBLISHED)
                        .orderByDesc(KnowledgeItem::getViewCount, KnowledgeItem::getLikeCount)
                        .last("LIMIT " + limit)
        );

        return popularItems.stream()
                .map(this::convertToItemDto)
                .collect(Collectors.toList());
    }

    /**
     * 获取知识统计信息
     */
    public KnowledgeStatsDto getKnowledgeStats() {
        log.info("获取知识统计信息");

        // 总分类数
        Long totalCategories = knowledgeCategoryRepository.selectCount(
                new LambdaQueryWrapper<KnowledgeCategory>()
                        .eq(KnowledgeCategory::getStatus, KnowledgeCategory.CategoryStatus.ACTIVE)
        );

        // 总条目数
        Long totalItems = knowledgeItemRepository.selectCount(
                new LambdaQueryWrapper<KnowledgeItem>()
                        .eq(KnowledgeItem::getStatus, KnowledgeItem.ItemStatus.PUBLISHED)
        );

        // 总浏览量
        List<KnowledgeItem> allItems = knowledgeItemRepository.selectList(
                new LambdaQueryWrapper<KnowledgeItem>()
                        .select(KnowledgeItem::getViewCount, KnowledgeItem::getLikeCount, KnowledgeItem::getFavoriteCount)
                        .eq(KnowledgeItem::getStatus, KnowledgeItem.ItemStatus.PUBLISHED)
        );

        long totalViews = allItems.stream()
                .mapToLong(item -> item.getViewCount() != null ? item.getViewCount() : 0)
                .sum();

        long totalLikes = allItems.stream()
                .mapToLong(item -> item.getLikeCount() != null ? item.getLikeCount() : 0)
                .sum();

        long totalFavorites = allItems.stream()
                .mapToLong(item -> item.getFavoriteCount() != null ? item.getFavoriteCount() : 0)
                .sum();

        return KnowledgeStatsDto.builder()
                .totalCategories(totalCategories)
                .totalItems(totalItems)
                .totalViews(totalViews)
                .totalLikes(totalLikes)
                .totalFavorites(totalFavorites)
                .monthlyGrowth(8.5) // 模拟数据，实际应该计算
                .averageDifficulty(3.2) // 模拟数据
                .build();
    }

    /**
     * 创建知识分类
     */
    public KnowledgeCreateResponseDto createCategory(String name, String key, String description, String image) {
        log.info("创建知识分类: name={}, key={}", name, key);

        try {
            // 检查key是否已存在
            KnowledgeCategory existing = knowledgeCategoryRepository.selectOne(
                    new LambdaQueryWrapper<KnowledgeCategory>()
                            .eq(KnowledgeCategory::getKey, key)
            );

            if (existing != null) {
                return KnowledgeCreateResponseDto.builder()
                        .success(false)
                        .message("分类键值已存在")
                        .build();
            }

            KnowledgeCategory category = new KnowledgeCategory();
            category.setName(name);
            category.setKey(key);
            category.setDescription(description);
            category.setImage(image);
            category.setStatus(KnowledgeCategory.CategoryStatus.ACTIVE);

            knowledgeCategoryRepository.insert(category);

            return KnowledgeCreateResponseDto.builder()
                    .success(true)
                    .message("分类创建成功")
                    .id(category.getId())
                    .build();

        } catch (Exception e) {
            log.error("创建知识分类失败", e);
            return KnowledgeCreateResponseDto.builder()
                    .success(false)
                    .message("创建分类失败")
                    .build();
        }
    }

    /**
     * 创建知识条目
     */
    public KnowledgeCreateResponseDto createKnowledgeItem(Long categoryId, String name, String key, String description,
                                                          String content, Long authorId) {
        log.info("创建知识条目: name={}, key={}, categoryId={}", name, key, categoryId);

        try {
            // 检查分类是否存在
            KnowledgeCategory category = knowledgeCategoryRepository.selectById(categoryId);
            if (category == null) {
                return KnowledgeCreateResponseDto.builder()
                        .success(false)
                        .message("分类不存在")
                        .build();
            }

            // 检查key是否已存在
            KnowledgeItem existing = knowledgeItemRepository.selectOne(
                    new LambdaQueryWrapper<KnowledgeItem>()
                            .eq(KnowledgeItem::getKey, key)
            );

            if (existing != null) {
                return KnowledgeCreateResponseDto.builder()
                        .success(false)
                        .message("知识条目键值已存在")
                        .build();
            }

            KnowledgeItem item = new KnowledgeItem();
            item.setCategoryId(categoryId);
            item.setName(name);
            item.setKey(key);
            item.setDescription(description);
            item.setContent(content);
            item.setAuthorId(authorId);
            item.setStatus(KnowledgeItem.ItemStatus.PUBLISHED);

            knowledgeItemRepository.insert(item);

            // 更新分类的条目数量
            knowledgeCategoryRepository.update(null,
                    new LambdaUpdateWrapper<KnowledgeCategory>()
                            .eq(KnowledgeCategory::getId, categoryId)
                            .setSql("item_count = item_count + 1")
            );

            return KnowledgeCreateResponseDto.builder()
                    .success(true)
                    .message("知识条目创建成功")
                    .id(item.getId())
                    .build();

        } catch (Exception e) {
            log.error("创建知识条目失败", e);
            return KnowledgeCreateResponseDto.builder()
                    .success(false)
                    .message("创建知识条目失败")
                    .build();
        }
    }

    /**
     * 搜索知识库
     */
    public KnowledgeSearchResultDto searchKnowledge(String keyword, Integer page, Integer size, String category) {
        log.info("搜索知识库: keyword={}, page={}, size={}, category={}", keyword, page, size, category);

        try {
            // 构建查询条件
            LambdaQueryWrapper<KnowledgeItem> queryWrapper = new LambdaQueryWrapper<KnowledgeItem>()
                    .eq(KnowledgeItem::getStatus, KnowledgeItem.ItemStatus.PUBLISHED)
                    .and(wrapper -> wrapper
                            .like(KnowledgeItem::getName, keyword)
                            .or()
                            .like(KnowledgeItem::getDescription, keyword)
                            .or()
                            .like(KnowledgeItem::getContent, keyword)
                    );

            // 如果指定了分类，添加分类过滤
            if (category != null && !category.isEmpty()) {
                KnowledgeCategory cat = knowledgeCategoryRepository.selectOne(
                        new LambdaQueryWrapper<KnowledgeCategory>()
                                .eq(KnowledgeCategory::getKey, category)
                );
                if (cat != null) {
                    queryWrapper.eq(KnowledgeItem::getCategoryId, cat.getId());
                }
            }

            // 计算总数
            Long total = knowledgeItemRepository.selectCount(queryWrapper);

            // 分页查询
            queryWrapper.orderByDesc(KnowledgeItem::getViewCount, KnowledgeItem::getLikeCount)
                    .last("LIMIT " + ((page - 1) * size) + ", " + size);

            List<KnowledgeItem> items = knowledgeItemRepository.selectList(queryWrapper);

            // 转换为DTO格式
            List<KnowledgeItemDto> itemDtos = items.stream()
                    .map(this::convertToItemDto)
                    .toList();

            return KnowledgeSearchResultDto.builder()
                    .items(itemDtos)
                    .total(total.intValue())
                    .page(page)
                    .size(size)
                    .pages((int) Math.ceil((double) total / size))
                    .keyword(keyword)
                    .category(category)
                    .build();

        } catch (Exception e) {
            log.error("搜索知识库失败", e);
            return KnowledgeSearchResultDto.builder()
                    .items(Collections.emptyList())
                    .total(0)
                    .page(page)
                    .size(size)
                    .pages(0)
                    .keyword(keyword)
                    .category(category)
                    .build();
        }
    }

    /**
     * 获取最新知识
     */
    public List<KnowledgeItemDto> getLatestKnowledge(Integer limit) {
        log.info("获取最新知识: limit={}", limit);

        try {
            List<KnowledgeItem> latestItems = knowledgeItemRepository.selectList(
                    new LambdaQueryWrapper<KnowledgeItem>()
                            .eq(KnowledgeItem::getStatus, KnowledgeItem.ItemStatus.PUBLISHED)
                            .orderByDesc(KnowledgeItem::getCreateTime)
                            .last("LIMIT " + limit)
            );

            return latestItems.stream()
                    .map(this::convertToItemDto)
                    .toList();

        } catch (Exception e) {
            log.error("获取最新知识失败", e);
            return Collections.emptyList();
        }
    }

    /**
     * 点赞知识条目
     */
    public OperationResultDto likeKnowledgeItem(Long itemId) {
        log.info("点赞知识条目: itemId={}", itemId);

        try {
            KnowledgeItem item = knowledgeItemRepository.selectById(itemId);
            if (item == null) {
                return OperationResultDto.builder()
                        .success(false)
                        .message("知识条目不存在")
                        .build();
            }

            // 增加点赞数
            knowledgeItemRepository.update(null,
                    new LambdaUpdateWrapper<KnowledgeItem>()
                            .eq(KnowledgeItem::getId, itemId)
                            .setSql("like_count = like_count + 1")
            );

            return OperationResultDto.builder()
                    .success(true)
                    .message("点赞成功")
                    .build();

        } catch (Exception e) {
            log.error("点赞知识条目失败", e);
            return OperationResultDto.builder()
                    .success(false)
                    .message("点赞失败")
                    .build();
        }
    }

    /**
     * 取消点赞知识条目
     */
    public OperationResultDto unlikeKnowledgeItem(Long itemId) {
        log.info("取消点赞知识条目: itemId={}", itemId);

        try {
            KnowledgeItem item = knowledgeItemRepository.selectById(itemId);
            if (item == null) {
                return OperationResultDto.builder()
                        .success(false)
                        .message("知识条目不存在")
                        .build();
            }

            // 减少点赞数
            knowledgeItemRepository.update(null,
                    new LambdaUpdateWrapper<KnowledgeItem>()
                            .eq(KnowledgeItem::getId, itemId)
                            .setSql("like_count = GREATEST(like_count - 1, 0)")
            );

            return OperationResultDto.builder()
                    .success(true)
                    .message("取消点赞成功")
                    .build();

        } catch (Exception e) {
            log.error("取消点赞知识条目失败", e);
            return OperationResultDto.builder()
                    .success(false)
                    .message("取消点赞失败")
                    .build();
        }
    }


    /**
     * 转换KnowledgeCategory为DTO
     */
    private KnowledgeCategoryDto convertToCategoryDto(KnowledgeCategory category) {
        return KnowledgeCategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .key(category.getKey())
                .description(category.getDescription())
                .image(category.getImage())
                .itemCount(category.getItemCount())
                .sortOrder(category.getSortOrder())
                .status(category.getStatus() != null ? category.getStatus().name() : null)
                .createTime(category.getCreateTime())
                .updateTime(category.getUpdateTime())
                .build();
    }

    /**
     * 转换KnowledgeItem为DTO
     */
    private KnowledgeItemDto convertToItemDto(KnowledgeItem item) {
        return KnowledgeItemDto.builder()
                .id(item.getId())
                .categoryId(item.getCategoryId())
                .name(item.getName())
                .key(item.getKey())
                .scientificName(item.getScientificName())
                .description(item.getDescription())
                .content(item.getContent())
                .images(item.getImages())
                .characteristics(item.getCharacteristics())
                .habitat(item.getHabitat())
                .lifespan(item.getLifespan())
                .relatedItems(item.getRelatedItems())
                .tags(item.getTags())
                .viewCount(item.getViewCount())
                .likeCount(item.getLikeCount())
                .favoriteCount(item.getFavoriteCount())
                .shareCount(item.getShareCount())
                .difficulty(item.getDifficulty())
                .sortOrder(item.getSortOrder())
                .status(item.getStatus() != null ? item.getStatus().name() : null)
                .authorId(item.getAuthorId())
                .reviewerId(item.getReviewerId())
                .reviewTime(item.getReviewTime())
                .createTime(item.getCreateTime())
                .updateTime(item.getUpdateTime())
                .build();
    }
}