# 地摊推荐平台 - 项目开发与部署文档

## 1. 项目概述
本项目是一个基于地理信息系统（GIS）的本地生活服务平台。旨在帮助用户查看、发布和搜索周边的地摊摊位，并在地图上直观显示，提供联系电话与导航功能。

## 2. 技术栈
*   **前端**：Vue 3 + TypeScript + Vite + Pinia + Vue Router
*   **UI 框架**：Tailwind CSS
*   **地图引擎**：Leaflet
*   **地图数据源**：天地图 (Tianditu)
*   **后端**：Java 11+ / 17, Spring Boot 2.7.18
*   **持久层框架**：Spring Data JPA + Hibernate Spatial
*   **数据库**：PostgreSQL 17 + PostGIS 3.4
*   **依赖管理**：Maven (后端), npm (前端)

## 3. 系统架构
项目采用前后端分离架构，前端通过 Vite 代理解决跨域及天地图 418 防火墙拦截问题，后端提供 RESTful API 接口进行业务逻辑处理和数据存储。
*   **前端端口**：`3000`
*   **后端端口**：`8080`
*   **通信机制**：前端通过 `axios` 请求 `http://localhost:3000/api/` 代理转发至后端 `http://localhost:8080/api/`。

## 4. 数据库设计 (核心表结构：stall)
```sql
CREATE TABLE stall (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(100) NOT NULL,
    category VARCHAR(30),
    location GEOGRAPHY(POINT, 4326) NOT NULL, -- 使用 Geography 类型，距离计算直接按“米”为单位
    address VARCHAR(200),
    status VARCHAR(20) DEFAULT 'OPEN',
    owner_id BIGINT,
    phone VARCHAR(20),
    description VARCHAR(500),
    image_url TEXT,
    expire_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

## 5. 环境搭建与快速启动 (一键生成工具)
为了方便克隆代码后快速在本地搭建环境，项目提供了 `deply.py` 和 `add_pages.py` 脚本辅助生成代码。
1.  **生成代码**：项目根目录下运行 `python3 deply.py`（生成完整前后端基础框架）。
2.  **增补页面**：运行 `python3 add_pages.py`（添加底部导航栏和列表页）。
3.  **环境准备**：本地需安装 `PostgreSQL 17`、`PostGIS`、`Java 11+`、`Maven`、`Node 18+`。
4.  **启动后端**：
    *   进入 `backend` 目录，修改 `application.yml` 中的数据库密码。
    *   运行命令 `mvn spring-boot:run`。
5.  **启动前端**：
    *   进入 `frontend` 目录。
    *   运行命令 `npm install` 安装依赖。
    *   运行命令 `npm run dev` 启动开发服务器。

## 6. 核心功能实现与亮点
### 6.1 地图展示与随动加载
*   **技术实现**：使用 Leaflet 加载天地图底图。
*   **动态刷新**：监听地图的 `moveend` 事件，获取当前地图中心点经纬度，结合防抖（Debounce）机制，在用户停止拖动 300ms 后，请求后端的“附近搜索”接口，实现**拖到哪里，图钉就更新到哪里**的丝滑体验。
*   **图钉弹窗**：点击图钉弹出精心设计的卡片，支持**图片展示、摊位介绍、一键拨打摊主电话、高德地图一键导航**。

### 6.2 附近摊位搜索 (PostGIS 空间查询)
*   **原生 SQL**：在 JpaRepository 中使用了 nativeQuery 进行距离计算和过滤。
*   **核心代码**：
    ```sql
    SELECT s.*, ST_Distance(s.location, CAST(ST_SetSRID(ST_MakePoint(:lng, :lat), 4326) AS geography)) AS dist 
    FROM stall s 
    WHERE ST_DWithin(s.location, CAST(ST_SetSRID(ST_MakePoint(:lng, :lat), 4326) AS geography), :radius) 
    AND s.status = 'OPEN' ORDER BY dist
    ```
*   **亮点**：成功绕过 Hibernate 对 `::geography` 语法的解析 Bug，使用 `CAST(... AS geography)` 实现安全且准确的地理距离计算。

### 6.3 用户友好的发布摊位功能
*   **一键 GPS 定位**：摊主无需手动输入经纬度，点击“📍 点击定位我当前所在的位置”按钮，自动调用浏览器 `navigator.geolocation` API 获取当前位置并填入表单。
*   **小地图选点**：如果 GPS 有误差，摊主可以在发布页的迷你地图上**点击**进行微调校准，极大降低了使用门槛。

### 6.4 全量摊位列表
*   提供独立的 `/api/stalls/all` 接口，结合倒序排序，在底部“列表”Tab 中展示全平台所有营业中的摊位，方便用户快速浏览信息。

## 7. API 接口文档
| 请求方法 | 路径 | 参数 | 说明 |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/stalls` | `StallRequest` (Body) | 发布一个新的摊位 |
| `GET` | `/api/stalls/nearby` | `lng`, `lat`, `radius` | 获取距离当前坐标指定半径(米)内的摊位 |
| `GET` | `/api/stalls/all` | 无 | 获取数据库中所有营业状态为 OPEN 的摊位列表 |

## 8. 代码版本控制 (GitHub 部署)
项目托管于 GitHub，采用 SSH 协议进行代码同步。请确保本地/服务器已配置好 SSH Key，并已添加到 GitHub 账号的 Settings -> SSH and GPG keys 中。

