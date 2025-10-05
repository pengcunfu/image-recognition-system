<template>
  <div class="terms-page">
    <!-- 顶部横幅 -->
    <div class="hero-section">
      <div class="hero-content">
        <h1>服务条款</h1>
        <p>使用我们的服务前，请仔细阅读以下条款</p>
        <div class="update-info">
          <span>最后更新时间：2025年1月1日</span>
        </div>
      </div>
    </div>

    <!-- 主要内容 -->
    <div class="main-content">
      <div class="terms-container">
        <!-- 侧边导航 -->
        <div class="sidebar">
          <a-affix :offset-top="100">
            <div class="nav-menu">
              <h4>目录</h4>
              <ul>
                <li><a href="#section-1" @click="scrollToSection('section-1')">接受条款</a></li>
                <li><a href="#section-2" @click="scrollToSection('section-2')">服务说明</a></li>
                <li><a href="#section-3" @click="scrollToSection('section-3')">用户注册</a></li>
                <li><a href="#section-4" @click="scrollToSection('section-4')">使用规则</a></li>
                <li><a href="#section-5" @click="scrollToSection('section-5')">知识产权</a></li>
                <li><a href="#section-6" @click="scrollToSection('section-6')">付费服务</a></li>
                <li><a href="#section-7" @click="scrollToSection('section-7')">免责声明</a></li>
                <li><a href="#section-8" @click="scrollToSection('section-8')">服务变更</a></li>
                <li><a href="#section-9" @click="scrollToSection('section-9')">法律适用</a></li>
              </ul>
            </div>
          </a-affix>
        </div>

        <!-- 主要内容 -->
        <div class="content">
          <!-- 引言 -->
          <section class="intro-section">
            <h2>服务条款概述</h2>
            <p>欢迎使用智能图像识别系统（以下简称"本服务"或"我们的服务"）！本服务由智能图像识别系统团队（以下简称"我们"）提供。在您开始使用我们的服务之前，请仔细阅读本服务条款（以下简称"本条款"）。</p>
            
            <a-alert
              message="重要提醒"
              description="您使用我们的服务即表示您已阅读、理解并同意受本条款的约束。如果您不同意本条款的任何部分，请不要使用我们的服务。"
              type="warning"
              show-icon
              class="alert-box"
            />
          </section>

          <!-- 接受条款 -->
          <section id="section-1" class="content-section">
            <h2><i class="fas fa-handshake"></i> 1. 接受条款</h2>
            
            <p>通过访问和使用本服务，您确认：</p>
            <ul>
              <li>您已年满18周岁，或在父母/监护人的同意下使用本服务</li>
              <li>您具有完全的民事行为能力</li>
              <li>您已仔细阅读、理解并同意遵守本条款</li>
              <li>您同意我们的隐私政策</li>
            </ul>

            <div class="terms-highlight">
              <i class="fas fa-exclamation-circle"></i>
              <div>
                <strong>特别提示：</strong>
                <p>如果您代表公司或其他实体使用本服务，您声明并保证您有权代表该实体接受本条款。</p>
              </div>
            </div>
          </section>

          <!-- 服务说明 -->
          <section id="section-2" class="content-section">
            <h2><i class="fas fa-cogs"></i> 2. 服务说明</h2>
            
            <h3>2.1 服务内容</h3>
            <p>我们提供基于人工智能技术的图像识别服务，包括但不限于：</p>

            <div class="service-grid">
              <div class="service-card">
                <i class="fas fa-eye"></i>
                <h4>图像识别</h4>
                <p>识别图片中的物体、动物、植物、食物等</p>
              </div>

              <div class="service-card">
                <i class="fas fa-layer-group"></i>
                <h4>批量处理</h4>
                <p>支持多张图片同时识别和分析</p>
              </div>

              <div class="service-card">
                <i class="fas fa-database"></i>
                <h4>历史记录</h4>
                <p>保存和管理您的识别历史</p>
              </div>

              <div class="service-card">
                <i class="fas fa-book"></i>
                <h4>知识库</h4>
                <p>提供相关知识和学习资源</p>
              </div>

              <div class="service-card">
                <i class="fas fa-users"></i>
                <h4>社区功能</h4>
                <p>用户交流和经验分享平台</p>
              </div>

              <div class="service-card">
                <i class="fas fa-crown"></i>
                <h4>VIP服务</h4>
                <p>高级功能和优质服务体验</p>
              </div>
            </div>

            <h3>2.2 服务可用性</h3>
            <p>我们努力确保服务的持续可用性，但不保证服务不会中断。服务可能因维护、升级或其他原因暂时不可用。</p>
          </section>

          <!-- 用户注册 -->
          <section id="section-3" class="content-section">
            <h2><i class="fas fa-user-plus"></i> 3. 用户注册与账户</h2>
            
            <h3>3.1 注册要求</h3>
            <ul>
              <li>提供准确、完整、最新的注册信息</li>
              <li>选择安全的密码并妥善保管</li>
              <li>及时更新个人信息</li>
              <li>不得冒用他人身份注册</li>
            </ul>

            <h3>3.2 账户安全</h3>
            <div class="security-tips">
              <div class="tip-item">
                <i class="fas fa-lock"></i>
                <div>
                  <strong>密码安全</strong>
                  <p>设置强密码，定期更换，不与他人共享</p>
                </div>
              </div>

              <div class="tip-item">
                <i class="fas fa-shield-alt"></i>
                <div>
                  <strong>账户保护</strong>
                  <p>启用双因子认证，提高账户安全性</p>
                </div>
              </div>

              <div class="tip-item">
                <i class="fas fa-sign-out-alt"></i>
                <div>
                  <strong>安全退出</strong>
                  <p>使用完毕后及时退出登录</p>
                </div>
              </div>
            </div>

            <h3>3.3 账户责任</h3>
            <p>您对使用您账户进行的所有活动负责。如发现账户被盗用，请立即联系我们。</p>
          </section>

          <!-- 使用规则 -->
          <section id="section-4" class="content-section">
            <h2><i class="fas fa-gavel"></i> 4. 使用规则与限制</h2>
            
            <h3>4.1 允许的使用</h3>
            <ul>
              <li>按照服务设计目的使用图像识别功能</li>
              <li>上传合法的图片进行识别</li>
              <li>参与社区讨论和知识分享</li>
              <li>合理使用服务资源</li>
            </ul>

            <h3>4.2 禁止的行为</h3>
            <div class="prohibited-actions">
              <div class="action-category">
                <h4><i class="fas fa-ban"></i> 内容限制</h4>
                <ul>
                  <li>上传包含暴力、色情、违法内容的图片</li>
                  <li>上传侵犯他人版权的图片</li>
                  <li>上传恶意软件或病毒</li>
                  <li>发布虚假、误导性信息</li>
                </ul>
              </div>

              <div class="action-category">
                <h4><i class="fas fa-user-slash"></i> 行为限制</h4>
                <ul>
                  <li>恶意攻击或干扰服务正常运行</li>
                  <li>尝试破解、逆向工程服务</li>
                  <li>滥用或过度使用服务资源</li>
                  <li>冒充他人或虚假身份</li>
                </ul>
              </div>

              <div class="action-category">
                <h4><i class="fas fa-exclamation-triangle"></i> 商业限制</h4>
                <ul>
                  <li>未经授权的商业用途</li>
                  <li>转售或再分发我们的服务</li>
                  <li>竞争性用途或反向工程</li>
                  <li>批量数据采集或爬虫</li>
                </ul>
              </div>
            </div>

            <a-alert
              message="违规处理"
              description="违反使用规则的用户，我们有权暂停或终止其账户，并保留追究法律责任的权利。"
              type="error"
              show-icon
              class="alert-box"
            />
          </section>

          <!-- 知识产权 -->
          <section id="section-5" class="content-section">
            <h2><i class="fas fa-copyright"></i> 5. 知识产权</h2>
            
            <h3>5.1 我们的知识产权</h3>
            <p>本服务包含的所有内容（包括但不限于文本、图片、音频、视频、软件、商标、专利）均受知识产权法保护。</p>

            <div class="ip-categories">
              <div class="ip-item">
                <i class="fas fa-code"></i>
                <div>
                  <strong>技术专利</strong>
                  <p>AI算法、识别技术等核心技术专利</p>
                </div>
              </div>

              <div class="ip-item">
                <i class="fas fa-trademark"></i>
                <div>
                  <strong>商标权</strong>
                  <p>产品名称、Logo、标识等商标权</p>
                </div>
              </div>

              <div class="ip-item">
                <i class="fas fa-file-code"></i>
                <div>
                  <strong>软件著作权</strong>
                  <p>软件代码、界面设计等著作权</p>
                </div>
              </div>
            </div>

            <h3>5.2 用户内容权利</h3>
            <ul>
              <li>您保留对上传内容的原有权利</li>
              <li>您授权我们为提供服务使用您的内容</li>
              <li>您承诺上传的内容不侵犯第三方权利</li>
              <li>我们不会将您的内容用于其他商业目的</li>
            </ul>
          </section>

          <!-- 付费服务 -->
          <section id="section-6" class="content-section">
            <h2><i class="fas fa-credit-card"></i> 6. 付费服务与计费</h2>
            
            <h3>6.1 VIP服务</h3>
            <p>我们提供多种VIP套餐，享受高级功能和优质服务：</p>

            <div class="vip-features">
              <div class="feature-card">
                <i class="fas fa-magic"></i>
                <h4>高级识别</h4>
                <p>更高准确率的识别算法</p>
              </div>

              <div class="feature-card">
                <i class="fas fa-layer-group"></i>
                <h4>批量处理</h4>
                <p>支持大量图片批量识别</p>
              </div>

              <div class="feature-card">
                <i class="fas fa-chart-line"></i>
                <h4>数据分析</h4>
                <p>详细的使用统计和分析</p>
              </div>

              <div class="feature-card">
                <i class="fas fa-headset"></i>
                <h4>优先支持</h4>
                <p>专属客服和技术支持</p>
              </div>
            </div>

            <h3>6.2 计费规则</h3>
            <ul>
              <li>VIP服务按月或年度收费</li>
              <li>费用在服务期开始时收取</li>
              <li>支持多种支付方式</li>
              <li>自动续费需要用户主动开启</li>
            </ul>

            <h3>6.3 退款政策</h3>
            <p>在特定条件下，我们提供退款服务：</p>
            <ul>
              <li>服务开始后7天内可申请全额退款</li>
              <li>因我们的技术问题导致服务不可用</li>
              <li>其他合理的退款申请</li>
            </ul>
          </section>

          <!-- 免责声明 -->
          <section id="section-7" class="content-section">
            <h2><i class="fas fa-exclamation-triangle"></i> 7. 免责声明与责任限制</h2>
            
            <h3>7.1 服务免责</h3>
            <div class="disclaimer-items">
              <div class="disclaimer-item">
                <i class="fas fa-info-circle"></i>
                <div>
                  <strong>识别准确性</strong>
                  <p>AI识别结果仅供参考，我们不保证100%准确</p>
                </div>
              </div>

              <div class="disclaimer-item">
                <i class="fas fa-wifi"></i>
                <div>
                  <strong>服务中断</strong>
                  <p>因网络、服务器等技术原因可能导致服务暂时不可用</p>
                </div>
              </div>

              <div class="disclaimer-item">
                <i class="fas fa-shield-alt"></i>
                <div>
                  <strong>数据安全</strong>
                  <p>虽然我们采取安全措施，但无法保证绝对安全</p>
                </div>
              </div>

              <div class="disclaimer-item">
                <i class="fas fa-user-friends"></i>
                <div>
                  <strong>第三方内容</strong>
                  <p>对用户生成的内容不承担责任</p>
                </div>
              </div>
            </div>

            <h3>7.2 责任限制</h3>
            <p>在法律允许的最大范围内，我们对以下情况不承担责任：</p>
            <ul>
              <li>因使用服务导致的直接或间接损失</li>
              <li>第三方的侵权行为</li>
              <li>不可抗力因素导致的服务中断</li>
              <li>用户违规使用导致的后果</li>
            </ul>

            <a-alert
              message="重要提醒"
              description="请您在重要决策前，结合其他信息来源验证AI识别结果的准确性。"
              type="info"
              show-icon
              class="alert-box"
            />
          </section>

          <!-- 服务变更 -->
          <section id="section-8" class="content-section">
            <h2><i class="fas fa-sync-alt"></i> 8. 服务变更与终止</h2>
            
            <h3>8.1 服务变更</h3>
            <p>我们保留随时修改、升级或终止部分或全部服务的权利：</p>
            <ul>
              <li>新功能的添加和现有功能的改进</li>
              <li>技术升级和维护</li>
              <li>服务条款的更新</li>
              <li>价格政策的调整</li>
            </ul>

            <h3>8.2 服务终止</h3>
            <p>在以下情况下，我们可能暂停或终止您的账户：</p>
            <ul>
              <li>违反本服务条款</li>
              <li>长期未使用账户</li>
              <li>涉嫌欺诈或非法活动</li>
              <li>其他合理原因</li>
            </ul>

            <h3>8.3 数据处理</h3>
            <p>账户终止后：</p>
            <ul>
              <li>您的个人数据将按照隐私政策处理</li>
              <li>识别历史记录可能被删除</li>
              <li>VIP服务将立即停止</li>
              <li>社区内容可能保留</li>
            </ul>
          </section>

          <!-- 法律适用 -->
          <section id="section-9" class="content-section">
            <h2><i class="fas fa-balance-scale"></i> 9. 法律适用与争议解决</h2>
            
            <h3>9.1 适用法律</h3>
            <p>本条款的解释、效力和争议解决均适用中华人民共和国法律。</p>

            <h3>9.2 争议解决</h3>
            <div class="dispute-steps">
              <div class="step-item">
                <div class="step-number">1</div>
                <div class="step-content">
                  <h4>友好协商</h4>
                  <p>首先通过友好协商解决争议</p>
                </div>
              </div>

              <div class="step-item">
                <div class="step-number">2</div>
                <div class="step-content">
                  <h4>调解程序</h4>
                  <p>协商不成时，可申请第三方调解</p>
                </div>
              </div>

              <div class="step-item">
                <div class="step-number">3</div>
                <div class="step-content">
                  <h4>仲裁诉讼</h4>
                  <p>最终通过仲裁或诉讼解决争议</p>
                </div>
              </div>
            </div>

            <h3>9.3 管辖权</h3>
            <p>因本条款引起的争议，由我们所在地人民法院管辖。</p>

            <h3>9.4 条款效力</h3>
            <p>如本条款的任何条款被认定为无效，其余条款仍然有效。</p>

            <div class="contact-section">
              <h3>联系我们</h3>
              <p>如果您对本服务条款有任何疑问，请联系我们：</p>
              <div class="contact-info">
                <div class="contact-item">
                  <i class="fas fa-envelope"></i>
                  <span>legal@image-ai.com</span>
                </div>
                <div class="contact-item">
                  <i class="fas fa-phone"></i>
                  <span>400-123-4567</span>
                </div>
                <div class="contact-item">
                  <i class="fas fa-map-marker-alt"></i>
                  <span>北京市朝阳区科技园区</span>
                </div>
              </div>
            </div>
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
.terms-page {
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

.terms-container {
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

/* 条款高亮 */
.terms-highlight {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  padding: 16px;
  background: #e6f7ff;
  border: 1px solid #91d5ff;
  border-radius: 8px;
  margin: 24px 0;
}

.terms-highlight i {
  color: #1890ff;
  font-size: 18px;
  margin-top: 2px;
}

.terms-highlight strong {
  color: #262626;
  display: block;
  margin-bottom: 4px;
}

.terms-highlight p {
  margin: 0;
  font-size: 14px;
  color: #666;
}

/* 服务网格 */
.service-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
  margin: 24px 0;
}

.service-card {
  text-align: center;
  padding: 24px;
  background: #fafafa;
  border-radius: 12px;
  transition: all 0.3s;
}

.service-card:hover {
  background: #f0f2ff;
  transform: translateY(-2px);
}

.service-card i {
  font-size: 32px;
  color: #1890ff;
  margin-bottom: 16px;
}

.service-card h4 {
  margin-bottom: 12px;
}

.service-card p {
  margin: 0;
  font-size: 14px;
  color: #666;
}

/* 安全提示 */
.security-tips {
  display: flex;
  flex-direction: column;
  gap: 16px;
  margin: 24px 0;
}

.tip-item {
  display: flex;
  align-items: flex-start;
  gap: 16px;
  padding: 16px;
  background: #f6ffed;
  border: 1px solid #b7eb8f;
  border-radius: 8px;
}

.tip-item i {
  color: #52c41a;
  font-size: 18px;
  margin-top: 2px;
}

.tip-item strong {
  color: #262626;
  display: block;
  margin-bottom: 4px;
}

.tip-item p {
  margin: 0;
  font-size: 14px;
  color: #666;
}

/* 禁止行为 */
.prohibited-actions {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
  margin: 24px 0;
}

.action-category {
  padding: 20px;
  background: #fff2f0;
  border: 1px solid #ffccc7;
  border-radius: 8px;
}

.action-category h4 {
  color: #cf1322;
  margin-bottom: 16px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.action-category ul {
  margin: 0;
  color: #666;
}

/* 知识产权 */
.ip-categories {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 16px;
  margin: 24px 0;
}

.ip-item {
  display: flex;
  align-items: flex-start;
  gap: 16px;
  padding: 16px;
  background: #fafafa;
  border-radius: 8px;
}

.ip-item i {
  color: #1890ff;
  font-size: 18px;
  margin-top: 2px;
}

.ip-item strong {
  color: #262626;
  display: block;
  margin-bottom: 4px;
}

.ip-item p {
  margin: 0;
  font-size: 14px;
  color: #666;
}

/* VIP功能 */
.vip-features {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
  gap: 16px;
  margin: 24px 0;
}

.feature-card {
  text-align: center;
  padding: 20px;
  background: #fff7e6;
  border: 1px solid #ffd591;
  border-radius: 8px;
}

.feature-card i {
  font-size: 24px;
  color: #fa8c16;
  margin-bottom: 12px;
}

.feature-card h4 {
  margin-bottom: 8px;
  color: #262626;
}

.feature-card p {
  margin: 0;
  font-size: 12px;
  color: #666;
}

/* 免责声明 */
.disclaimer-items {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 16px;
  margin: 24px 0;
}

.disclaimer-item {
  display: flex;
  align-items: flex-start;
  gap: 16px;
  padding: 16px;
  background: #fffbe6;
  border: 1px solid #fff1b8;
  border-radius: 8px;
}

.disclaimer-item i {
  color: #faad14;
  font-size: 18px;
  margin-top: 2px;
}

.disclaimer-item strong {
  color: #262626;
  display: block;
  margin-bottom: 4px;
}

.disclaimer-item p {
  margin: 0;
  font-size: 14px;
  color: #666;
}

/* 争议解决步骤 */
.dispute-steps {
  display: flex;
  gap: 24px;
  margin: 24px 0;
}

.step-item {
  flex: 1;
  text-align: center;
}

.step-number {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: #1890ff;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  margin: 0 auto 16px;
}

.step-content h4 {
  margin-bottom: 8px;
}

.step-content p {
  margin: 0;
  font-size: 14px;
  color: #666;
}

/* 联系信息 */
.contact-section {
  margin-top: 32px;
  padding: 24px;
  background: #f6ffed;
  border: 1px solid #b7eb8f;
  border-radius: 12px;
}

.contact-info {
  display: flex;
  flex-wrap: wrap;
  gap: 24px;
  margin-top: 16px;
}

.contact-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.contact-item i {
  color: #1890ff;
  font-size: 16px;
  width: 20px;
}

.contact-item span {
  color: #666;
  font-weight: 500;
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
  
  .terms-container {
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
  
  .service-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .prohibited-actions {
    grid-template-columns: 1fr;
  }
  
  .dispute-steps {
    flex-direction: column;
    gap: 16px;
  }
  
  .contact-info {
    flex-direction: column;
    gap: 12px;
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
  
  .service-grid {
    grid-template-columns: 1fr;
  }
  
  .vip-features {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .ip-categories {
    grid-template-columns: 1fr;
  }
  
  .disclaimer-items {
    grid-template-columns: 1fr;
  }
}
</style>
