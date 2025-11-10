<template>
  <div class="about-page">
    <!-- 顶部横幅 -->
    <div class="hero-section">
      <div class="hero-content">
        <h1>关于我们</h1>
        <p>致力于打造最先进的智能图像识别平台</p>
      </div>
    </div>

    <!-- 主要内容 -->
    <div class="main-content">
      <a-row :gutter="[24, 24]">
        <!-- 公司简介 -->
        <a-col :xs="24" :lg="12">
          <a-card title="公司简介" class="content-card">
            <div class="company-intro">
              <p>智能图像识别系统是一家专注于人工智能和计算机视觉技术的创新公司。我们致力于为用户提供最先进、最准确的图像识别服务。</p>
              
              <p>自成立以来，我们始终坚持技术创新和用户体验并重的理念，不断推动人工智能技术在图像识别领域的应用和发展。</p>
              
              <div class="features">
                <div class="feature-item">
                  <i class="fas fa-eye"></i>
                  <div>
                    <h4>先进算法</h4>
                    <p>采用最新的深度学习算法，确保识别准确率</p>
                  </div>
                </div>
                <div class="feature-item">
                  <i class="fas fa-rocket"></i>
                  <div>
                    <h4>快速响应</h4>
                    <p>毫秒级识别速度，提供实时图像分析</p>
                  </div>
                </div>
                <div class="feature-item">
                  <i class="fas fa-shield-alt"></i>
                  <div>
                    <h4>安全可靠</h4>
                    <p>企业级安全防护，保障用户数据安全</p>
                  </div>
                </div>
              </div>
            </div>
          </a-card>
        </a-col>

        <!-- 发展历程 -->
        <a-col :xs="24" :lg="12">
          <a-card title="发展历程" class="content-card">
            <a-timeline>
              <a-timeline-item color="blue">
                <template #dot>
                  <i class="fas fa-lightbulb"></i>
                </template>
                <div class="timeline-content">
                  <h4>2024年1月</h4>
                  <p>项目启动，开始核心算法研发</p>
                </div>
              </a-timeline-item>
              <a-timeline-item color="green">
                <template #dot>
                  <i class="fas fa-code"></i>
                </template>
                <div class="timeline-content">
                  <h4>2024年6月</h4>
                  <p>完成基础识别模型训练</p>
                </div>
              </a-timeline-item>
              <a-timeline-item color="orange">
                <template #dot>
                  <i class="fas fa-globe"></i>
                </template>
                <div class="timeline-content">
                  <h4>2024年10月</h4>
                  <p>平台正式上线，开放公测</p>
                </div>
              </a-timeline-item>
              <a-timeline-item color="purple">
                <template #dot>
                  <i class="fas fa-crown"></i>
                </template>
                <div class="timeline-content">
                  <h4>2025年1月</h4>
                  <p>推出VIP服务，提供高级功能</p>
                </div>
              </a-timeline-item>
            </a-timeline>
          </a-card>
        </a-col>
      </a-row>

      <a-row :gutter="[24, 24]" style="margin-top: 24px;">
        <!-- 核心团队 -->
        <a-col :xs="24" :lg="16">
          <a-card title="核心团队" class="content-card">
            <div class="team-grid">
              <div class="team-member">
                <a-avatar size="80" class="member-avatar">
                  <template #icon>
                    <i class="fas fa-user-tie"></i>
                  </template>
                </a-avatar>
                <div class="member-info">
                  <h4>彭存福</h4>
                  <p class="role">创始人 & CTO</p>
                  <p class="desc">深度学习专家，专注于计算机视觉算法研究</p>
                  <div class="skills">
                    <a-tag color="blue">深度学习</a-tag>
                    <a-tag color="green">计算机视觉</a-tag>
                    <a-tag color="orange">算法优化</a-tag>
                  </div>
                </div>
              </div>

              <div class="team-member">
                <a-avatar size="80" class="member-avatar">
                  <template #icon>
                    <i class="fas fa-user-cog"></i>
                  </template>
                </a-avatar>
                <div class="member-info">
                  <h4>技术团队</h4>
                  <p class="role">研发工程师</p>
                  <p class="desc">负责平台架构设计和功能开发</p>
                  <div class="skills">
                    <a-tag color="purple">前端开发</a-tag>
                    <a-tag color="red">后端开发</a-tag>
                    <a-tag color="cyan">系统架构</a-tag>
                  </div>
                </div>
              </div>

              <div class="team-member">
                <a-avatar size="80" class="member-avatar">
                  <template #icon>
                    <i class="fas fa-users"></i>
                  </template>
                </a-avatar>
                <div class="member-info">
                  <h4>产品团队</h4>
                  <p class="role">产品经理</p>
                  <p class="desc">负责产品规划和用户体验优化</p>
                  <div class="skills">
                    <a-tag color="gold">产品设计</a-tag>
                    <a-tag color="lime">用户体验</a-tag>
                    <a-tag color="volcano">市场分析</a-tag>
                  </div>
                </div>
              </div>
            </div>
          </a-card>
        </a-col>

        <!-- 联系信息 -->
        <a-col :xs="24" :lg="8">
          <a-card title="联系我们" class="content-card">
            <div class="contact-info">
              <div class="contact-item">
                <i class="fas fa-envelope"></i>
                <div>
                  <strong>邮箱</strong>
                  <p>contact@image-ai.com</p>
                </div>
              </div>
              <div class="contact-item">
                <i class="fas fa-phone"></i>
                <div>
                  <strong>电话</strong>
                  <p>400-123-4567</p>
                </div>
              </div>
              <div class="contact-item">
                <i class="fas fa-map-marker-alt"></i>
                <div>
                  <strong>地址</strong>
                  <p>北京市朝阳区科技园区</p>
                </div>
              </div>
              <div class="contact-item">
                <i class="fas fa-clock"></i>
                <div>
                  <strong>工作时间</strong>
                  <p>周一至周五 9:00-18:00</p>
                </div>
              </div>
            </div>
            
            <a-divider />
            
            <div class="social-links">
              <h4>关注我们</h4>
              <div class="social-icons">
                <a-button type="primary" shape="circle" size="large">
                  <i class="fab fa-weixin"></i>
                </a-button>
                <a-button type="primary" shape="circle" size="large" style="background: #1890ff;">
                  <i class="fab fa-qq"></i>
                </a-button>
                <a-button type="primary" shape="circle" size="large" style="background: #e6162d;">
                  <i class="fab fa-weibo"></i>
                </a-button>
              </div>
            </div>
          </a-card>
        </a-col>
      </a-row>
    </div>
  </div>