### 8.1 首次提交代码到 GitHub
```bash
# 1. 初始化 Git 仓库
git init

# 2. 配置提交者身份 (首次运行必须执行)
git config --global user.email "你的GitHub邮箱@example.com"
git config --global user.name "你的GitHub用户名"

# 3. 添加远程仓库地址 (使用 SSH 协议)
git remote add origin git@github.com:billywangyu/ditan.git

# 4. 添加所有文件至暂存区 (注意：生产环境需先配置好 .gitignore 避免上传 node_modules 和 target 等)
git add .

# 5. 创建主分支并提交
git branch -M main
git commit -m "first commit"

# 6. 推送至 GitHub
git push -u origin main
```

### 8.2 服务器更新代码
在阿里云服务器上更新最新代码：
```bash
cd /opt/ditan
git pull origin main
```
*(注：如果服务端根目录下包含 `ditan` 子文件夹导致嵌套 Git 警告，执行 `git rm --cached -r ditan` 取消嵌套即可。)*

## 9. 开发过程中踩过的坑与解决方案
1.  **天地图 418 报错（我是茶壶）**：
    *   *原因*：天地图 WAF 防火墙拦截了 `localhost` 的直接请求。
    *   *解决*：在 `vite.config.ts` 中配置 `/tianditu` 代理，并将前端所有 `tileLayer` 的请求地址改为 `/tianditu/DataServer?...`，完美绕过拦截。
2.  **Hibernate 原生 SQL `::geography` 解析失败**：
    *   *原因*：JPA/Hibernate 解析器会将 SQL 中的 `:` 误认为是命名参数，导致报 `语法错误 在 ":" 或附近`。
    *   *解决*：使用 `CAST(ST_SetSRID(...) AS geography)` 替代 `::geography`。
3.  **图片拉伸变形**：
    *   *原因*：CSS 强制设定了图片的固定宽高 (`w-full h-40`)。
    *   *解决*：引入 `object-contain` 配合 `flex items-center justify-center` 容器，保持图片原比例居中显示，不拉伸。
4.  **打包后 Leaflet 图钉图标丢失**：
    *   *原因*：Vite 打包时将 `node_modules/leaflet/dist/images/` 路径重写。
    *   *解决*：将 `images` 目录下的 `.png` 图片手动复制至 `frontend/public/images/`，并配置图钉 `iconUrl` 指向 `/images/marker-icon.png`。

## 10. 部署到阿里云轻量服务器 (Nginx + Spring Boot)
### 10.1 服务器环境准备
*   **JDK 17**：`apt install openjdk-17-jre-headless -y`
*   **Maven**：`apt install maven -y`
*   **Node.js 18**：`curl -fsSL https://deb.nodesource.com/setup_18.x | bash - && apt install -y nodejs`
*   **Nginx**：`apt install nginx -y`
*   **PostgreSQL 14 + PostGIS**：自行安装或使用宝塔面板一键部署，需配置 `pg_hba.conf` 和 `postgresql.conf` 允许本地连接。

### 10.2 后端部署
```bash
cd /opt/ditan/backend
# 打包并跳过测试
mvn clean package -DskipTests
# 后台启动（退出 SSH 仍运行）
pkill -f ditan-backend
nohup java -jar target/ditan-backend-0.0.1-SNAPSHOT.jar --spring.config.location=/opt/ditan/config/application.yml > nohup.out 2>&1 &
```

### 10.3 前端打包与部署
```bash
cd /opt/ditan/frontend
npm install
npm run build
# 生成的 dist 目录即为 Nginx 的 root 目录
```

### 10.4 Nginx 反向代理配置 (Nginx 核心配置)
```nginx
server {
    listen 80;
    server_name 120.26.140.51;  # 待域名通过备案后，改为 ditan2026.online

    root /opt/ditan/frontend/dist;
    index index.html;

    # 处理 Vue 路由
    location / {
        try_files $uri $uri/ /index.html;
    }

    # 转发后端 API 请求
    location /api/ {
        proxy_pass http://127.0.0.1:8080/api/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    }

    # 转发天地图瓦片请求（解决 418 防盗链及跨域问题）
    location /tianditu/ {
        proxy_pass https://t0.tianditu.gov.cn/;
        proxy_set_header Host t0.tianditu.gov.cn;
        proxy_set_header Referer 'http://120.26.140.51';
        proxy_cache off;
        proxy_no_cache 1;
    }
}
```
配置完成后执行 `systemctl reload nginx` 生效。

## 11. 后续维护与扩展建议
1.  **图片上传功能**：目前图片只支持 URL 填写的方案。后续可增加后端文件上传接口 (`MultipartFile`)，直接上传本地图片至服务器或云存储（如阿里云 OSS），再将返回的 URL 存入数据库。
2.  **真实用户登录**：使用 Spring Security + JWT 实现登录注册。将表里的 `owner_id` 与实际登录用户做绑定，实现真正的“我的摊位”数据隔离。
3.  **敏感信息安全**：切记在 `.gitignore` 中忽略 `.env.local`、`application-prod.yml` 等文件，不要将**天地图的密钥**或**数据库生产环境密码**提交到 GitHub 等公共代码仓库中。

---

**最后更新**：2026年7月15日
**项目地址**：`git@github.com:billywangyu/ditan.git`
