# Ledger - 礼金簿管理应用

一个简单易用的 Android 礼金管理工具，帮助您轻松管理婚礼、生日等场合的礼金记录。

## 📱 功能特性

### 核心功能
- ✅ **多礼金簿管理** - 支持创建多个独立的礼金簿，分类管理不同事件的礼金
- ✅ **宾客记录** - 详细记录每位宾客的姓名、金额、地址等信息
- ✅ **还礼管理** - 智能标记还礼状态，设置还礼时间和备注
- ✅ **高级搜索** - 支持按关键词、地址、金额范围、日期范围、还礼状态筛选
- ✅ **数据导入导出** - 支持 CSV 格式导入导出，方便数据备份和迁移
- ✅ **统计信息** - 实时显示记录数、总金额、已还礼金额

### 技术特性
- 🎨 **Material Design** - 现代化 UI 设计，支持深色模式
- 🔒 **本地数据库** - Room 持久化存储，数据安全可靠
- 📊 **MVVM 架构** - 清晰的代码结构，易于维护和扩展
- 🔍 **实时搜索** - 快速查找所需记录
- 📦 **离线使用** - 无需网络，完全本地运行

## 🏗️ 技术栈

- **开发语言**: Java
- **最低版本**: Android 7.0 (API 24)
- **目标版本**: Android 16 (API 36)
- **架构模式**: MVVM
- **数据库**: Room
- **UI 组件**: Material Components
- **数据绑定**: ViewBinding
- **生命周期**: Lifecycle + LiveData

## 📂 项目结构

```
app/src/main/java/com/gxg/ledger/
├── adapter/           # RecyclerView 适配器
├── database/          # Room 数据库和数据访问对象
├── dialog/            # 自定义对话框
├── model/             # 数据模型
├── repository/        # 数据仓库
├── utils/             # 工具类
├── viewmodel/         # ViewModel
├── MainActivity.java       # 主界面
├── BookDetailActivity.java # 礼金簿详情界面
└── SettingsActivity.java   # 设置界面
```

## 🚀 快速开始

### 环境要求
- Android Studio Hedgehog | 2023.1.1 或更高版本
- JDK 11 或更高版本
- Gradle 8+

### 安装步骤
1. 克隆项目到本地
```bash
git clone https://github.com/TinyCoderGoose/ledger.git
cd ledger
```

2. 使用 Android Studio 打开项目

3. 同步 Gradle 依赖

4. 运行应用
   - 连接 Android 设备或启动模拟器
   - 点击 Run 按钮

### 构建 Release APK
```bash
./gradlew assembleRelease
```
生成的 APK 位于：`app/build/outputs/apk/release/app-release.apk`

## 📖 使用说明

### 创建礼金簿
1. 点击主页右下角"+"按钮
2. 输入礼金簿名称（如"婚礼礼金"）
3. 点击确定

### 添加宾客记录
1. 进入某个礼金簿
2. 点击"添加记录"按钮
3. 填写宾客信息：姓名、金额、地址、事件日期等
4. 点击保存

### 标记还礼
1. 在记录列表中点击某条记录
2. 点击"还礼"按钮
3. 选择还礼日期，填写还礼备注
4. 点击确认

### 高级搜索
1. 点击"高级搜索"按钮
2. 设置筛选条件：
   - 关键词（姓名或备注）
   - 地址
   - 金额范围
   - 日期范围
   - 还礼状态
3. 点击确定查看结果

### 导入导出数据
- **导出**: 点击菜单 → 导出，将当前礼金簿导出为 CSV 文件
- **导入**: 点击菜单 → 导入，选择 CSV 文件导入到当前礼金簿

## 🎨 主题颜色

当前使用深蓝色主题:
- 主色：`#1565C0` (深蓝)
- 浅色：`#BBDEFB` (浅蓝)
- 深色：`#0D47A1` (深蓝)

## 📝 数据模型

### GiftBook (礼金簿)
- id: 礼金簿 ID
- name: 礼金簿名称
- description: 描述

### GiftRecord (宾客记录)
- id: 记录 ID
- bookId: 所属礼金簿 ID
- personName: 宾客姓名
- amount: 礼金金额
- address: 地址
- eventDate: 事件日期
- notes: 备注
- isReturned: 是否已还礼
- returnDate: 还礼日期
- returnNotes: 还礼备注

## 🔐 签名证书

项目包含自签名证书（有效期 100 年）:
- 密钥库：`ledger.keystore`
- 别名：`ledger_key`
- 配置：`keystore.properties`

⚠️ **注意**: 生产环境请使用自己的签名证书

## 📄 许可证

本项目仅供学习和个人使用。

## 👨‍💻 开发说明

### 代码规范
- 变量命名采用驼峰式
- 常量使用大写字母和下划线
- 类名使用 PascalCase
- 资源文件使用小写和下划线

### 提交规范
```
feat: 新功能
fix: 修复 bug
docs: 文档更新
style: 代码格式调整
refactor: 重构代码
test: 测试相关
chore: 构建/工具相关
```

## 🤝 贡献

欢迎提交 Issue 和 Pull Request!

## 📧 联系方式

如有问题或建议，请通过 GitHub Issues 联系。

---

**Made with ❤️ by TinyCoderGoose**
