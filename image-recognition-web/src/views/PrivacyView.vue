<template>
  <div class="privacy-page">
    <!-- 顶部横幅 -->
    <div class="hero-section">
      <div class="hero-content">
        <h1>隐私政策</h1>
        <p>我们承诺保护您的个人信息和隐私安全</p>
        <div class="update-info">
          <span>最后更新时间：2025年1月1日</span>
        </div>
      </div>
    </div>

    <!-- 主要内容 -->
    <div class="main-content">
      <div class="privacy-container">
        <!-- 侧边导航 -->
        <div class="sidebar">
          <a-affix :offset-top="100">
            <div class="nav-menu">
              <h4>目录</h4>
              <ul>
                <li><a href="#section-1" @click="scrollToSection('section-1')">信息收集</a></li>
                <li><a href="#section-2" @click="scrollToSection('section-2')">信息使用</a></li>
                <li><a href="#section-3" @click="scrollToSection('section-3')">信息共享</a></li>
                <li><a href="#section-4" @click="scrollToSection('section-4')">信息安全</a></li>
                <li><a href="#section-5" @click="scrollToSection('section-5')">Cookie政策</a></li>
                <li><a href="#section-6" @click="scrollToSection('section-6')">用户权利</a></li>
                <li><a href="#section-7" @click="scrollToSection('section-7')">未成年人保护</a></li>
                <li><a href="#section-8" @click="scrollToSection('section-8')">政策更新</a></li>
                <li><a href="#section-9" @click="scrollToSection('section-9')">联系我们</a></li>
              </ul>
            </div>
          </a-affix>
        </div>

        <!-- 主要内容 -->
        <div class="content">
          <!-- 引言 -->
          <section class="intro-section">
            <h2>引言</h2>
            <p>欢迎使用智能图像识别系统！我们深知个人信息对您的重要性，并会尽全力保护您的个人信息安全可靠。我们致力于维持您对我们的信任，恪守以下原则，保护您的个人信息：权责一致原则、目的明确原则、选择同意原则、最少够用原则、确保安全原则、主体参与原则、公开透明原则等。</p>
            
            <a-alert
              message="重要提示"
              description="在使用我们的产品或服务前，请您仔细阅读并充分理解本隐私政策，特别是以粗体标识的条款。如果您不同意本隐私政策的任何内容，您应立即停止访问/使用我们的产品或服务。"
              type="warning"
              show-icon
              class="alert-box"
            />
          </section>

          <!-- 信息收集 -->
          <section id="section-1" class="content-section">
            <h2><i class="fas fa-database"></i> 1. 我们收集的信息</h2>
            
            <h3>1.1 您主动提供的信息</h3>
            <ul>
              <li><strong>账户信息：</strong>用户名、邮箱地址、手机号码、密码等</li>
              <li><strong>个人资料：</strong>昵称、头像、个人简介、所在地等</li>
              <li><strong>上传内容：</strong>您上传用于识别的图片文件</li>
              <li><strong>联系信息：</strong>通过联系表单提供的姓名、邮箱、电话等</li>
            </ul>

            <h3>1.2 我们自动收集的信息</h3>
            <ul>
              <li><strong>设备信息：</strong>设备型号、操作系统、浏览器类型及版本</li>
              <li><strong>网络信息：</strong>IP地址、网络类型、运营商信息</li>
              <li><strong>使用信息：</strong>访问时间、页面浏览记录、功能使用情况</li>
              <li><strong>位置信息：</strong>基于IP地址的大概位置信息</li>
            </ul>

            <h3>1.3 从第三方获取的信息</h3>
            <ul>
              <li><strong>第三方登录：</strong>使用微信、QQ等第三方账号登录时获取的公开信息</li>
              <li><strong>支付信息：</strong>通过支付平台获取的交易记录</li>
            </ul>
          </section>

          <!-- 信息使用 -->
          <section id="section-2" class="content-section">
            <h2><i class="fas fa-cogs"></i> 2. 我们如何使用信息</h2>
            
            <div class="usage-grid">
              <div class="usage-item">
                <div class="usage-icon">
                  <i class="fas fa-eye"></i>
                </div>
                <div class="usage-content">
                  <h4>提供服务</h4>
                  <p>为您提供图像识别、结果分析、历史记录等核心功能服务</p>
                </div>
              </div>

              <div class="usage-item">
                <div class="usage-icon">
                  <i class="fas fa-user-shield"></i>
                </div>
                <div class="usage-content">
                  <h4>账户管理</h4>
                  <p>创建和管理您的用户账户，验证身份，提供客户支持</p>
                </div>
              </div>

              <div class="usage-item">
                <div class="usage-icon">
                  <i class="fas fa-chart-line"></i>
                </div>
                <div class="usage-content">
                  <h4>服务优化</h4>
                  <p>分析使用数据，改进产品功能，提升用户体验</p>
                </div>
              </div>

              <div class="usage-item">
                <div class="usage-icon">
                  <i class="fas fa-shield-alt"></i>
                </div>
                <div class="usage-content">
                  <h4>安全保护</h4>
                  <p>检测和预防欺诈、滥用及其他有害活动</p>
                </div>
              </div>
            </div>

            <a-alert
              message="数据处理原则"
              description="我们仅在为您提供服务所必需的范围内收集和使用您的个人信息，不会收集与服务无关的个人信息。"
              type="info"
              show-icon
              class="alert-box"
            />
          </section>

          <!-- 信息共享 -->
          <section id="section-3" class="content-section">
            <h2><i class="fas fa-share-alt"></i> 3. 信息共享和披露</h2>
            
            <p>我们不会向第三方出售、出租或以其他方式披露您的个人信息，除非：</p>

            <div class="sharing-conditions">
              <div class="condition-item">
                <i class="fas fa-check-circle"></i>
                <div>
                  <strong>获得您的明确同意</strong>
                  <p>在获得您明确同意的情况下，我们会与第三方共享您的个人信息</p>
                </div>
              </div>

              <div class="condition-item">
                <i class="fas fa-gavel"></i>
                <div>
                  <strong>法律法规要求</strong>
                  <p>根据法律法规、法律程序、政府要求或司法裁定需要披露</p>
                </div>
              </div>

              <div class="condition-item">
                <i class="fas fa-handshake"></i>
                <div>
                  <strong>服务提供商</strong>
                  <p>与为我们提供服务的第三方合作伙伴共享必要信息</p>
                </div>
              </div>

              <div class="condition-item">
                <i class="fas fa-exclamation-triangle"></i>
                <div>
                  <strong>紧急情况</strong>
                  <p>为保护用户、公众或公司的合法权益所必需的情况下</p>
                </div>
              </div>
            </div>
          </section>

          <!-- 信息安全 -->
          <section id="section-4" class="content-section">
            <h2><i class="fas fa-lock"></i> 4. 信息安全</h2>
            
            <p>我们非常重视您个人信息的安全，采用多种安全措施保护您的信息：</p>

            <div class="security-measures">
              <div class="measure-card">
                <i class="fas fa-key"></i>
                <h4>数据加密</h4>
                <p>采用HTTPS协议和先进的加密技术保护数据传输</p>
              </div>

              <div class="measure-card">
                <i class="fas fa-server"></i>
                <h4>安全存储</h4>
                <p>使用安全的服务器和数据库，定期备份和更新</p>
              </div>

              <div class="measure-card">
                <i class="fas fa-users-cog"></i>
                <h4>访问控制</h4>
                <p>严格限制员工对个人信息的访问权限</p>
              </div>

              <div class="measure-card">
                <i class="fas fa-search"></i>
                <h4>安全监控</h4>
                <p>实时监控系统安全状态，及时发现并处理威胁</p>
              </div>
            </div>

            <a-alert
              message="图片处理安全"
              description="您上传的图片仅用于识别处理，处理完成后会立即删除，不会长期存储在我们的服务器上。"
              type="success"
              show-icon
              class="alert-box"
            />
          </section>

          <!-- Cookie政策 -->
          <section id="section-5" class="content-section">
            <h2><i class="fas fa-cookie-bite"></i> 5. Cookie和类似技术</h2>
            
            <p>我们使用Cookie和类似技术来改善您的用户体验：</p>

            <div class="cookie-types">
              <div class="cookie-type">
                <h4>必需Cookie</h4>
                <p>确保网站正常运行所必需的Cookie，包括身份验证和安全功能</p>
              </div>

              <div class="cookie-type">
                <h4>功能Cookie</h4>
                <p>记住您的偏好设置，如语言选择、主题设置等</p>
              </div>

              <div class="cookie-type">
                <h4>分析Cookie</h4>
                <p>帮助我们了解网站的使用情况，以便改进服务质量</p>
              </div>
            </div>

            <p>您可以通过浏览器设置管理Cookie，但禁用Cookie可能影响某些功能的正常使用。</p>
          </section>

          <!-- 用户权利 -->
          <section id="section-6" class="content-section">
            <h2><i class="fas fa-user-check"></i> 6. 您的权利</h2>
            
            <p>您对自己的个人信息享有以下权利：</p>

            <div class="rights-list">
              <div class="right-item">
                <i class="fas fa-eye"></i>
                <div>
                  <h4>访问权</h4>
                  <p>您有权了解我们收集、使用您个人信息的情况</p>
                </div>
              </div>

              <div class="right-item">
                <i class="fas fa-edit"></i>
                <div>
                  <h4>更正权</h4>
                  <p>您有权要求我们更正不准确或不完整的个人信息</p>
                </div>
              </div>

              <div class="right-item">
                <i class="fas fa-trash"></i>
                <div>
                  <h4>删除权</h4>
                  <p>在特定情况下，您有权要求我们删除您的个人信息</p>
                </div>
              </div>

              <div class="right-item">
                <i class="fas fa-download"></i>
                <div>
                  <h4>数据导出权</h4>
                  <p>您有权要求我们提供您个人信息的副本</p>
                </div>
              </div>
            </div>

            <p>如需行使上述权利，请通过本政策末尾的联系方式与我们联系。</p>
          </section>

          <!-- 未成年人保护 -->
          <section id="section-7" class="content-section">
            <h2><i class="fas fa-child"></i> 7. 未成年人信息保护</h2>
            
            <p>我们非常重视未成年人个人信息的保护：</p>

            <ul>
              <li>我们不会主动收集未满14周岁儿童的个人信息</li>
              <li>如果您是未满18周岁的未成年人，在使用我们的服务前，应事先取得家长或法定监护人的同意</li>
              <li>如果我们发现在未事先获得可证实的父母同意的情况下收集了未满14周岁儿童的个人信息，会设法尽快删除相关数据</li>
            </ul>

            <a-alert
              message="家长监护提醒"
              description="如果您是儿童的家长或监护人，当您对您所监护的儿童的个人信息有相关疑问时，请通过本隐私政策公示的联系方式与我们联系。"
              type="warning"
              show-icon
              class="alert-box"
            />
          </section>

          <!-- 政策更新 -->
          <section id="section-8" class="content-section">
            <h2><i class="fas fa-sync-alt"></i> 8. 隐私政策的更新</h2>
            
            <p>我们可能会不定期修订本隐私政策。当隐私政策发生变更时，我们会：</p>

            <ul>
              <li>在本页面上发布新的隐私政策</li>
              <li>通过适当的方式通知您相关变更</li>
              <li>对于重大变更，会提前30天通知</li>
              <li>继续使用我们的服务即表示您接受更新后的隐私政策</li>
            </ul>
          </section>

          <!-- 联系我们 -->
          <section id="section-9" class="content-section">
            <h2><i class="fas fa-envelope"></i> 9. 联系我们</h2>
            
            <p>如果您对本隐私政策有任何疑问、意见或建议，请通过以下方式联系我们：</p>

            <div class="contact-info">
              <div class="contact-item">
                <i class="fas fa-envelope"></i>
                <div>
                  <strong>邮箱：</strong>
                  <span>privacy@image-ai.com</span>
                </div>
              </div>

              <div class="contact-item">
                <i class="fas fa-phone"></i>
                <div>
                  <strong>电话：</strong>
                  <span>400-123-4567</span>
                </div>
              </div>

              <div class="contact-item">
                <i class="fas fa-map-marker-alt"></i>
                <div>
                  <strong>地址：</strong>
                  <span>北京市朝阳区科技园区</span>
                </div>
              </div>
            </div>

            <p>我们将在收到您的联系后尽快回复，通常不超过15个工作日。</p>
          </section>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