</template>

<script setup lang="ts">
// 页面逻辑可以在这里添加
</script>

<style scoped>
.about-page {
  min-height: 100vh;
  background: #f0f2f5;
}

/* 顶部横幅 */
.hero-section {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 80px 0;
  text-align: center;
  margin-bottom: 40px;
}

.hero-content h1 {
  font-size: 48px;
  font-weight: bold;
  margin-bottom: 16px;
  color: white;
}

.hero-content p {
  font-size: 20px;
  opacity: 0.9;
  max-width: 600px;
  margin: 0 auto;
}

/* 主要内容 */
.main-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 24px 40px;
}

.content-card {
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
  height: 100%;
}

/* 公司简介 */
.company-intro p {
  line-height: 1.8;
  color: #666;
  margin-bottom: 20px;
}

.features {
  margin-top: 30px;
}

.feature-item {
  display: flex;
  align-items: flex-start;
  gap: 16px;
  margin-bottom: 24px;
}

.feature-item i {
  font-size: 24px;
  color: #1890ff;
  margin-top: 4px;
}

.feature-item h4 {
  color: #262626;
  margin-bottom: 8px;
  font-size: 16px;
}

.feature-item p {
  color: #666;
  margin: 0;
  line-height: 1.6;
}

/* 时间线 */
.timeline-content h4 {
  color: #262626;
  margin-bottom: 8px;
  font-size: 16px;
}

.timeline-content p {
  color: #666;
  margin: 0;
  line-height: 1.6;
}

/* 团队网格 */
.team-grid {
  display: flex;
  flex-direction: column;
  gap: 32px;
}

.team-member {
  display: flex;
  align-items: flex-start;
  gap: 20px;
  padding: 20px;
  background: #fafafa;
  border-radius: 12px;
  transition: all 0.3s;
}

.team-member:hover {
  background: #f0f2ff;
  transform: translateY(-2px);
}

.member-avatar {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  font-size: 32px;
  flex-shrink: 0;
}

.member-info h4 {
  color: #262626;
  margin-bottom: 8px;
  font-size: 18px;
}

.member-info .role {
  color: #1890ff;
  font-weight: 500;
  margin-bottom: 12px;
}

.member-info .desc {
  color: #666;
  line-height: 1.6;
  margin-bottom: 16px;
}

.skills {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

/* 联系信息 */
.contact-info {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.contact-item {
  display: flex;
  align-items: flex-start;
  gap: 12px;
}

.contact-item i {
  font-size: 18px;
  color: #1890ff;
  margin-top: 2px;
  width: 20px;
}

.contact-item strong {
  color: #262626;
  display: block;
  margin-bottom: 4px;
}

.contact-item p {
  color: #666;
  margin: 0;
  line-height: 1.6;
}

.social-links h4 {
  color: #262626;
  margin-bottom: 16px;
  text-align: center;
}

.social-icons {
  display: flex;
  justify-content: center;
  gap: 12px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .hero-content h1 {
    font-size: 32px;
  }
  
  .hero-content p {
    font-size: 16px;
  }
  
  .hero-section {
    padding: 60px 0;
  }
  
  .team-member {
    flex-direction: column;
    text-align: center;
  }
  
  .contact-item {
    flex-direction: column;
    text-align: center;
    gap: 8px;
  }
}

@media (max-width: 576px) {
  .main-content {
    padding: 0 16px 40px;
  }
  
  .hero-section {
    padding: 40px 0;
  }
  
  .team-grid {
    gap: 20px;
  }
  
  .team-member {
    padding: 16px;
  }
}
</style>
