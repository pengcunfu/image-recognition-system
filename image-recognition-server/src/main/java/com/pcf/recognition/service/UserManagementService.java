package com.pcf.recognition.service;

import com.pcf.recognition.entity.User;
import com.pcf.recognition.entity.UserFavorite;
import com.pcf.recognition.entity.RecognitionHistory;
import com.pcf.recognition.repository.UserRepository;
import com.pcf.recognition.repository.UserFavoriteRepository;
import com.pcf.recognition.repository.RecognitionHistoryRepository;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 用户管理服务 - 纯ORM实现
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class UserManagementService {

    private final UserRepository userRepository;
    private final UserFavoriteRepository favoriteRepository;
    private final RecognitionHistoryRepository historyRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 根据用户名或邮箱查找用户（MyBatis Plus方式）
     */
    public Optional<User> findByUsernameOrEmail(String usernameOrEmail) {
        log.info("查找用户: usernameOrEmail={}", usernameOrEmail);
        
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, usernameOrEmail)
               .or()
               .eq(User::getEmail, usernameOrEmail);
        
        User user = userRepository.selectOne(wrapper);
        return Optional.ofNullable(user);
    }

    /**
     * 创建新用户
     */
    @Transactional
    public User createUser(String username, String email, String password, String name) {
        log.info("创建用户: username={}, email={}", username, email);
        
        // 检查用户名是否已存在
        LambdaQueryWrapper<User> usernameWrapper = new LambdaQueryWrapper<>();
        usernameWrapper.eq(User::getUsername, username);
        if (userRepository.selectCount(usernameWrapper) > 0) {
            throw new RuntimeException("用户名已存在");
        }
        
        // 检查邮箱是否已存在
        LambdaQueryWrapper<User> emailWrapper = new LambdaQueryWrapper<>();
        emailWrapper.eq(User::getEmail, email);
        if (userRepository.selectCount(emailWrapper) > 0) {
            throw new RuntimeException("邮箱已被注册");
        }
        
        // 创建用户实体
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setName(name);
        user.setRole(User.UserRole.USER);
        user.setStatus(User.UserStatus.ACTIVE);
        user.setVipLevel(0);
        
        userRepository.insert(user);
        User savedUser = user;
        log.info("用户创建成功: userId={}", savedUser.getId());
        
        return savedUser;
    }

    /**
     * 更新用户信息
     */
    @Transactional
    public User updateUserProfile(Long userId, String name, String email, String phone, String bio) {
        log.info("更新用户信息: userId={}", userId);
        
        User user = userRepository.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        // 如果邮箱变更，检查是否已被使用
        if (email != null && !email.equals(user.getEmail())) {
            LambdaQueryWrapper<User> emailWrapper = new LambdaQueryWrapper<>();
            emailWrapper.eq(User::getEmail, email)
                       .ne(User::getId, userId); // 排除当前用户
            if (userRepository.selectCount(emailWrapper) > 0) {
                throw new RuntimeException("邮箱已被其他用户使用");
            }
            user.setEmail(email);
        }
        
        // 更新其他信息
        if (name != null) user.setName(name);
        if (phone != null) user.setPhone(phone);
        if (bio != null) user.setBio(bio);
        
        userRepository.updateById(user);
        User updatedUser = user;
        log.info("用户信息更新成功: userId={}", userId);
        
        return updatedUser;
    }

    /**
     * 修改密码
     */
    @Transactional
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        log.info("修改密码: userId={}", userId);
        
        User user = userDao.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        // 验证原密码
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("原密码错误");
        }
        
        // 更新密码
        user.setPassword(passwordEncoder.encode(newPassword));
        userDao.save(user);
        
        log.info("密码修改成功: userId={}", userId);
    }

    /**
     * 获取用户统计数据（纯ORM方式）
     */
    public Map<String, Object> getUserStats(Long userId) {
        log.info("获取用户统计数据: userId={}", userId);
        
        Map<String, Object> stats = new HashMap<>();
        
        // 总识别次数
        long totalRecognitions = historyDao.countByUserId(userId);
        stats.put("totalRecognitions", totalRecognitions);
        
        // 本月识别次数
        LocalDateTime startOfMonth = YearMonth.now().atDay(1).atStartOfDay();
        long thisMonthRecognitions = historyDao.countByUserIdSince(userId, startOfMonth);
        stats.put("thisMonthRecognitions", thisMonthRecognitions);
        
        // 收藏数量
        long favorites = favoriteDao.countByUserId(userId);
        stats.put("favorites", favorites);
        
        // 计算平均置信度（通过查询所有记录计算）
        List<RecognitionHistory> allHistories = historyDao.findRecentByUserId(userId, 1000);
        
        if (!allHistories.isEmpty()) {
            double avgConfidence = allHistories.stream()
                    .mapToInt(RecognitionHistory::getConfidence)
                    .average()
                    .orElse(0.0);
            stats.put("averageConfidence", Math.round(avgConfidence * 10) / 10.0);
        } else {
            stats.put("averageConfidence", 0.0);
        }
        
        // 其他统计数据
        stats.put("discussions", 8); // 模拟数据
        stats.put("likes", 45); // 模拟数据
        stats.put("shares", 12); // 模拟数据
        
        return stats;
    }

    /**
     * 获取用户收藏列表
     */
    public Page<UserFavorite> getUserFavorites(Long userId, int page, int size) {
        log.info("获取用户收藏列表: userId={}, page={}, size={}", userId, page, size);
        
        Pageable pageable = PageRequest.of(page - 1, size);
        return favoriteDao.findByUserId(userId, pageable);
    }

    /**
     * 添加收藏
     */
    @Transactional
    public UserFavorite addToFavorites(Long userId, Long recognitionId, String tags) {
        log.info("添加收藏: userId={}, recognitionId={}", userId, recognitionId);
        
        // 检查是否已收藏
        if (favoriteDao.existsByUserIdAndRecognitionId(userId, recognitionId)) {
            throw new RuntimeException("该识别结果已在收藏夹中");
        }
        
        // 验证识别记录存在
        RecognitionHistory history = historyDao.findById(recognitionId)
                .orElseThrow(() -> new RuntimeException("识别记录不存在"));
        
        // 验证记录属于当前用户
        if (!history.getUserId().equals(userId)) {
            throw new RuntimeException("无权限收藏该记录");
        }
        
        // 创建收藏
        UserFavorite favorite = new UserFavorite();
        favorite.setUserId(userId);
        favorite.setRecognitionId(recognitionId);
        favorite.setTags(tags);
        
        UserFavorite savedFavorite = favoriteDao.save(favorite);
        
        // 更新识别记录的收藏状态
        history.setIsFavorite(true);
        historyDao.save(history);
        
        log.info("收藏添加成功: favoriteId={}", savedFavorite.getId());
        return savedFavorite;
    }

    /**
     * 取消收藏
     */
    @Transactional
    public void removeFromFavorites(Long userId, Long favoriteId) {
        log.info("取消收藏: userId={}, favoriteId={}", userId, favoriteId);
        
        UserFavorite favorite = favoriteDao.findById(favoriteId)
                .orElseThrow(() -> new RuntimeException("收藏不存在"));
        
        // 验证收藏属于当前用户
        if (!favorite.getUserId().equals(userId)) {
            throw new RuntimeException("无权限删除该收藏");
        }
        
        // 更新识别记录的收藏状态
        RecognitionHistory history = historyDao.findById(favorite.getRecognitionId())
                .orElse(null);
        if (history != null) {
            history.setIsFavorite(false);
            historyDao.save(history);
        }
        
        // 删除收藏
        favoriteDao.deleteById(favoriteId);
        
        log.info("收藏删除成功: favoriteId={}", favoriteId);
    }

    /**
     * 更新用户登录时间
     */
    @Transactional
    public void updateLastLoginTime(Long userId) {
        User user = userDao.findById(userId).orElse(null);
        if (user != null) {
            user.setLastLoginTime(LocalDateTime.now());
            userDao.save(user);
        }
    }

    /**
     * 获取激活用户总数
     */
    public long getActiveUserCount() {
        return userDao.countByStatus(User.UserStatus.ACTIVE);
    }

    /**
     * 根据角色获取用户列表
     */
    public List<User> getUsersByRole(User.UserRole role) {
        return userDao.findByRole(role);
    }
}