function scrollToSection(sectionId: string) {
  const element = document.getElementById(sectionId)
  if (element) {
    element.scrollIntoView({ behavior: 'smooth' })
  }
}
</script>

<style scoped>
.privacy-page {
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
  margin-bottom: 24px;
}

.update-info {
  display: inline-block;
  background: rgba(255, 255, 255, 0.2);
  padding: 8px 16px;
  border-radius: 20px;
  font-size: 14px;
  opacity: 0.9;
}

/* 主要内容 */
.main-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 24px 40px;
}

.privacy-container {
  display: flex;
  gap: 32px;
  align-items: flex-start;
}

/* 侧边导航 */
.sidebar {
  width: 200px;
  flex-shrink: 0;
}

.nav-menu {
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}

.nav-menu h4 {
  color: #262626;
  margin-bottom: 16px;
  font-size: 16px;
  font-weight: 600;
}

.nav-menu ul {
  list-style: none;
  padding: 0;
  margin: 0;
}

.nav-menu li {
  margin-bottom: 8px;
}

.nav-menu a {
  color: #666;
  text-decoration: none;
  font-size: 14px;
  line-height: 1.5;
  transition: color 0.3s;
}

.nav-menu a:hover {
  color: #1890ff;
}

/* 内容区域 */
.content {
  flex: 1;
  background: white;
  border-radius: 12px;
  padding: 40px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}

