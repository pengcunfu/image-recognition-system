package com.pcf.recognition.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pcf.recognition.entity.KnowledgeCategory;
import com.pcf.recognition.entity.KnowledgeItem;
import com.pcf.recognition.repository.KnowledgeCategoryRepository;
import com.pcf.recognition.repository.KnowledgeItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

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
    public Map<String, Object> getKnowledgeCategories() {
        log.info("获取知识分类");
        
        try {
            List<KnowledgeCategory> categories = knowledgeCategoryRepository.selectList(
                new LambdaQueryWrapper<KnowledgeCategory>()
                    .eq(KnowledgeCategory::getStatus, KnowledgeCategory.CategoryStatus.ACTIVE)
                    .orderByAsc(KnowledgeCategory::getSortOrder, KnowledgeCategory::getId)
            );
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", categories);
            
            return response;
            
        } catch (Exception e) {
            log.error("获取知识分类失败", e);
            return createErrorResponse("获取分类失败");
        }
    }

    /**
     * 获取知识条目列表
     */
    public Map<String, Object> getKnowledgeItems(String category, Integer page, Integer size, String keyword) {
        log.info("获取知识条目列表: category={}, page={}, size={}, keyword={}", category, page, size, keyword);
        
        try {
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
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", result.getRecords());
            response.put("total", result.getTotal());
            response.put("pages", result.getPages());
            response.put("current", result.getCurrent());
            response.put("size", result.getSize());
            
            return response;
            
        } catch (Exception e) {
            log.error("获取知识条目列表失败", e);
            return createErrorResponse("获取知识条目失败");
        }
    }

    /**
     * 获取知识条目详情
     */
    public Map<String, Object> getKnowledgeDetail(String id) {
        log.info("获取知识条目详情: id={}", id);
        
        try {
            KnowledgeItem item = knowledgeItemRepository.selectById(Long.parseLong(id));
            
            if (item == null || item.getStatus() != KnowledgeItem.ItemStatus.PUBLISHED) {
                return createErrorResponse("知识条目不存在");
            }
            
            // 增加浏览量
            knowledgeItemRepository.update(null,
                new LambdaUpdateWrapper<KnowledgeItem>()
                    .eq(KnowledgeItem::getId, id)
                    .setSql("view_count = view_count + 1")
            );
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", item);
            
            return response;
            
        } catch (Exception e) {
            log.error("获取知识条目详情失败", e);
            return createErrorResponse("获取详情失败");
        }
    }

    /**
     * 搜索知识条目
     */
    public Map<String, Object> searchKnowledge(String keyword, Integer page, Integer size) {
        log.info("搜索知识条目: keyword={}, page={}, size={}", keyword, page, size);
        
        try {
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
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", result.getRecords());
            response.put("total", result.getTotal());
            response.put("pages", result.getPages());
            response.put("current", result.getCurrent());
            response.put("size", result.getSize());
            response.put("keyword", keyword);
            
            return response;
            
        } catch (Exception e) {
            log.error("搜索知识条目失败", e);
            return createErrorResponse("搜索失败");
        }
    }

    /**
     * 获取热门知识条目
     */
    public Map<String, Object> getPopularKnowledge(Integer limit) {
        log.info("获取热门知识条目: limit={}", limit);
        
        try {
            List<KnowledgeItem> popularItems = knowledgeItemRepository.selectList(
                new LambdaQueryWrapper<KnowledgeItem>()
                    .eq(KnowledgeItem::getStatus, KnowledgeItem.ItemStatus.PUBLISHED)
                    .orderByDesc(KnowledgeItem::getViewCount, KnowledgeItem::getLikeCount)
                    .last("LIMIT " + limit)
            );
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", popularItems);
            
            return response;
            
        } catch (Exception e) {
            log.error("获取热门知识条目失败", e);
            return createErrorResponse("获取热门条目失败");
        }
    }

    /**
     * 获取知识统计信息
     */
    public Map<String, Object> getKnowledgeStats() {
        log.info("获取知识统计信息");
        
        try {
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
                    .select(KnowledgeItem::getViewCount)
                    .eq(KnowledgeItem::getStatus, KnowledgeItem.ItemStatus.PUBLISHED)
            );
            
            long totalViews = allItems.stream()
                .mapToLong(item -> item.getViewCount() != null ? item.getViewCount() : 0)
                .sum();
            
            Map<String, Object> stats = new HashMap<>();
            stats.put("totalCategories", totalCategories);
            stats.put("totalItems", totalItems);
            stats.put("totalViews", totalViews);
            stats.put("monthlyGrowth", 8.5); // 模拟数据，实际应该计算
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("stats", stats);
            
            return response;
            
        } catch (Exception e) {
            log.error("获取知识统计信息失败", e);
            return createErrorResponse("获取统计失败");
        }
    }

    /**
     * 创建知识分类
     */
    public Map<String, Object> createCategory(String name, String key, String description, String image) {
        log.info("创建知识分类: name={}, key={}", name, key);
        
        try {
            // 检查key是否已存在
            KnowledgeCategory existing = knowledgeCategoryRepository.selectOne(
                new LambdaQueryWrapper<KnowledgeCategory>()
                    .eq(KnowledgeCategory::getKey, key)
            );
            
            if (existing != null) {
                return createErrorResponse("分类键值已存在");
            }
            
            KnowledgeCategory category = new KnowledgeCategory();
            category.setName(name);
            category.setKey(key);
            category.setDescription(description);
            category.setImage(image);
            category.setStatus(KnowledgeCategory.CategoryStatus.ACTIVE);
            
            knowledgeCategoryRepository.insert(category);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "分类创建成功");
            response.put("categoryId", category.getId());
            
            return response;
            
        } catch (Exception e) {
            log.error("创建知识分类失败", e);
            return createErrorResponse("创建分类失败");
        }
    }

    /**
     * 创建知识条目
     */
    public Map<String, Object> createKnowledgeItem(Long categoryId, String name, String key, String description, 
                                                  String content, Long authorId) {
        log.info("创建知识条目: name={}, key={}, categoryId={}", name, key, categoryId);
        
        try {
            // 检查分类是否存在
            KnowledgeCategory category = knowledgeCategoryRepository.selectById(categoryId);
            if (category == null) {
                return createErrorResponse("分类不存在");
            }
            
            // 检查key是否已存在
            KnowledgeItem existing = knowledgeItemRepository.selectOne(
                new LambdaQueryWrapper<KnowledgeItem>()
                    .eq(KnowledgeItem::getKey, key)
            );
            
            if (existing != null) {
                return createErrorResponse("知识条目键值已存在");
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
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "知识条目创建成功");
            response.put("itemId", item.getId());
            
            return response;
            
        } catch (Exception e) {
            log.error("创建知识条目失败", e);
            return createErrorResponse("创建知识条目失败");
        }
    }

    /**
     * 点赞知识条目
     */
    public Map<String, Object> likeKnowledgeItem(Long itemId, Long userId) {
        log.info("点赞知识条目: itemId={}, userId={}", itemId, userId);
        
        try {
            KnowledgeItem item = knowledgeItemRepository.selectById(itemId);
            if (item == null) {
                return createErrorResponse("知识条目不存在");
            }
            
            // 增加点赞数
            knowledgeItemRepository.update(null,
                new LambdaUpdateWrapper<KnowledgeItem>()
                    .eq(KnowledgeItem::getId, itemId)
                    .setSql("like_count = like_count + 1")
            );
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "点赞成功");
            
            return response;
            
        } catch (Exception e) {
            log.error("点赞知识条目失败", e);
            return createErrorResponse("点赞失败");
        }
    }
    
    /**
     * 创建错误响应
     */
    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", message);
        return response;
    }
}