.intro-section {
  margin-bottom: 40px;
}

.content-section {
  margin-bottom: 40px;
  padding-bottom: 32px;
  border-bottom: 1px solid #f0f0f0;
}

.content-section:last-child {
  border-bottom: none;
  margin-bottom: 0;
}

.content h2 {
  color: #262626;
  font-size: 24px;
  font-weight: 600;
  margin-bottom: 24px;
  display: flex;
  align-items: center;
  gap: 12px;
}

.content h2 i {
  color: #1890ff;
}

.content h3 {
  color: #262626;
  font-size: 18px;
  font-weight: 500;
  margin: 24px 0 16px;
}

.content h4 {
  color: #262626;
  font-size: 16px;
  font-weight: 500;
  margin-bottom: 8px;
}

.content p {
  color: #666;
  line-height: 1.8;
  margin-bottom: 16px;
}

.content ul {
  color: #666;
  line-height: 1.8;
  margin-bottom: 16px;
  padding-left: 20px;
}

.content li {
  margin-bottom: 8px;
}

.alert-box {
  margin: 24px 0;
  border-radius: 8px;
}

/* 使用用途网格 */
.usage-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
  margin: 24px 0;
}

.usage-item {
  display: flex;
  align-items: flex-start;
  gap: 16px;
  padding: 20px;
  background: #fafafa;
  border-radius: 12px;
}

.usage-icon {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
  flex-shrink: 0;
}

.usage-content h4 {
  margin-bottom: 8px;
}

.usage-content p {
  margin: 0;
  font-size: 14px;
  color: #666;
}

/* 共享条件 */
.sharing-conditions {
  display: flex;
  flex-direction: column;
  gap: 16px;
  margin: 24px 0;
}

.condition-item {
  display: flex;
  align-items: flex-start;
  gap: 16px;
  padding: 16px;
  background: #fafafa;
  border-radius: 8px;
}

.condition-item i {
  color: #52c41a;
  font-size: 18px;
  margin-top: 2px;
}

.condition-item strong {
  color: #262626;
  display: block;
  margin-bottom: 4px;
}

.condition-item p {
  margin: 0;
  font-size: 14px;
  color: #666;
}

/* 安全措施 */
.security-measures {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
  margin: 24px 0;
}

.measure-card {
  text-align: center;
  padding: 24px;
  background: #fafafa;
  border-radius: 12px;
  transition: all 0.3s;
}

.measure-card:hover {
  background: #f0f2ff;
  transform: translateY(-2px);
}

.measure-card i {
  font-size: 32px;
  color: #1890ff;
  margin-bottom: 16px;
}

.measure-card h4 {
  margin-bottom: 12px;
}

.measure-card p {
  margin: 0;
  font-size: 14px;
  color: #666;
}

/* Cookie类型 */
.cookie-types {
  display: flex;
  flex-direction: column;
  gap: 16px;
  margin: 24px 0;
}

.cookie-type {
  padding: 16px;
  background: #fafafa;
  border-radius: 8px;
}

.cookie-type h4 {
  margin-bottom: 8px;
}

.cookie-type p {
  margin: 0;
  font-size: 14px;
  color: #666;
}

/* 权利列表 */
.rights-list {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 16px;
  margin: 24px 0;
}

.right-item {
  display: flex;
  align-items: flex-start;
  gap: 16px;
  padding: 16px;
  background: #fafafa;
  border-radius: 8px;
}

.right-item i {
  color: #1890ff;
  font-size: 18px;
  margin-top: 2px;
}

.right-item h4 {
  margin-bottom: 4px;
}

.right-item p {
  margin: 0;
  font-size: 14px;
  color: #666;
}

/* 联系信息 */
.contact-info {
  display: flex;
  flex-direction: column;
  gap: 16px;
  margin: 24px 0;
}

.contact-item {
  display: flex;
  align-items: center;
  gap: 12px;
}

.contact-item i {
  color: #1890ff;
  font-size: 16px;
  width: 20px;
}

.contact-item strong {
  color: #262626;
  margin-right: 8px;
}

.contact-item span {
  color: #666;
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
  
  .privacy-container {
    flex-direction: column;
    gap: 24px;
  }
  
  .sidebar {
    width: 100%;
  }
  
  .nav-menu {
    padding: 16px;
  }
  
  .nav-menu ul {
    display: flex;
    flex-wrap: wrap;
    gap: 8px;
  }
  
  .nav-menu li {
    margin-bottom: 0;
  }
  
  .usage-grid {
    grid-template-columns: 1fr;
  }
  
  .rights-list {
    grid-template-columns: 1fr;
  }
  
  .security-measures {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 576px) {
  .main-content {
    padding: 0 16px 40px;
  }
  
  .hero-section {
    padding: 40px 0;
  }
  
  .content {
    padding: 24px 16px;
  }
  
  .content h2 {
    font-size: 20px;
  }
  
  .security-measures {
    grid-template-columns: 1fr;
  }
}
</style>
